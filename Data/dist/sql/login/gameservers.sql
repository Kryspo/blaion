/*
Navicat MySQL Data Transfer

Source Server         : Server
Source Server Version : 50522
Source Host           : localhost:3306
Source Database       : l2jls

Target Server Type    : MYSQL
Target Server Version : 50522
File Encoding         : 65001

Date: 2018-01-22 14:49:32
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `gameservers`
-- ----------------------------
DROP TABLE IF EXISTS `gameservers`;
CREATE TABLE `gameservers` (
  `server_id` int(11) NOT NULL DEFAULT '0',
  `hexid` varchar(50) NOT NULL DEFAULT '',
  `host` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`server_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of gameservers
-- ----------------------------
INSERT INTO `gameservers` VALUES ('1', '6fd722731753b7b3ea951f7507a94257', '');
