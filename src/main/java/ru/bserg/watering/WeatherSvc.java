package ru.bserg.watering;

import lombok.extern.log4j.Log4j2;
import org.jooq.DSLContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.bserg.watering.db.tables.Weather;
import ru.bserg.watering.db.tables.records.WeatherRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@SuppressWarnings("unused")
@Service
@Controller
@RequestMapping("/weather")
@Log4j2
public class WeatherSvc {

    private final DSLContext dsl;
    private final DataSvc dataSvc;
    private final AccuweatherAutocollection accuweatherAutocollection;

    private static final Weather W = Weather.WEATHER;


    @Autowired
    public WeatherSvc(DSLContext dsl, DataSvc dataSvc, AccuweatherAutocollection accuweatherAutocollection) {
        this.dsl = dsl;
        this.dataSvc = dataSvc;
        this.accuweatherAutocollection = accuweatherAutocollection;
    }


    /**
     * Инициализация "хранимых" функций
     */
    public void initStoredFunctions() {
        String p = "ru.bserg.watering.WeatherSvc.";
        dataSvc.recreateAlias("ДОЖДЬ", p + "rain");
        dataSvc.recreateAlias("ВЕТЕР", p + "wind");
        dataSvc.recreateAlias("ТЕМП", p + "avgTemp");
        dataSvc.recreateAlias("ТЕМП_МАКС", p + "maxTemp");
        dataSvc.recreateAlias("ТЕМП_МИН", p + "minTemp");
    }


    /**
     * Запуск потока автоматического сбора данных погоды с сайта accuweather.com
     */
    public void initAutocollection() {
        accuweatherAutocollection.run();
    }


    public static Double aggregate(Connection conn, String column, String aggFun, int hours) throws SQLException {
        String datesCondition = "START_DATE < CURRENT_TIMESTAMP and END_DATE > DATEADD('hour', ?, CURRENT_TIMESTAMP)";
        boolean forecast = false;

        if (hours > 0) {
            datesCondition = "END_DATE > CURRENT_TIMESTAMP and START_DATE < DATEADD('hour', ?, CURRENT_TIMESTAMP)";
            forecast = true;
        }

        PreparedStatement stmt = conn.prepareStatement("select " + aggFun + "(" + column + ") from WTR.WEATHER where FORECAST = ? and " + datesCondition);
        stmt.setBoolean(1, forecast);
        stmt.setInt(2, hours);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        return rs.getDouble(1);
    }


    public static Double rain(Connection conn, int hours) throws SQLException {
        return aggregate(conn, "RAIN", "SUM", hours);
    }


    public static Double wind(Connection conn, int hours) throws SQLException {
        return aggregate(conn, "WIND_SPEED", "MAX", hours);
    }


    public static Double maxTemp(Connection conn, int hours) throws SQLException {
        return aggregate(conn, "TEMP_MAX", "MAX", hours);
    }


    public static Double minTemp(Connection conn, int hours) throws SQLException {
        return aggregate(conn, "TEMP_MIN", "MIN", hours);
    }


    public static Double avgTemp(Connection conn, int hours) throws SQLException {
        return aggregate(conn, "(TEMP_MAX+TEMP_MIN)/2", "AVG", hours);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Возвращает данные о погоде
     */
    @GetMapping()
    @ResponseBody
    public String get(@RequestParam boolean forecast, @RequestParam int dateIncrement) {
        try {
            LocalDateTime d = LocalDateTime.now();
            d = d.truncatedTo(ChronoUnit.DAYS);
            d = d.plusDays(dateIncrement);

            JSONArray arr = new JSONArray();
            for (WeatherRecord r : dsl.selectFrom(W)
                    .where(W.FORECAST.eq(forecast))
                    .and(W.END_DATE.gt(d))
                    .orderBy(W.START_DATE)
            ) {
                arr.put(Utils.recIntoMap(r));
            }

            JSONObject res = new JSONObject();
            res.put("date", Utils.dateToStr(d));
            res.put("data", arr);

            return Utils.restOk(res);
        } catch (Exception ex) {
            return Utils.restError(ex);
        }
    }

}
