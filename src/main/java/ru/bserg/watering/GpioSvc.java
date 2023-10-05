package ru.bserg.watering;

import com.pi4j.io.gpio.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Log4j2
public class GpioSvc {

    // create gpio controller
    private static GpioController gpio = null;

    private static final HashMap<String, Pin> pinByAddr = new HashMap<>();

    // Provisioned output pins
    private final HashMap<Long, GpioPinDigitalOutput> provOutputs = new HashMap<>();


    /**
     * Возвращает адрес в формате библиотеки Pi4j
     */
    public Pin getRaspiPin(String addr) throws Exception {
        Pin res = pinByAddr.get(addr);
        if (res != null)
            return res;
        throw new Exception("Некорректный адрес GPIO - " + addr);
    }


    /**
     * Настройка пина на вывод дискретных сигналов и установка в первоначальное состояние
     */
    public GpioPinDigitalOutput configureOutput(Long hwAddrId, Pin raspiPin, Integer defaultSig) {
        PinState state = PinState.LOW;
        if (defaultSig == 1)
            state = PinState.HIGH;
        GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(raspiPin, "-", state);
        provOutputs.put(hwAddrId, pin);
        pin.setShutdownOptions(true, state);
        return pin;
    }


    /**
     * Настройка пина на вывод дискретных сигналов и установка в первоначальное состояние
     */
    public GpioPinDigitalOutput getProvisionedOutput(Long hwAddrId) {
        return provOutputs.get(hwAddrId);
    }


    static {
        if (!SettingSvc.DEBUG) {
            gpio = GpioFactory.getInstance();
        }

        pinByAddr.put("00", RaspiPin.GPIO_00);
        pinByAddr.put("01", RaspiPin.GPIO_01);
        pinByAddr.put("02", RaspiPin.GPIO_02);
        pinByAddr.put("03", RaspiPin.GPIO_03);
        pinByAddr.put("04", RaspiPin.GPIO_04);
        pinByAddr.put("05", RaspiPin.GPIO_05);
        pinByAddr.put("06", RaspiPin.GPIO_06);
        pinByAddr.put("07", RaspiPin.GPIO_07);
        pinByAddr.put("08", RaspiPin.GPIO_08);
        pinByAddr.put("09", RaspiPin.GPIO_09);
        pinByAddr.put("10", RaspiPin.GPIO_10);
        pinByAddr.put("11", RaspiPin.GPIO_11);
        pinByAddr.put("12", RaspiPin.GPIO_12);
        pinByAddr.put("13", RaspiPin.GPIO_13);
        pinByAddr.put("14", RaspiPin.GPIO_14);
        pinByAddr.put("15", RaspiPin.GPIO_15);
        pinByAddr.put("16", RaspiPin.GPIO_16);
        // Следующие пины отсутствуют
        //pinByAddr.put("17", RaspiPin.GPIO_17);
        //pinByAddr.put("18", RaspiPin.GPIO_18);
        //pinByAddr.put("19", RaspiPin.GPIO_19);
        //pinByAddr.put("20", RaspiPin.GPIO_20);
        pinByAddr.put("21", RaspiPin.GPIO_21);
        pinByAddr.put("22", RaspiPin.GPIO_22);
        pinByAddr.put("23", RaspiPin.GPIO_23);
        pinByAddr.put("24", RaspiPin.GPIO_24);
        pinByAddr.put("25", RaspiPin.GPIO_25);
        pinByAddr.put("26", RaspiPin.GPIO_26);
        pinByAddr.put("27", RaspiPin.GPIO_27);
        pinByAddr.put("28", RaspiPin.GPIO_28);
        pinByAddr.put("29", RaspiPin.GPIO_29);
    }

}
