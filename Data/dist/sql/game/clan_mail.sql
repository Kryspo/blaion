/*
Navicat MySQL Data Transfer

Source Server         : L2jPDT
Source Server Version : 50555
Source Host           : localhost:3306
Source Database       : l2jgs

Target Server Type    : MYSQL
Target Server Version : 50555
File Encoding         : 65001

Date: 2018-05-04 10:09:07
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `clan_mail`
-- ----------------------------
DROP TABLE IF EXISTS `clan_mail`;
CREATE TABLE `clan_mail` (
  `clan_id` int(11) NOT NULL DEFAULT '0',
  `title` varchar(30) NOT NULL,
  `content` varchar(255) DEFAULT NULL,
  `date` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`clan_id`,`title`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of clan_mail
-- ----------------------------
