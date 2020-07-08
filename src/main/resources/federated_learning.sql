/*
 Navicat Premium Data Transfer

 Source Server         : 10.160.32.24
 Source Server Type    : MySQL
 Source Server Version : 50729
 Source Host           : 10.160.32.24:3306
 Source Schema         : federated_learning

 Target Server Type    : MySQL
 Target Server Version : 50729
 File Encoding         : 65001

 Date: 24/06/2020 17:35:51
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for hibernate_sequence
-- ----------------------------
DROP TABLE IF EXISTS `hibernate_sequence`;
CREATE TABLE `hibernate_sequence`  (
  `next_val` bigint(20) NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hibernate_sequence
-- ----------------------------
INSERT INTO `hibernate_sequence` VALUES (67);
INSERT INTO `hibernate_sequence` VALUES (67);

-- ----------------------------
-- Table structure for tb_algorithm
-- ----------------------------
DROP TABLE IF EXISTS `tb_algorithm`;
CREATE TABLE `tb_algorithm`  (
  `id` int(11) NOT NULL COMMENT '算法ID',
  `algorithm_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '算法名',
  `display_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '算法中文名',
  `federation_type` tinyint(1) NULL DEFAULT NULL COMMENT '联邦学习类型（0：纵向，1：横向）',
  `algorithm_type` tinyint(1) NULL DEFAULT NULL COMMENT '算法类型（0：分类，1：回归）',
  `param` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '算法参数',
  `algorithm_desc` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '算法描述',
  `template` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '算法模板名',
  `algorithm_component` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '算法组件名（联邦学习中涉及）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `algorithm_name`(`algorithm_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '算法信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_algorithm
-- ----------------------------
INSERT INTO `tb_algorithm` VALUES (1, 'HeteroLR', '纵向逻辑回归', 1, 0, '[{\"paramName\":\"learning_rate\",\"displayName\":\"学习率\",\"defaultValue\":0.015,\"validRule\":{\"min\":0.001,\"max\":100}},{\"paramName\":\"loss\",\"displayName\":\"最终损失\",\"defaultValue\":0.00001,\"validRule\":{\"min\":0,\"max\":1}},{\"paramName\":\"iteration\",\"displayName\":\"迭代次数\",\"defaultValue\":100,\"validRule\":{\"min\":10,\"max\":1000}}]', NULL, '/com/cmcc/HeteroLR.ftl');

-- ----------------------------
-- Table structure for tb_dataset
-- ----------------------------
DROP TABLE IF EXISTS `tb_dataset`;
CREATE TABLE `tb_dataset`  (
  `id` smallint(6) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `party_id` int(11) NULL DEFAULT NULL,
  `rows` int(11) NULL DEFAULT NULL,
  `size` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_dataset
-- ----------------------------
INSERT INTO `tb_dataset` VALUES (1, 'aaaaa.csv', 0, 100000, '20MB', '2020-06-18 12:34:56.000000');
INSERT INTO `tb_dataset` VALUES (2, 'bbbbb.csv', 0, 100000, '20MB', '2020-06-18 12:34:56.000000');
INSERT INTO `tb_dataset` VALUES (3, 'ccccc.csv', 0, 100000, '20MB', '2020-06-18 12:34:56.000000');
INSERT INTO `tb_dataset` VALUES (4, 'aaaaa.csv', 1, 100000, '20MB', '2020-06-18 12:34:56.000000');
INSERT INTO `tb_dataset` VALUES (5, 'ddddd.csv', 1, 100000, '20MB', '2020-06-18 12:34:56.000000');
INSERT INTO `tb_dataset` VALUES (6, 'ddddd.csv', 1, 100, '5KB', '2020-06-20 12:34:56.000000');

-- ----------------------------
-- Table structure for tb_federation
-- ----------------------------
DROP TABLE IF EXISTS `tb_federation`;
CREATE TABLE `tb_federation`  (
  `id` smallint(6) NOT NULL,
  `algorithm_id` int(11) NULL DEFAULT NULL,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `data_format` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `guest` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `hosts` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `param` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `status` int(11) NULL DEFAULT NULL,
  `type` bit(1) NULL DEFAULT NULL,
  `uuid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_federation
-- ----------------------------
INSERT INTO `tb_federation` VALUES (63, 1, '2020-06-24 17:13:20.566000', '{\"feature\":\"a;b;c\",\"identity\":\"test\",\"label\":\"test\"}', NULL, '1', '', '测试一下', '{\"learning_rate\":0.015,\"loss\":1.0E-5,\"iteration\":100.0}', 0, b'0', '36b0bc025d4349ed9b15a7b195b79bf9');
INSERT INTO `tb_federation` VALUES (65, 1, '2020-06-24 17:23:44.525000', '{\"feature\":\"3；3；4\",\"identity\":\"1\",\"label\":\"2\"}', '字节跳动行政餐饮前负责人三年贪腐1000余万，负责人回应称属实', '1', '', '字节跳动行政餐饮前负责人三年贪腐1000余万，负责人回应称属实', '{\"learning_rate\":0.015,\"loss\":1.0E-5,\"iteration\":100.0}', 0, b'0', '142c6ebd57764291819d502e31d5627e');
INSERT INTO `tb_federation` VALUES (66, 1, '2020-06-24 17:24:47.101000', '{\"feature\":\"4\",\"identity\":\"2\",\"label\":\"3\"}', NULL, '1', '', '我与慈禧太后不得不说二三事', '{\"learning_rate\":0.015,\"loss\":1.0E-5,\"iteration\":100.0}', 0, b'0', '9e6d8a416aca448ebf362d06a1156043');

-- ----------------------------
-- Table structure for tb_federation_dataset
-- ----------------------------
DROP TABLE IF EXISTS `tb_federation_dataset`;
CREATE TABLE `tb_federation_dataset`  (
  `id` smallint(6) NOT NULL,
  `federation_uuid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `rows` int(11) NULL DEFAULT NULL,
  `size` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `type` smallint(6) NULL DEFAULT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `user_id` int(11) NULL DEFAULT NULL,
  `party_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_federation_dataset
-- ----------------------------
INSERT INTO `tb_federation_dataset` VALUES (1, 'cb68ca3fe5b8420c9bf940af9db047e1', 'aaaaa.csv', 100000, '20MB', 0, '2020-06-18 12:34:56.000000', 1, NULL);
INSERT INTO `tb_federation_dataset` VALUES (4, 'a06a66ab2c2b4d71bf51f96f41c1c583', 'ddddd.csv', 100000, '20MB', 0, '2020-06-18 12:34:56.000000', NULL, 1);
INSERT INTO `tb_federation_dataset` VALUES (64, '36b0bc025d4349ed9b15a7b195b79bf9', 'aaaaa.csv', 100000, '20MB', 0, '2020-06-18 12:34:56.000000', NULL, 1);

-- ----------------------------
-- Table structure for tb_menu
-- ----------------------------
DROP TABLE IF EXISTS `tb_menu`;
CREATE TABLE `tb_menu`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `permissioncode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_menu
-- ----------------------------
INSERT INTO `tb_menu` VALUES (1, 'user:list', '用户管理', '/user/list', NULL);
INSERT INTO `tb_menu` VALUES (2, 'federation:management', '联邦管理', '/federations', NULL);
INSERT INTO `tb_menu` VALUES (3, 'home:page', '首页', NULL, NULL);
INSERT INTO `tb_menu` VALUES (4, 'data:annotation', '数据标注', NULL, NULL);
INSERT INTO `tb_menu` VALUES (5, 'federation:my', '我的联邦', NULL, NULL);
INSERT INTO `tb_menu` VALUES (6, 'federation:list', '联邦列表', NULL, NULL);

-- ----------------------------
-- Table structure for tb_predict
-- ----------------------------
DROP TABLE IF EXISTS `tb_predict`;
CREATE TABLE `tb_predict`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '预测记录ID',
  `uuid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '预测任务UUID',
  `federation_uuid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联邦UUID',
  `train_uuid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '训练记录UUID',
  `algorithm_id` int(11) NULL DEFAULT NULL COMMENT '算法ID',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '预测状态(0:运行中，1:成功，2:失败)',
  `job_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '预测详情URL',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '预测开始时间',
  `duration` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '预测耗时',
  `predict_param` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '预测参数(包括模型、数据、运行时参数)',
  `job_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '预测任务ID（用于导出数据）',
  `output_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '导出文件路径',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_predict
-- ----------------------------
INSERT INTO `tb_predict` VALUES (1, '98a2984d5e9d4c6ab94d077075c2366e', '98a2984d5e9d4c6ab94d0770767a366e', '571d884d5e9d4c6ab94d0770767a35a8', 1, 0, 'https:fateboard/2345154321', '2020-06-23 17:12:33', '1min5s', '{\"param1\":0.035,\"param2\":0.01001,\"param3\":100.0}', NULL, NULL);
INSERT INTO `tb_predict` VALUES (2, '98a2984d5e9d4c6ab94d07707194366e', '98a2984d5e9d4c6ab94d0770767a366e', '571d884d5e9d4c6ab94d0770767a35a8', 1, 1, 'https:fateboard/2345154320', '2020-06-22 21:05:44', '8min37s', '{\"param1\":0.035,\"param2\":0.01001,\"param3\":100.0}', '20190810154805024303', '/root/output');

-- ----------------------------
-- Table structure for tb_role
-- ----------------------------
DROP TABLE IF EXISTS `tb_role`;
CREATE TABLE `tb_role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rolename` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `keyword` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `description` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_role
-- ----------------------------
INSERT INTO `tb_role` VALUES (1, 'admin', NULL, '管理员');
INSERT INTO `tb_role` VALUES (2, 'guest', NULL, '联邦创建者');
INSERT INTO `tb_role` VALUES (3, 'host', NULL, '联邦参与者');

-- ----------------------------
-- Table structure for tb_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `tb_role_menu`;
CREATE TABLE `tb_role_menu`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) NULL DEFAULT NULL,
  `menu_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `menu_id`(`menu_id`) USING BTREE,
  INDEX `tb_role_menu_ibfk_1`(`role_id`) USING BTREE,
  CONSTRAINT `tb_role_menu_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `tb_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tb_role_menu_ibfk_2` FOREIGN KEY (`menu_id`) REFERENCES `tb_menu` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_role_menu
-- ----------------------------
INSERT INTO `tb_role_menu` VALUES (5, 1, 1);
INSERT INTO `tb_role_menu` VALUES (6, 1, 2);
INSERT INTO `tb_role_menu` VALUES (7, 1, 3);
INSERT INTO `tb_role_menu` VALUES (8, 2, 3);
INSERT INTO `tb_role_menu` VALUES (9, 2, 4);
INSERT INTO `tb_role_menu` VALUES (10, 2, 5);
INSERT INTO `tb_role_menu` VALUES (11, 2, 6);

-- ----------------------------
-- Table structure for tb_train
-- ----------------------------
DROP TABLE IF EXISTS `tb_train`;
CREATE TABLE `tb_train`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '训练任务ID',
  `uuid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '训练记录UUID',
  `federation_uuid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联邦UUID',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '训练状态(0:运行中，1:成功，2:失败)',
  `job_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '训练详情URL',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '训练开始时间',
  `duration` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '训练耗时',
  `AUC` float(6, 0) NULL DEFAULT NULL COMMENT 'AUC值',
  `accuracy` float(6, 0) NULL DEFAULT NULL COMMENT '准确率',
  `algorithm_id` int(11) NULL DEFAULT NULL COMMENT '算法模型ID',
  `train_param` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '训练参数（包括数据与运行时参数）',
  `model` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '输出模型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_train
-- ----------------------------
INSERT INTO `tb_train` VALUES (1, '98a2984d5e9d4c6ab94d0770767a35a8', '98a2984d5e9d4c6ab94d0770767a366e', 0, 'https:fateboard/9876567891.com', '2020-06-11 12:18:26', '2min30s', NULL, NULL, NULL, '{\"param1\":0.05,\"param2\":0.0001,\"param3\":100.0}', NULL);
INSERT INTO `tb_train` VALUES (3, '571d884d5e9d4c6ab94d0770767a35a8', '98a2984d5e9d4c6ab94d0770767a366e', 1, 'https:fateboard/9876567890.com', '2020-06-20 20:34:58', '11min7s', 1, 1, 1, '{\"param1\":0.035,\"param2\":0.01001,\"param3\":100.0}', '{\r\n    \"model_id\": \"arbiter-10000#guest-10000#host-10000#model\",\r\n    \"model_version\": \"20190810154805024303_1\"\r\n}');

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `party_id` int(11) NULL DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `role` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `del_flag` int(11) NULL DEFAULT 0,
  `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `company_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `company_phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES (1, '5e95e56cd6c8421ca26a28582d4ec6fd', NULL, 'admin', '123', '15812345678', NULL, 'admin', 0, NULL, NULL, NULL);
INSERT INTO `tb_user` VALUES (2, '622ca575bbbd4c498a2ab53a11967d26', NULL, '小建', '123', NULL, NULL, 'guest', 0, NULL, NULL, NULL);
INSERT INTO `tb_user` VALUES (3, '880636f45ed946c688647db85b6214f7', NULL, '小参', '123', NULL, NULL, 'host', 0, NULL, NULL, NULL);
INSERT INTO `tb_user` VALUES (5, 'd291d89bb5754e38808d29647dd42745', NULL, '小黑', '123', NULL, NULL, '', 0, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for tb_user_federation
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_federation`;
CREATE TABLE `tb_user_federation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NULL DEFAULT NULL COMMENT '用户id',
  `federation_uuid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联邦uuid',
  `status` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '0:参与者,1:创建者',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `federation_id`(`federation_uuid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_user_federation
-- ----------------------------
INSERT INTO `tb_user_federation` VALUES (37, 1, 'cb68ca3fe5b8420c9bf940af9db047e1', '0');
INSERT INTO `tb_user_federation` VALUES (38, 1, 'fd302115339547bc856ade1e3161a0fc', '0');
INSERT INTO `tb_user_federation` VALUES (40, 5, '4eeedd0e9037429195ca3e2cb9ebfdb0', '0');
INSERT INTO `tb_user_federation` VALUES (41, 5, 'bcbcec5f0795498a9b83b5b8e731a975', '0');
INSERT INTO `tb_user_federation` VALUES (42, 5, '8f7d50f2591649cf9980b4c8219f1cf9', '0');
INSERT INTO `tb_user_federation` VALUES (43, 5, 'bb4251fb374546fe935dbddba079d8ed', '0');
INSERT INTO `tb_user_federation` VALUES (44, 1, 'f9c998cb8f2f426aab941b9f0e7481fb', '1');
INSERT INTO `tb_user_federation` VALUES (45, 1, '4dfd3b2767814a55a67b1650e8bdbdf1', '1');
INSERT INTO `tb_user_federation` VALUES (46, 2, '98a2984d5e9d4c6ab94d0770767a366e', '0');
INSERT INTO `tb_user_federation` VALUES (47, 2, '4eeedd0e9037429195ca3e2cb9ebfdb0', '0');
INSERT INTO `tb_user_federation` VALUES (48, 1, '36b0bc025d4349ed9b15a7b195b79bf9', '1');
INSERT INTO `tb_user_federation` VALUES (49, 1, '142c6ebd57764291819d502e31d5627e', '1');
INSERT INTO `tb_user_federation` VALUES (50, 1, '9e6d8a416aca448ebf362d06a1156043', '1');

SET FOREIGN_KEY_CHECKS = 1;
