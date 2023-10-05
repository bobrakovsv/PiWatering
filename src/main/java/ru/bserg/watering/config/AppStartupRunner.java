package ru.bserg.watering.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.bserg.watering.DataSvc;
import ru.bserg.watering.ValveSvc;
import ru.bserg.watering.WeatherSvc;


/**
 * Для одноразового выполнения во время старта системы какой-либо логики
 */
@Component
@Log4j2
public class AppStartupRunner implements ApplicationRunner {

    final DataSvc dataSvc;

    final WeatherSvc weatherSvc;

    final ValveSvc valveSvc;


    @Autowired
    public AppStartupRunner(DataSvc dataSvc, WeatherSvc weatherSvc, ValveSvc valveSvc) {
        this.dataSvc = dataSvc;
        this.weatherSvc = weatherSvc;
        this.valveSvc = valveSvc;
    }


    @Override
    public void run(ApplicationArguments args) {
        try {
            // Инициализация "хранимых" функций
            dataSvc.initStoredFunctions();
            weatherSvc.initStoredFunctions();
            // Запуск потока автоматического сбора данных погоды с сайта accuweather.com
            weatherSvc.initAutocollection();
        } catch (Exception ex) {
            log.error("Ошибка инициализации сервиса погоды: " + ex.getMessage());
        }

        // Запуск потоков автоматической обработки кранов
        try {
            valveSvc.startAutocontrol();
        } catch (Exception ex) {
            log.error("Ошибка запуска процессов автоматич. управления: " + ex.getMessage());
        }
    }

}