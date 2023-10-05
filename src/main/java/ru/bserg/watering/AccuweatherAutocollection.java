package ru.bserg.watering;

import org.jooq.DSLContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.bserg.watering.db.tables.Weather;
import ru.bserg.watering.db.tables.records.WeatherRecord;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Реализация автоматического сбора данных погоды с сайта accuweather.com
 */
@Service
public class AccuweatherAutocollection {

    private final DSLContext dsl;
    private final SettingSvc settingSvc;
    private final MsgSvc msgSvc;
    private final DataSvc dataSvc;

    private static final Weather W = Weather.WEATHER;


    @Autowired
    public AccuweatherAutocollection(DSLContext dsl, SettingSvc settingSvc, MsgSvc msgSvc, DataSvc dataSvc) {
        this.dsl = dsl;
        this.settingSvc = settingSvc;
        this.msgSvc = msgSvc;
        this.dataSvc = dataSvc;
    }


    /**
     * Запрос погоды
     * forecast=true - прогноз
     * forecast=false - фактические данные
     */
    private String query(boolean forecast) throws Exception {
        String urls;
        if (forecast)
            urls = settingSvc.getValue(SettingSvc.ACW_FORECAST_URL);
        else
            urls = settingSvc.getValue(SettingSvc.ACW_WEATHER_URL);

        URL url = new URL(urls);
        URLConnection conn = url.openConnection();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String inputLine;
            StringBuilder sb = new StringBuilder();

            while ((inputLine = in.readLine()) != null)
                sb.append(inputLine).append("\n");

            return sb.toString();
        }
    }


    /**
     * Основной поток автоматического сбора данных о погоде
     */
    @SuppressWarnings("BusyWait")
    @Async
    public void run() {
        msgSvc.put(3, "Инициализация автоматического сбора данных погоды с сайта accuweather.com");

        // Интервал проверки условия сбора данных о погоде
        int interval;
        try {
            interval = Integer.parseInt(settingSvc.getValue(SettingSvc.ACW_QRY_CONDITION_INTERVAL)) * 1000;
        } catch (Exception ex) {
            msgSvc.put(2, "Некорректное значение параметра '" + SettingSvc.ACW_QRY_CONDITION_INTERVAL + "'. По умолчанию берется 1");
            interval = 1000;
        }

        // Основной цикл
        while (true) {
            try {
                // Условие запроса и обновления погоды
                String qryCondition = settingSvc.getValue(SettingSvc.ACW_QRY_CONDITION);

                boolean qry = dataSvc.evaluateCondition(qryCondition);
                if (qry) {
                    msgSvc.put(3, "Условие обновления погоды '" + qryCondition + "' выполнено");
                    updateFact();
                    updateForecast();
                }

                Thread.sleep(interval);
            } catch (InterruptedException e) {
                msgSvc.put(2, "Принудительное завершение автоматического управления");
                return;
            } catch (Exception ex) {
                msgSvc.put("AccuweatherAutocollection: " + ex.getMessage(), ex);
                try {
                    Thread.sleep(interval);
                } catch (Exception ignore) {
                }
            }
        }

    }


    /**
     * Получить осадки в мм
     */
    private Double rain_mm(JSONObject r) {
        if (r == null)
            return null;

        double val = r.optDouble("Value");

        if (Double.isNaN(val))
            return null;

        String unit = r.optString("Unit");
        if (unit != null) {
            if ("in".equals(unit)) {
                return (25.4f * val);
            } else if ("mm".equals(unit)) {
                return val;
            }
        }

        return null;
    }


    /**
     * Получить температуру в градусах
     */
    private Integer temp_c(JSONObject t) {
        if (t == null)
            return null;

        float val = t.optFloat("Value");

        if (Float.isNaN(val))
            return null;

        String unit = t.optString("Unit");
        if (unit != null) {
            if ("C".equals(unit)) {
                return Math.round(val);
            } else if ("F".equals(unit)) {
                return Math.round((val - 32f) * 5f / 9f);
            }
        }

        return null;
    }


    /**
     * Получение и обновление фактических данных
     */
    private void updateFact() throws Exception {
        JSONObject r;
        try {
            // Вместо объекта данный сервис возвращает массив из одного объекта
            JSONArray ar = new JSONArray(query(false));
            r = ar.getJSONObject(0);

            if (r == null) throw new Exception("Пустой ответ");
        } catch (Exception ex) {
            throw new Exception("Ошибка разбора ответа сервера о фактической погоде: " + ex.getMessage());
        }

        WeatherRecord w = dsl.newRecord(W);
        w.setForecast(false);
        LocalDateTime now = LocalDateTime.now();
        w.setQryDate(now);
        w.setEndDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(r.getLong("EpochTime") * 1000), ZoneId.systemDefault()));            // Дата наблюдения
        w.setStartDate(LocalDateTime.ofInstant(Instant.ofEpochMilli((r.getLong("EpochTime") - 3600L) * 1000), ZoneId.systemDefault())); // На час раньше

        JSONObject p1h = r.optJSONObject("Precip1hr");
        if (p1h != null) {
            Double r1 = rain_mm(p1h.optJSONObject("Metric"));
            Double r2 = rain_mm(p1h.getJSONObject("Imperial"));
            if (r1 != null && r2 != null && r2 > r1)
                r1 = r2;
            else if (r1 == null)
                r1 = r2;

            if (r1 != null && r1 > 0)
                w.setRain(r1);
        }

        JSONObject temp = r.optJSONObject("Temperature");
        if (temp != null) {
            Integer t = temp_c(temp.optJSONObject("Metric"));
            if (t == null) {
                t = temp_c(temp.optJSONObject("Imperial"));
            }
            if (t != null) {
                w.setTempMin(t);
                w.setTempMax(t);
            }
        }

        int cloud_pct = r.optInt("CloudCover");
        if (cloud_pct != 0) {
            w.setCloudsPct(cloud_pct);
        }

        JSONObject wind = r.optJSONObject("Wind");

        if (wind != null) {
            JSONObject dir = wind.getJSONObject("Direction");
            if (dir != null && dir.has("Degrees")) {
                int deg = dir.getInt("Degrees");
                w.setWindDeg(deg);
            }
            JSONObject speed = wind.getJSONObject("Speed");
            if (speed != null && speed.has("Metric") && speed.getJSONObject("Metric").getString("Unit").equals("km/h")) {
                float kmh = speed.getJSONObject("Metric").optFloat("Value");
                if (!Float.isNaN(kmh)) {
                    int s = Math.round(kmh * 10 / 36);
                    w.setWindSpeed(s);
                }
            }
        }

        dsl.deleteFrom(W)
                .where(W.FORECAST.eq(w.getForecast()))
                .and(W.START_DATE.eq(w.getStartDate()))
                .execute();

        w.insert();
    }


    /**
     * Обновление за 12 часов (раздел Day или Night данных)
     */
    private void updateForecast12(
            LocalDateTime qryDate,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Integer temperature,
            JSONObject dayOrNight
    ) {
        WeatherRecord w = dsl.newRecord(W);
        w.setForecast(true);
        w.setQryDate(qryDate);
        w.setStartDate(startDate);
        w.setEndDate(endDate);
        w.setTempMin(temperature);
        w.setTempMax(temperature);

        if (dayOrNight != null) {
            Double rain;
            rain = rain_mm(dayOrNight.optJSONObject("Rain"));
            if (rain != null && rain > 0f)
                w.setRain(rain);

            int cloud_pct = dayOrNight.optInt("CloudCover");
            if (cloud_pct != 0) {
                w.setCloudsPct(cloud_pct);
            }

            JSONObject wind = dayOrNight.optJSONObject("Wind");

            if (wind != null) {
                JSONObject dir = wind.getJSONObject("Direction");
                if (dir != null && dir.has("Degrees")) {
                    int deg = dir.getInt("Degrees");
                    w.setWindDeg(deg);
                }
                JSONObject speed = wind.getJSONObject("Speed");
                if (speed != null && speed.has("Unit") && speed.get("Unit").equals("mi/h")) {
                    float mih = speed.optFloat("Value");
                    if (!Float.isNaN(mih)) {
                        int s = Math.round(mih * 1.6f * 10 / 36);
                        w.setWindSpeed(s);
                    }
                }
            }
        }

        dsl.deleteFrom(W)
                .where(W.FORECAST.eq(w.getForecast()))
                .and(W.START_DATE.eq(w.getStartDate()))
                .execute();

        w.insert();
    }


    /**
     * Получение и обновление прогнозных данных
     */
    private void updateForecast() throws Exception {
        JSONArray ar;
        try {
            JSONObject r = new JSONObject(query(true));
            ar = r.optJSONArray("DailyForecasts");
            if (ar == null) throw new Exception("Пустой ответ");
        } catch (Exception ex) {
            throw new Exception("Ошибка разбора ответа сервера о фактической погоде: " + ex.getMessage());
        }

        for (int i = 0; i < ar.length(); i++) {
            JSONObject r = ar.getJSONObject(i);

            JSONObject temp = r.optJSONObject("Temperature");
            Integer tempMin;
            Integer tempMax = null;

            if (temp != null) {
                tempMin = temp_c(temp.optJSONObject("Minimum"));
                tempMax = temp_c(temp.optJSONObject("Maximum"));
                if (tempMin == null)
                    tempMin = tempMax;
                if (tempMax == null)
                    tempMax = tempMin;
            }

            LocalDateTime qryDate = LocalDateTime.now();
            updateForecast12(
                    qryDate,
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(r.getLong("EpochDate") * 1000), ZoneId.systemDefault()),
                    LocalDateTime.ofInstant(Instant.ofEpochMilli((r.getLong("EpochDate") + 43200) * 1000), ZoneId.systemDefault()), // Плюc 12 часов
                    tempMax,
                    r.optJSONObject("Day")
            );

            updateForecast12(
                    qryDate,
                    LocalDateTime.ofInstant(Instant.ofEpochMilli((r.getLong("EpochDate") + 43200) * 1000), ZoneId.systemDefault()), // Плюc 12 часов
                    LocalDateTime.ofInstant(Instant.ofEpochMilli((r.getLong("EpochDate") + 86400) * 1000), ZoneId.systemDefault()), // Плюc 24 часа
                    tempMax,
                    r.optJSONObject("Night")
            );
        }
    }


}
  

