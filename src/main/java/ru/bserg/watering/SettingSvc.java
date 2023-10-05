package ru.bserg.watering;

import lombok.extern.log4j.Log4j2;
import org.jooq.DSLContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.bserg.watering.db.tables.Setting;
import ru.bserg.watering.db.tables.records.SettingRecord;

/**
 * Настройки приложения
 */
@SuppressWarnings("CommentedOutCode")
@Service
@Controller
@RequestMapping("/settings")
@Log4j2
public class SettingSvc {

    // Для запуска в режиме отладки поменять на true
    // В режиме отладки отключается взаимодействие с GPIO (general-purpose input/output) Raspberry Pi
    public static boolean DEBUG = true;

    private final DSLContext dsl;

    private static final Setting S = Setting.SETTING;


    @Autowired
    public SettingSvc(DSLContext dsl) {
        this.dsl = dsl;
    }


//  /////////////////////////////////////////////////////////////////////////////////////////
//  ///////////////////////////// Для openweathermap.org ////////////////////////////////////
//  /////////////////////////////////////////////////////////////////////////////////////////
//
//  /**
//   * Ключ API сайта погоды openwhetermap.org
//   */
//  public static final String OWM_APPID = "owm_appid";
//
//  /**
//   * Шаблон url для получения фактической погоды с сайта погоды openwhetermap.org
//   * Должен содержать подстановочные строки {{lon}}, {{lat}}, {{appid}}
//   */
//  public static final String OWM_WEATHER_URL = "owm_weather_url";
//
//  /**
//   * Шаблон url для получения прогноза погоды с сайта погоды openwhetermap.org
//   * Должен содержать подстановочные строки {{lon}}, {{lat}}, {{appid}}
//   */
//  public static final String OWM_FORECAST_URL = "owm_forecast_url";
//
//  /**
//   * Условие запроса данных погоды
//   * Должно возвращать булевский тип
//   */
//  public static final String OWM_QRY_CONDITION = "owm_qry_condition";
//
//  /**
//   * Частота проверки условия запроса данных погоды, сек (целое значение)
//   */
//  public static final String OWM_QRY_CONDITION_INTERVAL = "owm_qry_condition_interval";


    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////// Для accuweather.com /////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Частота проверки условия запроса данных погоды, сек (целое значение)
     */
    public static final String ACW_QRY_CONDITION_INTERVAL = "acw_qry_condition_interval";

    /**
     * Условие запроса данных погоды
     * Должно возвращать булевский тип
     */
    public static final String ACW_QRY_CONDITION = "acw_qry_condition";

    /**
     * url для получения фактической погоды с сайта погоды accuweather.com
     * Должен содержать location key, api key и details=true
     */
    public static final String ACW_WEATHER_URL = "acw_weather_url";

    /**
     * url для получения прогноза погоды с сайта погоды accuweather.com
     * Должен содержать location key, api key и details=true
     */
    public static final String ACW_FORECAST_URL = "acw_forecast_url";


    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    private static final Setting STG = Setting.SETTING;

    /**
     * Получить значение параметра
     */
    public String getValue(String name, String defaultValue) {
        String res = dsl
                .selectFrom(STG)
                .where(STG.NAME.eq(name))
                .fetchOne(STG.VALUE);
        if (res == null)
            return defaultValue;
        return res;
    }


    /**
     * Получить значение параметра
     */
    public String getValue(String name) {
        return getValue(name, null);
    }


    /**
     * Установить значение параметра
     */
    public void setValue(String name, String value) {
        int count = dsl.selectCount()
                .from(STG)
                .where(STG.NAME.eq(name))
                .fetchOne(0, int.class);

        if (count == 0) {
            dsl.insertInto(STG, STG.NAME, STG.VALUE)
                    .values(name, value)
                    .execute();

        } else {
            dsl.update(STG)
                    .set(STG.VALUE, value)
                    .where(STG.NAME.eq(name))
                    .execute();
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Возвращает настройки
     */
    @GetMapping()
    @ResponseBody
    public String list() {
        try {
            JSONArray res = new JSONArray();
            for (SettingRecord r : dsl.selectFrom(S).orderBy(S.NAME)) {
                res.put(Utils.recIntoMap(r));
            }
            return Utils.restOk(res);
        } catch (Exception ex) {
            return Utils.restError(ex);
        }
    }


    /**
     * Сохранение настроек
     */
    @PostMapping
    @ResponseBody
    public String save(@RequestBody String json) {
        try {
            JSONArray v = new JSONArray(json);
            for (int i = 0; i < v.length(); i++) {
                JSONObject o = v.optJSONObject(i);
                setValue(o.getString("NAME"), o.getString("VALUE"));
            }
            return Utils.restOk();
        } catch (Exception ex) {
            return Utils.restError(ex);
        }
    }


}
