CREATE TABLE weather_user (
 id        		BIGINT AUTO_INCREMENT PRIMARY KEY,
 email			VARCHAR(250) NOT NULL,
 slack			VARCHAR(250),
 name     		VARCHAR(50),
 surname     	VARCHAR(50),
 password 		VARCHAR(250) NOT NULL
);

CREATE TABLE alert_definition(
 id        		BIGINT AUTO_INCREMENT PRIMARY KEY,
 weather_user_id BIGINT NOT NULL,
 alert_name		VARCHAR(250),
 duration		INTEGER,
 location		VARCHAR(100) NOT NULL,
 active			BOOLEAN DEFAULT false,
 email_notif	BOOLEAN DEFAULT false,
 slack_notif	BOOLEAN DEFAULT false,
 FOREIGN KEY(weather_user_id) REFERENCES weather_user(id)  
);

CREATE TABLE definition_parameter(
 id        		BIGINT AUTO_INCREMENT PRIMARY KEY,
 alert_definition_id BIGINT NOT NULL,
 parameter_name	VARCHAR(50) NOT NULL,
 parameter_limit INTEGER,
 comparison_type INTEGER,
 FOREIGN KEY(alert_definition_id) REFERENCES alert_definition(id)
);

CREATE TABLE alert(
 id        		BIGINT AUTO_INCREMENT PRIMARY KEY,
 weather_user_id BIGINT NOT NULL,
 name			VARCHAR(250) NOT NULL,
 date			TIMESTAMP,
 location		VARCHAR(100) NOT NULL,
 duration		INTEGER,
 FOREIGN KEY(weather_user_id) REFERENCES weather_user(id)  
);

CREATE TABLE alert_history(
 id        		BIGINT AUTO_INCREMENT PRIMARY KEY,
 alert_id 		BIGINT NOT NULL,
 parameter_name	VARCHAR(50) NOT NULL,
 parameter_value INTEGER,
 parameter_limit INTEGER,
 FOREIGN KEY(alert_id) REFERENCES alert(id)  
)
