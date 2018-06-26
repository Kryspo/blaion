/*
Navicat MySQL Data Transfer

Source Server         : L2jPDT
Source Server Version : 50555
Source Host           : localhost:3306
Source Database       : l2jgs

Target Server Type    : MYSQL
Target Server Version : 50555
File Encoding         : 65001

Date: 2018-04-06 21:22:59
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `communitybuff`
-- ----------------------------
DROP TABLE IF EXISTS `communitybuff`;
CREATE TABLE `communitybuff` (
  `key` int(11) DEFAULT NULL,
  `skillID` int(11) DEFAULT NULL,
  `buff_id` int(11) DEFAULT NULL,
  `price` int(11) DEFAULT NULL,
  `itemid` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of communitybuff
-- ----------------------------
INSERT INTO `communitybuff` VALUES ('1', '1035', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('2', '1036', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('3', '1040', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('4', '1044', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('5', '1045', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('6', '1048', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('7', '1059', '2', '10000', '57');
INSERT INTO `communitybuff` VALUES ('8', '1047', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('9', '1062', '2', '10000', '57');
INSERT INTO `communitybuff` VALUES ('10', '1068', '1', '10000', '57');
INSERT INTO `communitybuff` VALUES ('11', '1077', '1', '10000', '57');
INSERT INTO `communitybuff` VALUES ('12', '1078', '2', '10000', '57');
INSERT INTO `communitybuff` VALUES ('13', '1087', '1', '10000', '57');
INSERT INTO `communitybuff` VALUES ('14', '1085', '2', '10000', '57');
INSERT INTO `communitybuff` VALUES ('15', '1086', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('16', '1182', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('17', '1189', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('18', '1191', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('19', '1204', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('20', '1240', '1', '10000', '57');
INSERT INTO `communitybuff` VALUES ('21', '1242', '1', '10000', '57');
INSERT INTO `communitybuff` VALUES ('22', '1243', '1', '10000', '57');
INSERT INTO `communitybuff` VALUES ('23', '1257', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('24', '1259', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('25', '1268', '1', '10000', '57');
INSERT INTO `communitybuff` VALUES ('26', '1303', '2', '10000', '57');
INSERT INTO `communitybuff` VALUES ('27', '1304', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('28', '1307', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('29', '1311', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('30', '1460', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('31', '1392', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('32', '1393', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('33', '1352', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('34', '1354', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('35', '1353', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('36', '1043', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('37', '1397', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('38', '1007', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('39', '1010', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('40', '1002', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('41', '1006', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('42', '1009', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('43', '1308', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('44', '1309', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('45', '1252', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('46', '1251', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('47', '1284', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('48', '1388', '1', '10000', '57');
INSERT INTO `communitybuff` VALUES ('49', '1389', '2', '10000', '57');
INSERT INTO `communitybuff` VALUES ('50', '1391', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('51', '1390', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('52', '1461', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('53', '1323', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('54', '1355', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('55', '1356', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('56', '1357', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('57', '1363', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('58', '1414', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('59', '1413', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('60', '4699', '1', '10000', '57');
INSERT INTO `communitybuff` VALUES ('61', '4700', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('62', '4702', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('63', '4703', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('64', '826', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('65', '825', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('66', '827', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('67', '828', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('68', '829', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('69', '830', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('70', '273', '2', '10000', '57');
INSERT INTO `communitybuff` VALUES ('71', '276', '2', '10000', '57');
INSERT INTO `communitybuff` VALUES ('72', '272', '1', '10000', '57');
INSERT INTO `communitybuff` VALUES ('73', '271', '1', '10000', '57');
INSERT INTO `communitybuff` VALUES ('74', '275', '1', '10000', '57');
INSERT INTO `communitybuff` VALUES ('75', '274', '1', '10000', '57');
INSERT INTO `communitybuff` VALUES ('76', '310', '1', '10000', '57');
INSERT INTO `communitybuff` VALUES ('77', '277', '0', '10000', '57');
INSERT INTO `communitybuff` VALUES ('78', '307', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('79', '309', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('80', '311', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('81', '365', '2', '10000', '57');
INSERT INTO `communitybuff` VALUES ('82', '530', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('83', '915', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('84', '264', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('85', '267', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('86', '268', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('87', '269', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('88', '304', '1', '10000', '57');
INSERT INTO `communitybuff` VALUES ('89', '266', '1', '10000', '57');
INSERT INTO `communitybuff` VALUES ('90', '265', '1', '10000', '57');
INSERT INTO `communitybuff` VALUES ('91', '270', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('92', '305', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('93', '306', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('94', '308', '3', '10000', '57');
INSERT INTO `communitybuff` VALUES ('95', '349', '2', '10000', '57');
INSERT INTO `communitybuff` VALUES ('96', '363', '2', '10000', '57');
INSERT INTO `communitybuff` VALUES ('97', '364', '2', '10000', '57');
INSERT INTO `communitybuff` VALUES ('98', '529', '2', '10000', '57');
