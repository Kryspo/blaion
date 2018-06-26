/*
Navicat MySQL Data Transfer

Source Server         : L2jPDT
Source Server Version : 50555
Source Host           : localhost:3306
Source Database       : l2jgs

Target Server Type    : MYSQL
Target Server Version : 50555
File Encoding         : 65001

Date: 2018-04-27 20:59:50
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `character_visual_armors`
-- ----------------------------
DROP TABLE IF EXISTS `character_visual_armors`;
CREATE TABLE `character_visual_armors` (
  `charId` int(10) unsigned NOT NULL DEFAULT '0',
  `isUsingVisual` tinyint(3) NOT NULL DEFAULT '0',
  `armor` int(10) NOT NULL DEFAULT '0',
  `leggings` int(10) NOT NULL DEFAULT '0',
  `feet` int(10) NOT NULL DEFAULT '0',
  `gloves` int(10) NOT NULL DEFAULT '0',
  `lHand` int(10) NOT NULL DEFAULT '0',
  `sword` int(10) NOT NULL DEFAULT '0',
  `bow` int(10) NOT NULL DEFAULT '0',
  `pole` int(10) NOT NULL DEFAULT '0',
  `dualWeapons` int(10) NOT NULL DEFAULT '0',
  `bigSword` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`charId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of character_visual_armors
-- ----------------------------
