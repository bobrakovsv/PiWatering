package ru.bserg.watering;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import lombok.extern.log4j.Log4j2;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.bserg.watering.db.tables.HwAddress;
import ru.bserg.watering.db.tables.PolaritySwitch;
import ru.bserg.watering.db.tables.Valve;
import ru.bserg.watering.db.tables.ValveStateHist;
import ru.bserg.watering.db.tables.records.HwAddressRecord;
import ru.bserg.watering.db.tables.records.PolaritySwitchRecord;
import ru.bserg.watering.db.tables.records.ValveRecord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("DuplicatedCode")
@Service
@Controller
@RequestMapping("/valve")
@Log4j2
public class ValveSvc {

    // Длительность операции открытия/закрытия шарового крана (в миллисекундах)
    public static final int ballOperationDuration_ms = 5000;

    // Интервал цикла автоматической смены состояний (в миллисекундах)
    public static final int autoControlInterval_ms = 1000;

    private final DSLContext dsl;
    private final MsgSvc msgSvc;
    private final DataSvc dataSvc;

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private GpioSvc gpioSvc;

    private static final Valve V = Valve.VALVE;
    private static final ValveStateHist VH = ValveStateHist.VALVE_STATE_HIST;
    private static final HwAddress HWA = HwAddress.HW_ADDRESS;
    private static final PolaritySwitch PSW = PolaritySwitch.POLARITY_SWITCH;


    @Autowired
    public ValveSvc(DSLContext dsl, MsgSvc msgSvc, DataSvc dataSvc) {
        this.dsl = dsl;
        this.msgSvc = msgSvc;
        this.dataSvc = dataSvc;
    }


    /**
     * Вернуть запись по id
     */
    public ValveRecord getRow(Long id) {
        return dsl.selectFrom(V).where(V.ID.eq(id)).fetchOne();
    }


    /**
     * Вернуть наименование по id
     */
    public String getName(Long id) {
        return dsl.select(V.NAME).from(V).where(V.ID.eq(id)).fetchOne(V.NAME);
    }


    /**
     * Добавление записи в историю состояний крана
     */
    public void writeHistory(ValveRecord valve, LocalDateTime stateDate, Boolean isOpen) {
        dsl.insertInto(VH, VH.VALVE_ID, VH.STATE_DATE, VH.IS_OPEN)
                .values(valve.getId(), stateDate, isOpen)
                .execute();
    }


    /**
     * Обновление состояния крана с записью истории смены состояний
     */
    private void updateState(ValveRecord v, Boolean isOpen) {
        if (isOpen != null && isOpen.equals(v.getIsOpen()))
            return;
        LocalDateTime stateDate = Utils.getCurrentDate();
        v.setStateDate(stateDate);
        v.setIsOpen(isOpen);
        v.setNextStateDate(null);
        writeHistory(v, stateDate, isOpen);
    }


    /**
     * Получить пин для крана
     */
    private Pin getPin(ValveRecord v) throws Exception {
        HwAddressRecord adr = dsl.selectFrom(HWA)
                .where(HWA.ID.eq(v.getHwadrId()))
                .fetchOne();
        if (Cst.HWA_PI4J_GPIO.equals(adr.getTypeId())) {
            return gpioSvc.getRaspiPin(adr.getValue());
        }
        throw new Exception("Внутренняя ошибка. Невозможно получить пин для клапана " + v.getId());
    }


    /**
     * Получить пин для переключателя полярности
     */
    private Pin getPin(PolaritySwitchRecord psw) throws Exception {
        HwAddressRecord adr = dsl.selectFrom(HWA)
                .where(HWA.ID.eq(psw.getHwadrId()))
                .fetchOne();
        if (Cst.HWA_PI4J_GPIO.equals(adr.getTypeId())) {
            log.debug("getPin, addr=" + adr.getValue());
            return gpioSvc.getRaspiPin(adr.getValue());
        }
        throw new Exception("Внутренняя ошибка. Невозможно получить пин для переключателя полярности " + psw.getId());
    }


