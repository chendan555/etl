<template>
  <base-dialog v-bind="$attrs" v-on="$listeners" @save="saveTableConfig">
    <div class="addKey">
      <el-button size="mini" type="primary" @click="addKey" icon="el-icon-circle-plus-outline">新增字段</el-button>
    </div>
    <el-table :data="tableData">
      <el-table-column type="index" width="50"></el-table-column>
      <el-table-column label="字段名称">
        <template slot-scope="scope">
          <el-input size="mini" v-model="scope.row.name"></el-input>
        </template>
      </el-table-column>
      <el-table-column label="字段类型">
        <template slot-scope="scope">
          <el-input size="mini" v-model="scope.row.type"></el-input>
        </template>
      </el-table-column>
      <el-table-column label="字段长度">
        <template slot-scope="scope">
          <el-input size="mini" v-model="scope.row.lenght"></el-input>
        </template>
      </el-table-column>
      <el-table-column label="字段备注">
        <template slot-scope="scope">
          <el-input size="mini" v-model="scope.row.comments"></el-input>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template slot-scope="scope">
          <el-button class="deleteBtn" type="text" icon="el-icon-delete" @click="deleteKey(scope)"></el-button>
        </template>
      </el-table-column>
    </el-table>
  </base-dialog>
</template>
<script lang="ts">
import Vue from 'vue';
import BaseDialog from '@/components/BaseDialog/index.vue';
export default Vue.extend({
  name: 'configKey',
  components: { BaseDialog },
  data() {
    return {
      tableData: <any>[],
      defaultConfig: { name: '', type: '', lenght: '', comments: '' }
    };
  },
  mounted() {
    console.log(this.tableData);
    this.$eventBus.$on('showKeys', (data: any) => {
      this.tableData = data;
      console.log(this.tableData);
    });
  },
  methods: {
    saveTableConfig() {
      this.$emit('closeDialog');
      this.$eventBus.$emit('passNewKeys', this.tableData);
    },
    addKey() {
      this.tableData.unshift(this.$utils.deepCopy(this.defaultConfig));
    },
    deleteKey(scope: any) {
      this.$utils.confirm().then(() => {
        this.tableData.splice(scope.$index, 1);
      });
    }
  }
});
</script>
<style lang="scss" scoped>
.deleteBtn {
  color: #ce2c2c;
}
</style>
