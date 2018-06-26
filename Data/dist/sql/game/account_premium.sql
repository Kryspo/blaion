/*
Navicat MySQL Data Transfer

Source Server         : Server
Source Server Version : 50522
Source Host           : localhost:3306
Source Database       : l2jgs

Target Server Type    : MYSQL
Target Server Version : 50522
File Encoding         : 65001

Date: 2017-11-23 15:54:21
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `account_premium`
-- ----------------------------
DROP TABLE IF EXISTS `account_premium`;
CREATE TABLE `account_premium` (
  `account_name` varchar(45) NOT NULL DEFAULT '',
  `premium_service` int(1) NOT NULL DEFAULT '0',
  `enddate` decimal(20,0) NOT NULL DEFAULT '0',
  PRIMARY KEY (`account_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
