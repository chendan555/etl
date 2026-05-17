<template>
  <div class="chsdProcList">
    <el-table :data="dataList" style="width: 100%" height="220">
      <el-table-column type="index"></el-table-column>
      <el-table-column
        v-for="(item, index) in procInforLabel"
        :key="index"
        :prop="item.prop"
        :label="item.label"
      >
        <template slot-scope="scope">
          <p
            v-if="item.prop === 'describe'"
            class="describe"
            :title="scope.row[item.prop]"
          >{{scope.row[item.prop]}}</p>
          <p v-else>{{scope.row[item.prop]}}</p>
        </template>
      </el-table-column>
      <el-table-column label="执行进度" width="200" v-if="showCase==true">
        <template slot-scope="scope">
          <el-progress :percentage="scope.row.progress"></el-progress>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" v-if="showCase==false">
        <template slot-scope="scope">
          <p class="btn delete" @click="removeItem(scope.$index)">
            <i class="el-icon-delete"></i>
          </p>
          <p class="btn moveUp" @click="moveUp(scope.$index)">
            <i class="el-icon-top"></i>
          </p>
          <p class="btn moveDown" @click="moveDown(scope.$index)">
            <i class="el-icon-bottom"></i>
          </p>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
export default {
  name: 'chsdProcList',
  props: ['dataList', 'showCase'],
  watch: {
    dataList: {
      deep: true,
      handler(val) {}
    }
  },
  data() {
    return {
      procInforLabel: [
        {
          label: '流程名称',
          prop: 'name',
          value: ''
        },
        {
          label: '项目分组名称',
          prop: 'belongToTaskGroup',
          value: ''
        },
        // {
        //   label: '创建人',
        //   prop: 'createUser',
        //   value: ''
        // },
        {
          label: '创建时间',
          prop: 'createDate',
          value: ''
        },
        {
          label: '描述',
          prop: 'describe',
          value: ''
        }
      ]
    };
  },
  methods: {
    removeItem(index) {
      this.dataList.splice(index, 1);
      // this.$emit('removeChsdProc', index);
    },
    moveUp(index) {
      if (index !== 0) {
        let item = this.dataList.splice(index, 1);
        this.dataList.splice(index - 1, 0, item[0]);
      } else {
        this.$message.info('已到第一位！');
      }
    },
    moveDown(index) {
      if (index !== this.dataList.length - 1) {
        let item = this.dataList.splice(index, 1);
        this.dataList.splice(index + 1, 0, item[0]);
      } else {
        this.$message.info('已到最后一位！');
      }
    }
  }
};
</script>
<style lang="scss" scoped>
.chsdProcList {
  width: 100%;
  height: 100%;

  .btn {
    display: inline-block;
    cursor: pointer;
    margin: 0 5px;
    &:hover {
      color: #2b9f57;
    }
  }
}
</style>
