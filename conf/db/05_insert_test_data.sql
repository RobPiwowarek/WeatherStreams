INSERT INTO weather_user(id, email, name, password) values 
(1, 'weatherstreams@gmail.com', 'WeatherStreams', 'weatherstreams');

INSERT INTO alert_definition(id, weather_user_id, alert_name, location, active, email_notif) values 
(1, 1, 'Zimno i pada w Warszawie', 'Warsaw', true, true),
(2, 1, 'Gorąco w Warszawie', 'Warsaw', false, true);

INSERT INTO definition_parameter(id, alert_definition_id, parameter_name, parameter_limit, comparison_type) values 
(1, 1, 'TEMP', 5, 1),
(2, 1, 'WIND', 10, 2),
(3, 2, 'TEMP', 30, 2);

INSERT INTO alert(id, weather_user_id, name, date, location, duration) values 
(1, 1, 'Gorąco w Warszawie', '2018-05-23 13:55:10', 'Warsaw', 2);

INSERT INTO alert_history(id, alert_id, parameter_name, parameter_value, parameter_limit) values
(1, 1, 'TEMP', 31, 30),
(2, 1, 'WIND', 2, null),
(3, 1, 'HUMI', 35, null),
(4, 1, 'RAIN', 0, null);
