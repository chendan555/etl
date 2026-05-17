# vue-jsplumb-editor

[在线预览](https://ivorfang.top)

## 功能点

主要分三个区域：

![](/public/md.png)

#### 菜单节点区域

- [x] 从菜单中拖动一个节点到主设计区域，可放置该节点
- [x] 按节点类型组织节点

#### 主设计区域

- [x] 操作可撤销
- [x] 背景的平移和缩放
- [x] 可接收从菜单中拖入的节点
- [x] 拖入的节点会根据当前的节点信息生成对应的端点（用于拖出连线）
- [x] 生成的端点分两种类型：源端点和目标端点，应在样式上作区分
- [x] 源端点放置在节点 bottom 位置，目标节点防止在节点的 top 位置（便于从上往下拖出连接线）
- [x] 所有节点可通过拖拽移动位置
- [x] 所有节点和连接线可删除
- [x] 所有节点和端点在鼠标悬浮需要有 tooltip 框来提示节点或端点信息
- [x] 所有节点、端点和连接线在鼠标悬浮时需要设置不同的样式
- [x] 所有节点和连接线可被选中
- [ ] 操作实时保存数据
- [x] 导入数据生成流程图

#### 节点配置区域

- 在设计区域选中节点时，节点配置区域会显示一个对应的节点配置面板
- 不同节点应拥有对应的节点配置面板
- 节点面板用来展示和配置节点属性

#### 右键菜单功能

- 节点：
  - [x] 复制
  - [x] 删除
  - [x] 查看数据
  - [x] 重命名
- 连接线：
  - [x] 删除
- 背景：
  - [x] 粘贴

## 安装依赖

```
npm install
```

## 开发模式

```
npm run serve
```

## 编译生产代码

```
npm run build
```

## ETL 流程组件配置项说明

```
const config = {
    //组件名称，与组件文件名相同，名称由后端提供，需要匹配后端调用字段，否则组件无法显示
    type: '',   
    created: function(val) {},  //组件初始化时执行方法，val为ruleForm对象，一般在数据回显时有作用
    //控制组件信息填报展示的控制字段
    dialog: {       
        title: '',    //组件弹框标题
        visible: false    //组件弹框显隐控制字段
    },
    //表单填报验证，默认步骤名称验证，其他验证根据组件需求进行添加，验证配置方式参考elementUI表单填报验证
    formItemRuleConfig: {    
        stepName: [     //组件名称验证，默认每个组件必须有
            {
                required: true,
                message: '请输入步骤名称',
                trigger: 'blur'
            }
        ]
    },
    //表单内容配置项，默认必须含有步骤名称项，其他表单填报根据组件需求进行添加
    formItemConfig: [{
      label: '步骤名称',    //填报项名称
      placeholder: '',   //输入框填报提示内容
      type: 'input',    //填报方式，该字段影响在MixinComponents组件中填报方式的选择及呈现方式
      valueKey: 'stepName',    //填报项绑定字段，该字段需要在下方ruleForm对象中手动添加一次，以保障字段存在，并减少代码报错，并能正确传给后端
      span: 24,    //填报项横向展布宽度
      watch: function (val) {}   //作用类似于created，只是触发条件为所在对象填报项的值变化时候，实际触发点在MixinComponents组件中changeValue事件
    }, {
        label: '填报项名称',
        type: 'select',
        options: 'getDatabases',    //配置接口名称，获取备选项目数据，作为下拉框等有选项的填报方式的备选项。如果备选项为固定内容，也可以配置一个备选数据数组，如下所示。
        <!-- options: [{
                name: '是',
                value: 'Y'
            },
            {
                name: '否',
                value: 'N'
            }
        ] -->
        optionsConfig: {        //备选项名称、数据配置项，参考elementUI的props属性
            name: 'name',
            value: 'name'
        },
        optionsParams: {},      //当options为接口名称时，该项作为接口参数传递到后端
    }, {
        label: '填报项名称',
        type: 'inputButtonTree',   //下列展示当填报类型为inputButtonTree时的配置项，该填报类型主要用于数据表的选择
        valueKey: 'schema',    //弹框展示的树形结构接收的数据参数
        nameKey: 'table',    //弹框树形结构选择确认后输入框中展示的内容
        treeConfig: {    //弹框树形选择方式配置项
            title: '数据库浏览',
            inputId: 'tableOut',
            propsApi: {
                api: 'getExplorer',
                params: treeParams
            },
            nodeKey: 'id',
            lazy: true,
            filterable: true,
            loadParentId: 'nodeId',
            valueBy: 'nodeId',
            props: {
                label: 'text',
                children: 'children',
                disabled: false
            }
        }
    }],
    //Tab下表单内容配置项，配置内容同formItemConfig
    formItemConfigDown:[...],
    //表单配置字段，需要包含formItemConfig，formItemConfigDown中的所有valueKey（不然保存表单时该字段无效），可设置初始值
    ruleForm:{},
    //table表格列配置
    tableColumnConfig: [{
        label: "名称",                   //table列名称
        prop: "name",                   //对应列字段名
        showOverflowTooltip: true,           //内容超出是否显示省略号
        align: "left",                      //表格内容对齐方式 left right center
        headerAlign: "left",            //表头对齐方式
        isEdit: true,                   //是否可编辑
        //表格编辑配置项
        editConfig: { 
            type: "select",                 //编辑填报方式，该字段影响在MixinComponents组件中填报方式的选择及呈现方式
            optionsConfig: {                //备选项名称、数据配置项，参考elementUI的props属性
                name: "name",
                value: "name"
            },
            options: "getExcelFields",          //配置接口名称，获取备选项目数据，作为下拉框等有选项的填报方式的备选项。如果备选项为固定内容，也可以配置一个备选数据数组，如下所示。
            optionsParams: {                    //当options为接口名称时，该项作为接口参数传递到后端
                path: "",
                index: "",
                spreadsheet_type: "Excel 2007 XLSX (Apache POI)"
            }
            }
        },
    ],

}
```
