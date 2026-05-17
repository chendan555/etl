
// 水波球echart配置项
export const sbqOptions = {
  xAxis: {
    show: false
  },
  yAxis: {
    show: false
  },
  grid: {
    top: '2.5|%',
    right: '40',
    bottom: '0%',
    left: 0
  },
  series: [
    {
      type: 'liquidFill',
      radius: '80%',
      center: ['50%', '50%'],
      data: [0, 0],
      label: {
        textStyle: {
          color: '#f2e129',
          fontSize: 30
        }
      },
      color: ['#165334', '#29dc81'],
      backgroundStyle: {
        color: 'transparent',
        borderWidth: '2',
        borderColor: '#0f956a',
      },
      outline: {
        show: true,
        borderDistance: 10,
        itemStyle: {
          borderColor: '#38a157',
          borderWidth: 5,
          shadowBlur: 'none'
        }
      }
    }
  ]
};
// 数据处理流程echart配置项
export const dataDealOptions = {
  color: ['#46AA6E', '#B25A5A', '#3F7890'],
  tooltip: {
    trigger: 'axis',
    axisPointer: {            // 坐标轴指示器，坐标轴触发有效
      type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
    }
  },
  legend: {
    right: 0,
    data: ['执行完成', '执行异常'],
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
    splitLine: {
      show: false
    }
  },
  yAxis: {
    type: 'category',
    data: ['今日执行', '昨日执行', '全部执行'],
    axisLabel: {
      textStyle: {
        color: '#fff'
      },
    }
  },
  series: [
    {
      name: '执行完成',
      type: 'bar',
      barWidth: 20,
      stack: '总量',
      label: {
        normal: {
          show: true,
          position: 'insideRight'
        }
      },
      data: ['0', '0', 9]
    },
    {
      name: '执行异常',
      type: 'bar',
      stack: '总量',
      label: {
        normal: {
          show: true,
          position: 'insideRight'
        }
      },
      data: [0, 0, 1],
    }
  ]
}

// 质量审核echart配置项
export const dataReviewOptions = {
  color: ['#46AA6E', '#B25A5A', '#3F7890'],
  legend: {
    orient: 'horizontal',
    y: 'bottom',
    data: ['待审核', '已审核', '已驳回'],
    textStyle: {
      color: '#fff'
    }
  },
  series: [
    {
      name: '访问来源',
      type: 'pie',
      radius: ['30%', '50%'],
      avoidLabelOverlap: false,
      label: {
        normal: {
          show: true,
          position: 'outside',
          formatter: '{b}: {c} / {d}%',
          textStyle: {
            fontSize: '14',
            fontWeight: 'bold'
          }
        }
      },
      data: [
        { value: 335, name: '待审核' },
        { value: 310, name: '已审核' },
        { value: 234, name: '已驳回' }
      ]
    }
  ]
}

// 数据整编echart配置项
export const dataReductionOptions = {
  grid: {
    left: '3%',
    right: '4%',
    bottom: '12%',
    containLabel: true
  },
  legend: {
    data: ['待整编', '已审核', '已驳回'],
    textStyle: {
      color: '#fff'
    },
    right: 0
  },
  xAxis: [
    {
      type: 'category',
      // name: '昨日',
      // nameLocation: 'middle',
      nameTextStyle: {
        color: '#fff'
      },
      data: ['已驳回', '待整编', '已审核'],
      axisTick: {
        alignWithLabel: true
      },
      axisLabel: {
        textStyle: {
          color: '#fff'
        }
      },
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
      splitLine: {
        lineStyle: {
          color: '#666'
        }
      }
    }
  ],
  series: [
    {
      name: '数据',
      type: 'bar',
      barWidth: '60%',
      data: [{
        value: 10,
        name: '已驳回',
        itemStyle: {
          normal: {
            color: '#B25A5A'
          }
        },
        label: {
          normal: {
            show: true,
            position: 'top',
            textStyle: {
              color: '#fff'
            }
          }
        }
      }, {
        value: 20,
        name: '待整编',
        itemStyle: {
          normal: {
            color: '#3F7890'
          }
        },
        label: {
          normal: {
            show: true,
            position: 'top',
            textStyle: {
              color: '#fff'
            }
          }
        }
      }, {
        value: 40,
        name: '已审核',
        itemStyle: {
          normal: {
            color: '#46AA6E'
          }
        },
        label: {
          normal: {
            show: true,
            position: 'top',
            textStyle: {
              color: '#fff'
            }
          }
        }
      }]
    }
  ]
}

// 数据质量评价echart配置项
export const qaOptions = {
  radar: {
    // shape: 'circle',
    indicator: [
      { name: '一致性', max: 100 },
      { name: '唯一性', max: 100 },
      { name: '完整性', max: 100 },
      { name: '及时性', max: 100 },
      { name: '有效性', max: 100 },
      { name: '准确性', max: 100 }
    ],
    name: {
      textStyle: {
        color: '#fff'
      }
    },
    splitArea: {
      areaStyle: {
        color: 'transparent',
        shadowColor: 'rgba(0, 0, 0, 0.3)',
        shadowBlur: 10
      }
    },
    axisLine: {
      lineStyle: {
        color: 'rgba(255, 255, 255, 0.2)'
      }
    },
    splitLine: {
      lineStyle: {
        color: 'rgba(255, 255, 255, 0.1)'
      }
    },
    axisLabel: {
      textStyle: {
        color: '#fff'
      }
    }
  },
  series: [{
    type: 'radar',
    itemStyle: {
      normal: {
        color: '#fff'
      }
    },
    lineStyle: {
      normal: {
        color: '#377953'
      }
    },
    areaStyle: {
      normal: {
        color: 'rgba(41, 175, 77, .6)'
      }
    },
    data: [
      {
        value: [4300, 10000, 28000, 35000, 50000, 19000],
        label: {
          normal: {
            show: true,
            textStyle: {
              color: '#fff'
            }
          }
        }
      }
    ]
  }]
}