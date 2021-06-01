-- --------------------------------------------------------
-- 主机:                           172.16.16.125
-- 服务器版本:                        5.7.27-log - MySQL Community Server (GPL)
-- 服务器操作系统:                      Win64
-- HeidiSQL 版本:                  11.0.0.5919
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- 导出  表 styx_data.sys_map_text 结构
CREATE TABLE IF NOT EXISTS `sys_map_text` (
  `key` varchar(50) CHARACTER SET utf8mb4 NOT NULL,
  `value` mediumtext CHARACTER SET utf8mb4 NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。

-- 导出  表 styx_data.terminal_alarm 结构
CREATE TABLE IF NOT EXISTS `terminal_alarm` (
  `terminal_id` int(11) NOT NULL COMMENT '终端ID',
  `alarm_id` int(11) NOT NULL COMMENT '报警ID',
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '报警类型',
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '开始时间',
  PRIMARY KEY (`terminal_id`,`alarm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。

-- 导出  表 styx_data.terminal_data 结构
CREATE TABLE IF NOT EXISTS `terminal_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `terminal_id` int(11) NOT NULL DEFAULT '0',
  `is_online` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否在线',
  `create_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '数据接收时间',
  `day` int(10) unsigned DEFAULT NULL COMMENT '日期',
  `hour` tinyint(3) unsigned DEFAULT NULL COMMENT '小时',
  PRIMARY KEY (`id`),
  KEY `index_day` (`day`) USING BTREE,
  KEY `index_terminal` (`terminal_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。

-- 导出  表 styx_data.terminal_data_detail 结构
CREATE TABLE IF NOT EXISTS `terminal_data_detail` (
  `data_id` bigint(20) NOT NULL COMMENT '数据ID',
  `var_id` int(11) NOT NULL COMMENT '变量ID',
  `value` float(13,2) DEFAULT NULL COMMENT '变量值',
  PRIMARY KEY (`data_id`,`var_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
