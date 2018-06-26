/*
Navicat MySQL Data Transfer

Source Server         : L2jPDT
Source Server Version : 50555
Source Host           : localhost:3306
Source Database       : l2jgs

Target Server Type    : MYSQL
Target Server Version : 50555
File Encoding         : 65001

Date: 2018-04-06 21:22:55
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `community_skillsave`
-- ----------------------------
DROP TABLE IF EXISTS `community_skillsave`;
CREATE TABLE `community_skillsave` (
  `charId` int(10) DEFAULT NULL,
  `skills` text,
  `pet` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of community_skillsave
-- ----------------------------
INSERT INTO `community_skillsave` VALUES ('268482618', '11000100001000000011000000000000000000000000000000000000000000000000000000000000000000000000000000', null);
