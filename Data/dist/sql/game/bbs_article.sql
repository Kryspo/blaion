/*
Navicat MySQL Data Transfer

Source Server         : L2jPDT
Source Server Version : 50555
Source Host           : localhost:3306
Source Database       : l2jgs

Target Server Type    : MYSQL
Target Server Version : 50555
File Encoding         : 65001

Date: 2018-05-09 16:43:38
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `bbs_article`
-- ----------------------------
DROP TABLE IF EXISTS `bbs_article`;
CREATE TABLE `bbs_article` (
  `number` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `author` int(11) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `title` varchar(45) DEFAULT NULL,
  `content` varchar(400) DEFAULT NULL,
  `date` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`number`)
) ENGINE=MyISAM AUTO_INCREMENT=41 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of bbs_article
-- ----------------------------
INSERT INTO `bbs_article` VALUES ('1', '268482618', 'Sell', 'WTS Arcana Mace Acumen +12', 'WTS Arcana Mace Acumen +12', '10-07-2017');
INSERT INTO `bbs_article` VALUES ('2', '268482618', 'Sell', 'WTS Vesper Noble Set', 'WTS Vesper Noble Set', '15-05-2016');
INSERT INTO `bbs_article` VALUES ('4', '268482618', 'Other', 'Admin is best', 'Admin is best', '10-05-2018');
INSERT INTO `bbs_article` VALUES ('5', '268482618', 'Buy', 'WTB Angel Slayer', 'WTB Angel Slayer', '03-05-2015');
INSERT INTO `bbs_article` VALUES ('27', '268482618', 'Sell', 'Dynasty Shield', 'Dynasty Shield', '08-04-2018');
INSERT INTO `bbs_article` VALUES ('28', '268482618', 'Invite', 'To clan lv 10', 'We looking for player to our clan lv 10. Contact our Clan Leader. Clan is DragonFruit, you can PM it, or send email.', '01-01-2012');
INSERT INTO `bbs_article` VALUES ('29', '268482618', 'Other', 'I want to help with quest', 'Hello, Imwant to help with pailaka quest', '03-05-2018');
INSERT INTO `bbs_article` VALUES ('30', '268482618', 'Sell', 'WTS Arcana Mace Acumen +12', 'WTS Arcana Mace Acumen +12', '10-07-2017');
INSERT INTO `bbs_article` VALUES ('31', '268482618', 'Sell', 'WTS Vesper Noble Set', 'WTS Vesper Noble Set', '15-05-2016');
INSERT INTO `bbs_article` VALUES ('32', '268482618', 'Other', 'Admin is best', 'Admin is best', '10-05-2018');
INSERT INTO `bbs_article` VALUES ('33', '268482618', 'Buy', 'WTB Angel Slayer', 'WTB Angel Slayer', '03-05-2015');
INSERT INTO `bbs_article` VALUES ('34', '268482618', 'Sell', 'Dynasty Shield', 'Dynasty Shield', '08-04-2018');
INSERT INTO `bbs_article` VALUES ('35', '268482618', 'Invite', 'To clan lv 10', 'We looking for player to our clan lv 10. Contact our Clan Leader. Clan is DragonFruit, you can PM it, or send email.', '01-01-2012');
INSERT INTO `bbs_article` VALUES ('36', '268482618', 'Other', 'I want to help with quest', 'Hello, Imwant to help with pailaka quest', '03-05-2018');
INSERT INTO `bbs_article` VALUES ('37', '268482618', 'Sell', 'WTS Vesper Noble Set', 'WTS Vesper Noble Set', '15-05-2016');
INSERT INTO `bbs_article` VALUES ('38', '268482618', 'Other', 'Admin is best', 'Admin is best', '10-05-2018');
INSERT INTO `bbs_article` VALUES ('39', '268482618', 'Buy', 'WTB Angel Slayer', 'WTB Angel Slayer', '03-05-2015');
INSERT INTO `bbs_article` VALUES ('40', '268482618', 'Sell', 'Dynasty Shield', 'Dynasty Shield', '08-04-2018');
