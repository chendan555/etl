<template>
  <base-dialog :dialogConfig="dialogConfig" @closeDialog="closeDialog">
        <base-table v-bind="{...tableConfig}"></base-table>
    </el-tabs>
  </base-dialog>
</template>
<script>
import BaseDialog from '@/components/BaseDialog/index.vue';
import BaseTable from '@/components/BaseTable/index.vue';
import { dataCenterService } from '@/api/index.ts';
export default {
  name: 'fileHandleRecord',
  components: { BaseDialog, BaseTable },
  props: {
    dataParams: {
      type: Object,
      default() {
        return {};
      }
    }
  },
  data() {
    return {
      crtTab: 'upload',
      dialogConfig: {
        visible: true,
        title: '操作记录',
        width: '80%'
      },
      tableConfig: {
        tableData: [],
        tableColumnConfig: [
          {
            label: '文件名称',
            prop: 'fileName'
          },
          {
            label: '文件类型',
            prop: 'fileSuffix'
          },
          {
            label: '文件大小(KB)',
            prop: 'fileSize'
          },
          {
            label: '存储路径',
            prop: 'fileServerPath'
          },
          {
            label: '所属任务',
            prop: 'taskName'
          },
          {
            label: '所属节点',
            prop: 'taskNodeNamePath'
          },
          {
            label: '操作时间',
            prop: 'createTime'
          },
          {
            label: '操作人',
            prop: 'createName'
          },
          {
            label: '操作类型',
            prop: 'recordTypeName'
          },
          {
            label: '备注',
            prop: 'remark'
          }
        ]
      },
    };
  },
  mounted() {
    this.getUploadRecords();
  },
  methods: {
    closeDialog() {
      this.$emit('closeDialog');
    },
    getUploadRecords() {
      const params = {
        pageIndex: 1,
        pageSize: 99999,
        searchData: {
          serverSourceId: this.dataParams.id
        }
      };
      dataCenterService.getUploadRecords(params).then(({ data }) => {
        console.log(data);
        this.tableConfig.tableData = data.data.records;
      });
    }
  }
};
</script>
