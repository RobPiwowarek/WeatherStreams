CREATE TABLE weather_user (
 id        		BIGINT AUTO_INCREMENT PRIMARY KEY,
 email			VARCHAR(250) NOT NULL,
 slack			VARCHAR(250),
 name     		VARCHAR(250),
 surname     		VARCHAR(250),
 password 		VARCHAR(250) NOT NULL
);


