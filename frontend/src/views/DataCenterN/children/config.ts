import validate from './validate';

// 表格内容配置
const lmTableConfig = {
  tableColumnConfig: [
    {
      label: '名称',
      prop: 'name'
    },
    {
      label: '别名',
      prop: 'comments'
    },
    {
      label: '数据类型',
      prop: 'type'
    },
    {
      label: '数据长度',
      prop: 'lenght'
    }
  ]
};
// 表格配置
const tableConfig = {
  tableColumnConfig: [
    {
      label: '数据表名称',
      prop: 'tableName'
    },
    {
      label: '字段数量',
      prop: 'keyNums'
    },
    {
      label: '备注',
      prop: 'remarks'
    }
  ]
};

// 表格配置
const fileDataTableConfig = {
  tableColumnConfig: [
    {
      label: '文件名称',
      prop: 'fileName'
    },
    {
      label: '存储路径',
      prop: 'directory'
    }
  ]
};

const emulateInforFormConfig = {
  ruleForm: {
    name: '',
    sourceUrl: ''
  },
  formItemConfig: [
    {
      label: '仿真地址名称',
      type: 'text',
      labelWidth: '100px',
      valueKey: 'name',
      disabled: false
    },
    {
      label: '仿真地址',
      type: 'text',
      labelWidth: '100px',
      valueKey: 'sourceUrl',
      disabled: false
    }
  ]
};

const databaseFormConfig = {
  ruleForm: {
    name: '',
    dbType: '',
    host: '',
    port: '',
    username: '',
    password: '',
    categoriesId: '',
    sid: 'ORCL',
    schemaList: [],
    isInclude: '1',
    ruleCont: ''
  },
  rules: {
    name: [{ required: true, message: '请输入数据库名称', trigger: 'blur' }],
    dbType: [{ required: true, message: '请选择类型分类', trigger: 'blur' }],
    host: [{ required: true, validator: validate.validateServerName, trigger: 'blur' }],
    port: [{ required: true, validator: validate.validatePort, trigger: 'blur' }],
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
    categoriesId: [{ required: true, message: '请选择使用类型', trigger: 'blur' }]
  }
};

const emulateDataFormConfig = {
  ruleForm: {
    name: '',
    sourceUrl: ''
  },
  formItemConfig: [
    {
      label: '仿真数据名称',
      type: 'input',
      labelWidth: '140px',
      valueKey: 'name',
      placeholder: '请输入仿真数据名称（示例：仿真数据）'
    },
    {
      label: '仿真数据地址',
      type: 'input',
      labelWidth: '140px',
      valueKey: 'sourceUrl',
      placeholder: '请输入仿真数据地址（示例：***.***.***.*** | ***为0~255范围）'
    }
  ]
};

const fileDataFormConfig = {
  ruleForm: {
    name: '',
    fileIp: '',
    filePort: '',
    sourceUrl: '',
    fileUsername: '',
    filePassword: '',
    fileType: 'FTP'
  },
  formItemRuleConfig: {
    name: [{ required: true, message: '请输入文件服务器名称', trigger: 'blur' }],
    fileIp: [{ required: true, validator: validate.validateServerName, trigger: 'blur' }],
    filePort: [{ required: true, validator: validate.validatePort, trigger: 'blur' }],
    // sourceUrl: [{ required: true, message: '请输入文件服务器目录', trigger: 'blur' }],
    fileUsername: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    filePassword: [{ required: true, message: '请输入密码', trigger: 'blur' }]
  },
  formItemConfig: [
    {
      label: '文件服务器名称',
      type: 'input',
      labelWidth: '140px',
      valueKey: 'name',
      placeholder: '请输入文件服务器名称（示例：文件测试服务器）'
    },
    {
      label: '文件服务器类型',
      type: 'radio',
      labelWidth: '140px',
      valueKey: 'fileType',
      options: [{ name: 'FTP', value: 'FTP' }, { name: 'SMB', value: 'SMB' }]
    },
    {
      label: '文件服务器IP',
      type: 'input',
      labelWidth: '140px',
      valueKey: 'fileIp',
      placeholder: '请输入文件服务器IP（示例：***.***.***.*** | ***为0~255范围）'
    },
    {
      label: '文件服务器端口',
      type: 'input',
      labelWidth: '140px',
      valueKey: 'filePort',
      placeholder: '请输入文件服务器端口（示例：8000）',
      showCondition: {
        key: 'fileType',
        value: 'FTP'
      }
    },
    {
      label: '用户名',
      type: 'input',
      labelWidth: '140px',
      valueKey: 'fileUsername'
    },
    {
      label: '密码',
      type: 'input',
      labelWidth: '140px',
      valueKey: 'filePassword'
    },
    {
      label: '文件服务器目录',
      type: 'input',
      labelWidth: '140px',
      valueKey: 'sourceUrl',
      placeholder: '请输入文件服务器目录（示例：/***/）'
    }
  ]
};

enum fileType {
  'database' = '1',
  'file' = '2',
  'emulate' = '3'
}

export default {
  defaultNavTreeData: [{ id: '1', name: '源数据库', children: [] }, { id: '2', name: '引接库', children: [] }, { id: '3', name: '处理库', children: [] }],
  tableConfig,
  fileDataTableConfig,
  lmTableConfig,
  emulateInforFormConfig,
  databaseFormConfig,
  emulateDataFormConfig,
  fileDataFormConfig,
  fileType
};
