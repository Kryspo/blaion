/*
Navicat MySQL Data Transfer

Source Server         : L2 Blaion
Source Server Version : 50173
Source Host           : 158.69.247.91:3306
Source Database       : l2freya

Target Server Type    : MYSQL
Target Server Version : 50173
File Encoding         : 65001

Date: 2018-06-16 23:35:06
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ctf
-- ----------------------------
DROP TABLE IF EXISTS `ctf`;
CREATE TABLE `ctf` (
  `eventName` varchar(255) NOT NULL DEFAULT '',
  `eventDesc` varchar(255) NOT NULL DEFAULT '',
  `joiningLocation` varchar(255) NOT NULL DEFAULT '',
  `minlvl` int(4) NOT NULL DEFAULT '0',
  `maxlvl` int(4) NOT NULL DEFAULT '0',
  `npcId` int(11) NOT NULL DEFAULT '0',
  `npcX` int(11) NOT NULL DEFAULT '0',
  `npcY` int(11) NOT NULL DEFAULT '0',
  `npcZ` int(11) NOT NULL DEFAULT '0',
  `npcHeading` int(11) NOT NULL DEFAULT '0',
  `rewardId` int(11) NOT NULL DEFAULT '0',
  `rewardAmount` int(11) NOT NULL DEFAULT '0',
  `teamsCount` int(4) NOT NULL DEFAULT '0',
  `joinTime` int(11) NOT NULL DEFAULT '0',
  `eventTime` int(11) NOT NULL DEFAULT '0',
  `minPlayers` int(4) NOT NULL DEFAULT '0',
  `maxPlayers` int(4) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ctf
-- ----------------------------
INSERT INTO `ctf` VALUES ('CTF', 'Capture The Flag', 'Giran', '76', '86', '50009', '83425', '148585', '-3406', '20467', '26639', '20', '2', '5', '10', '2', '30');
