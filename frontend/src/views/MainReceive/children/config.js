let colorList = ['#c23531', '#61a0a8', '#d48265', '#91c7ae', '#749f83', '#ca8622', '#bda29a', '#6e7074', '#546570', '#c4ccd3'];

export function setTopLeftOption(taskLit) {
  let taskNameList = [];
  let dataTableNum = [];
  let fileNum = [];
  let emulateNum = [];
  taskLit.forEach(item => {
    taskNameList.push(item.taskName);
    dataTableNum.push(item.dataTable);
    fileNum.push(item.file);
    emulateNum.push(item.emulate);
  });
  return {
    color: colorList,
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        // 坐标轴指示器，坐标轴触发有效
        type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
      }
    },
    legend: {
      data: ['数据表', '文件', '仿真'],
      textStyle: {
        color: '#fff'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      axisLabel: {
        textStyle: {
          color: '#fff'
        }
      },
      axisLine: {
        lineStyle: {
          color: '#ccc'
        }
      },
      splitLine: {
        show: false
      }
    },
    yAxis: {
      type: 'category',
      data: taskNameList,
      axisLabel: {
        textStyle: {
          color: '#fff'
        }
      },
      axisTick: {
        lineStyle: {
          color: '#ccc'
        }
      },
      axisLine: {
        lineStyle: {
          color: '#ccc'
        }
      }
    },
    series: [
      {
        name: '数据表',
        type: 'bar',
        stack: '总量',
        barWidth: 40,
        label: {
          normal: {
            show: true,
            position: 'insideRight'
          }
        },
        data: dataTableNum
      },
      {
        name: '文件',
        type: 'bar',
        stack: '总量',
        barWidth: 40,
        label: {
          normal: {
            show: true,
            position: 'insideRight'
          }
        },
        data: fileNum
      },
      {
        name: '仿真',
        type: 'bar',
        stack: '总量',
        barWidth: 40,
        label: {
          normal: {
            show: true,
            position: 'insideRight'
          }
        },
        data: emulateNum
      }
    ]
  };
}

export function setTopRightOption(taskLit) {
  let taskNameList = [];
  let dataTableNum = [];
  let fileNum = [];
  let emulateNum = [];
  let dataTableTotal = 0;
  let fileTotal = 0;
  let emulateTotal = 0;
  taskLit.forEach(item => {
    taskNameList.push(item.taskName);
    dataTableNum.push({
      value: item.dataTable,
      name: item.taskName
    });
    dataTableTotal += item.dataTable;
    fileNum.push({
      value: item.file,
      name: item.taskName
    });
    fileTotal += item.file;
    emulateNum.push({
      value: item.emulate,
      name: item.taskName
    });
    emulateTotal += item.emulate;
  });
  return {
    title: [
      {
        text: '数据表',
        subtext: '总计 ' + dataTableTotal,
        textStyle: {
          color: '#fff'
        },
        x: '20%',
        y: '65%',
        textAlign: 'center'
      },
      {
        text: '文件',
        subtext: '总计 ' + fileTotal,
        textStyle: {
          color: '#fff'
        },
        x: '50%',
        y: '35%',
        textAlign: 'center'
      },
      {
        text: '仿真',
        subtext: '总计 ' + emulateTotal,
        textStyle: {
          color: '#fff'
        },
        x: '80%',
        y: '65%',
        textAlign: 'center'
      }
    ],
    color: colorList,
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b} : {c} ({d}%)'
    },
    legend: {
      x: 'center',
      y: 'top',
      data: taskNameList,
      textStyle: {
        color: '#fff'
      }
    },
    calculable: true,
    series: [
      {
        name: '数据表',
        type: 'pie',
        radius: ['5%', '30%'],
        center: ['20%', '35%'],
        roseType: 'radius',
        label: {
          normal: {
            formatter: '{b}({c})',
            textStyle: {
              color: '#fff'
            }
          }
        },
        data: dataTableNum
      },
      {
        name: '文件',
        type: 'pie',
        radius: ['5%', '30%'],
        center: ['50%', '75%'],
        roseType: 'area',
        label: {
          normal: {
            formatter: '{b}({c})',
            textStyle: {
              color: '#fff'
            }
          }
        },
        data: fileNum
      },
      {
        name: '仿真',
        type: 'pie',
        radius: ['5%', '30%'],
        center: ['80%', '35%'],
        roseType: 'area',
        label: {
          normal: {
            formatter: '{b}({c})',
            textStyle: {
              color: '#fff'
            }
          }
        },
        data: emulateNum
      }
    ]
  };
}

export function setBtmLeftOption(taskList) {
  let taskNameList = [];
  let finished = [];
  let undo = [];
  taskList.forEach(item => {
    taskNameList.push(item.taskName);
    finished.push(item.finished);
    undo.push(item.undo);
  });
  return {
    color: colorList,
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        // 坐标轴指示器，坐标轴触发有效
        type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
      }
    },
    legend: {
      data: ['已执行', '未执行'],
      textStyle: {
        color: '#fff'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        textStyle: {
          color: '#fff'
        }
      },
      axisTick: {
        lineStyle: {
          color: '#ccc'
        }
      },
      axisLine: {
        lineStyle: {
          color: '#ccc'
        }
      },
      splitLine: {
        show: false
      }
    },
    xAxis: {
      type: 'category',
      data: taskNameList,
      axisLabel: {
        textStyle: {
          color: '#fff'
        }
      },
      axisLine: {
        lineStyle: {
          color: '#ccc'
        }
      },
      splitLine: {
        show: false
      }
    },
    series: [
      {
        name: '已执行',
        type: 'bar',
        stack: '总量',
        barWidth: 40,
        label: {
          normal: {
            show: true,
            position: 'insideTop'
          }
        },
        data: finished
      },
      {
        name: '未执行',
        type: 'bar',
        stack: '总量',
        barWidth: 40,
        itemStyle: {
          normal: {
            opacity: 0.5
          }
        },
        label: {
          normal: {
            show: true,
            position: 'insideTop'
          }
        },
        data: undo
      }
    ]
  };
}

