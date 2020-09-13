CREATE TABLE IF NOT EXISTS `items` (

    `id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `title` varchar(30),
    `data` varchar(50),
    `updated_date` datetime,
    `created_date` datetime

)ENGINE=InnoDB DEFAULT CHARSET=UTF8;