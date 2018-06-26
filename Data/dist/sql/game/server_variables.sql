/*
Navicat MySQL Data Transfer

Source Server         : Server
Source Server Version : 50522
Source Host           : localhost:3306
Source Database       : l2jgs

Target Server Type    : MYSQL
Target Server Version : 50522
File Encoding         : 65001

Date: 2017-12-18 10:55:17
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `server_variables`
-- ----------------------------
DROP TABLE IF EXISTS `server_variables`;
CREATE TABLE `server_variables` (
  `name` varchar(86) NOT NULL DEFAULT '',
  `value` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of server_variables
-- ----------------------------
INSERT INTO `server_variables` VALUES ('SoI_CohemenesCount', '0');
INSERT INTO `server_variables` VALUES ('SoI_EkimusCount', '0');
INSERT INTO `server_variables` VALUES ('SoI_stage', '1');
INSERT INTO `server_variables` VALUES ('SoI_hoedefkillcount', '0');
INSERT INTO `server_variables` VALUES ('SoI_opened', '0');
