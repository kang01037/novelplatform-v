NEW_FILE_CODE
-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
                                      `user_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
                                      `username` VARCHAR(50) NOT NULL COMMENT '用户名（唯一）',
    `password` VARCHAR(255) NOT NULL COMMENT '加密后的密码',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像 URL',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `gender` TINYINT DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
    `birthday` DATE DEFAULT NULL COMMENT '生日',
    `user_status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常 2-锁定',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `login_count` INT DEFAULT 0 COMMENT '登录次数',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标志 0-未删除 1-已删除',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_email` (`email`),
    KEY `idx_phone` (`phone`),
    KEY `idx_created_time` (`created_time`)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 小说表
CREATE TABLE IF NOT EXISTS `novel` (
                                       `novel_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '小说 ID',
                                       `novel_name` VARCHAR(200) NOT NULL COMMENT '小说名称',
    `author_id` BIGINT NOT NULL COMMENT '作者 ID',
    `category_id` BIGINT NOT NULL COMMENT '1，玄幻奇幻，2，武侠仙侠，3，都市言情，4，科幻灵异，5，历史军事，6，游戏竞技',
    `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图片 URL',
    `content` TEXT COMMENT '详细介绍',
    `novel_status` TINYINT DEFAULT 0 COMMENT '状态 0-连载中 1-已完结 2-暂停',
    `click_count` BIGINT DEFAULT 0 COMMENT '点击量',
    `collect_count` BIGINT DEFAULT 0 COMMENT '收藏量',
    `recommend_count` BIGINT DEFAULT 0 COMMENT '推荐量',
    `score` DECIMAL(3,2) DEFAULT 0.00 COMMENT '评分 0-10',
    `score_count` INT DEFAULT 0 COMMENT '评分人数',
    `last_update_time` DATETIME DEFAULT NULL COMMENT '最后更新时间',
    `last_chapter_id` BIGINT DEFAULT NULL COMMENT '最新章节 ID',
    `last_chapter_name` VARCHAR(200) DEFAULT NULL COMMENT '最新章节名称',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-下架 1-上架',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标志 0-未删除 1-已删除',
    PRIMARY KEY (`novel_id`),
    KEY `idx_novel_name` (`novel_name`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_click_count` (`click_count`),
    KEY `idx_collect_count` (`collect_count`),
    KEY `idx_create_time` (`created_time`)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小说表';

-- 章节表
CREATE TABLE IF NOT EXISTS `chapter` (
                                         `chapter_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '章节 ID',
                                         `novel_id` BIGINT NOT NULL COMMENT '小说 ID',
                                         `novel_name` VARCHAR(200) NOT NULL COMMENT '小说名称',
    `chapter_num` INT NOT NULL COMMENT '章节号',
    `chapter_title` VARCHAR(200) NOT NULL COMMENT '章节标题',
    `chapter_content` LONGTEXT COMMENT '章节内容',
    `word_count` INT DEFAULT 0 COMMENT '字数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`chapter_id`),
    UNIQUE KEY `uk_novel_chapter_num` (`novel_id`, `chapter_num`),
    KEY `idx_novel_id` (`novel_id`),
    KEY `idx_create_time` (`create_time`)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='章节表';

-- 书架表（用户收藏）
CREATE TABLE IF NOT EXISTS `bookshelf` (
                                           `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
                                           `user_id` BIGINT NOT NULL COMMENT '用户 ID',
                                           `novel_id` BIGINT NOT NULL COMMENT '小说 ID',
                                           `last_read_chapter_id` BIGINT DEFAULT NULL COMMENT '最后阅读章节 ID',
                                           `last_read_time` DATETIME DEFAULT NULL COMMENT '最后阅读时间',
                                           `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                           PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_novel` (`user_id`, `novel_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_novel_id` (`novel_id`)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书架表';

-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
                                         `comment_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论 ID',
                                         `user_id` BIGINT NOT NULL COMMENT '用户 ID',
                                         `novel_id` BIGINT NOT NULL COMMENT '小说 ID',
                                         `chapter_id` BIGINT DEFAULT NULL COMMENT '章节 ID',
                                         `parent_id` BIGINT DEFAULT 0 COMMENT '父评论 ID 0-一级评论',
                                         `content` TEXT NOT NULL COMMENT '评论内容',
                                         `like_count` INT DEFAULT 0 COMMENT '点赞数',
                                         `reply_count` INT DEFAULT 0 COMMENT '回复数',
                                         `status` TINYINT DEFAULT 1 COMMENT '状态 0-隐藏 1-显示',
                                         `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         `deleted` TINYINT DEFAULT 0 COMMENT '删除标志 0-未删除 1-已删除',
                                         PRIMARY KEY (`comment_id`),
    KEY `idx_novel_id` (`novel_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_create_time` (`created_time`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';







