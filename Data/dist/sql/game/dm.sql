/*
Navicat MySQL Data Transfer

Source Server         : L2 Blaion
Source Server Version : 50173
Source Host           : 158.69.247.91:3306
Source Database       : l2freya

Target Server Type    : MYSQL
Target Server Version : 50173
File Encoding         : 65001

Date: 2018-06-16 23:57:43
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for dm
-- ----------------------------
DROP TABLE IF EXISTS `dm`;
CREATE TABLE `dm` (
  `eventName` varchar(255) NOT NULL DEFAULT '',
  `eventDesc` varchar(255) NOT NULL DEFAULT '',
  `joiningLocation` varchar(255) NOT NULL DEFAULT '',
  `minlvl` int(4) NOT NULL DEFAULT '0',
  `maxlvl` int(4) NOT NULL DEFAULT '0',
  `npcId` int(8) NOT NULL DEFAULT '0',
  `npcX` int(11) NOT NULL DEFAULT '0',
  `npcY` int(11) NOT NULL DEFAULT '0',
  `npcZ` int(11) NOT NULL DEFAULT '0',
  `rewardId` int(11) NOT NULL DEFAULT '0',
  `rewardAmount` int(11) NOT NULL DEFAULT '0',
  `color` int(11) NOT NULL DEFAULT '0',
  `playerX` int(11) NOT NULL DEFAULT '0',
  `playerY` int(11) NOT NULL DEFAULT '0',
  `playerZ` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of dm
-- ----------------------------
INSERT INTO `dm` VALUES ('DeathMatch', 'All vs All in arena', 'Giran', '76', '85', '50010', '83425', '148585', '-3406', '26639', '1', '0', '149465', '46702', '-3413');
