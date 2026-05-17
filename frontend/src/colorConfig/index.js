/**
 * Create By Leo
 * 界面样式配置
 * 包括HJJ、LJ和HJ三个J种配色各白天和黑夜两个场景的界面配色
 * 根据用户不同进行设置
 * 本项目界面配色不属于本配置项
 * 根据客户平台系统配色定制化调整
 *
 * 特别说明：
 * 本次525-22-ZHPG项目主题色根据客户方平台系统制定
 * 取消了主题色配置功能
 * 主体沿用HJ的配色
 */
const colorConfig = [
  // HuoJianJun
  [
    // 黑夜版
    {
      // 页面头部区域背景配色
      headerBg: '#253025',
      // 页面内容区域背景配色
      conterBg: '#253025',
      // 侧边区域标题背景配色
      sideBarTtlBgClr: '#2B4538',
      // 当前树形菜单节点背景配色
      crtTreeNode: '#324a32',
      // 中心区标题背景配色
      centerTtlBgClr: '#334536',
      // 卡片标题区域背景配色
      cardTtlBgClr: '#23342b',
      // 卡片内容区域背景配色
      cardContBgClr: '#2b4538',
      // 输入框背景配色
      inputBgClr: '#1d2822',
      // 当前菜单背景配色
      crtNavBgClr: '#345740',
      // 当前页面背景配色
      crtPageBgClr: '#435b44',
      // 表格偶数行背景配色
      tableOddBgClr: '#2B4538',
      // 菜单栏字体配色
      mainNavClr: '#fefcb9',
      // 时间字体配色
      timeClr: '#b5d0c3',
      // 一般字体配色
      normalWordsClr: '#fff',
      // 页面箭头配色
      pageArrowClr: '#fff',
      // 弹窗标题区背景配色
      dialogTtlBgClr: '#394e39',
      // 弹窗内容区背景配色
      dialogContBgClr: '#1F2B25'
    },
    // 白天版
    {
      headerBg: '#e4f7e4',
      conterBg: '#fff',
      mainNavClr: '#402a2a',
      sideBarTtlBgClr: '#2c392b',
      crtTreeNode: '#324a32',
      centerTtlBgClr: '#2c392b',
      cardTtlBgClr: '#89c5a5',
      cardContBgClr: '#c2ecd7',
      inputBgClr: '#385D50',
      crtNavBgClr: '#345740',
      crtPageBgClr: '#435b44',
      tableOddBgClr: '#2B4538',
      timeClr: '#000',
      normalWordsClr: '#000',
      pageArrowClr: '#385D50',
      dialogTtlBgClr: '#394e39',
      dialogContBgClr: '#1F2B25'
    }
  ],
  // LuJun
  [
    // 黑夜版
    {
      headerBg: '#122b1d',
      conterBg: '#0e2719',
      sideBarTtlBgClr: '#2b4538',
      crtTreeNode: '#43865e',
      centerTtlBgClr: '#2b4538',
      cardTtlBgClr: '#244c36',
      cardContBgClr: '#205f3f',
      inputBgClr: '#1d3126',
      crtNavBgClr: '#245033',
      crtPageBgClr: '#435b44',
      tableOddBgClr: '#244c36',
      dialogTtlBgClr: '#244c36',
      dialogContBgClr: '#2a4035',
      mainPageItemBgClr: '#132f20',
      normalWordsClr: '#fff'
    },
    // 白天版
    {
      headerBg: '#122b1d',
      conterBg: '#fff',
      // mainNavClr: '#fff',
      sideBarTtlBgClr: '#2c392b',
      crtTreeNode: '#324a32',
      centerTtlBgClr: '#2c392b',
      cardTtlBgClr: '#89c5a5',
      cardContBgClr: '#c2ecd7',
      inputBgClr: '#385D50',
      crtNavBgClr: '#345740',
      crtPageBgClr: '#435b44',
      tableOddBgClr: '#2B4538',
      // timeClr: '#000',
      normalWordsClr: '#000',
      pageArrowClr: '#385D50',
      dialogTtlBgClr: '#394e39',
      dialogContBgClr: '#1F2B25'
      // 275396
    }
  ],
  // HaiJun
  [
    // 黑夜版
    {
      headerBg: '#061730',
      conterBg: '#032662',
      // mainNavClr: '#fff',
      sideBarTtlBgClr: '#0b3a94',
      crtTreeNode: '#dce7f3',
      centerTtlBgClr: '#01162D',
      cardTtlBgClr: '#02346E',
      cardContBgClr: '#022146',
      inputBgClr: '#265496',
      crtNavBgClr: '#012358',
      crtPageBgClr: '#1460d2',
      tableOddBgClr: '#001A36',
      dialogTtlBgClr: '#124693',
      dialogContBgClr: '#152a4a',
      mainPageItemBgClr: '#0a3577',
      normalWordsClr: '#fff',
      defaultBtnBgClr: '#0c3c84',
      messageBoxBgClr: '#152a4a',
      currentRow: '#2487cd',
      dropMenuBgClr: '#034aa2',
      tableBorder: '#0e7cd5',
      flowBgClr: '#091f40',
      expandTable: '#01162D'
    },
    // 白天版
    {
      headerBg: '#01162D',
      conterBg: '#01162D',
      // mainNavClr: '#62a6ec',
      sideBarTtlBgClr: '#0b3a94',
      centerTtlBgClr: '#01162D',
      crtTreeNode: '#1460d2',
      cardTtlBgClr: '#2265ca',
      cardContBgClr: '#6291d8',
      inputBgClr: '#082b5f',
      crtNavBgClr: '#012358',
      crtPageBgClr: '#1460d2',
      tableOddBgClr: '#0e7cd5',
      normalWordsClr: '#000',
      pageArrowClr: '#0e7cd5',
      dialogTtlBgClr: '#153461',
      dialogContBgClr: '#fff',
      defaultBtnBgClr: '#1c5dbf',
      mainPageItemBgClr: '#0a3577',
      // defaultBtnBgClr: '#1c5dbf',
      messageBoxBgClr: '#153461',
      currentRow: '#186097',
      dropMenuBgClr: '#034aa2',
      tableBorder: '#fff',
      flowBgClr: '#082b5f',
      expandTable: '#003A64'
    }
  ],
  // 临时调整
  [
    // 黑夜版
    {
      headerBg: '#061730',
      conterBg: '#0288f4',
      // mainNavClr: '#fff',
      sideBarTtlBgClr: '#0b3a94',
      crtTreeNode: '#dce7f3',
      centerTtlBgClr: '#01162D',
      cardTtlBgClr: '#02346E',
      cardContBgClr: '#022146',
      inputBgClr: '#fff',
      crtNavBgClr: '#012358',
      crtPageBgClr: '#1460d2',
      tableOddBgClr: '#001A36',
      dialogTtlBgClr: '#124693',
      dialogContBgClr: '#152a4a',
      mainPageItemBgClr: '#0a3577',
      normalWordsClr: '#fff',
      defaultBtnBgClr: '#0c3c84',
      messageBoxBgClr: '#152a4a',
      currentRow: '#2487cd',
      dropMenuBgClr: '#034aa2',
      tableBorder: '#0e7cd5',
      flowBgClr: '#091f40',
      expandTable: '#01162D'
    },
    // 白天版
    {
      headerBg: '#01162D',
      conterBg: '#01162D',
      // mainNavClr: '#62a6ec',
      sideBarTtlBgClr: '#0b3a94',
      centerTtlBgClr: '#01162D',
      crtTreeNode: '#1460d2',
      cardTtlBgClr: '#2265ca',
      cardContBgClr: '#6291d8',
      inputBgClr: '#082b5f',
      crtNavBgClr: '#012358',
      crtPageBgClr: '#1460d2',
      tableOddBgClr: '#0e7cd5',
      normalWordsClr: '#000',
      pageArrowClr: '#0e7cd5',
      dialogTtlBgClr: '#153461',
      dialogContBgClr: '#fff',
      defaultBtnBgClr: '#1c5dbf',
      mainPageItemBgClr: '#0a3577',
      // defaultBtnBgClr: '#1c5dbf',
      messageBoxBgClr: '#153461',
      currentRow: '#186097',
      dropMenuBgClr: '#034aa2',
      tableBorder: '#fff',
      flowBgClr: '#082b5f',
      expandTable: '#003A64'
    }
  ]
];
export default colorConfig;
