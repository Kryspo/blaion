/*
Navicat MySQL Data Transfer

Source Server         : Server
Source Server Version : 50522
Source Host           : localhost:3306
Source Database       : l2jgs

Target Server Type    : MYSQL
Target Server Version : 50522
File Encoding         : 65001

Date: 2018-01-20 16:17:56
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `user_variables`
-- ----------------------------
DROP TABLE IF EXISTS `user_variables`;
CREATE TABLE `user_variables` (
  `obj_id` int(11) NOT NULL DEFAULT '0',
  `type` varchar(86) NOT NULL DEFAULT '0',
  `name` varchar(86) NOT NULL DEFAULT '0',
  `value` text NOT NULL,
  `expire_time` int(11) NOT NULL DEFAULT '0',
  UNIQUE KEY `prim` (`obj_id`,`type`,`name`),
  KEY `obj_id` (`obj_id`),
  KEY `type` (`type`),
  KEY `name` (`name`),
  KEY `expire_time` (`expire_time`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_variables
-- ----------------------------
