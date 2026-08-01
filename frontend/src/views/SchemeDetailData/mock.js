/**
 * 仿真方案详细数据 —— mock 数据
 * 接入真实数据后：删除本文件，页面改用 API.sampleCascade.getSchemeDetailData(schemeId)
 */

const RED = '#E54D42';
const BLUE = '#2F7DE1';

/** 生成一台装备的四类信息 */
function buildInfo(name, type, overrides = {}) {
  return {
    basic: [
      { label: '型号', value: overrides.model || name },
      { label: '类型', value: type },
      { label: '状态', value: overrides.status || '作战中', tag: overrides.tag || 'danger' },
      { label: '位置', value: overrides.pos || '东经120° 北纬35°' },
      { label: '速度', value: overrides.speed || '60 km/h' },
      { label: '航向', value: overrides.course || '东北 45°' },
      { label: '武器配置', value: overrides.weapon || '制式装备' },
      { label: '油弹量', value: overrides.ammo || '85%' }
    ],
    status: [
      { label: '当前状态', value: overrides.status || '作战中', tag: overrides.tag || 'danger' },
      { label: '航速', value: overrides.speed || '60 km/h' },
      { label: '油量', value: overrides.ammo || '85%' },
      { label: '电子系统', value: '正常', tag: 'success' }
    ],
    combat: [
      { label: '命中率', value: '92%' },
      { label: '侦测范围', value: '80 km' },
      { label: '载弹量', value: '42 枚' },
      { label: '持续作战时间', value: '6 h' }
    ],
    records: [
      { time: '2026-08-01 10:30:00', item: '航速', value: '58 km/h' },
      { time: '2026-08-01 10:20:00', item: '油量', value: '86%' },
      { time: '2026-08-01 10:10:00', item: '航向', value: '45°' },
      { time: '2026-08-01 10:00:00', item: '海拔', value: '320 m' },
      { time: '2026-08-01 09:50:00', item: '航速', value: '55 km/h' },
      { time: '2026-08-01 09:40:00', item: '油量', value: '88%' }
    ]
  };
}

function equip(id, name, type, overrides) {
  return { id, name, type, info: buildInfo(name, type, overrides) };
}

const RED_EQUIPS = [
  equip(1, '99式主战坦克', '主战坦克', { model: '99A', weapon: '125mm滑膛炮', ammo: '82%' }),
  equip(2, '歼-20', '隐身战斗机', { model: '歼-20A', speed: '2.2 马赫', status: '巡逻中', tag: 'warning', ammo: '90%' }),
  equip(3, '空警-2000', '预警机', { model: '空警-2000', speed: '850 km/h' }),
  equip(4, '红旗-9', '防空导弹车', { model: 'HQ-9', status: '待命', tag: 'warning' }),
  equip(5, '东风-17', '弹道导弹车', { model: 'DF-17', weapon: 'DF-17高超音速导弹' }),
  equip(6, '武直-10', '武装直升机', { model: '直-10', speed: '270 km/h', status: '巡逻中', tag: 'warning' }),
  equip(7, '山猫侦察车', '侦察车', { model: '山猫', speed: '80 km/h', status: '侦察中', tag: 'warning' }),
  equip(8, '指挥方舱', '指挥车', { model: '指挥方舱', speed: '40 km/h', status: '待命', tag: 'warning' })
];

const BLUE_EQUIPS = [
  equip(11, 'M1A2主战坦克', '主战坦克', { model: 'M1A2 SEPv3', weapon: '120mm滑膛炮', ammo: '78%' }),
  equip(12, 'F-35战机', '隐身战斗机', { model: 'F-35B', speed: '1.6 马赫', status: '巡逻中', tag: 'warning', ammo: '88%' }),
  equip(13, '宙斯盾驱逐舰', '驱逐舰', { model: 'DDG-51', speed: '30 节', status: '巡航中', tag: 'warning' }),
  equip(14, '全球鹰无人机', '无人机', { model: 'RQ-4', speed: '575 km/h', status: '侦察中', tag: 'warning' }),
  equip(15, '爱国者导弹', '防空导弹车', { model: 'MIM-104', status: '待命', tag: 'warning' }),
  equip(16, 'CH-53直升机', '运输直升机', { model: 'CH-53K', speed: '315 km/h' }),
  equip(17, '斯特赖克装甲车', '装甲运兵车', { model: 'LAV III', speed: '97 km/h' }),
  equip(18, '两栖登陆舰', '登陆舰', { model: 'LHD-1', speed: '22 节', status: '待命', tag: 'warning' })
];

/**
 * 生成方案详细数据 mock
 * @param {number|string} schemeId 方案 id
 */
export function getMockSchemeDetailData(schemeId) {
  return {
    schemeId: Number(schemeId) || 1,
    schemeName: 'XX红蓝对抗演练-方案' + (schemeId || 1),
    status: 1,
    createTime: '2026-08-01 10:00:00',
    sides: [
      { side: 'red', name: '红方', color: RED, equipmentList: RED_EQUIPS },
      { side: 'blue', name: '蓝方', color: BLUE, equipmentList: BLUE_EQUIPS }
    ]
  };
}
