CREATE TABLE IF NOT EXISTS `test`.`foo` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gender` enum('F','M') NOT NULL DEFAULT 'F' COMMENT 'gender',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `email` varchar(64) NOT NULL DEFAULT '' COMMENT 'email',
  `age` int(3) DEFAULT NULL COMMENT 'age',
  `my_column` tinyint(4) NOT NULL DEFAULT 1 COMMENT '测试字段',
  `ext` varchar (4000) DEFAULT NULL COMMENT 'json 扩展',
  `c_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT='foo';

CREATE TABLE IF NOT EXISTS `test2`.`foo` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gender` tinyint(4) NOT NULL DEFAULT 1 COMMENT 'gender',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `email` varchar(64) NOT NULL DEFAULT '' COMMENT 'email',
  `age` int(3) DEFAULT NULL COMMENT 'age',
  `my_column` tinyint(4) NOT NULL DEFAULT 1 COMMENT '测试字段',
  `ext` varchar (4000) DEFAULT NULL COMMENT 'json 扩展',
  `c_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT='foo';
