
INSERT INTO WTR.SETTING(ID, DESCR, NAME, VALUE) VALUES (3152, 'Широта местности', 'latitude', '51.688790');
INSERT INTO WTR.SETTING(ID, DESCR, NAME, VALUE) VALUES (3153, 'Долгота местности', 'longitude', '39.314470');
INSERT INTO WTR.SETTING(ID, DESCR, NAME, VALUE) VALUES (3154, 'Ключ API сайта погоды openwhetermap.org', 'owm_appid', 'b3f0529522fd1851153c71c00c529162');
INSERT INTO WTR.SETTING(ID, DESCR, NAME, VALUE) VALUES (3155, 'Шаблон url для получения фактической погоды с сайта погоды openwhetermap.org Должен содержать подстановочные строки {{lon}}, {{lat}}, {{appid}}', 'owm_weather_url', 'api.openweathermap.org/data/2.5/weather?lat={{lat}}&lon={{lon}}&APPID={{appid}}');
INSERT INTO WTR.SETTING(ID, DESCR, NAME, VALUE) VALUES (3156, 'Шаблон url для получения прогноза погоды на 5 дней с разбивкой по 3 часа с сайта погоды openwhetermap.org Должен содержать подстановочные строки {{lon}}, {{lat}}, {{appid}}', 'owm_forecast_url', 'api.openweathermap.org/data/2.5/forecast?lat={{lat}}&lon={{lon}}&APPID={{appid}}');
INSERT INTO WTR.SETTING(ID, DESCR, NAME, VALUE) VALUES (3157, 'Условие запроса данных погоды. Должно возвращать булевский тип', 'owm_qry_condition', 'minute(current_time)=58');
INSERT INTO WTR.SETTING(ID, DESCR, NAME, VALUE) VALUES (3158, 'Частота проверки условия запроса данных погоды, сек (целое значение)', 'owm_qry_condition_interval', '30');
INSERT INTO WTR.SETTING(ID, DESCR, NAME, VALUE) VALUES (4752, 'Частота проверки условия запроса данных погоды, сек (целое значение)', 'acw_qry_condition_interval', '30');
INSERT INTO WTR.SETTING(ID, DESCR, NAME, VALUE) VALUES (4753, 'url для получения фактической погоды с сайта погоды accuweather.com Должен содержать location key, api key и details=true', 'acw_weather_url', 'http://dataservice.accuweather.com/currentconditions/v1/2515027?apikey=D7czy42aUgpOKsPzw4KTEMQqc93zzcS3&details=true');
INSERT INTO WTR.SETTING(ID, DESCR, NAME, VALUE) VALUES (4754, 'url для получения прогноза погоды на 5 дней с сайта погоды accuweather.com Должен содержать location key, api key и details=true', 'acw_forecast_url', 'http://dataservice.accuweather.com/forecasts/v1/daily/5day/2515027?apikey=D7czy42aUgpOKsPzw4KTEMQqc93zzcS3&details=true');
INSERT INTO WTR.SETTING(ID, DESCR, NAME, VALUE) VALUES (4755, 'Условие запроса данных погоды. Должно возвращать булевский тип', 'acw_qry_condition', 'minute(current_time)=57');


INSERT INTO WTR.TYPE(ID, CODE, DESCR, NAME, TYPE_ID) VALUES (1, null, null, 'Оборудование', null);
INSERT INTO WTR.TYPE(ID, CODE, DESCR, NAME, TYPE_ID) VALUES (2, null, null, 'Клапаны', 1);
INSERT INTO WTR.TYPE(ID, CODE, DESCR, NAME, TYPE_ID) VALUES (3, null, null, 'Шаровый клапан', 2);
//INSERT INTO WTR.TYPE(ID, CODE, DESCR, NAME, TYPE_ID) VALUES (4, null, null, 'Соленоидный клапан', 2);
//INSERT INTO WTR.TYPE(ID, CODE, DESCR, NAME, TYPE_ID) VALUES (5, null, null, 'Датчики', 1);
//INSERT INTO WTR.TYPE(ID, CODE, DESCR, NAME, TYPE_ID) VALUES (6, null, null, 'Датчики температуры и влажности', 5);
//INSERT INTO WTR.TYPE(ID, CODE, DESCR, NAME, TYPE_ID) VALUES (7, null, null, 'DHT22', 6);
INSERT INTO WTR.TYPE(ID, CODE, DESCR, NAME, TYPE_ID) VALUES (8, null, null, 'Аппаратные адреса устройств', null);
INSERT INTO WTR.TYPE(ID, CODE, DESCR, NAME, TYPE_ID) VALUES (9, null, null, 'Pi4j GPIO', 8);
//INSERT INTO WTR.TYPE(ID, CODE, DESCR, NAME, TYPE_ID) VALUES (10, null, null, 'I2C', 8);


INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (52, null, 'Переключатель полярности', '00', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (53, '1-й слева от дорожки (зеленый) (шаровый кран)', '1.1 (шаровый кран)', '01', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (54, '2-й слева от дорожки (красный) (шаровый кран)', '1.2 (шаровый кран)', '02', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (55, '3-й слева от дорожки (оранжевый) (шаровый кран)', '1.3 (шаровый кран)', '03', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (56, '4-й слева от дорожки (б.-зеленый) (шаровый кран)', '1.4 (шаровый кран)', '04', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (57, '5-й слева от дорожки (б.-красный) (шаровый кран)', '1.5 (шаровый кран)', '05', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (58, 'Cправа от дорожки (б.-оранжевый) (шаровый кран)', '1.6 (шаровый кран)', '06', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (59, 'Между домом и забором (зеленый) (шаровый кран)', '2.1 (шаровый кран)', '12', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (202, 'На участке со скважинами (красный) (шаровый кран)', '3.1 (шаровый кран)', '26', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (1552, 'Капельный полив клубники, помидоров (оранжевый) (шаровый кран)', '4.1 (шаровый кран)', '27', 9);

INSERT INTO WTR.POLARITY_SWITCH(ID, DESCR, NAME, OFF_SIG, ON_SIG, HWADR_ID) VALUES (102, 'Переключатель полярности для шаровых кранов', 'Переключатель полярности', 1, 0, 52);

INSERT INTO WTR.VALVE(ID, AUTO_CONTROL, DESCR, IS_OPEN, NAME, OFF_SIG, ON_SIG, OPEN_CONDITION_EXPR, OPEN_DURATION_EXPR, HWADR_ID, OPEN_AFTER_ID, POLSWTCH_ID, TYPE_ID) VALUES (152, FALSE, '1-й слева от дорожки (зеленый)', FALSE, '1.1', 1, 0, 'hour(current_time)=20 and minute(current_time)=30 and wtr_rain(-24)<3 and wtr_rain(24)<3 and wtr_temp_min(24)>10', '900', 53, null, 102, 3);
INSERT INTO WTR.VALVE(ID, AUTO_CONTROL, DESCR, IS_OPEN, NAME, OFF_SIG, ON_SIG, OPEN_CONDITION_EXPR, OPEN_DURATION_EXPR, HWADR_ID, OPEN_AFTER_ID, POLSWTCH_ID, TYPE_ID) VALUES (153, FALSE, '2-й слева от дорожки (красный)', FALSE, '1.2', 1, 0, null, '900', 54, 152, 102, 3);
INSERT INTO WTR.VALVE(ID, AUTO_CONTROL, DESCR, IS_OPEN, NAME, OFF_SIG, ON_SIG, OPEN_CONDITION_EXPR, OPEN_DURATION_EXPR, HWADR_ID, OPEN_AFTER_ID, POLSWTCH_ID, TYPE_ID) VALUES (154, FALSE, '3-й слева от дорожки (оранжевый)', FALSE, '1.3', 1, 0, null, '900', 55, 153, 102, 3);
INSERT INTO WTR.VALVE(ID, AUTO_CONTROL, DESCR, IS_OPEN, NAME, OFF_SIG, ON_SIG, OPEN_CONDITION_EXPR, OPEN_DURATION_EXPR, HWADR_ID, OPEN_AFTER_ID, POLSWTCH_ID, TYPE_ID) VALUES (155, FALSE, '4-й слева от дорожки (б.-зеленый)', FALSE, '1.4', 1, 0, null, '900', 56, 154, 102, 3);
INSERT INTO WTR.VALVE(ID, AUTO_CONTROL, DESCR, IS_OPEN, NAME, OFF_SIG, ON_SIG, OPEN_CONDITION_EXPR, OPEN_DURATION_EXPR, HWADR_ID, OPEN_AFTER_ID, POLSWTCH_ID, TYPE_ID) VALUES (156, FALSE, '5-й слева от дорожки (б.-красный)', FALSE, '1.5', 1, 0, null, '900', 57, 155, 102, 3);
INSERT INTO WTR.VALVE(ID, AUTO_CONTROL, DESCR, IS_OPEN, NAME, OFF_SIG, ON_SIG, OPEN_CONDITION_EXPR, OPEN_DURATION_EXPR, HWADR_ID, OPEN_AFTER_ID, POLSWTCH_ID, TYPE_ID) VALUES (157, FALSE, 'Cправа от дорожки (б.-оранжевый)', FALSE, '1.6', 1, 0, null, '900', 58, 156, 102, 3);
INSERT INTO WTR.VALVE(ID, AUTO_CONTROL, DESCR, IS_OPEN, NAME, OFF_SIG, ON_SIG, OPEN_CONDITION_EXPR, OPEN_DURATION_EXPR, HWADR_ID, OPEN_AFTER_ID, POLSWTCH_ID, TYPE_ID) VALUES (158, FALSE, 'Между домом и забором (зеленый)', FALSE, '2.1', 1, 0, 'hour(current_time)=15 and minute(current_time)=00  and wtr_rain(-24)<3 and wtr_rain(24)<3 and wtr_temp_min(24)>10', '1200', 59, null, 102, 3);
INSERT INTO WTR.VALVE(ID, AUTO_CONTROL, DESCR, IS_OPEN, NAME, OFF_SIG, ON_SIG, OPEN_CONDITION_EXPR, OPEN_DURATION_EXPR, HWADR_ID, OPEN_AFTER_ID, POLSWTCH_ID, TYPE_ID) VALUES (252, FALSE, 'На участке со скважинами (красный)', FALSE, '3.1', 1, 0, null, '900', 202, 157, 102, 3);
INSERT INTO WTR.VALVE(ID, AUTO_CONTROL, DESCR, IS_OPEN, NAME, OFF_SIG, ON_SIG, OPEN_CONDITION_EXPR, OPEN_DURATION_EXPR, HWADR_ID, OPEN_AFTER_ID, POLSWTCH_ID, TYPE_ID) VALUES (1602, FALSE, 'Капельный полив клубники, помидоров, огурцов (оранжевый)', FALSE, '4.1', 1, 0, 'hour(current_time) in (10) and minute(current_time)=00  and wtr_rain(-24)<5 and wtr_rain(24)<5 and wtr_temp_min(24)>10', '2000', 1552, null, 102, 3);

