/*
Navicat MySQL Data Transfer

Source Server         : Server
Source Server Version : 50522
Source Host           : localhost:3306
Source Database       : l2jsun

Target Server Type    : MYSQL
Target Server Version : 50522
File Encoding         : 65001

Date: 2017-12-11 14:14:58
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `character_variables`
-- ----------------------------
DROP TABLE IF EXISTS `character_variables`;
CREATE TABLE `character_variables` (
  `charId` int(10) unsigned NOT NULL,
  `var` varchar(255) NOT NULL,
  `val` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of character_variables
-- ----------------------------
