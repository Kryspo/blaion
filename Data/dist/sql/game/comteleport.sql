/*
Navicat MySQL Data Transfer

Source Server         : L2jPDT
Source Server Version : 50555
Source Host           : localhost:3306
Source Database       : l2jgs

Target Server Type    : MYSQL
Target Server Version : 50555
File Encoding         : 65001

Date: 2018-04-06 21:23:04
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `comteleport`
-- ----------------------------
DROP TABLE IF EXISTS `comteleport`;
CREATE TABLE `comteleport` (
  `TpId` int(11) NOT NULL AUTO_INCREMENT,
  `charId` int(11) DEFAULT NULL,
  `Xpos` text,
  `Ypos` text,
  `Zpos` text,
  `name` text,
  PRIMARY KEY (`TpId`)
) ENGINE=MyISAM AUTO_INCREMENT=333 DEFAULT CHARSET=cp1251;

-- ----------------------------
-- Records of comteleport
-- ----------------------------
