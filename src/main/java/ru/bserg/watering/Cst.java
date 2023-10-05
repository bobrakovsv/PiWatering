package ru.bserg.watering;

import java.time.format.DateTimeFormatter;

@SuppressWarnings("unused")
public class Cst {

    /**
     * Клапаны
     */
    public static final Long VALVES = 2L;

    /**
     * Шаровый клапан
     */
    public static final Long VALVE_BALL = 3L;

    /**
     * Соленоидный клапан
     */
    public static final Long VALVE_SOLENOID = 4L;

    /////////////////////////////////////////////////////////////////

    /**
     * Датчики
     */
    public static final Long SENSORS = 5L;

    /**
     * Датчики температуры и влажности
     */
    public static final Long SENSOR_TEMP_HUM = 6L;

    /**
     * Датчик температуры и влажности DHT22
     */
    public static final Long SENSOR_DHT22 = 7L;

    /////////////////////////////////////////////////////////////////

    /**
     * Аппаратные адреса устройств
     */
    public static final Long HW_ADDRESSES = 8L;

    /**
     * Pi4j GPIO
     */
    public static final Long HWA_PI4J_GPIO = 9L;

    /**
     * I2C
     */
    public static final Long HWA_I2C = 10L;

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Формат отображения дат
     */
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss";
    public static final String TIME_FORMAT = "HH:mm:ss";

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT);

    ////////////////////////////////////////////////////////////////////////////////
}
