const info = {
  category: 10000,
  name: "html",
  icon: "html",
  type: "html", //组件的唯一类型
  size: "normal"
};
const userInputConfig = {
  x: 1,
  y: 10,
  w: "auto",
  h: "auto",
  zIndex: "1",
  fontSize: "16",
  background: "",
  textAlign: "left",
  positionAlign: "",
  verticalAlign: "inherit",
  fontWeight: false,
  fontStyle: false,
  textDecoration: false,
  textTransform: "inherit",
  labelColor: "",
  color: "#0e0e0e",
  opacity: "100",
  lineHeight: "",
  paddingTop: 0,
  paddingBottom: 0,
  paddingLeft: 0,
  paddingRight: 0,
  fontFamily: "黑体",
  whiteSpace: "nowrap"
};
const config = [
  {
    prop: "hidden",
    type: "switch",
    label: "是否隐藏"
  }
];
const userAttrConfig = {
  name: "",
  content: "html结构",
  hidden: false
};

export { info, userInputConfig, userAttrConfig, config };
