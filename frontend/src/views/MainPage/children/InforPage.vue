<template>
  <div class="inforPage">
    <div class="headTitle">
      <div class="name">
        <img src="~@/assets/mainPage/taskInfor.png" alt>
        <p>任务信息</p>
      </div>
    </div>
    <div class="content">
      <div class="item" v-for="(item, index) in labelList" :key="index">
        <p class="name">{{item.name}}：</p>
        <p class="cont">{{taskInfor[item.label]}}</p>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'inforPage',
  data() {
    return {
      labelList: [
        {
          label: 'taskName',
          name: '任务名称'
        },
        {
          label: 'taskCode',
          name: '任务代码'
        },
        {
          label: 'taskType',
          name: '任务类型'
        },
        {
          label: 'organization',
          name: '组织单位'
        },
        {
          label: 'chargePerson',
          name: '责任人'
        },
        {
          label: 'taskTarget',
          name: '任务目的'
        },
        {
          label: 'startTime',
          name: '开始时间'
        },
        {
          label: 'endTime',
          name: '结束时间'
        },
        {
          label: 'position',
          name: '采集点位'
        },
        {
          label: 'collectUnit',
          name: '采集单位'
        },
        {
          label: 'equip',
          name: '采集设备'
        }
      ],
      taskInfor: {
        taskName: 'DF17发射数据采集任务',
        taskCode: 'XXX546-DD',
        taskType: '地方试验',
        organization: '火工大',
        chargePerson: '李建国',
        taskTarget: '测试新型导弹数据',
        startTime: '2020-04-06 10:18',
        endTime: '2020-04-08 15:54',
        position: '靖宇',
        collectUnit: 'XXX旅，XXX团',
        equip: '便携式采集终端、数据传办理中端、移动采集装备'
      }
    };
  }
};
</script>
<style lang="scss" scoped>
@import '@/styles/theme.scss';
.inforPage {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  .headTitle {
    width: 100%;
    height: 40px;
    display: flex;
    justify-content: space-between;
    padding: 0 5px;
    background: #2b4538;
    > div {
      display: flex;
      align-items: center;
    }
    .name p {
      font-weight: bold;
      margin-left: 5px;
    }
  }
  .content {
    flex: 1;
    padding: 5px;
    color: $normalWordsClr;
    .item {
      width: 100%;
      min-height: 30px;
      margin: 10px 0;
      display: flex;
      line-height: 30px;
      font-size: 14px;
      .name {
        width: 80px;
        text-align: right;
      }
      .cont {
        width: calc(100% - 80px);
      }
    }
  }
}
</style>