    /**
     * Инициализация пина
     */
    void initPin(PolaritySwitchRecord psw) throws Exception {
        if (SettingSvc.DEBUG)
            return;
        gpioSvc.configureOutput(psw.getHwadrId(), getPin(psw), psw.getOffSig());
    }


    /**
     * Инициализация пина
     */
    void initPin(ValveRecord v) throws Exception {
        if (SettingSvc.DEBUG)
            return;
        gpioSvc.configureOutput(v.getHwadrId(), getPin(v), v.getOffSig());
    }


    /**
     * Перевести пин в состояние "включен"
     */
    void setOn(ValveRecord v) {
        log.debug("setOn " + v.getName());
        if (SettingSvc.DEBUG) {
            return;
        }
        GpioPinDigitalOutput p = gpioSvc.getProvisionedOutput(v.getHwadrId());
        if (v.getOnSig() == 0) {
            p.low();
        } else if (v.getOnSig() == 1) {
            p.high();
        }
    }


    /**
     * Перевести пин в состояние "выключен"
     */
    void setOff(ValveRecord v) {
        log.debug("setOff " + v.getName());
        if (SettingSvc.DEBUG) {
            return;
        }
        GpioPinDigitalOutput p = gpioSvc.getProvisionedOutput(v.getHwadrId());
        if (v.getOffSig() == 0) {
            p.low();
        } else if (v.getOffSig() == 1) {
            p.high();
        }
    }


    /**
     * Перевести пин в состояние "включен"
     */
    void setOn(PolaritySwitchRecord psw) {
        log.debug("setOn " + psw.getName());
        if (SettingSvc.DEBUG) {
            return;
        }
        GpioPinDigitalOutput p = gpioSvc.getProvisionedOutput(psw.getHwadrId());
        if (psw.getOnSig() == 0) {
            p.low();
        } else if (psw.getOnSig() == 1) {
            p.high();
        }
    }


    /**
     * Перевести пин в состояние "выключен"
     */
    void setOff(PolaritySwitchRecord psw) {
        log.debug("setOff " + psw.getName());
        if (SettingSvc.DEBUG) {
            return;
        }
        GpioPinDigitalOutput p = gpioSvc.getProvisionedOutput(psw.getHwadrId());
        if (psw.getOffSig() == 0) {
            p.low();
        } else if (psw.getOffSig() == 1) {
            p.high();
        }
    }


    /**
     * Открыть или закрыть несколько шаровых кранов, относящихся к данному переключателю полярности
     */
    private void setState(PolaritySwitchRecord psw, List<ValveRecord> valves, boolean toOpen) {
        // Сделана обертка для отказа от одновременного открытия/закрытия кранов для уменьшения нагрузки на проводку (в основном на провод 0В)
        for (ValveRecord v : valves) {
            List<ValveRecord> vs = new ArrayList<>(1);
            vs.add(v);
            setState_(psw, vs, toOpen);
        }
    }


    /**
     * Открыть или закрыть несколько шаровых кранов, относящихся к данному переключателю полярности
     */
    private void setState_(PolaritySwitchRecord psw, List<ValveRecord> valves, boolean toOpen) {
        if (toOpen) {
            setOn(psw);
        } else {
            setOff(psw);
        }

        for (ValveRecord v : valves) {

            setOn(v);

            // Обновление информации о текущем состоянии крана
            // и в случае операции открытия вычисление даты закрытия
            try {
                updateState(v, toOpen);
                if (toOpen) {
                    int duration;
                    try {
                        duration = dataSvc.evaluateDurationlExpr(v.getOpenDurationExpr());
                    } catch (Exception ex) {
                        msgSvc.put("Ошибка вычисления длительности открытия для " + v.getName() + ": " + ex.getMessage(), ex);
                        duration = 0;
                    }
                    v.setNextStateDate(Utils.getNextDate(duration * 1000L * 60L));
                }
                v.update();
            } catch (Exception ex) {
                msgSvc.put("Ошибка обновления информации о текущем состоянии крана " + v.getName() + ": " + ex.getMessage(), ex);
            }

        }

        Utils.sleep(ballOperationDuration_ms);

        for (ValveRecord v : valves) {
            setOff(v);
        }

        if (toOpen) {
            setOff(psw);
        }
    }


