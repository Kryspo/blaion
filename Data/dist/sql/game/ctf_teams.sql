/*
Navicat MySQL Data Transfer

Source Server         : L2 Blaion
Source Server Version : 50173
Source Host           : 158.69.247.91:3306
Source Database       : l2freya

Target Server Type    : MYSQL
Target Server Version : 50173
File Encoding         : 65001

Date: 2018-06-16 23:34:47
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ctf_teams
-- ----------------------------
DROP TABLE IF EXISTS `ctf_teams`;
CREATE TABLE `ctf_teams` (
  `teamId` int(4) NOT NULL DEFAULT '0',
  `teamName` varchar(255) NOT NULL DEFAULT '',
  `teamX` int(11) NOT NULL DEFAULT '0',
  `teamY` int(11) NOT NULL DEFAULT '0',
  `teamZ` int(11) NOT NULL DEFAULT '0',
  `teamColor` int(11) NOT NULL DEFAULT '0',
  `flagX` int(11) NOT NULL DEFAULT '0',
  `flagY` int(11) NOT NULL DEFAULT '0',
  `flagZ` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`teamId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ctf_teams
-- ----------------------------
INSERT INTO `ctf_teams` VALUES ('0', 'Purpless', '-87255', '-50894', '-10727', '4334564', '-87752', '-51102', '-10625');
INSERT INTO `ctf_teams` VALUES ('1', 'Greeness', '-78580', '-44641', '-10727', '388888', '-78251', '-44203', '-10625');
