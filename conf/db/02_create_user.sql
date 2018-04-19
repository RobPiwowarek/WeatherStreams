CREATE USER 'weather'@'localhost' IDENTIFIED BY 'weather';
CREATE USER 'weather'@'%' IDENTIFIED BY 'weather';

GRANT ALL ON weather.* TO 'weather'@'localhost';
GRANT ALL ON weather.* TO 'weather'@'%';


