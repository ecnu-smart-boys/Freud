CREATE TABLE `arrangement` (
                               `date` date NOT NULL COMMENT '日期',
                               `user_id` bigint NOT NULL COMMENT '员工id',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                               `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               PRIMARY KEY (`date`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `comment` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价id',
                           `conversation_id` bigint NOT NULL COMMENT '会话id',
                           `score` int DEFAULT '0' COMMENT '访客评分',
                           `text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '评价内容',
                           `user_id` bigint NOT NULL COMMENT '评价人id',
                           `is_commented` tinyint(1) DEFAULT '0' COMMENT '是否已评价',
                           `tag` varchar(255) NOT NULL DEFAULT '' COMMENT '咨询师评价标识',
                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                           `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `comment_index` (`conversation_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=456 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `consulvisor` (
                               `consultant_id` bigint NOT NULL COMMENT '咨询师id',
                               `supervisor_id` bigint NOT NULL COMMENT '督导id',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                               `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               PRIMARY KEY (`consultant_id`,`supervisor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `conversation` (
                                `conversation_id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话id',
                                `from_id` bigint NOT NULL COMMENT '发起会话人id',
                                `to_id` bigint NOT NULL COMMENT '接收会话人id',
                                `start_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
                                `end_time` datetime DEFAULT NULL COMMENT '结束时间',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                `helper_id` bigint DEFAULT '-1' COMMENT '请求的督导',
                                `is_consultation` tinyint(1) DEFAULT NULL COMMENT '是咨询还是请求',
                                `is_shown` tinyint(1) DEFAULT '0' COMMENT '是否被看过了',
                                PRIMARY KEY (`conversation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=299 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `message` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息id',
                           `conversation_id` bigint DEFAULT NULL COMMENT '所属会话id',
                           `msg_key` varchar(255) DEFAULT NULL COMMENT '消息的标识',
                           `iterator` bigint DEFAULT NULL COMMENT '消息的迭代器',
                           `msg_body` text COMMENT '消息内容',
                           `from_id` bigint NOT NULL COMMENT '发送人id',
                           `to_id` bigint NOT NULL COMMENT '接收人id',
                           `time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                           `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           `revoked` tinyint(1) DEFAULT '0' COMMENT '是否撤回',
                           PRIMARY KEY (`id`),
                           KEY `iterator_index` (`iterator`)
) ENGINE=InnoDB AUTO_INCREMENT=1672804064444276738 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `staff_info` (
                              `staff_id` bigint NOT NULL COMMENT '用户id',
                              `id_number` char(18) NOT NULL COMMENT '身份证',
                              `department` varchar(32) NOT NULL COMMENT '工作单位',
                              `title` varchar(32) NOT NULL COMMENT '职称',
                              `qualification` varchar(255) NOT NULL COMMENT '资质',
                              `qualification_code` varchar(255) NOT NULL COMMENT '资质编号',
                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                              `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              `arrangement` int DEFAULT '0' COMMENT '排班',
                              `max_conversations` int DEFAULT '0' COMMENT '最大在线会话数量',
                              PRIMARY KEY (`staff_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `sys_user` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `open_id` char(28) DEFAULT NULL COMMENT '微信open_id',
                            `username` varchar(32) DEFAULT NULL COMMENT '用户名',
                            `password` varchar(255) DEFAULT NULL COMMENT '密码',
                            `name` varchar(32) NOT NULL COMMENT '姓名',
                            `avatar` varchar(1023) DEFAULT '' COMMENT '头像url',
                            `gender` int DEFAULT '0' COMMENT '性别',
                            `age` int DEFAULT '0' COMMENT '年龄',
                            `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
                            `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
                            `is_disabled` tinyint(1) DEFAULT '0' COMMENT '是否禁用',
                            `role` varchar(20) DEFAULT NULL COMMENT '角色',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `open_id` (`open_id`),
                            UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `visitor_info` (
                                `visitor_id` bigint NOT NULL COMMENT '用户id',
                                `emergency_contact` varchar(32) NOT NULL COMMENT '紧急联系人',
                                `emergency_phone` varchar(20) NOT NULL COMMENT '紧急联系人电话',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                PRIMARY KEY (`visitor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

