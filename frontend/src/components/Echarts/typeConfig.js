export default function filterChartsData(datas) {
  if (datas.series.type === 'rose1' || datas.series.type === 'rose2') {
    let pieOption = {
      title: {
        text: '',
        x: 'left',
        y: (datas.series.type === 'rose1' && '-3%') || '-1%',
        textStyle: {
          color: '#D6C5A4' // 标题颜色
        }
      },
      grid: {
        left: '0%',
        top: '-5%',
        right: '0%',
        bottom: '-50%',
        containLabel: true
      },
      legend: {
        // 图例的配置
        type: 'scroll',
        show: true,
        orient: 'horizontal', // 图例的排序顺序  horizontal：纵排  vertical：横排
        x: 'right',
        y: '10%',
        data: [],
        selectedMode: false,
        textStyle: {
          color: '#BDB097'
        }
      },
      tooltip: datas.tooltip,
      calculable: true,
      color: ['#27727B', '#C1232B', '#B5C334', '#FCCE10', '#E87C25', '#52AD98', '#aaad52'], // 对应的数据在图中的颜色
      series: [
        {
          name: '',
          type: 'pie',
          radius: [20, datas.series.type === 'rose1' ? 60 : 100],
          // radius: '50%',
          center: ['42%', '60%'],
          roseType: 'area',
          x: '20%', // for funnel
          // max: 20,                // for funnel
          sort: 'ascending', // for funnel
          label: {
            normal: {
              position: 'outside',
              formatter: '{c} '
            }
          },
          labelLine: {
            length: 2,
            length2: 2
          },
          data: []
        }
      ]
    };
    pieOption.title.text = datas.title;
    pieOption.legend.data = datas.legend;
    pieOption.series[0].data = datas.series.sData;
    // pieOption.tooltip = datas.tooltip || pieOption.tooltip;
    pieOption.tooltip = datas.tooltip ? Object.assign({}, datas.tooltip) : pieOption.tooltip;
    //                  if(datas.tooltip.link){
    //                      pieOption.tooltip.formatter = (params)=>{
    //                          window.sessionStorage.setItem('charts-pmnm',params.data.pmnm);
    //                          router.push({path:datas.tooltip.link,query:{equipAmountCompareName:params.name}});
    //                      }
    //                  }
    return pieOption;
  }
  if (datas.series.type === 'rose4') {
    let pieOption = {
      title: {
        text: '',
        x: 'left',
        y: (datas.series.type === 'rose1' && '-3%') || '-1%',
        textStyle: {
          color: '#D6C5A4' // 标题颜色
        }
      },
      grid: {
        left: '0%',
        top: '-5%',
        right: '0%',
        bottom: '-40%',
        containLabel: true
      },
      legend: [
        {
          // 图例的配置
          type: 'scroll',
          show: true,
          left: '0',
          right: '0',
          bottom: 'auto',
          top: 'auto',

          selectedMode: false,
          orient: 'horizontal', // 图例的排序顺序  horizontal：纵排  vertical：横排
          pageIconColor: '#BDB097',
          pageIconInactiveColor: '#2f4554',
          pageTextStyle: {
            color: '#BDB097'
          },
          x: 'right',
          //y: '10%',
          data: [],
          textStyle: {
            color: '#BDB097'
          }
        },
        {
          // 图例的配置
          type: 'scroll',
          show: true,
          left: '0',
          right: 'auto',
          bottom: 'auto',
          top: '25',

          selectedMode: false,
          orient: 'horizontal', // 图例的排序顺序  horizontal：纵排  vertical：横排,
          pageIconColor: '#BDB097',
          pageIconInactiveColor: '#2f4554',
          pageTextStyle: {
            color: '#BDB097'
          },
          //x: 'right',
          //y: '10%',
          data: [],
          textStyle: {
            color: '#BDB097'
          }
        }
      ],
      tooltip: {
        // trigger: 'item',
        // formatter: '{a} <br/>{b} : {c} 台套 ({d}%)'
      },
      // tooltip: datas.tooltip,
      calculable: true,
      color: ['#f0bb13', '#a5ff2e', '#df3bea', '#01c7ec', '#0083ff', '#43b134', '#aaad52'], // 对应的数据在图中的颜色
      series: [
        {
          name: '',
          type: 'pie',
          // radius: [15, (datas.series.type === 'rose1' ? 60 : 60)],
          radius: '64%',
          center: ['55%', '60%'],
          roseType: 'area',
          x: '0', // for funnelrose4
          max: 20, // for funnel
          sort: 'ascending', // for funnel
          label: {
            normal: {
              position: 'outside',
              formatter: '{d}% '
            }
          },
          labelLine: {
            length: 3,
            length2: 0
          },
          data: []
        }
      ]
    };

    pieOption.title.text = datas.title;

    let legendIndex = 0;
    datas.legend.forEach((item, index) => {
      if (index % 4 == 0) {
        legendIndex++;
      }

      pieOption.legend[legendIndex - 1].data.push(item);
    });

    pieOption.series[0].data = datas.series.sData;

    // pieOption.tooltip = datas.tooltip || pieOption.tooltip;
    pieOption.tooltip = datas.tooltip ? Object.assign({}, datas.tooltip) : pieOption.tooltip;

    return pieOption;
  }

  if (datas.series.type === 'rose3') {
    let pieOption = {
      title: {
        text: '',
        x: 'left',
        y: '0%',
        textStyle: {
          color: '#D6C5A4' // 标题颜色
        }
      },
      grid: {
        left: '0%',
        top: '20%',
        right: '0%',
        bottom: '-20%',
        containLabel: true
      },
      legend: {
        // 图例的配置
        show: true,
        orient: 'horizontal', // 图例的排序顺序  horizontal：纵排  vertical：横排
        x: 'left',
        y: '0%',
        itemGap: 15,
        itemWidth: 20,
        data: [],
        selectedMode: false,
        textStyle: {
          color: '#BDB097'
        }
      },
      tooltip: datas.tooltip,
      calculable: true,
      color: ['#f0bb13', '#a5ff2e', '#df3bea', '#01c7ec', '#0083ff', '#43b134', '#aaad52'], // 对应的数据在图中的颜色
      series: [
        {
          name: '',
          type: 'pie',
          radius: ['20%', '60%'],
          // radius: '50%',
          center: ['50%', '50%'],
          roseType: 'area',
          x: '20%', // for funnel
          // max: 20,                // for funnel
          sort: 'ascending', // for funnel
          label: {
            normal: {
              show: true,
              position: 'outside',
              formatter: '{c} '
            }
          },
          labelLine: {
            length: 2,
            length2: 2
          },
          data: []
        }
      ]
    };
    pieOption.title.text = datas.title;
    pieOption.legend.data = datas.legend;
    pieOption.series[0].data = datas.series.sData;
    // pieOption.tooltip = datas.tooltip || pieOption.tooltip;
    pieOption.tooltip = datas.tooltip ? Object.assign({}, datas.tooltip) : pieOption.tooltip;
    //                  if(datas.tooltip.link){
    //                      pieOption.tooltip.formatter = (params)=>{
    //                          window.sessionStorage.setItem('charts-pmnm',params.data.pmnm);
    //                          router.push({path:datas.tooltip.link,query:{equipAmountCompareName:params.name}});
    //                      }
    //                  }
    return pieOption;
  }
  if (datas.series.type === 'horBar') {
    let horBarOption = {
      title: {
        text: '',
        subtext: '',
        textStyle: {
          color: datas.textColor ? datas.textColor : '#A3966D',
          fontSize: '12',
          fontWeight: 'bold',
          textAlign: 'center'
        },
        left: datas.textLeft ? datas.textLeft : '',
        top: datas.textTop ? datas.textTop : '',
        subtextStyle: {
          color: '#fff',
          textAlign: 'left',
          fontSize: '14'
        },
        itemGap: -20
      },
      color: ['#417C3A', '#4FCDF6', '#DF9630', '#BF4947', '#1E9EF5', '#52AD98', '#aaad52'],
      tooltip:
        datas.style && datas.style.tooltip
          ? {
              formatter: datas.style.tooltip
            }
          : {
              trigger: 'axis',
              axisPointer: {
                // 坐标轴指示器，坐标轴触发有效
                type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
              }
            },
      grid: {
        left: datas.style && datas.style.left ? datas.style.left : '2%',
        right: '0%',
        bottom: '1%',
        top: datas.style && datas.style.top ? datas.style.top : '11%',
        x: 14,
        containLabel: true,
        y: 17
      },
      xAxis: {
        type: 'category',
        triggerEvent: true,
        data: [],
        axisTick: {
          alignWithLabel: true
        },
        axisLabel: {
          show: true,
          interval: 0,
          rotate: datas.style && (datas.style.rotate || datas.style.rotate == 0) ? datas.style.rotate : 57,
          textStyle: {
            color: datas.style && datas.style.textColor ? datas.style.textColor : '#D6C5A4',
            fontSize: '14'
          }
        },
        axisLine: {
          lineStyle: {
            type: 'solid',
            color: datas.style && datas.style.axisLinexColor ? datas.style.axisLinexColor : '#A3966D', // 左边线的颜色
            width: '1' // 坐标线的宽度
          }
        }
      },
      yAxis: [
        {
          name: datas.yAxis && datas.yAxis.name ? datas.yAxis.name : '',
          nameTextStyle: {
            color: '#fff'
          },
          show: true,
          triggerEvent: true,
          type: 'value',
          axisTick: {
            show: false
          },
          splitLine: {
            show: datas.yAxis && datas.yAxis.splitLineShow ? datas.yAxis.splitLineShow : false,
            lineStyle: datas.yAxis && datas.yAxis.lineStyle ? datas.yAxis.lineStyle : null
          },
          axisLine: {
            lineStyle: {
              type: 'solid',
              color: datas.style && datas.style.axisLineyColor ? datas.style.axisLineyColor : '#A3966D', // 左边线的颜色
              width: '1' // 坐标线的宽度
            }
          },
          axisLabel: {
            textStyle: {
              color: datas.style && datas.style.textyColor ? datas.style.textyColor : '#D6C5A4',
              fontSize: '14'
            }
          },
          minInterval: 1
        }
      ],
      series: [
        {
          name: '装备数量',
          type: 'bar',
          barWidth: datas.style && datas.style.barWidth ? datas.style.barWidth : '25%',
          itemStyle: {
            normal: {
              color:
                datas.style && datas.style.colors
                  ? function(params) {
                      // build a color map as your need.
                      var colorList = datas.style.colors;
                      return colorList[params.dataIndex];
                    }
                  : new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
                      {
                        offset: 0,
                        color: '#17d65b'
                      },
                      {
                        offset: 1,
                        color: '#80d10d'
                      }
                    ]),
              label: {
                show: datas.style && (datas.style.showSeriesLable || !datas.style.showSeriesLable) ? datas.style.showSeriesLable : true,
                position: 'top',
                color: datas.style && datas.style.labelColor ? datas.style.labelColor : '#46e3da'
              }
            }
          },
          markLine:
            datas.style && datas.style.showMarkLine
              ? {
                  symbol: 'none',
                  data: [
                    {
                      name: '平均线',
                      type: 'average',
                      lineStyle: {
                        normal: {
                          color: '#b957dd',
                          type: 'solid'
                        }
                      }
                    }
                  ]
                }
              : null,
          data: []
        }
      ]
    }; // horBarOption结束
    horBarOption.title.text = datas.title;
    horBarOption.title.subtext = datas.subtext;
    horBarOption.xAxis.data = datas.xAxis.xData;
    horBarOption.series[0].data = datas.series.sData;
    horBarOption.series[0].name = datas.series.name ? '装备缺编数量' : datas.series.name;
    horBarOption.tooltip = datas.tooltip ? Object.assign({}, datas.tooltip) : horBarOption.tooltip;
    return horBarOption;
  }
  if (datas.series.type === 'groupBar') {
    let groupData = [
      {
        type: 'bar',
        label: {
          normal: {
            show: true,
            position: 'top',
            textStyle: {
              color: '#ffffff'
            }
          }
        },
        barGap: '50%',
        itemStyle: {
          normal: {
            color: '#2d89ca'
          }
        }
      },
      {
        type: 'bar',
        label: {
          normal: {
            show: true,
            position: 'top',
            textStyle: {
              color: '#ffffff'
            }
          }
        },
        itemStyle: {
          normal: {
            color: '#ce564a'
          }
        }
      }
    ];
    //barWidth: '25%',
    (function(pData) {
      for (let i = 0; i < pData.series.name.length; i++) {
        groupData[i].name = pData.series.name[i];
        groupData[i].barWidth = '30%';
        groupData[i].data = pData.series.sData[i];
      }
      return groupData;
    })(datas); //将多组柱状图的数据注入groupData

    let groupBarOption = {
      title: {
        text: '',
        textStyle: {
          color: '#ffffff'
        }
      },
      grid: {
        left: '6%',
        right: '6%',
        bottom: '3%',
        top: '30%',
        containLabel: true
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          // 坐标轴指示器，坐标轴触发有效
          type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
        }
      },
      legend: {
        data: [],
        right: '5%',
        top: '5%',
        textStyle: {
          fontWeight: 'normal',
          color: '#ffffff'
        }
      },
      xAxis: [
        {
          type: 'category',
          data: [],
          axisLine: {
            show: true,
            lineStyle: {
              color: '#2f4234',
              width: 2
            }
          },
          splitLine: {
            show: true,
            lineStyle: {
              color: '#2f4234'
            }
          },
          axisTick: {
            show: false
          },
          axisLabel: {
            show: true,
            interval: 0,
            rotate: 0,
            textStyle: {
              color: '#ffffff',
              fontSize: '14'
            }
          }
        }
      ],
      color: ['#FF637D', '#00F6AB', '#FFDF49', '#7382E1', '#41C0FF', '#52AD98', '#aaad52'],
      yAxis: [
        {
          show: true,
          type: 'value',
          axisTick: {
            show: true
          },
          axisLine: {
            show: true,
            lineStyle: {
              color: '#2f4234', //左边线的颜色
              width: '2' //坐标线的宽度
            }
          },
          axisLabel: {
            show: true,
            interval: 0,
            textStyle: {
              color: '#ffffff',
              fontSize: '14'
            }
          },
          splitLine: {
            show: true,
            lineStyle: {
              color: ['#2f4234']
            }
          }
        }
      ],
      series: [{}]
    }; //groupBarOption结束
    groupBarOption.title.text = datas.title;
    groupBarOption.legend.data = datas.legend;
    groupBarOption.xAxis[0].data = datas.xAxis.xData;
    groupBarOption.series = groupData;

    return groupBarOption;
  }
  if (datas.series.type === 'gauge1') {
    return {
      tooltip: {
        formatter: '{b} : {c}%'
      },
      series: [
        {
          type: 'gauge',
          radius: '110%',
          center: ['50%', '80%'],
          startAngle: 180,
          endAngle: 0,
          min: 0, // 最小值
          max: 100, // 最大值
          precision: 0, // 小数精度，默认为0，无小数点
          splitNumber: 5, // 分割段数，默认为5
          splitLine: {
            // 分隔线
            show: true, // 默认显示，属性show控制显示与否
            length: -22, // 属性length控制线长
            lineStyle: {
              // 属性lineStyle（详见lineStyle）控制线条样式
              color: '#D6C5A4',
              width: 0,
              type: 'solid'
            }
          },
          axisLine: {
            // 坐标轴线
            lineStyle: {
              // 属性lineStyle控制线条样式
              color: [[0.8, '#D1644D'], [1, '#407C3A']],
              width: 18
            }
          },
          axisTick: {
            // 坐标轴小标记
            show: true, // 属性show控制显示与否，默认不显示
            splitNumber: 2, // split细分多少段
            length: 3, // 属性length控制线长
            lineStyle: {
              // 属性lineStyle控制线条样式
              color: '#D6C5A4',
              width: 1,
              type: 'solid'
            }
          },
          pointer: {
            width: 2,
            color: 'auto'
          },
          detail: {
            show: true,
            backgroundColor: 'rgba(0,0,0,0)',
            borderWidth: 0,
            borderColor: '#D6C5A4',
            width: 140,
            height: 120,
            offsetCenter: ['5%', '-45%'], // x, y，单位px
            formatter: '{value}%',
            textStyle: {
              // 其余属性默认使用全局文本样式，详见TEXTSTYLE
              color: '#D6C5A4',
              fontSize: 16
            }
          },
          title: {
            show: true,
            offsetCenter: ['0%', '20%'], // x, y，单位px
            textStyle: {
              // 其余属性默认使用全局文本样式，详见TEXTSTYLE
              color: '#D6C5A4',
              fontSize: 15,
              fontWeight: '200'
            }
          },
          data: [
            {
              value: datas.series.sData,
              name: datas.title
            }
          ]
        }
      ]
    };
  }
  if (datas.series.type === 'gauge2') {
    return {
      tooltip: {
        formatter: '{b} : {c}%'
      },
      series: [
        {
          type: 'gauge',
          radius: '160%',
          center: ['50%', '80%'],
          startAngle: 180,
          endAngle: 0,
          min: 0, // 最小值
          max: 100, // 最大值
          precision: 0, // 小数精度，默认为0，无小数点
          splitNumber: 5, // 分割段数，默认为5
          splitLine: {
            // 分隔线
            show: true, // 默认显示，属性show控制显示与否
            length: -22, // 属性length控制线长
            lineStyle: {
              // 属性lineStyle（详见lineStyle）控制线条样式
              color: '#D6C5A4',
              width: 0,
              type: 'solid'
            }
          },
          axisLine: {
            // 坐标轴线
            lineStyle: {
              // 属性lineStyle控制线条样式
              color: [[0.8, '#D1644D'], [1, '#407C3A']],
              width: 18
            }
          },
          axisTick: {
            // 坐标轴小标记
            show: true, // 属性show控制显示与否，默认不显示
            splitNumber: 2, // split细分多少段
            length: 3, // 属性length控制线长
            lineStyle: {
              // 属性lineStyle控制线条样式
              color: '#D6C5A4',
              width: 1,
              type: 'solid'
            }
          },
          pointer: {
            width: 2,
            color: 'auto'
          },
          detail: {
            show: true,
            backgroundColor: 'rgba(0,0,0,0)',
            borderWidth: 0,
            borderColor: '#D6C5A4',
            width: 140,
            height: 120,
            offsetCenter: ['5%', '-45%'], // x, y，单位px
            formatter: '{value}%',
            textStyle: {
              // 其余属性默认使用全局文本样式，详见TEXTSTYLE
              color: '#D6C5A4',
              fontSize: 16
            }
          },
          title: {
            show: true,
            offsetCenter: ['0%', '15%'], // x, y，单位px
            textStyle: {
              // 其余属性默认使用全局文本样式，详见TEXTSTYLE
              color: '#D6C5A4',
              fontSize: 15,
              fontWeight: '200'
            }
          },
          data: [
            {
              value: datas.series.sData,
              name: datas.title
            }
          ]
        }
      ]
    };
  }
  if (datas.series.type === 'gauge3') {
    return {
      tooltip: {
        formatter: '{b} : {c}%'
      },
      series: [
        {
          type: 'gauge',
          radius: '100%',
          center: ['30%', '80%'],
          startAngle: 180,
          endAngle: 0,
          min: 0, // 最小值
          max: 100, // 最大值
          precision: 0, // 小数精度，默认为0，无小数点
          splitNumber: 5, // 分割段数，默认为5
          splitLine: {
            // 分隔线
            show: true, // 默认显示，属性show控制显示与否
            length: -45, // 属性length控制线长
            lineStyle: {
              // 属性lineStyle（详见lineStyle）控制线条样式
              color: '#D6C5A4',
              width: 0,
              type: 'solid'
            }
          },
          axisLine: {
            // 坐标轴线
            lineStyle: {
              // 属性lineStyle控制线条样式
              color: [[0.5, '#D1644D'], [1, '#407C3A']],
              width: 180
            }
          },
          axisLabel: {
            textStyle: {
              color: '#fff'
            },
            formatter: function(v) {
              switch (v + '') {
                case '0':
                  return '故障数';
                case '100':
                  return '完好数';
                default:
                  return '';
              }
            }
          },
          axisTick: {
            // 坐标轴小标记
            show: false, // 属性show控制显示与否，默认不显示
            splitNumber: 2, // split细分多少段
            length: 3, // 属性length控制线长
            lineStyle: {
              // 属性lineStyle控制线条样式
              color: '#D6C5A4',
              width: 1,
              type: 'solid'
            }
          },
          pointer: {
            width: 5,
            color: 'auto'
          },
          detail: {
            show: true,
            backgroundColor: 'rgba(0,0,0,0)',
            borderWidth: 0,
            borderColor: '#D6C5A4',
            width: 140,
            height: 120,
            offsetCenter: ['5%', '0%'], // x, y，单位px
            formatter: '{value}',
            textStyle: {
              // 其余属性默认使用全局文本样式，详见TEXTSTYLE
              color: '#D6C5A4',
              fontSize: 16
            }
          },
          title: {
            show: true,
            offsetCenter: ['0%', '-120%'], // x, y，单位px
            textStyle: {
              // 其余属性默认使用全局文本样式，详见TEXTSTYLE
              color: '#D6C5A4',
              fontSize: 15,
              fontWeight: '200'
            }
          },
          data: [
            {
              value: datas.series.sData,
              name: datas.title
            }
          ]
        }
      ]
    };
  }
  // 环形图
  if (datas.series.type === 'circle') {
    let circleOption = {
      title: {
        show: true,
        text: datas.title ? datas.title : '',
        x: 'left',
        y: '0%',
        textStyle: {
          color: '#D6C5A4',
          fontSize: '16'
        }
      },
      tooltip: {
        // trigger: 'item',
        // formatter: '{a} <br/>{b} : {c} ({d}%)'
      },
      width: '80%',
      legend: {
        type: 'scroll',
        orient: 'vertical',
        x: 'left',
        y: '16%',
        itemColor: 'red',
        textStyle: {
          color: '#fff'
        },
        data: [],
        show: true,
        selectedMode: false
      },
      calculable: false,
      //color: ['#f1bb13', '#43b134', '#01c7ec', '#0084ff', '#44b235', 'gray', '#27727B', '#aaad52', '#E87C25'], // 对应的数据在图中的颜色
      color: ['#f0bb13', '#a5ff2e', '#0083ff', '#01c7ec', '#df3bea', '#43b134'], // 对应的数据在图中的颜色
      series: [
        {
          //name: '人员类别',
          // name: '',
          type: 'pie',
          center: ['70%', '50%'],
          radius: ['40%', '60%'],
          selectedMode: 'mutile',
          // funnelAlign: 'right',
          normal: {
            labelLine: {
              show: true
            }
          },
          label: {
            position: 'outside',
            formatter: params => {
              return params.value;
            }
          },
          labelLine: {
            show: false,
            length: 1,
            length2: 5
          },
          data: []
        }
      ]
    }; // embCircleOption结束
    circleOption.legend.data = datas.legend;
    circleOption.series[0].data = datas.series.sData;
    return circleOption;
  }
  // 内嵌环形图
  if (datas.series.type === 'embCircle') {
    let embCircleOption = {
      tooltip: {
        show: false,
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)'
      },
      legend: {
        type: 'scroll',
        orient: 'horizontal',
        left: 50,
        top: '10%',
        itemGap: 15,
        itemWidth: 20,
        borderRadius: 20,
        textStyle: {
          color: '#fff',
          fontSize: 14
        },
        selectedMode: false,
        data: []
      },

      // legend: {
      //   orient: 'vertical',
      //   x: 'right',
      //   data: datas.legend,
      //   show: true,
      //   label: {
      //     normal: {
      //       position: 'outside',
      //       formatter: '{b} : {c} ({d}%)'
      //     }
      //   },
      // },
      calculable: false,
      color: ['#f0bb13', '#a5ff2e', '#df3bea', '#01c7ec', '#0083ff', '#43b134', '#aaad52'], // 对应的数据在图中的颜色
      series: [
        {
          name: '装备',
          type: 'pie',
          selectedMode: 'mutile',
          radius: [0, '50%'],
          // for funnel
          x: '20%',
          width: '40%',
          funnelAlign: 'right',
          normal: {
            // labelLine : {
            //     show : true
            // }
          },
          label: {
            position: 'center',
            formatter: params => {
              console.log('============');
              console.log(params);
              console.log('============');
              return params.value.substring(0, 5) + '%' + '\n' + '装备完好率';
            },
            textStyle: {
              color: '#fff',
              fontSize: 16
            }
          },
          labelLine: {
            show: true,
            length: 1,
            length2: 2
          },
          data: datas.series.sData[0]
        },
        {
          name: '装备',
          type: 'pie',
          radius: ['60%', '75%'],
          label: {
            normal: {
              position: 'outside',
              //formatter: '{c}'
              formatter: params => {
                // console.log("============")
                // console.log(params.data.name + ':' + params.data.value)
                // console.log("============")
                return params.data.name + ':' + params.data.value;
              }
            },
            labelLine: {
              show: true,
              length: 6,
              length2: 6
            }
          },
          // for funnel
          x: '50%',
          width: '35%',
          funnelAlign: 'left',
          max: 1048,

          data: datas.series.sData[1]
        }
      ]
    }; // embCircleOption结束

    embCircleOption.legend.data = datas.legend;
    embCircleOption.series[0].data = datas.series.sData[0];
    embCircleOption.series[1].data = datas.series.sData[1];
    embCircleOption.series[0].name = datas.series.hasOwnProperty('name') ? datas.series.name[0] : embCircleOption.series[0].name;
    embCircleOption.series[1].name = datas.series.hasOwnProperty('name') ? datas.series.name[1] : embCircleOption.series[1].name;
    return embCircleOption;
  }
  // 变形环形饼图
  if (datas.series.type === 'loopPie') {
    let placeHolderStyle = {
      normal: {
        color: 'rgba(0,0,0,0)',
        label: {
          show: false
        },
        labelLine: {
          show: false
        }
      },
      emphasis: {
        color: 'rgba(0,0,0,0)'
      }
    };
    let dataStyle = {
      normal: {
        label: {
          show: false
        },
        labelLine: {
          show: false
        }
      }
    };
    let loopOption = {
      normal: {
        label: {
          show: false
        },
        labelLine: {
          show: false
        }
      },
      grid: {
        top: '-8%',
        bottom: '-8%',
        left: '0%'
      },
      title: {
        show: false,
        text: datas.title,
        x: '40%',
        y: 'top',
        itemGap: 12,
        textStyle: {
          color: '#D6C5A4'
        }
      },

      legend: {
        orient: 'horizontal',
        x: 185,
        y: -5,
        itemGap: 0.5,
        itemHeight: 9,
        itemWidth: 10,
        selectedMode: false,
        textStyle: {
          color: '#D6C5A4',
          fontSize: 10
        },

        label: {
          normal: {
            position: 'outside',
            formatter: '{b} : {c} ({d}%)'
          }
        },
        icon: 'triangle',
        data: datas.legend
      },
      tooltip: {
        show: true,
        formatter: '{b} : {c} ({d}%)'
      },
      series: [
        {
          name: '1',
          type: 'pie',
          clockWise: false,
          radius: [58, 70],
          center: ['50%', '50%'],
          itemStyle: dataStyle,
          data: [
            {
              value: datas.series.sData[0].value,
              name: datas.series.sData[0].name,
              itemStyle: {
                normal: {
                  color: '#27727B'
                }
              }
            },
            {
              value: datas.totalData - datas.series.sData[0].value,
              name: datas.series.sData[0].name,
              itemStyle: placeHolderStyle
            }
          ]
        },
        {
          name: '2',
          type: 'pie',
          clockWise: false,
          radius: [46, 58],
          center: ['50%', '50%'],
          itemStyle: dataStyle,
          data: [
            {
              value: datas.series.sData[1].value,
              name: datas.series.sData[1].name,
              itemStyle: {
                normal: {
                  color: '#C1232B'
                }
              }
            },
            {
              value: datas.totalData - datas.series.sData[1].value,
              name: datas.series.sData[1].name,
              itemStyle: placeHolderStyle
            }
          ]
        },
        {
          name: '3',
          type: 'pie',
          clockWise: false,
          radius: [34, 46],
          center: ['50%', '50%'],
          itemStyle: dataStyle,
          data: [
            {
              value: datas.series.sData[2].value,
              name: datas.series.sData[2].name,
              itemStyle: {
                normal: {
                  color: '#B5C334'
                }
              }
            },
            {
              value: datas.totalData - datas.series.sData[2].value,
              name: datas.series.sData[2].name,
              itemStyle: placeHolderStyle
            }
          ]
        },
        {
          name: '4',
          type: 'pie',
          clockWise: false,
          radius: [22, 34],
          center: ['50%', '50%'],
          itemStyle: dataStyle,
          data: [
            {
              value: datas.series.sData[3].value,
              name: datas.series.sData[3].name,
              itemStyle: {
                normal: {
                  color: '#FCCE10'
                }
              }
            },
            {
              value: datas.totalData - datas.series.sData[3].value,
              name: datas.series.sData[3].name,
              itemStyle: placeHolderStyle
            }
          ]
        },
        {
          name: '5',
          type: 'pie',
          clockWise: false,
          radius: [10, 22],
          center: ['50%', '50%'],
          itemStyle: dataStyle,
          data: [
            {
              value: datas.series.sData[4].value,
              name: datas.series.sData[4].name,
              itemStyle: {
                normal: {
                  color: '#E87C25'
                }
              }
            },
            {
              value: datas.totalData - datas.series.sData[4].value,
              name: datas.series.sData[4].name,
              itemStyle: placeHolderStyle
            }
          ]
        }
      ]
    };
    return loopOption;
  }
  // 垂直柱状图1
  if (datas.series.type === 'verBar1') {
    let verBarOption1 = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        },
        formatter: item => {
          return `${item[1].name}:${item[1].value}`;
        }
      },
      grid: {
        left: '10%',
        right: '5%',
        bottom: '1%',
        top: '-5%',
        containLabel: true
      },
      xAxis: {
        type: 'value',
        axisLabel: {
          interval: 0,
          rotate: 0,
          color: '#fff',
          fontSize: '16',
          show: false
        },

        axisLine: {
          show: false
        },
        splitLine: {
          show: false
        },
        boundaryGap: [0, 0.01]
      },
      yAxis: {
        type: 'category',
        triggerEvent: true,
        zlevel: 2,
        axisLabel: {
          interval: 0,
          rotate: 0,
          color: '#fff',
          fontSize: '14',
          margin: -45,
          formatter: item => {
            let itemTra = {
              '板   件': 'banjian',
              '整   机': 'zhengji',
              整系统: 'zhengxitong',
              '整   车': 'zhengce',
              修理所: 'xiulisuo',
              仓库: 'cangku'
            };
            if (itemTra[item]) {
              if (item === '仓库') {
                return ['{' + itemTra[item] + '|}'].join('\n') + '　　' + item;
              }
              return ['{' + itemTra[item] + '|}'].join('\n') + '　' + item;
            } else {
              return item;
            }
          },
          rich: {
            banjian: {
              backgroundColor: {
                image: require('@/assets/imgs/banjian.png')
              }
            },
            zhengji: {
              backgroundColor: {
                image: require('@/assets/imgs/zhengji.png')
              }
            },
            zhengxitong: {
              backgroundColor: {
                image: require('@/assets/imgs/zhengxitong.png')
              }
            },
            zhengce: {
              backgroundColor: {
                image: require('@/assets/imgs/zhengce.png')
              }
            },
            xiulisuo: {
              height: 18,
              width: 18,
              backgroundColor: {
                image: require('@/assets/imgs/xiulisuo.png')
              }
            },
            cangku: {
              height: 18,
              width: 18,
              backgroundColor: {
                image: require('@/assets/imgs/cangku.png')
              }
            }
          }
        },
        axisLine: {
          show: false
        },
        splitLine: {
          show: false
        },
        data: []
      },
      series: [
        {
          type: 'bar',
          itemStyle: {
            normal: {
              color: 'rgba(68, 82, 66, 0.4)'
            }
          },
          barGap: '-100%',
          barCategoryGap: '40%',
          barWidth: 18,
          data: [],
          animation: false
        },
        {
          //  name: '2011年',
          type: 'bar',
          silent: true,
          barWidth: 18,
          borderRadius: 5,
          label: {
            normal: {
              position: 'right',
              show: true,
              color: '#fff'
              // formatter: (item) => {

              // }
            }
          },
          itemStyle: {
            normal: {
              color: '#417C3A'
            }
          },
          data: []
        }
      ]
    };
    verBarOption1.yAxis.data = datas.yAxis.yData;
    let maxBar = JSON.parse(JSON.stringify(datas.series.sData)).sort((a, b) => {
      return b.value - a.value;
    })[0];
    let bgBar = [];
    for (let i = 0; i < datas.series.sData.length; i++) {
      bgBar.push({
        name: '',
        value: maxBar.value || 100
      });
    }
    verBarOption1.series[0].data = bgBar;
    verBarOption1.series[1].data = datas.series.sData;
    return verBarOption1;
  }
  // 堆叠图
  if (datas.series.type === 'stackBar2') {
    let stackBarData = datas.series.name.map(item => {
      return {};
    });
    let colors = ['#27727B', '#C1232B', '#B5C334', '#FCCE10', '#E87C25', '#52AD98', '#aaad52'];
    (function(pData) {
      for (let i = 0; i < datas.series.name.length; i++) {
        stackBarData[i].name = pData.series.name[i];
        stackBarData[i].data = pData.series.sData[i];
        if (i == datas.series.name.length - 1) {
          // 最后一个数据为线，将其设置为线
          stackBarData[i].type = 'line';
          stackBarData[i].stack = '0';
          stackBarData[i].barCategoryGap = '20%';
          stackBarData[i].barWidth = '20';
          stackBarData[i].itemStyle = {
            normal: {
              color: colors[i]
            }
          };
        } else {
          stackBarData[i].type = 'bar';
          stackBarData[i].stack = '1';
          stackBarData[i].barCategoryGap = '20%';
          stackBarData[i].barWidth = '20';
          stackBarData[i].itemStyle = {
            normal: {
              color: colors[i]
            }
          };
        }
      }
    })(datas);

    let stackBarOption = {
      title: {
        text: ''
      },
      tooltip: {
        trigger: 'item'
      },
      grid: {
        left: '10%',
        right: '10%',
        bottom: '18%',
        top: '10%'
      },
      calculable: true,
      legend: {
        selectedMode: false,
        data: [],
        x: 'right',
        orient: 'horizontal',
        textStyle: {
          color: '#D6C5A4'
        }
      },
      xAxis: [
        {
          type: 'category',
          triggerEvent: true,
          splitLine: {
            show: false
          },
          axisTick: {
            alignWithLabel: true
          },
          axisLabel: {
            show: true,
            interval: 0,
            rotate: 25,
            textStyle: {
              color: '#D6C5A4',
              fontSize: '16'
            }
          },
          axisLine: {
            lineStyle: {
              type: 'solid',
              color: '#A3966D', // 左边线的颜色
              width: '1' // 坐标线的宽度
            }
          },
          data: []
        }
      ],
      yAxis: [
        {
          type: 'value',
          position: 'left',
          triggerEvent: true,
          axisLabel: {
            show: true,
            interval: 25
          },
          axisLine: {
            lineStyle: {
              type: 'solid',
              color: '#A3966D', // 左边线的颜色
              width: '1' // 坐标线的宽度
            }
          },
          splitLine: {
            show: false
          }
        }
      ],
      series: [{}]
    };
    // stackBarOption.title.text = datas.title;
    stackBarOption.legend.data = datas.legend;
    stackBarOption.legend.selectedMode = datas.legendSelectedMode !== -1;
    if (!datas.legend) {
      delete stackBarOption.legend;
    }
    stackBarOption.xAxis[0].data = datas.xAxis.xData;
    stackBarOption.series = stackBarData;
    stackBarOption.tooltip = datas.tooltip ? Object.assign({}, datas.tooltip) : stackBarData.tooltip;

    return stackBarOption;
  }
  if (datas.series.type === 'tree') {
    let treeOptions = {
      tooltip: {
        trigger: 'item',
        triggerOn: 'mousemove'
      },
      series: [
        {
          type: 'tree',

          data: [],

          top: '1%',
          left: '10%',
          bottom: '1%',
          right: '10%',
          symbol: 'image://' + require('@/assets/imgs/tree-image-fire.png'),
          symbolSize: 20,
          roam: true,
          initialTreeDepth: -1,

          itemStyle: {
            normal: {
              color: '#2f4539',
              borderColor: '#458242',
              borderWidth: 1,
              label: {
                // position: 'insideLeft',
                position: 'right',
                verticalAlign: 'middle',
                align: 'left',
                fontSize: 16,
                color: 'white'
              },
              lineStyle: {
                color: '#458242',
                width: 1
                // curveness: 1,
              }
            },
            emphasis: {
              color: '#458242',
              label: {
                color: '#f9fafa',
                fontWeight: 'bold',
                fontSize: '16'
              }
            }
          },

          expandAndCollapse: true,
          animationDuration: 550,
          animationDurationUpdate: 750
        }
      ]
    }; // treeOptions结束
    treeOptions.series[0].data = [datas.series.sData];
    treeOptions.tooltip = datas.tooltip;
    return treeOptions;
  } // 树图结束
  // 折线图
  if (datas.series.type === 'line') {
    let lineOption = {
      title: {
        text: '五年装备数量年度趋势',
        subtext: '',
        textStyle: {
          color: '#D6C5A4',
          fontSize: '14',
          fontWeight: 'bold'
        },
        subtextStyle: {
          color: '#D6C5A4',
          align: 'center',
          fontSize: '12'
        }
      },
      xAxis: {
        type: 'category',
        data: [],
        axisLine: {
          lineStyle: {
            type: 'solid',
            color: '#A3966D', // 左边线的颜色
            width: '1' // 坐标线的宽度
          }
        },
        axisLabel: {
          show: true,
          interval: 0,
          rotate: 40,
          textStyle: {
            color: '#D6C5A4',
            fontSize: '14'
          }
        },
        axisTick: {
          show: false
        }
      },
      yAxis: {
        axisLine: {
          lineStyle: {
            type: 'solid',
            color: '#A3966D', // 左边线的颜色
            width: '1' // 坐标线的宽度
          }
        },
        type: 'value',
        triggerEvent: true,
        axisTick: {
          show: false
        },
        splitLine: {
          show: true,
          lineStyle: {
            type: 'solid',
            color: '#A3966D40', // 左边线的颜色 'rgba(0,196,219,0.3)'
            width: '1' // 坐标线的宽度
          }
        }
      },
      series: [
        {
          data: [],
          type: 'line',
          symbol: 'circle',
          symbolSize: 8
        }
      ]
    }; // lineOption结束

    lineOption.title.text = datas.title;
    lineOption.xAxis.data = datas.xAxis.xData;
    lineOption.series[0].type = 'line';
    lineOption.series[0].data = datas.series.sData;

    return lineOption;
  }
  // 饼图
  if (datas.series.type === 'pie') {
    let pieOption = {
      title: {
        text: '',
        x: datas.style && datas.style.titleAlign ? datas.style.titleAlign : 'left',
        y: datas.style && datas.style.titleTop ? datas.style.titleTop : '2%',
        textStyle: {
          color: datas.style && datas.style.titleColor ? datas.style.titleColor : '#A3966D' // 标题颜色
        }
      },
      grid: {
        left: datas.style && datas.style.left ? datas.style.left : '4%',
        right: datas.style && datas.style.right ? datas.style.right : '4%',
        bottom: datas.style && datas.style.bottom ? datas.style.bottom : '3%',
        containLabel: true
      },
      tooltip: {
        // 提示框组件配置
        trigger: 'item', // 触发类型 item：数据项图形触发   axis：坐标轴触发circle
        formatter: datas.style && datas.style.tooltip ? datas.style.tooltip : '{b} : {c} ({d}%)' // 提示框浮层内容格式器，支持字符串模板和回调函数两种形式。
      },
      color: datas.style && datas.style.colors ? datas.style.colors : ['#27727B', '#a5ff2e', '#B5C334', '#FCCE10', '#E87C25', '#52AD98', '#aaad52'], // 对应的数据在图中的颜色
      legend: {
        // 图例的配置

        // orient: datas.legendConfig ? datas.legendConfig.orient : 'horizontal', // 图例的排序顺序  horizontal：纵排  vertical：横排
        // left: datas.legendConfig ? datas.legendConfig.right ? undefined : '40%' : '40%',
        // right: datas.legendConfig ? datas.legendConfig.right ? datas.legendConfig.right : undefined : undefined,
        // top: datas.legendConfig ? datas.legendConfig.top ? datas.legendConfig.top : undefined : undefined,

        orient: datas.legendConfig && datas.legendConfig.orient ? datas.legendConfig.orient : 'horizontal', // 图例的排序顺序  horizontal：纵排  vertical：横排
        left: datas.legendConfig && datas.legendConfig.right ? undefined : '4%',
        right: datas.legendConfig && datas.legendConfig.right ? datas.legendConfig.right : undefined,
        top: datas.legendConfig && datas.legendConfig.top ? datas.legendConfig.top : undefined,
        // center: 'center',
        data: [],
        selectedMode: false,
        itemWidth: datas.legendConfig && datas.legendConfig.itemWidth ? datas.legendConfig.itemWidth : '',
        itemHeight: datas.legendConfig && datas.legendConfig.itemHeight ? datas.legendConfig.itemHeight : '',
        textStyle: {
          color: datas.style && datas.style.legendTextColor ? datas.style.legendTextColor : '#A3966D'
        }
      },
      series: [
        {
          // name: '访问来源',
          type: '',
          radius: datas.style && datas.style.radius ? datas.style.radius : '60%',
          center: datas.style && datas.style.center ? datas.style.center : ['50%', '62%'],
          label: {
            normal: {
              position: 'outside',
              formatter: datas.style && datas.style.labelFormatter ? datas.style.labelFormatter : '{d}%'
            }
          },

          labelLine: {
            show: true,
            length: 3,
            length2: 6
          },
          data: []
        }
      ]
    }; // pieOption结束
    pieOption.title.text = datas.title;
    pieOption.legend.data = datas.legend;
    pieOption.series[0].type = datas.series.type;
    pieOption.series[0].data = datas.series.sData;
    pieOption.tooltip = datas.tooltip ? Object.assign({}, datas.tooltip) : pieOption.tooltip;
    // pie5option.color = this.echColor;

    return pieOption;
  }
  if (datas.series.type === 'pie2') {
    let pieOption = {
      title: {
        text: '',
        x: 'center',
        y: '2%',
        textStyle: {
          color: '#A3966D' // 标题颜色
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      tooltip: {
        // 提示框组件配置
        trigger: 'item', // 触发类型 item：数据项图形触发   axis：坐标轴触发
        formatter: '{b} : {c} ({d}%)' // 提示框浮层内容格式器，支持字符串模板和回调函数两种形式。
      },
      color: ['#f0bb13', '#a5ff2e', '#df3bea', '#01c7ec', '#0083ff', '#43b134', '#aaad52'], // 对应的数据在图中的颜色
      legend: {
        orient: 'horizontal',
        left: 10,
        top: '10%',
        itemGap: 15,
        itemWidth: 20,
        borderRadius: 20,
        selectedMode: false,
        textStyle: {
          color: '#fff',
          fontSize: 14
        },
        data: []
      },
      series: [
        {
          // name: '访问来源',
          type: 'pie',
          radius: '46%',
          center: ['55%', '60%'],
          label: {
            normal: {
              position: 'outside',
              // formatter: '{b}:{c}({d}%)'
              formatter: '{c}'
            }
          },
          labelLine: {
            show: true,
            length: 6,
            length2: 6
          },
          data: []
        }
      ]
    }; // pieOption结束
    pieOption.title.text = datas.title;
    pieOption.legend.data = datas.legend;
    pieOption.series[0].data = datas.series.sData;
    // pie5option.color = this.echColor;

    return pieOption;
  }
  // 垂直柱状图开始
  if (datas.series.type === 'verBar') {
    let yData = datas.yAxis.yData.length > 0 ? JSON.parse(JSON.stringify(datas.yAxis.yData)).reverse() : [];
    let sdata = datas.series.sData.length > 0 ? JSON.parse(JSON.stringify(datas.series.sData)).reverse() : [];
    let verBarOption = {
      tooltip: {
        formatter: datas.style && datas.style.tooltip ? datas.style.tooltip : undefined
      },
      title: {
        text: '',
        x: '25%',
        textAlign: 'center'
      },
      color: ['#fcffb6'],
      grid: {
        left: datas.style && datas.style.left ? datas.style.left : '30%',
        right: '10%', //修改图案大小
        bottom: datas.style && datas.style.bottom ? datas.style.bottom : '6%',
        top: this.overflow ? '2%' : '',
        x: 14,
        y: 17
      },
      dataZoom: this.overflow
        ? [
            {
              width: 8,
              type: 'slider',
              top: '20%',
              bottom: '20%',
              show: true,
              yAxisIndex: [0],
              showDetail: false,
              start: 1,
              end: 75,
              zoomLock: true,
              maxSpan: 3,
              minSpan: 0,
              backgroundColor: '#111814',
              fillerColor: '#386b4f'
            },
            {
              width: 8,
              type: 'inside',
              top: '20%',
              bottom: '20%',
              show: true,
              yAxisIndex: [0],
              start: 1,
              end: 75,
              zoomLock: true,
              maxSpan: 3,
              minSpan: 0,
              showDetail: false,
              backgroundColor: '#111814',
              fillerColor: '#386b4f'
            }
          ]
        : false,
      xAxis: {
        type: 'value',
        triggerEvent: true,
        // show: true,
        data: sdata,
        axisLine: {
          show: datas.style && datas.style.showaxisLine ? datas.style.showaxisLine : false,
          lineStyle: {
            color: '#39503f'
          }
        },
        axisLabel: {
          show: datas.style && datas.style.showayisLine ? datas.style.showayisLine : false,
          formatter: '{value}',
          textStyle: {
            color: datas.style && datas.style.axisLabelxColor ? datas.style.axisLabelxColor : '#bdb097'
          }
        },
        splitLine: {
          show: this.overflow ? true : false,
          lineStyle: {
            color: '#39503f', //datas.style && datas.style.axisLineyColor ? datas.style.axisLineyColor : this.overflow ? '#39503f' : '#374337',
            width: this.overflow ? 1 : 2
          }
        }
      },
      yAxis: {
        type: 'category',
        triggerEvent: true,
        data: yData,
        zlevel: 2,
        textStyle: {
          align: 'right'
        },
        itemStyle: {
          normal: {
            color: '#fcffb6'
          }
        },
        axisLabel: {
          interval: 0,
          rotate: 0,
          color: datas.style && datas.style.axisLabelyColor ? datas.style.axisLabelyColor : '#bdb097',
          fontSize: '12',
          margin: 3, //改变文字方向
          textStyle: {
            align: 'right'
          }
          // formatter:function(value,item){
          //   return `TOP${datas.series.sData.length-item}　　${value}`
          // }
        },
        axisLine: {
          show: true,
          lineStyle: {
            // color: '#39503f',
            color: datas.style && datas.style.axisLinexColor ? datas.style.axisLinexColor : '#fcffb6',
            width: 1,
            type: 'solid'
          }
        },
        axisTick: {
          show: false
        },
        splitLine: {
          show: false
        }
      },
      series: [
        {
          type: 'bar',
          z: 3,
          barWidth: this.overflow ? 30 : 18,
          borderRadius: 5,
          label: {
            normal: {
              position: 'right',
              show: true,
              color: datas.style && datas.style.labelColor ? datas.style.labelColor : '#fcffb6',
              formatter: params => {
                let max = Math.max.apply(
                  Math,
                  datas.series.sData.map(function(o) {
                    return o.value;
                  })
                );
                let whiteSpace = '';
                if ((datas.width / max) * params.value >= params.name.length * 13) {
                  return params.value;
                } else {
                  for (let i = 0; i < (params.name.length * 13 - (datas.width / max) * params.value) / 16; i++) {
                    whiteSpace += '　';
                  }
                  return whiteSpace + params.value;
                }
              }
            }
          },
          itemStyle: {
            normal: {
              color:
                datas.style && datas.style.chartColor
                  ? new this.$echarts.graphic.LinearGradient(1, 0, 0, 0, [
                      {
                        offset: 0,
                        color: '#40b87b'
                      },
                      {
                        offset: 1,
                        color: '#407c39'
                      }
                    ])
                  : '#fcffb6'
            }
          },
          data: []
        }
      ]
    };

    verBarOption.yAxis.data = datas.yAxis.yData.length > 0 ? JSON.parse(JSON.stringify(datas.yAxis.yData)).reverse() : [];
    verBarOption.series[0].data = datas.series.sData.length > 0 ? JSON.parse(JSON.stringify(datas.series.sData)).reverse() : [];

    return verBarOption;
  }
  // 堆叠折线图
  if (datas.series.type === 'stackLine') {
    let stackBarData = [];
    let colors = ['#c65c5c', '#9ef52c', '#ea3cf5', '#f0bb13', '#47b636', '#0f6eca', '#01c7ec'];
    (function(pData) {
      for (let i = 0; i < datas.series.sData.length; i++) {
        stackBarData.push({
          type: 'line',
          name: datas.series.sData[i].name,
          stack: '总量',
          areaStyle: {
            normal: {
              color: colors[i]
            }
          },
          smooth: true,
          data: datas.series.sData[i].value
        });
      }
    })(datas);
    let stackLineOption = {
      title: {
        text: ''
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'cross',
          label: {
            backgroundColor: '#6a7985'
          }
        }
      },
      grid: {
        left: '10%',
        right: '10%',
        bottom: '28%',
        top: '10%'
      },
      calculable: true,
      legend: {
        data: [],
        selectedMode: false,
        textStyle: {
          color: '#D6C5A4'
        }
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        splitLine: {
          show: false
        },
        axisTick: {
          alignWithLabel: true
        },
        axisLabel: {
          show: true,
          interval: 0,
          rotate: 25,
          textStyle: {
            color: '#D6C5A4',
            fontSize: '16'
          }
        },
        axisLine: {
          lineStyle: {
            type: 'solid',
            color: '#A3966D', // 左边线的颜色
            width: '1' // 坐标线的宽度
          }
        },
        data: []
      },
      yAxis: {
        type: 'value',
        position: 'left',
        axisLabel: {
          show: true,
          interval: 25
        },
        axisLine: {
          lineStyle: {
            type: 'solid',
            color: '#A3966D', // 左边线的颜色
            width: '1' // 坐标线的宽度
          }
        },
        splitLine: {
          show: false
        }
      },
      series: []
    };
    // stackBarOption.title.text = datas.title;
    stackLineOption.legend.data = datas.legend;
    stackLineOption.legend.selectedMode = datas.legendSelectedMode !== -1;
    if (!datas.legend) {
      delete stackLineOption.legend;
    }
    stackLineOption.xAxis.data = datas.xAxis.xData;
    stackLineOption.series = stackBarData;

    return stackLineOption;
  } // 堆叠折线图
  if (datas.series.type === 'stackBar') {
    let stackBarData = [];
    /** 颜色调整--刘强   001  **/
    let colors = ['#f0bb13', '#a5ff2e', '#df3bea', '#01c7ec', '#0083ff', '#43b134', '#aaad52'];
    (function(pData) {
      for (let i = 0; i < datas.series.sData.length; i++) {
        stackBarData.push({
          type: 'bar',
          name: datas.series.sData[i].name,
          stack: '总量',
          areaStyle: {
            normal: {
              color: colors[i]
            }
          },
          barWidth: '50%',
          smooth: true,
          data: datas.series.sData[i].value
        });
      }
    })(datas);
    let stackLineOption = {
      title: {
        text: ''
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'cross',
          label: {
            backgroundColor: '#6a7985'
          }
        }
      },
      color: ['#f0bb13', '#a5ff2e', '#df3bea', '#01c7ec', '#0083ff', '#43b134', '#aaad52'], // 对应的数据在图中的颜色
      grid: {
        left: '10%',
        right: '10%',
        bottom: '28%',
        top: '23%'
      },
      calculable: true,
      legend: {
        data: [],
        selectedMode: false,
        textStyle: {
          color: '#D6C5A4'
        }
      },
      xAxis: {
        type: 'category',
        boundaryGap: true,
        splitLine: {
          show: false
        },
        axisTick: {
          alignWithLabel: true
        },
        axisLabel: {
          show: true,
          interval: 0,
          rotate: 25,
          textStyle: {
            color: '#D6C5A4',
            fontSize: '16'
          }
        },
        axisLine: {
          lineStyle: {
            type: 'solid',
            color: '#A3966D', // 左边线的颜色
            width: '1' // 坐标线的宽度
          }
        },
        data: []
      },
      yAxis: {
        type: 'value',
        position: 'left',
        axisLabel: {
          show: true,
          interval: 25
        },
        axisLine: {
          lineStyle: {
            type: 'solid',
            color: '#A3966D', // 左边线的颜色
            width: '1' // 坐标线的宽度
          }
        },
        splitLine: {
          show: false
        }
      }
    };
    // stackBarOption.title.text = datas.title;
    stackLineOption.legend.data = datas.legend;
    // stackLineOption.legend.selectedMode = datas.legendSelectedMode !== -1
    if (!datas.legend) {
      delete stackLineOption.legend;
    }
    stackLineOption.xAxis.data = datas.xAxis.xData;
    stackLineOption.series = stackBarData;

    return stackLineOption;
  }
  if (datas.series.type === 'groupLine') {
    let lineBarData = [];
    // let colors = ['#f0bb13', '#a5ff2e', '#df3bea', '#01c7ec', '#0083ff', '#43b134', '#aaad52'];
    (function(pData) {
      for (let item of datas.series.sData) {
        lineBarData.push({
          type: 'line',
          name: item.name,
          smooth: datas.style && (datas.style.smooth || !datas.style.smooth) ? datas.style.smooth : true,
          data: item.value,
          itemStyle: {
            normal: {
              color: datas.style && datas.style.lineColor ? datas.style.lineColor : 'yellow'
            }
          },
          label: {
            normal: {
              show: datas.style && datas.style.labelShow ? datas.style.labelShow : false
            }
          },
          lineStyle: {
            normal: {
              color: datas.style && datas.style.lineColor ? datas.style.lineColor : 'yellow'
            }
          }
        });
      }
    })(datas);
    let groupLineOption = {
      title: {
        text: datas.title ? datas.title : '',
        subtext: datas.subText ? datas.subText : '',
        textAlign: 'left'
      },
      tooltip:
        datas.style && datas.style.tooltip
          ? {
              formatter: datas.style.tooltip
            }
          : {
              trigger: 'axis',
              axisPointer: {
                type: 'cross',
                label: {
                  backgroundColor: '#6a7985'
                }
              }
            },
      grid: {
        left: datas.style && datas.style.left ? datas.style.left : '10%',
        right: datas.style && datas.style.right ? datas.style.right : '10%',
        bottom: datas.style && datas.style.bottom ? datas.style.bottom : '18%',
        top: datas.style && datas.style.top ? datas.style.top : '12%'
      },
      calculable: true,
      legend: {
        data: [],
        selectedMode: false,
        textStyle: {
          color: datas.style && datas.style.legendTextColor ? datas.style.legendTextColor : '#D6C5A4'
        }
      },
      color: ['#f0bb13', '#a5ff2e', '#df3bea', '#01c7ec', '#0083ff', '#43b134', '#aaad52'],
      xAxis: {
        name: datas.xAxis && datas.xAxis.name ? datas.xAxis.name : '',
        nameTextStyle: {
          color: '#fff'
        },
        type: 'category',
        boundaryGap: false,
        splitLine: {
          show: false
        },
        axisTick: {
          alignWithLabel: true
        },
        axisLabel: {
          show: true,
          interval: 0,
          rotate: datas.style && (datas.style.rotate || datas.style.rotate == 0) ? datas.style.rotate : 25,
          textStyle: {
            color: datas.style && datas.style.axisyLabelColor ? datas.style.axisyLabelColor : '#D6C5A4',
            fontSize: '14'
          }
        },
        axisLine: {
          lineStyle: {
            type: 'solid',
            color: datas.style && datas.style.axisxLineColor ? datas.style.axisxLineColor : '#A3966D', // 左边线的颜色
            width: '1' // 坐标线的宽度
          }
        },
        data: []
      },
      yAxis: {
        name: datas.yAxis && datas.yAxis.name ? datas.yAxis.name : '',
        nameTextStyle: {
          color: '#fff'
        },
        type: 'value',
        position: 'left',
        axisLabel: {
          show: true,
          interval: 25,
          textStyle: {
            color: datas.style && datas.style.axisxLabelColor ? datas.style.axisxLabelColor : '#D6C5A4',
            fontSize: '14'
          }
        },
        axisLine: {
          lineStyle: {
            type: 'solid',
            color: datas.style && datas.style.axisyLineColor ? datas.style.axisyLineColor : '#A3966D', // 左边线的颜色
            width: '1' // 坐标线的宽度
          }
        },
        splitLine: {
          show: false
        }
      },
      series: []
    };
    // stackBarOption.title.text = datas.title;
    groupLineOption.legend.data = datas.legend;
    groupLineOption.xAxis.data = datas.xAxis.xData;
    groupLineOption.series = lineBarData;

    return groupLineOption;
  }
  if (datas.series.type == 'treemap') {
    let that = this;
    return {
      tooltip: {
        trigger: 'item',
        formatter: function(params) {
          let total = params.treePathInfo;
          // console.log(total);
          // "{b}: {c}"
          return params.data.ryzy + ' : ' + total[1].value + ' (' + that.$common.common.toDecimal2((total[1].value * 100) / total[0].value) + '%)';
        }
      },
      calculable: false,
      series: [
        {
          name: '矩形图',
          type: 'treemap',
          roam: false,
          // roam:false,
          itemStyle: {
            normal: {
              label: {
                show: true,
                formatter: '{b}'
              },
              borderWidth: 1
            },
            emphasis: {
              label: {
                show: true
              }
            }
          },
          breadcrumb: {
            show: false
          },

          levels: [
            {
              // colorSaturation: [0.3, 0.6],
              itemStyle: {
                normal: {
                  borderColorSaturation: 0.2,
                  gapWidth: 10,
                  borderWidth: 0,
                  borderColor: 'transparent'
                }
              }
            }
            //   {
            //     color: ['#499c3f'],
            //   colorSaturation: [0.3, 0.6],
            //   itemStyle: {
            //     normal: {
            //       borderColorSaturation: 0.1,
            //       gapWidth: 2,
            //       borderWidth: 4
            //     }
            //   }
            // },
          ],
          data: datas.series.data
        }
      ]
    };

  }

  if (datas.series.type === 'organTable') {
    return {
      color: ['#3398DB'],
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          // 坐标轴指示器，坐标轴触发有效
          type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
        }
      },
      grid: {
        left: '1z%',
        right: '10%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: [
        {
          type: 'category',
          data: ['编制数', '实有数', '缺编数'],
          axisTick: {
            alignWithLabel: false
          },
          axisLine: {
            show: false
          },
          axisLabel: {
            textStyle: {
              color: '#fff'
            }
          }
        }
      ],
      yAxis: [
        {
          type: 'value',
          show: false
        }
      ],
      series: [
        {
          name: '直接访问',
          type: 'bar',
          barWidth: '60%',
          label: {
            normal: {
              show: true,
              position: 'top',
              textStyle: {
                color: '#fff'
              }
            }
          },
          data: datas.series.data
        }
      ]
    };

  }

  if (datas.series.type === 'organPie') {
    // console.log(datas.series.legend.sort('指挥军官','专业技术军官','文职干部','士官','义务兵','其他','未知'));
    return {
      title: {
        show: false,
        text: '人员编配对比情况',
        x: 'left',
        left: '15',
        top: '20',
        textStyle: {
          color: '#fff',
          fontSize: '14',
          fontFamily: ''
        }
      },
      tooltip: {
        trigger: 'item',
        formatter:
          "<div style='display: flex;justify-content: flex-start;'>" +
          "<span style='width: 5px;height: 5px;border-radius: 5px;background-color: yellow;margin-top: 10px;margin-right: 10px'></span>" +
          "<span style='vertical-align: middle'>{b} : {c} ({d}%)</span></div>"
      },
      legend: {
        show: true,
        orient: 'horizontal',
        selectedMode: false,
        top: '5%',
        left: 'center',
        itemWidth: 10,
        itemHeight: 10,
        x: 'left',
        textStyle: {
          color: '#cacccb'
        }, //['指挥军官','专业技术军官','文职干部','士官','义务兵','其他','未知']
        data: datas.series.legend
      },
      color: ['#ce564a', '#0CD4A5', '#4e9e44', '#f7e62d', '#0877e2', '#2de2da', '#ff9800'],
      series: [
        {
          name: '访问来源',
          type: 'pie',
          radius: ['25%', '25%'],
          silent: true,
          label: {
            normal: {
              position: 'center',
              formatter: function(p) {
                return '总人数\r\n' + p.value;
              },
              textStyle: {
                color: '#fff',
                fontSize: 20
              }
            }
          },
          selectedOffset: 0,
          labelLine: {
            normal: {
              show: false
            }
          },
          data: datas.series.total
        },
        {
          name: '访问来源',
          type: 'pie',
          radius: ['30%', '60%'],
          label: {
            normal: {
              show: true,
              formatter: '{b} {c}'
            }
          },
          labelLine: {
            normal: {
              smooth: true,
              length: 1,
              length2: 10
            }
          },
          data: datas.series.data
        }
      ]
    };

  }

  if (datas.series.type === 'xpPie') {
    console.log(datas.series);
    return {
      title: datas.title,
      tooltip: {
        trigger: 'item',
        formatter: '{b} : {c} ({d}%)'
      },
      color: ['#e4bc59', '#2d89ca', '#407c39', '#ce564a', '#c7803b', '#e269c7', '#796a47'],
      legend: datas.legend,
      series: [
        {
          // name: '访问来源',
          type: 'pie',
          radius: '55%',
          center: datas.series.radius,
          data: datas.series.data,
          itemStyle: {
            emphasis: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          },
          label: {
            normal: {
              formatter: '{c}'
            }
          },
          labelLine: {
            normal: {
              smooth: true,
              length: 1,
              length2: 20
            }
          }
        }
      ]
    };

  }

  if (datas.series.type === 'xpBar') {
    let yAxis = {
      axisLine: this.axisLine,
      splitLine: this.axisLine
    };

    let xAxis = {
      axisLine: this.axisLine
    };

    datas.yAxis = Object.assign(yAxis, datas.yAxis);

    datas.xAxis = Object.assign(xAxis, datas.xAxis);

    console.log(datas);

    return {
      title: {
        show: false,
        text: '机构分布情况趋势',
        x: 'left',
        left: '9',
        top: '10',
        textStyle: {
          color: '#fff',
          fontSize: '14',
          fontFamily: ''
        }
      },
      grid: {
        right: '1%',
        left: '5%'
      },
      legend: {
        show: true,
        top: '10',
        itemWidth: 10,
        itemHeight: 10,
        selectedMode: false,
        textStyle: {
          color: '#cacccb'
        },
        right: '10',
        data: datas.series.legend
      },
      color: datas.series.color,
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          // 坐标轴指示器，坐标轴触发有效
          type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
        },
        textStyle: {
          align: 'left'
        }
      },
      xAxis: datas.xAxis,
      yAxis: datas.yAxis,
      series: datas.series.data
    };

  }

  if (datas.series.type === 'qcxxTjt') {
    let option = {
      title: {
        text: '某地区蒸发量和降水量',
        subtext: '纯属虚构'
      },
      tooltip: {
        trigger: 'axis'
      },
      dataZoom: {
        show: true,
        realtime: true,
        y: 36,
        height: 20,
        start: 20,
        end: 80
      },
      legend: {
        type: 'scrool',
        data: ['蒸发量', '降水量'],
        right: '15%'
      },
      toolbox: {
        show: true,
        feature: {
          mark: {
            show: true
          },
          dataView: {
            show: true,
            readOnly: false
          },
          magicType: {
            show: true,
            type: ['line', 'bar']
          },
          restore: {
            show: true
          },
          saveAsImage: {
            show: true
          }
        }
      },
      calculable: true,
      xAxis: [
        {
          type: 'category',
          boundaryGap: false,
          axisLine: {
            show: false
          },
          data: [
            '1月',
            '2月',
            '3月',
            '4月',
            '5月',
            '6月',
            '7月',
            '8月',
            '9月',
            '10月',
            '11月',
            '12月',
            '10月',
            '11月',
            '12月',
            '10月',
            '11月',
            '12月',
            '10月',
            '11月',
            '12月',
            '10月',
            '11月',
            '12月',
            '10月',
            '11月',
            '12月',
            '10月',
            '11月',
            '12月'
          ]
        }
      ],
      yAxis: [
        {
          type: 'value'
        }
      ],
      series: [
        {
          name: '蒸发量',
          type: 'bar',
          data: [
            2.0,
            4.9,
            7.0,
            23.2,
            25.6,
            76.7,
            135.6,
            162.2,
            32.6,
            20.0,
            6.4,
            3.3,
            162.2,
            32.6,
            20.0,
            6.4,
            3.3,
            162.2,
            32.6,
            20.0,
            6.4,
            3.3,
            162.2,
            32.6,
            20.0,
            6.4,
            3.3
          ],
          markPoint: {
            data: [
              {
                type: 'max',
                name: '最大值'
              },
              {
                type: 'min',
                name: '最小值'
              }
            ]
          },
          markLine: {
            data: [
              {
                type: 'average',
                name: '平均值'
              }
            ]
          }
        },
        {
          name: '降水量',
          type: 'bar',
          data: [
            2.6,
            5.9,
            9.0,
            26.4,
            28.7,
            70.7,
            175.6,
            182.2,
            48.7,
            18.8,
            6.0,
            2.3,
            162.2,
            32.6,
            20.0,
            6.4,
            3.3,
            162.2,
            32.6,
            20.0,
            6.4,
            3.3,
            162.2,
            32.6,
            20.0,
            6.4,
            3.3
          ],
          markPoint: {
            data: [
              {
                name: '年最高',
                value: 182.2,
                xAxis: 7,
                yAxis: 183,
                symbolSize: 18
              },
              {
                name: '年最低',
                value: 2.3,
                xAxis: 11,
                yAxis: 3
              }
            ]
          },
          markLine: {
            data: [
              {
                type: 'average',
                name: '平均值'
              }
            ]
          }
        }
      ]
    };

    console.log(datas.series.data);
    return optionQcxx;
  }
}