export function setBtmRightOption(taskList) {
  let taskNameList = [];
  let tableData = [];
  let fileData = [];
  let emulateData = [];
  taskList.forEach(item => {
    taskNameList.push(item.taskName);
    tableData.push(item.dataTable);
    fileData.push(item.file);
    emulateData.push(item.emulate);
  });
  return {
    color: colorList,
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      }
    },
    legend: {
      data: ['数据表数据', '文件数据', '仿真数据'],
      textStyle: {
        color: '#fff'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        boundaryGap: true,
        data: taskNameList,
        axisLabel: {
          textStyle: {
            color: '#fff'
          }
        },
        axisTick: {
          lineStyle: {
            color: '#ccc'
          }
        },
        axisLine: {
          lineStyle: {
            color: '#ccc'
          }
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        axisLabel: {
          textStyle: {
            color: '#fff'
          }
        },
        axisTick: {
          lineStyle: {
            color: '#ccc'
          }
        },
        axisLine: {
          lineStyle: {
            color: '#ccc'
          }
        },
        splitLine: {
          show: false
        }
      }
    ],
    series: [
      {
        name: '数据表数据',
        type: 'line',
        // stack:'总量',
        label: {
          normal: {
            show: true,
            position: 'top',
            textStyle: {
              color: '#fff'
            }
          }
        },
        data: tableData
      },
      {
        name: '文件数据',
        type: 'line',
        // stack:'总量',
        label: {
          normal: {
            show: true,
            position: 'top',
            textStyle: {
              color: '#fff'
            }
          }
        },
        data: fileData
      },
      {
        name: '仿真数据',
        type: 'line',
        // stack:'总量',
        label: {
          normal: {
            show: true,
            position: 'top',
            textStyle: {
              color: '#fff'
            }
          }
        },
        data: emulateData
      }
    ]
  };
}

export const topOption = {
  // color: ['#46AA6E', '#B25A5A', '#0D86B9','#D69D5F'],
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b} : {c}'
  },
  series: [
    {
      name: '数据源数据统计',
      type: 'sunburst',
      data: [
        {
          name: '源数据库',
          value: 8,
          children: [
            {
              value: 5,
              name: '本地'
            },

            {
              value: 3,
              name: '225'
            }
          ]
        },
        {
          name: '引接库',
          value: 10,
          children: [
            {
              value: 3,
              name: '中文名称'
            },
            {
              value: 7,
              name: '109'
            }
          ]
        },
        {
          name: '处理库',
          value: 10,
          children: [
            {
              value: 6,
              name: '105'
            },
            {
              value: 4,
              name: '测试库'
            }
          ]
        }
        // {
        //     name: "仿真",
        //     value: 10,
        //     children: [{
        //         value: 10,
        //         name: "外部服务器",

        //     }]
        // },
      ]
    }
  ]
};

export const topTwoOption = {};
export const buttomOneOption = {
  // color: ['#46AA6E', '#B25A5A', '#0D86B9','#D69D5F'],
  title: {
    text: '',
    subtext: '',
    x: 'center'
  },
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b} : {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 'left',
    data: ['数据源', '文件', '仿真', '表单'],
    textStyle: {
      color: '#fff'
    }
  },
  series: [
    {
      name: '最近引接数量',
      type: 'pie',
      radius: '55%',
      center: ['50%', '60%'],
      data: [{ value: 335, name: '数据源' }, { value: 310, name: '文件' }, { value: 234, name: '仿真' }, { value: 135, name: '表单' }],
      itemStyle: {
        emphasis: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
};
export const buttomTwoOption = {
  // color: ['#46AA6E', '#B25A5A', '#0D86B9','#D69D5F'],
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      // 坐标轴指示器，坐标轴触发有效
      type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
    }
  },
  legend: {
    data: ['数据源', '文件', '仿真', '表单'],
    textStyle: {
      color: '#fff'
    }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: ['批次一', '批次二', '批次三', '批次四', '批次五', '批次六'],
    axisLabel: {
      textStyle: {
        color: '#fff'
      }
    }
  },
  yAxis: {
    type: 'value',
    axisLabel: {
      textStyle: {
        color: '#fff'
      }
    }
  },
  series: [
    {
      name: '数据源',
      type: 'bar',
      label: {
        normal: {
          show: true,
          position: 'insideRight'
        }
      },
      // stack: '总量',
      data: [320, 302, 301, 334, 390, 330, 320]
    },
    {
      name: '文件',
      type: 'bar',
      label: {
        normal: {
          show: true,
          position: 'insideRight'
        }
      },
      // stack: '总量',
      data: [120, 132, 101, 134, 90, 230, 210]
    },
    {
      name: '仿真',
      type: 'bar',
      label: {
        normal: {
          show: true,
          position: 'insideRight'
        }
      },
      // stack: '总量',
      data: [220, 182, 191, 234, 290, 330, 310]
    },
    {
      name: '表单',
      type: 'bar',
      label: {
        normal: {
          show: true,
          position: 'insideRight'
        }
      },
      // stack: '总量',
      data: [150, 212, 201, 154, 190, 330, 410]
    }
  ]
};
