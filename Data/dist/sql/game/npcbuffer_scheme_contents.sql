/*
Navicat MySQL Data Transfer

Source Server         : L2jPDT
Source Server Version : 50555
Source Host           : localhost:3306
Source Database       : l2jgs

Target Server Type    : MYSQL
Target Server Version : 50555
File Encoding         : 65001

Date: 2018-06-17 10:17:32
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `npcbuffer_scheme_contents`
-- ----------------------------
DROP TABLE IF EXISTS `npcbuffer_scheme_contents`;
CREATE TABLE `npcbuffer_scheme_contents` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `scheme_id` int(11) DEFAULT NULL,
  `skill_id` int(8) DEFAULT NULL,
  `skill_level` int(4) DEFAULT NULL,
  `buff_class` int(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of npcbuffer_scheme_contents
-- ----------------------------
INSERT INTO `npcbuffer_scheme_contents` VALUES ('1', '2', '1036', '2', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('2', '2', '1040', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('3', '2', '1043', '1', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('4', '2', '1044', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('5', '2', '1045', '6', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('6', '2', '1047', '4', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('7', '2', '1048', '6', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('8', '2', '1059', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('9', '2', '1068', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('10', '2', '1077', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('11', '2', '1085', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('12', '2', '1086', '2', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('13', '2', '1087', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('14', '2', '1204', '2', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('15', '2', '1240', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('16', '2', '1242', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('17', '2', '1243', '6', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('18', '2', '1257', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('19', '2', '1268', '4', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('20', '2', '1303', '2', '0');
