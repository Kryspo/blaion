/*
Navicat MySQL Data Transfer

Source Server         : L2jPDT
Source Server Version : 50555
Source Host           : localhost:3306
Source Database       : l2jgs

Target Server Type    : MYSQL
Target Server Version : 50555
File Encoding         : 65001

Date: 2018-05-09 16:44:23
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `hide_and_seek`
-- ----------------------------
DROP TABLE IF EXISTS `hide_and_seek`;
CREATE TABLE `hide_and_seek` (
  `npc` int(10) DEFAULT NULL,
  `x` int(10) DEFAULT NULL,
  `y` int(10) DEFAULT NULL,
  `z` int(10) DEFAULT NULL,
  `first_clue` varchar(100) DEFAULT NULL,
  `second_clue` varchar(100) DEFAULT NULL,
  `third_clue` varchar(100) DEFAULT NULL,
  `rewards` varchar(100) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of hide_and_seek
-- ----------------------------
INSERT INTO `hide_and_seek` VALUES ('50016', '169754', '16519', '-3397', 'Im near Aden. Many Skeletons around...', 'Im in a rest zone.', 'Im behind a tree of cemetary!', '57,10000;5575,10');
INSERT INTO `hide_and_seek` VALUES ('50014', '83467', '150593', '-3533', 'I can hear how potions are cooked...', 'Im behind a shop.', 'Im in the Giran Castle Town!', '57,10000;5575,10');
INSERT INTO `hide_and_seek` VALUES ('50015', '178289', '-85594', '-7217', 'Im in a undergound site...', 'I can see 4 entrances to anywhere...', 'You can go to Frintezza from here!', '57,10000;5575,10');
