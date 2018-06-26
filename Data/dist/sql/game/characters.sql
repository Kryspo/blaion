/*
Navicat MySQL Data Transfer

Source Server         : L2jPDT
Source Server Version : 50555
Source Host           : localhost:3306
Source Database       : l2jgs

Target Server Type    : MYSQL
Target Server Version : 50555
File Encoding         : 65001

Date: 2018-04-10 21:34:24
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `characters`
-- ----------------------------
DROP TABLE IF EXISTS `characters`;
CREATE TABLE `characters` (
  `account_name` varchar(45) DEFAULT NULL,
  `charId` int(10) unsigned NOT NULL DEFAULT '0',
  `char_name` varchar(35) NOT NULL,
  `level` tinyint(3) unsigned DEFAULT NULL,
  `maxHp` mediumint(8) unsigned DEFAULT NULL,
  `curHp` mediumint(8) unsigned DEFAULT NULL,
  `maxCp` mediumint(8) unsigned DEFAULT NULL,
  `curCp` mediumint(8) unsigned DEFAULT NULL,
  `maxMp` mediumint(8) unsigned DEFAULT NULL,
  `curMp` mediumint(8) unsigned DEFAULT NULL,
  `face` tinyint(3) unsigned DEFAULT NULL,
  `hairStyle` tinyint(3) unsigned DEFAULT NULL,
  `hairColor` tinyint(3) unsigned DEFAULT NULL,
  `sex` tinyint(3) unsigned DEFAULT NULL,
  `heading` mediumint(9) DEFAULT NULL,
  `x` mediumint(9) DEFAULT NULL,
  `y` mediumint(9) DEFAULT NULL,
  `z` mediumint(9) DEFAULT NULL,
  `exp` bigint(20) unsigned DEFAULT '0',
  `expBeforeDeath` bigint(20) unsigned DEFAULT '0',
  `sp` int(10) unsigned NOT NULL DEFAULT '0',
  `karma` int(10) unsigned DEFAULT NULL,
  `fame` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `pvpkills` smallint(5) unsigned DEFAULT NULL,
  `pkkills` smallint(5) unsigned DEFAULT NULL,
  `clanid` int(10) unsigned DEFAULT NULL,
  `race` tinyint(3) unsigned DEFAULT NULL,
  `classid` tinyint(3) unsigned DEFAULT NULL,
  `base_class` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `transform_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `deletetime` bigint(13) unsigned NOT NULL DEFAULT '0',
  `cancraft` tinyint(3) unsigned DEFAULT NULL,
  `title` varchar(16) DEFAULT NULL,
  `title_color` mediumint(8) unsigned NOT NULL DEFAULT '16777079',
  `accesslevel` mediumint(9) DEFAULT '0',
  `online` tinyint(3) unsigned DEFAULT NULL,
  `onlinetime` int(11) DEFAULT NULL,
  `char_slot` tinyint(3) unsigned DEFAULT NULL,
  `newbie` mediumint(8) unsigned DEFAULT '1',
  `lastAccess` bigint(13) unsigned NOT NULL DEFAULT '0',
  `clan_privs` mediumint(8) unsigned DEFAULT '0',
  `wantspeace` tinyint(3) unsigned DEFAULT '0',
  `isin7sdungeon` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `punish_level` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `punish_timer` int(10) unsigned NOT NULL DEFAULT '0',
  `power_grade` tinyint(3) unsigned DEFAULT NULL,
  `nobless` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `subpledge` smallint(6) NOT NULL DEFAULT '0',
  `lvl_joined_academy` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `apprentice` int(10) unsigned NOT NULL DEFAULT '0',
  `sponsor` int(10) unsigned NOT NULL DEFAULT '0',
  `varka_ketra_ally` tinyint(4) NOT NULL DEFAULT '0',
  `clan_join_expiry_time` bigint(13) unsigned NOT NULL DEFAULT '0',
  `clan_create_expiry_time` bigint(13) unsigned NOT NULL DEFAULT '0',
  `death_penalty_level` smallint(5) unsigned NOT NULL DEFAULT '0',
  `bookmarkslot` smallint(5) unsigned NOT NULL DEFAULT '0',
  `vitality_points` smallint(5) unsigned NOT NULL DEFAULT '0',
  `createTime` bigint(13) unsigned NOT NULL DEFAULT '0',
  `pccafe_points` int(6) DEFAULT NULL,
  `language` varchar(2) DEFAULT NULL,
  PRIMARY KEY (`charId`),
  KEY `account_name` (`account_name`),
  KEY `char_name` (`char_name`),
  KEY `clanid` (`clanid`),
  KEY `online` (`online`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;