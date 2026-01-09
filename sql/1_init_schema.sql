-- ==========================================
-- 鹅鸭杀辅助工具数据库结构初始化 (1_init_schema.sql)
-- ==========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `eys_game` 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE `eys_game`;

-- ==========================================
-- 1. 系统用户与统计模块 (System & Statistics)
-- ==========================================

-- 1.1 系统用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `openid` varchar(64) UNIQUE DEFAULT NULL COMMENT '微信小程序OpenID',
  `username` varchar(50) UNIQUE DEFAULT NULL COMMENT '后台管理员账号',
  `password` varchar(100) DEFAULT NULL COMMENT '加密密码',
  `nickname` varchar(50) NOT NULL COMMENT '用户昵称',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `role_type` tinyint DEFAULT 0 COMMENT '权限: 0-玩家, 1-DM, 2-管理员',
  `is_enabled` tinyint DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户基础表';

-- 1.2 用户总战绩表 (阵营维度)
CREATE TABLE IF NOT EXISTS `sys_user_stats` (
  `user_id` bigint PRIMARY KEY COMMENT '关联 sys_user.id',
  `total_matches` int DEFAULT 0 COMMENT '总对局数',
  `total_wins` int DEFAULT 0 COMMENT '总胜场',
  `goose_wins` int DEFAULT 0 COMMENT '鹅阵营胜场',
  `duck_wins` int DEFAULT 0 COMMENT '鸭阵营胜场',
  `neutral_wins` int DEFAULT 0 COMMENT '中立胜场',
  INDEX `idx_total_wins` (`total_wins`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户总战绩统计表';

-- 1.3 用户角色战绩明细表 (角色维度)
CREATE TABLE IF NOT EXISTS `sys_user_stats_role` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '关联 cfg_role.id',
  `match_count` int DEFAULT 0 COMMENT '该角色总场次',
  `win_count` int DEFAULT 0 COMMENT '该角色胜场次',
  `last_played_at` datetime DEFAULT NULL COMMENT '最后一次游玩该角色的时间',
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户单角色战绩明细表';


-- ==========================================
-- 2. 核心配置模块 (Configuration)
-- ==========================================

-- 2.1 地图基础配置
CREATE TABLE IF NOT EXISTS `cfg_map` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '地图名称',
  `background_url` varchar(255) NOT NULL COMMENT '底图资源URL',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地图基础配置';

-- 2.2 地图出生点位
CREATE TABLE IF NOT EXISTS `cfg_map_spawn_point` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `map_id` bigint NOT NULL COMMENT '所属地图ID',
  `area_name` varchar(50) NOT NULL COMMENT '区域名称 (如: 实验室)',
  `pos_x` int NOT NULL COMMENT 'X坐标',
  `pos_y` int NOT NULL COMMENT 'Y坐标',
  `active_width` int DEFAULT 60 COMMENT '交互生效宽度',
  `active_height` int DEFAULT 60 COMMENT '交互生效高度',
  INDEX `idx_map` (`map_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地图出生点配置';

-- 2.3 角色定义
CREATE TABLE IF NOT EXISTS `cfg_role` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(20) NOT NULL COMMENT '角色名称',
  `camp_type` tinyint NOT NULL COMMENT '阵营: 0-鹅, 1-鸭, 2-中立',
  `description` text COMMENT '角色描述',
  `img_url` varchar(255) COMMENT '角色卡牌/头像URL',
  `is_enabled` tinyint DEFAULT 1 COMMENT '是否启用: 1-启用, 0-禁用'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色基础信息';

-- 2.4 技能规则定义 (核心) - 重构版
CREATE TABLE IF NOT EXISTS `cfg_skill` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `role_id` bigint NOT NULL COMMENT '所属角色',
  `name` varchar(50) NOT NULL COMMENT '技能名称 (同时也是Tag名称)',
  
  -- [A. 什么时候推?]
  `trigger_phases` varchar(100) DEFAULT 'PRE_VOTE' COMMENT '自动分发阶段(逗号分隔), 空则仅DM手动推',
  
  -- [B. 前端怎么显?]
  `interaction_type` tinyint NOT NULL DEFAULT 1 COMMENT '0:直接触发, 1:选人, 2:选人+猜身份',
  `target_count` int DEFAULT 1 COMMENT '选几个人',

  -- [C. 限制条件]
  `max_usage_total` int DEFAULT -1 COMMENT '全局总次数,-1无限',
  `max_usage_round` int DEFAULT 1 COMMENT '每轮次数',
  `target_alive_state` tinyint DEFAULT 1 COMMENT '1-活人, 2-死人, 0-不限',
  `exclude_self` tinyint DEFAULT 1 COMMENT '1-排除自己, 0-可选自己',

  -- [D. 后端干什么?]
  `behavior_type` varchar(20) NOT NULL DEFAULT 'LOG' COMMENT 'LOG:纯记录, TAG:记录+贴标签, QUERY:记录+查验反馈',
  
  -- [E. 标签生命周期] (仅 TAG 类型有效)
  `tag_expiry_rule` varchar(50) DEFAULT 'NEXT_ROUND' COMMENT 'NEXT_ROUND:下轮失效, PELICAN:鹈鹕存活则在, PERMANENT:永久',
  `tag_restriction` varchar(50) DEFAULT 'NONE' COMMENT 'BLOCK_SKILL:禁止接收技能推送, NONE:无限制',
  
  -- [F. 查验字段] (仅 QUERY 类型有效)
  `query_field` varchar(50) DEFAULT NULL COMMENT 'ROLE_ID:角色ID, CAMP_TYPE:阵营, IS_DUCK:是否是鸭',

  `img_url` varchar(255) COMMENT '技能图标URL',
  `description` varchar(255) COMMENT '技能描述',
  INDEX `idx_role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技能逻辑配置表(重构版)';

-- 2.5 预设牌组
CREATE TABLE IF NOT EXISTS `cfg_deck` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '牌组名称',
  `player_count` int NOT NULL COMMENT '适用玩家人数',
  `role_ids` json NOT NULL COMMENT '角色ID数组 [1, 2, 5...]',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预设角色板子';


-- ==========================================
-- 3. 对局运行时模块 (Game Session Runtime)
-- ==========================================

-- 3.1 对局主记录
CREATE TABLE IF NOT EXISTS `ga_game_record` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `room_code` varchar(10) UNIQUE NOT NULL COMMENT '房间邀请码',
  `dm_user_id` bigint NOT NULL COMMENT '带本DM ID',
  `map_id` bigint NOT NULL COMMENT '地图ID',
  `status` varchar(20) DEFAULT 'PREPARING' COMMENT '游戏状态: PREPARING, PLAYING, FINISHED',
  `current_round` int DEFAULT 1 COMMENT '当前轮次',
  `current_stage` varchar(20) DEFAULT 'START' COMMENT '当前阶段',
  `victory_type` tinyint DEFAULT NULL COMMENT '胜利类型: 1-鹅胜, 2-鸭胜, 3-中立个人胜',
  `winner_user_id` bigint DEFAULT NULL COMMENT '中立获胜者ID (仅victory_type=3时有效)',
  `started_at` datetime DEFAULT NULL COMMENT '游戏开始时间',
  `finished_at` datetime DEFAULT NULL COMMENT '游戏结束时间',
  INDEX `idx_dm` (`dm_user_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对局主表';

-- 3.2 玩家基础信息
CREATE TABLE IF NOT EXISTS `ga_game_player` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `game_id` bigint NOT NULL COMMENT '对局ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `seat_no` int NOT NULL COMMENT '座位号',
  `init_role_id` bigint DEFAULT NULL COMMENT '初始角色ID',
  `curr_role_id` bigint DEFAULT NULL COMMENT '当前角色ID (鹦鹉/继承后会变)',
  `is_winner` tinyint DEFAULT 0 COMMENT '结算: 1-胜, 0-负',
  UNIQUE KEY `uk_game_user` (`game_id`, `user_id`),
  UNIQUE KEY `uk_game_seat` (`game_id`, `seat_no`),
  INDEX `idx_game` (`game_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对局玩家绑定表';

-- 3.3 玩家实时状态表
CREATE TABLE IF NOT EXISTS `ga_player_status` (
  `game_player_id` bigint PRIMARY KEY COMMENT '关联 ga_game_player.id',
  `is_alive` tinyint DEFAULT 1 COMMENT '1-存活, 0-死亡',
  `death_round` int DEFAULT NULL COMMENT '死亡轮次',
  `death_stage` varchar(20) DEFAULT NULL COMMENT '死亡阶段',
  `active_effects` json DEFAULT NULL COMMENT '当前生效的Buff/Debuff集合JSON',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='玩家实时状态与效果表';

-- 3.4 技能运行时实例
CREATE TABLE IF NOT EXISTS `ga_skill_instance` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `game_player_id` bigint NOT NULL COMMENT '对局玩家ID',
  `skill_id` bigint NOT NULL COMMENT '关联 cfg_skill.id',
  `remain_count` int DEFAULT 0 COMMENT '剩余次数',
  `is_active` tinyint DEFAULT 1 COMMENT '1-激活, 0-失效',
  `group_id` bigint DEFAULT NULL COMMENT '技能组ID, 同组技能共享次数',
  INDEX `idx_player` (`game_player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技能运行时实例表';


-- ==========================================
-- 4. 动作流水与审计模块 (Action Logs)
-- ==========================================

-- 4.1 全量动作流水表
CREATE TABLE IF NOT EXISTS `ga_action_log` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `game_id` bigint NOT NULL COMMENT '对局ID',
  `round_no` int NOT NULL COMMENT '轮次',
  `stage` varchar(20) NOT NULL COMMENT '阶段',
  `source_type` tinyint NOT NULL COMMENT '来源: 0-玩家发起, 1-DM录入/干预',
  `action_type` varchar(20) NOT NULL COMMENT '动作类型: SKILL, KILL, REVIVE, VOTE, SYSTEM',
  `actor_id` bigint NOT NULL COMMENT '执行者ID (玩家ID, DM操作则为0)',
  `skill_id` bigint DEFAULT NULL COMMENT '关联技能ID',
  `action_data` json DEFAULT NULL COMMENT '动作载荷JSON',
  `result_note` varchar(255) COMMENT '结果简述',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_game_round` (`game_id`, `round_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='全量动作流水审计表';

-- 4.2 投票记录表
CREATE TABLE IF NOT EXISTS `ga_vote_log` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `game_id` bigint NOT NULL COMMENT '对局ID',
  `round_no` int NOT NULL COMMENT '轮次',
  `voter_id` bigint NOT NULL COMMENT '投票者ID',
  `target_id` bigint DEFAULT NULL COMMENT '被投者ID, NULL为弃票',
  `is_skipped` tinyint DEFAULT 0 COMMENT '是否跳过: 0-正常, 1-被技能跳过',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_game_round` (`game_id`, `round_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投票详情表';

-- 4.3 轮次出生点记录
CREATE TABLE IF NOT EXISTS `ga_player_spawn` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `game_id` bigint NOT NULL COMMENT '对局ID',
  `round_no` int NOT NULL COMMENT '轮次',
  `game_player_id` bigint NOT NULL COMMENT '对局玩家ID',
  `spawn_point_id` bigint NOT NULL COMMENT '关联 cfg_map_spawn_point',
  UNIQUE KEY `uk_round_spawn` (`game_id`, `round_no`, `game_player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='玩家每轮次出生位置记录';
