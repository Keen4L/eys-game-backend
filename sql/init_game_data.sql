-- ==========================================
-- 6. 游戏数据初始化 (Roles & Skills)
-- ==========================================

USE `eys_game`;

-- 清空旧数据（仅开发阶段）
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `cfg_role`;
TRUNCATE TABLE `cfg_skill`;
SET FOREIGN_KEY_CHECKS = 1;

-- ------------------------------------------
-- 6.1 角色定义 (cfg_role)
-- ------------------------------------------
-- 阵营: 0-鹅(好人), 1-鸭(坏人), 2-中立

INSERT INTO `cfg_role` (`id`, `name`, `camp_type`, `description`) VALUES
(1, '网红鹅', 0, '被刀后会同归于尽（所有好人收到警报）。'),
(2, '正义鹅', 0, '全局只有一刀，杀好人自己死。'),
(3, '先知鹅', 0, '可查身份，每次消耗金币。'),
(4, '警长鹅', 0, '每晚一刀，刀好人自己死。'),
(5, '保镖鹅', 0, '每晚可保护一人。'),
(6, '加拿大鹅', 0, '被刀后即刻报警。'),
(7, '医生鹅', 0, '有解药和毒药。'),
(8, '殡仪鹅', 0, '可验尸查看角色。'),
(9, '大白鹅', 0, '每晚查验是否是鸭子。'),
(10, '菲律宾鹅', 0, '刺鸭对方死，刺好人自己死，限一次。'),
(11, '恋爱脑鹅', 0, '连接两人，同生共死。'),
(12, '魔术鹅', 0, '置换两人生命状态。'),
(13, '决斗鹅', 0, '发起决斗，胜者生败者死。'),
(14, '等式鹅', 0, '查验两人是否同一阵营。'),
(15, '猎鹰', 2, '每夜一刀，最后幸存获胜。'),
(16, '呆呆鸟', 2, '被投出局获胜。'),
(17, '鹦鹉', 2, '复制他人技能。'),
(18, '鹈鹕', 2, '吞噬玩家，肚子里的人最后死。'),
(19, '鸭王', 1, '每晚一刀，死亡带走一人。'),
(20, '美女鸭', 1, '连接一人，自己死对方死。'),
(21, '忍者鸭', 1, '连接一人，自己被投对方死。'),
(22, '禁言鸭', 1, '每晚禁言一人，可囤积。'),
(23, '刺客鸭', 1, '每晚猜身份刺杀。'),
(24, '炸弹鸭', 1, '传递炸弹。'),
(25, '梦魇鸭', 1, '每晚梦话一人（封刀封技能）。'),
(26, '火种鸭', 1, '每晚一刀，剩自己时双倍。');

-- ------------------------------------------
-- 6.2 技能定义 (cfg_skill)
-- ------------------------------------------
-- interaction_type: 0-NONE, 1-PLAYER, 2-PLAYER_ROLE
-- skill_logic: { "handler_key": "xxx", "config": { ... } }

INSERT INTO `cfg_skill` (`role_id`, `name`, `interaction_type`, `skill_logic`, `description`) VALUES
-- 1. 网红鹅 (被动，无主动技能)
-- (1, '网红警报', 0, '{"handler_key": "Passive", "config": {}}', '被动'),

-- 2. 正义鹅 (StandardKillHandler)
(2, '正义处决', 1, '{"handler_key": "StandardKillHandler", "config": {"penalty_on_good": true, "limit": 1}}', '单局限一次，杀错自己死'),

-- 3. 先知鹅 (InvestigationHandler)
(3, '查验身份', 1, '{"handler_key": "InvestigationHandler", "config": {"return_type": "ROLE"}}', '查看目标角色'),

-- 4. 警长鹅 (StandardKillHandler)
(4, '处决', 1, '{"handler_key": "StandardKillHandler", "config": {"penalty_on_good": true}}', '无限制，杀错自己死'),

-- 5. 保镖鹅 (StatusEffectHandler)
(5, '贴身保护', 1, '{"handler_key": "StatusEffectHandler", "config": {"effect_key": "PROTECTED", "duration": 1}}', '保护目标一晚'),

-- 6. 加拿大鹅 (被动)

-- 7. 医生鹅 (需要特殊 Handler 处理多技能或通过前端选毒/奶)
-- 暂时只配复活/解药，或者分两个技能入口？这里假设后端支持多技能，或者先给毒药
-- 这里用 StatusEffectHandler 模拟毒药
(7, '注射毒药', 1, '{"handler_key": "StatusEffectHandler", "config": {"effect_key": "POISONED", "duration": 1}}', '目标延迟死亡'),

-- 9. 大白鹅 (InvestigationHandler)
(9, '查验鸭子', 1, '{"handler_key": "InvestigationHandler", "config": {"return_type": "DUCK_CHECK"}}', '判断是否为鸭阵营'),

-- 10. 菲律宾鹅 (StandardKillHandler)
(10, '复仇刺杀', 1, '{"handler_key": "StandardKillHandler", "config": {"penalty_on_good": true, "limit": 1}}', '同正义鹅逻辑'),

-- 11. 恋爱脑鹅 (StatusEffectHandler)
(11, '绑定恋人', 1, '{"handler_key": "StatusEffectHandler", "config": {"effect_key": "LOVE_LINKED"}}', '连接目标'),

-- 15. 猎鹰 (StandardKillHandler)
(15, '猎杀', 1, '{"handler_key": "StandardKillHandler", "config": {"penalty_on_good": false}}', '无限制击杀'),

-- 18. 鹈鹕 (PelicanHandler)
(18, '吞噬', 1, '{"handler_key": "PelicanHandler", "config": {}}', '吞入腹中'),

-- 19. 鸭王 (StandardKillHandler)
(19, '刺杀', 1, '{"handler_key": "StandardKillHandler", "config": {"penalty_on_good": false}}', '普通击杀'),

-- 20. 美女鸭 (StatusEffectHandler)
(20, '魅惑连接', 1, '{"handler_key": "StatusEffectHandler", "config": {"effect_key": "BEAUTY_LINKED"}}', '连接目标'),

-- 21. 忍者鸭 (StatusEffectHandler)
(21, '隐忍标记', 1, '{"handler_key": "StatusEffectHandler", "config": {"effect_key": "NINJA_LINKED"}}', '连接目标'),

-- 22. 禁言鸭 (StatusEffectHandler)
(22, '禁言', 1, '{"handler_key": "StatusEffectHandler", "config": {"effect_key": "SILENCED", "duration": 1, "can_stack": true}}', '禁言目标'),

-- 23. 刺客鸭 (AssassinHandler)
(23, '狙击', 2, '{"handler_key": "AssassinHandler", "config": {}}', '猜身份击杀'),

-- 25. 梦魇鸭 (StatusEffectHandler)
(25, '梦魇缠绕', 1, '{"handler_key": "StatusEffectHandler", "config": {"effect_key": "NIGHTMARED", "duration": 1}}', '封禁目标技能'),

-- 26. 火种鸭 (StandardKillHandler)
(26, '灭口', 1, '{"handler_key": "StandardKillHandler", "config": {"penalty_on_good": false}}', '普通击杀');

-- 其他特殊角色技能（魔术、鹦鹉、炸弹、决斗）暂时留空或待实现
