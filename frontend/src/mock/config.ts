/**
 *
 * mock样例数据文件
 * 定义各类样例数信息
 *
 */

/**
 * 文件数据
 */
const file_code_key: Array<any> = [
  {
    code: '01010001',
    key: 'deploy_lng',
    comment: '部署经度'
  },
  // {
  //   code: '01010002',
  //   key: 'deploy_lat',
  //   comment: '部署纬度'
  // },
  // {
  //   code: '01010003',
  //   key: 'deploy_height',
  //   comment: '部署高度'
  // },
  // {
  //   code: '01010004',
  //   key: 'target_lng',
  //   comment: '目指经度'
  // },
  // {
  //   code: '01010005',
  //   key: 'target_lat',
  //   comment: '目指纬度'
  // },
  // {
  //   code: '01010006',
  //   key: 'target_height',
  //   comment: '目指高度'
  // },
  // {
  //   code: '01010007',
  //   key: 'real_lng',
  //   comment: '实时经度'
  // },
  // {
  //   code: '01010008',
  //   key: 'real_lat',
  //   comment: '实时纬度'
  // },
  // {
  //   code: '01010009',
  //   key: 'real_height',
  //   comment: '实时高度'
  // },
  // {
  //   code: '01010010',
  //   key: 'Vx',
  //   comment: 'Vx'
  // },
  // {
  //   code: '01010011',
  //   key: 'Vy',
  //   comment: 'Vy'
  // },
  // {
  //   code: '01010012',
  //   key: 'Vz',
  //   comment: 'Vz'
  // },
  // {
  //   code: '01010013',
  //   key: 'angle_a',
  //   comment: '俯仰角'
  // },
  // {
  //   code: '01010014',
  //   key: 'angle_b',
  //   comment: '偏航角'
  // },
  // {
  //   code: '01010015',
  //   key: 'angle_c',
  //   comment: '滚转角'
  // },
  // {
  //   code: '01010016',
  //   key: 'status',
  //   comment: '实时状态'
  // },
  // {
  //   code: '01010017',
  //   key: 'target_length',
  //   comment: '距离目标距离'
  // },
  // {
  //   code: '01010018',
  //   key: 'bait_mun',
  //   comment: '诱饵数量'
  // },
  // {
  //   code: '01010019',
  //   key: 'rcs',
  //   comment: 'RCS值'
  // }
];
/**
 * 创建文件数据
 */
function createFileDemoData() {
  let list = <any>[];
  for (let i = 0; i < 500; i++) {
    let codeRandom = Math.floor(Math.random() * 19);
    let codeObj = file_code_key[codeRandom];
    let data = [];
    let d = new Date();
    data.push(d.getTime());
    data.push(codeObj.code);
    data.push(Math.floor(Math.random() * 100));
    list.push(data);
  }
  return list;
}
// console.log(createFileDemoData());