    /**
     * Инициализация данного переключателя полярности и всех шаровых кранов, которые к нему относятся
     * Краны переводятся в закрытое состояние
     */
    private void init(PolaritySwitchRecord psw) throws Exception {
        // Вначале инициализируются все пины
        initPin(psw);
        List<ValveRecord> vl = dsl
                .selectFrom(V)
                .where(V.POLSWTCH_ID.eq(psw.getId()))
                .and(V.TYPE_ID.eq(Cst.VALVE_BALL))
                .fetch();
        for (ValveRecord v : vl) {
            initPin(v);
        }
        setState(psw, vl, false);
    }


    /**
     * Запуск потоков автоматической обработки кранов
     * по одному на каждый переключатель полярности
     */
    public void startAutocontrol() {
        msgSvc.put(3, "Запуск потоков автоматического управления кранами");
        for (PolaritySwitchRecord psw : dsl.selectFrom(PSW).fetch()) {
            mainAutocontrolCycle(psw);
        }
    }


    /**
     * Основной цикл управления по одному переключателю полярности
     */
    @Async
    protected void mainAutocontrolCycle(PolaritySwitchRecord psw) {
        msgSvc.put(3, "Инициализация автоматического управления для " + psw.getName());

        try {
            // Привести все шаровые краны в исходное состояние (закрыть)
            log.debug("Инициализация состояния кранов (выключение) для " + psw.getName());
            init(psw);
        } catch (Exception ex) {
            msgSvc.put("Ошибка инициализации состояния кранов: " + ex.getMessage(), ex);
            msgSvc.put(1, "Автоматическое управление невозможно для " + psw.getName());
            return;
        }

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                boolean sleep = true;

                /////////////////////////////////////////////////////////
                // 1. Закрыть краны, для которых подошло время закрытия
                /////////////////////////////////////////////////////////
                List<ValveRecord> valves2Close = dsl.selectFrom(V)
                        .where(V.POLSWTCH_ID.eq(psw.getId()))
                        .and(V.TYPE_ID.eq(Cst.VALVE_BALL))
                        .and(V.IS_OPEN.eq(true))
                        .and(V.NEXT_STATE_DATE.lessOrEqual(LocalDateTime.now()))
                        .fetch();
                if (!valves2Close.isEmpty()) {
                    sleep = false;
                    setState(psw, valves2Close, false);
                }

                /////////////////////////////////////////////////////////
                // 2. Пометить необходимость открытия кранов, которые должны быть открытими после одного из только что закрытых кранов
                //    (безотносительно привязки к переключателю полярности)
                //    Обрабатываются только краны с автоматическим управлением
                /////////////////////////////////////////////////////////
                if (!valves2Close.isEmpty()) {
                    for (ValveRecord cv : valves2Close) {
                        if (cv.getAutoControl()) {
                            for (ValveRecord v : dsl.selectFrom(V)
                                    .where(V.TYPE_ID.eq(Cst.VALVE_BALL))
                                    .and(V.IS_OPEN.eq(false))
                                    .and(V.AUTO_CONTROL.eq(true))
                                    .and(V.OPEN_AFTER_ID.eq(cv.getId()))
                                    .fetch()
                            ) {
                                v.setNextStateDate(LocalDateTime.now());
                                v.update();
                            }
                        }
                    }
                }

                /////////////////////////////////////////////////////////
                // 3. Открыть краны, для которых подошло время открытия
                /////////////////////////////////////////////////////////
                List<ValveRecord> valves2Open = dsl.selectFrom(V)
                        .where(V.POLSWTCH_ID.eq(psw.getId()))
                        .and(V.TYPE_ID.eq(Cst.VALVE_BALL))
                        .and(V.IS_OPEN.eq(false))
                        .and(V.NEXT_STATE_DATE.lessOrEqual(LocalDateTime.now()))
                        .fetch();
                if (!valves2Open.isEmpty()) {
                    sleep = false;
                    setState(psw, valves2Open, true);
                }

                /////////////////////////////////////////////////////////
                // 4. Открыть краны по условию открытия
                /////////////////////////////////////////////////////////
                valves2Open = new ArrayList<>();
                for (ValveRecord v : dsl.selectFrom(V)
                        .where(V.POLSWTCH_ID.eq(psw.getId()))
                        .and(V.TYPE_ID.eq(Cst.VALVE_BALL))
                        .and(V.IS_OPEN.eq(false))
                        .and(V.AUTO_CONTROL.eq(true))
                        .and(DSL.length(V.OPEN_CONDITION_EXPR).gt(0))
                ) {
                    try {
                        boolean open = dataSvc.evaluateCondition(v.getOpenConditionExpr());
                        if (open) {
                            valves2Open.add(v);
                        }
                    } catch (Exception ex) {
                        msgSvc.put("Для крана " + v.getName() + ": ", ex);
                    }
                }
                if (!valves2Open.isEmpty()) {
                    sleep = false;
                    setState(psw, valves2Open, true);
                }

                if (sleep)
                    Utils.sleep(autoControlInterval_ms);

            } catch (Exception ex) {
                msgSvc.put("mainAutocontrolCycle:", ex);
                Utils.sleep(autoControlInterval_ms * 10);

            }
        }
    }


    /**
     * Проверки записи перед сохранением
     */
    private void validate(ValveRecord r) throws Exception {
        Utils.cleanStrings(r);

        if (r.getName() == null)
            Utils.err("Обозначение должно быть задано");

        if (r.getDescr() == null)
            Utils.err("Описание должно быть задано");

        if (r.getOpenDurationExpr() == null)
            Utils.err("Длительность открытия должна быть задана");

        if (r.getOpenAfterId() == null && r.getOpenConditionExpr() == null)
            Utils.err("Условия открытия должно быть задано");

        if (r.getOpenConditionExpr() != null)
            r.setOpenAfterId(null);

        // Проверка корректности выражения для длительности открытия
        try {
            dataSvc.evaluateDurationlExpr(r.getOpenDurationExpr());
        } catch (Exception ex) {
            Utils.err4ui("Некорректное выражение длительности открытия", Utils.getExceptionText(ex));
        }

        // Проверка корректности условия открытия
        if (r.getOpenConditionExpr() != null) {
            try {
                dataSvc.evaluateCondition(r.getOpenConditionExpr());
            } catch (Exception ex) {
                Utils.err4ui("Некорректное условие открытия", Utils.getExceptionText(ex));
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Возвращает список кранов
     */
    @GetMapping("list")
    @ResponseBody
    public String list() {
        try {
            JSONArray res = new JSONArray();
            for (ValveRecord r : dsl.selectFrom(V).orderBy(V.NAME)) {
                Map<String, Object> m = Utils.recIntoMap(r);
                if (r.getOpenAfterId() != null) {
                    m.put("open_after_name", getName(r.getOpenAfterId()));
                }
                m.put("state_date", Utils.dateTimeToShortStr(r.getStateDate()));
                m.put("next_state_date", Utils.dateTimeToShortStr(r.getNextStateDate()));
                res.put(m);
            }
            return Utils.restOk(res);
        } catch (Exception ex) {
            return Utils.restError(ex);
        }
    }


    /**
     * Смена режима работы крана (автоматический/ручной)
     */
    @PostMapping("/{id}/toggleControl")
    @ResponseBody
    public String toggleControl(@PathVariable Long id) {
        try {
            ValveRecord r = getRow(id);
            if (r == null)
                Utils.err("Кран " + id + " не найден");
            r.setAutoControl(!r.getAutoControl());
            r.update();
            return Utils.restOk();
        } catch (Exception ex) {
            return Utils.restError(ex);
        }
    }


    /**
     * Смена состояния крана (открыт/закрыт)
     */
    @PostMapping("/{id}/toggleState")
    @ResponseBody
    public String toggleState(@PathVariable Long id) {
        try {
            ValveRecord r = getRow(id);
            if (r == null)
                Utils.err("Кран " + id + " не найден");
            if (r.getAutoControl())
                Utils.err("Кран " + r.getName() + " управляется автоматически. Сначала нужно сменить режим управления");
            r.setNextStateDate(Utils.getCurrentDate());
            r.update();
            JSONObject res = new JSONObject();
            res.put("next_state_date", Utils.dateTimeToStr(r.getNextStateDate()));
            return Utils.restOk(res);
        } catch (Exception ex) {
            return Utils.restError(ex);
        }
    }


    /**
     * Возвращает кран
     */
    @GetMapping("/{id}")
    @ResponseBody
    public String get(@PathVariable Long id) {
        try {
            ValveRecord r = getRow(id);
            if (r == null)
                Utils.err("Кран " + id + " не найден");
            Map<String, Object> m = Utils.recIntoMap(r);
            JSONObject res = new JSONObject(m);
            return Utils.restOk(res);
        } catch (Exception ex) {
            return Utils.restError(ex);
        }
    }


    /**
     * Сохранение атрибутов крана
     */
    @PostMapping
    @ResponseBody
    public String save(@RequestBody String json) {
        try {
            JSONObject v = new JSONObject(json);
            long id = v.optLong("ID", -1);
            if (id == -1L)
                Utils.err("Создание пока не раелизовано");
            ValveRecord r = getRow(id);
            if (r == null)
                Utils.err("Кран " + id + " не найден");
            r.setName(v.optString("NAME"));
            r.setDescr(v.optString("DESCR"));
            r.setOpenDurationExpr(v.optString("OPEN_DURATION_EXPR"));
            r.setOpenConditionExpr(v.optString("OPEN_CONDITION_EXPR"));
            Long openAfterId = v.optLong("OPEN_AFTER_ID", -1L);
            if (openAfterId == -1L)
                openAfterId = null;
            r.setOpenAfterId(openAfterId);
            validate(r);
            r.update();
            JSONObject res = new JSONObject();
            res.put("ID", r.getId());
            return Utils.restOk(res);
        } catch (Exception ex) {
            return Utils.restError(ex);
        }
    }


    /**
     * Возвращает историю смены состояний крана
     */
    @GetMapping("/{id}/history")
    @ResponseBody
    public String getHistory(@PathVariable Long id) {
        try {
            Boolean currOpen = true;
            LocalDateTime currDate = null;

            JSONArray res = new JSONArray();
            for (Record r : dsl.select(VH.ID, VH.STATE_DATE, VH.IS_OPEN)
                    .from(VH)
                    .where(VH.VALVE_ID.eq(id))
                    .orderBy(VH.STATE_DATE.desc())
            ) {
                Boolean prevOpen = r.get(VH.IS_OPEN);
                LocalDateTime prevDate = r.get(VH.STATE_DATE);
                if (!Utils.nvl(currOpen, true)
                        && currDate != null
                        && Utils.nvl(prevOpen, false)
                        && prevDate != null
                ) {
                    JSONObject j = new JSONObject();
                    j.put("openDate", Utils.dateTimeToShortStr(prevDate));
                    j.put("duration", Utils.intervalToStr(prevDate, currDate));
                    res.put(j);
                }
                currOpen = prevOpen;
                currDate = prevDate;
            }
            return Utils.restOk(res);
        } catch (Exception ex) {
            return Utils.restError(ex);
        }
    }

}