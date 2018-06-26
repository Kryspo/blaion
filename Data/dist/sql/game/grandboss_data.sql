/*
Navicat MySQL Data Transfer

Source Server         : Server
Source Server Version : 50522
Source Host           : localhost:3306
Source Database       : l2jgs

Target Server Type    : MYSQL
Target Server Version : 50522
File Encoding         : 65001

Date: 2018-02-21 00:22:38
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `grandboss_data`
-- ----------------------------
DROP TABLE IF EXISTS `grandboss_data`;
CREATE TABLE `grandboss_data` (
  `boss_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `loc_x` mediumint(6) NOT NULL DEFAULT '0',
  `loc_y` mediumint(6) NOT NULL DEFAULT '0',
  `loc_z` mediumint(6) NOT NULL DEFAULT '0',
  `heading` mediumint(6) NOT NULL DEFAULT '0',
  `respawn_time` bigint(13) unsigned NOT NULL DEFAULT '0',
  `currentHP` decimal(30,15) NOT NULL,
  `currentMP` decimal(30,15) NOT NULL,
  `status` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`boss_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of grandboss_data
-- ----------------------------
INSERT INTO `grandboss_data` VALUES ('29001', '-21610', '181594', '-5720', '0', '0', '229898.000000000000000', '667.000000000000000', '0');
INSERT INTO `grandboss_data` VALUES ('29006', '17726', '108915', '-6472', '0', '0', '622493.000000000000000', '3793.000000000000000', '0');
INSERT INTO `grandboss_data` VALUES ('29014', '55024', '17368', '-5512', '10126', '0', '622493.000000000000000', '3793.000000000000000', '0');
INSERT INTO `grandboss_data` VALUES ('29020', '115621', '17036', '10085', '40890', '0', '4068372.000000000000000', '39960.000000000000000', '0');
INSERT INTO `grandboss_data` VALUES ('29028', '-105200', '-253104', '-15536', '0', '0', '223107426.000000000000000', '4497143.000000000000000', '0');
INSERT INTO `grandboss_data` VALUES ('29062', '-16397', '-53308', '-10448', '16384', '0', '275385.000000000000000', '4553.000000000000000', '0');
INSERT INTO `grandboss_data` VALUES ('29065', '27734', '-6938', '-1962', '26681', '0', '1639965.000000000000000', '4553.000000000000000', '0');
INSERT INTO `grandboss_data` VALUES ('29066', '185708', '114298', '-8221', '32768', '0', '14518000.000000000000000', '3996000.000000000000000', '0');
INSERT INTO `grandboss_data` VALUES ('29067', '185708', '114298', '-8221', '32768', '0', '16184000.000000000000000', '3996000.000000000000000', '0');
INSERT INTO `grandboss_data` VALUES ('29068', '185708', '114298', '-8221', '0', '0', '204677315.000000000000000', '3996000.000000000000000', '0');
INSERT INTO `grandboss_data` VALUES ('29118', '16323', '213059', '-9352', '49152', '0', '4113688.000000000000000', '1220000.000000000000000', '0');
