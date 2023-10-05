
Сервер автоматизированного полива на базе Raspberry Pi.
------------------------------------------------------------------

Основные возможности:
- Управление открытием/закрытием шаровых кранов с электроприводом через GPIO Raspberry Pi.
  Программно устанавливаются логические 0 или 1 для выходов (пинов), к которым подключены дополнительные реле (не входят в состав Raspberry Pi), 
  через которые в свою очередь подается питание 12В на электроприводы кранов. Одно из реле используется для смены полярности напряжения (закрытие/открытие).
  Поддерживается до 12-ти кранов.

- Управление кранами в автоматическом или ручном режиме. 
  Для автоматического управления задается условие в формате выражения СУБД H2 при выполнении которого кран открывается, 
  а также длительность его открытия. Условия позволяют гибко контролировать полив с учетом времени суток и погодных условий 
  (температура, скорость ветра, интенсивность осадков).

- Получение фактических и прогнозных данных о погоде с использованием сервиса сайта accuweather.com в заданной координатами точке.
  Данные хранятся в локальной БД и могут использоваться для автоматического управления поливом.
 
- Пользовательский web-интерфейс, адаптированный для смартфонов, позволяющий: 
    - изменять настройки приложения, которые хранятся в локальной БД сервера;
    - контролировать настройки режима работы каждого крана;
    - просматривать историю открытия/закрытия каждого крана;
    - просматривать историю погоды.


Программный стек реализации сервера
------------------------------------------------------------------
1. Backend 
    - Java 11, 
    - Spring boot 2.2, 
    - JOOQ 3.11, 
    - СУБД H2 1.4, 
    - pi4j 1.2
2. Frontend 
    - Vue.js 2.6, 
    - Vuetify 2.5


Используемое железо
------------------------------------------------------------------

1. Raspberry Pi 3 Model B V 1.2

2. 16-ти канальный блок реле типа:
   https://aliexpress.ru/item/1005002806849848.html?sku_id=12000022278377995&spm=a2g2w.productlist.search_results.19.45be4aa6Zl5HoA
   с питснием 5В.
   Дополнительное реле для смены полярности.

3. Шаровые краны с электроприводом типа:
   https://aliexpress.ru/item/32807890976.html?spm=a2g2w.orderdetail.0.0.53c54aa6lAb0cb&sku_id=64410735949
   (вариант DN20, 12В, CR01)

4. Блок питания 5В, 12В.

5. Провода.

