
delete from wtr.setting where name like 'owm_%';

update wtr.setting set value = 35
where name = 'acw_qry_condition_interval';

update wtr.setting set value = 'мин()=57'
where name = 'acw_qry_condition';

delete from wtr.valve_state_hist where 1=1;
delete from wtr.valve where 1=1;
delete from wtr.polarity_switch where 1=1;
delete from wtr.hw_address where 1=1;

INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (101, '1, зеленый',     'K01', '01', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (102, '1, коричневый',  'K02', '02', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (103, '1, оранжевый',   'K03', '03', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (104, '1, с/зеленый',   'K04', '04', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (105, '1, с/коричневый','K05', '05', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (106, '1, с/оранжевый', 'K06', '06', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (107, '1, с/синий',     'K07', '10', 9);

INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (109, '2, зеленый',     'K09', '12', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (110, '2, коричневый',  'K10', '26', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (111, '2, оранжевый',   'K11', '27', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (112, '2, с/зеленый',   'K12', '28', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (113, '2, с/коричневый','K13', '13', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (114, '2, с/оранжевый', 'K14', '14', 9);
INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (115, '2, с/синий',     'K15', '29', 9);

INSERT INTO WTR.HW_ADDRESS(ID, DESCR, NAME, VALUE, TYPE_ID) VALUES (116, 'Переключатель полярности', 'K16', '00', 9);

INSERT INTO WTR.POLARITY_SWITCH(ID, DESCR, NAME, OFF_SIG, ON_SIG, HWADR_ID) VALUES
(1, 'Переключатель полярности для шаровых кранов', 'Переключатель полярности', 1, 0, 116);

INSERT INTO WTR.VALVE(ID, AUTO_CONTROL, DESCR, IS_OPEN, NAME, OFF_SIG, ON_SIG, OPEN_CONDITION_EXPR, OPEN_DURATION_EXPR, HWADR_ID, OPEN_AFTER_ID, POLSWTCH_ID, TYPE_ID) VALUES
(1, FALSE, '1-й слева от дорожки, газон (коричневый)', FALSE, '1.1', 1, 0, 'время()=''19:30'' and дождь(-24)<3 and дождь(24)<3 and темп_мин(24)>15', '7', 102, null, 1, 3);
INSERT INTO WTR.VALVE(ID, AUTO_CONTROL, DESCR, IS_OPEN, NAME, OFF_SIG, ON_SIG, OPEN_CONDITION_EXPR, OPEN_DURATION_EXPR, HWADR_ID, OPEN_AFTER_ID, POLSWTCH_ID, TYPE_ID) VALUES
(2, FALSE, '2-й слева от дорожки, газон (оранжевый)', FALSE, '1.2', 1, 0, null, '7', 103, 1, 1, 3);

INSERT INTO WTR.VALVE(ID, AUTO_CONTROL, DESCR, IS_OPEN, NAME, OFF_SIG, ON_SIG, OPEN_CONDITION_EXPR, OPEN_DURATION_EXPR, HWADR_ID, OPEN_AFTER_ID, POLSWTCH_ID, TYPE_ID) VALUES
(3, FALSE, '1-й справа от дорожки, газон (зеленый)', FALSE, '1.3', 1, 0, null, '7', 101, 2, 1, 3);
INSERT INTO WTR.VALVE(ID, AUTO_CONTROL, DESCR, IS_OPEN, NAME, OFF_SIG, ON_SIG, OPEN_CONDITION_EXPR, OPEN_DURATION_EXPR, HWADR_ID, OPEN_AFTER_ID, POLSWTCH_ID, TYPE_ID) VALUES
(4, FALSE, '2-й справа от дорожки, газон, овощи (с/коричневый)', FALSE, '1.4', 1, 0, null, '7', 105, 3, 1, 3);

INSERT INTO WTR.VALVE(ID, AUTO_CONTROL, DESCR, IS_OPEN, NAME, OFF_SIG, ON_SIG, OPEN_CONDITION_EXPR, OPEN_DURATION_EXPR, HWADR_ID, OPEN_AFTER_ID, POLSWTCH_ID, TYPE_ID) VALUES
(5, FALSE, '3-й справа от дорожки, цветы (с/оранжевый)', FALSE, '1.5', 1, 0, null, '7', 106, 4, 1, 3);

INSERT INTO WTR.VALVE(ID, AUTO_CONTROL, DESCR, IS_OPEN, NAME, OFF_SIG, ON_SIG, OPEN_CONDITION_EXPR, OPEN_DURATION_EXPR, HWADR_ID, OPEN_AFTER_ID, POLSWTCH_ID, TYPE_ID) VALUES
(6, FALSE, '3-й слева от дорожки, розы, капельный (с/зеленый)', FALSE, '1.6', 1, 0, 'день_недели()=1 and дождь(-168)<10', '20', 104, null, 1, 3);

INSERT INTO WTR.VALVE(ID, AUTO_CONTROL, DESCR, IS_OPEN, NAME, OFF_SIG, ON_SIG, OPEN_CONDITION_EXPR, OPEN_DURATION_EXPR, HWADR_ID, OPEN_AFTER_ID, POLSWTCH_ID, TYPE_ID) VALUES
(7, FALSE, 'Между домом и забором, малина (зеленый)', FALSE, '2.1', 1, 0, 'время()=''20:30'' and дождь(-24)<3 and дождь(24)<3 and темп_мин(24)>15', '10', 109, null, 1, 3);
INSERT INTO WTR.VALVE(ID, AUTO_CONTROL, DESCR, IS_OPEN, NAME, OFF_SIG, ON_SIG, OPEN_CONDITION_EXPR, OPEN_DURATION_EXPR, HWADR_ID, OPEN_AFTER_ID, POLSWTCH_ID, TYPE_ID) VALUES
(8, FALSE, 'На участке со скважинами, цветы (красный)', FALSE, '2.2', 1, 0, null, '7', 110, 5, 1, 3);

INSERT INTO WTR.VALVE(ID, AUTO_CONTROL, DESCR, IS_OPEN, NAME, OFF_SIG, ON_SIG, OPEN_CONDITION_EXPR, OPEN_DURATION_EXPR, HWADR_ID, OPEN_AFTER_ID, POLSWTCH_ID, TYPE_ID) VALUES
(9, FALSE, 'Грядки с клубникой, капельный полив (оранжевый)', FALSE, '2.3', 1, 0, 'время() in (''08:00'', ''11:00'', ''16:00'', ''18:00'') and дождь(-24)<3 and дождь(24)<3 and темп_мин(24)>15', '10', 111, null, 1, 3);

