<template>
  <el-dialog
    :title="ccbDialogConfig.title"
    :visible.sync="ccbDialogConfig.visible"
    @closed="closedDialog"
  >
    <div class="content">
      <base-form v-bind="{...formConfig}"></base-form>
    </div>
    <div slot="footer">
      <el-button type="primary" @click="saveDialog">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { planManage } from '@/api/index';
import BaseForm from '@/components/BaseForm/index.vue';
export default {
  name: 'chooseCaseBank',
  components: { BaseForm },
  props: {
    ccbDialogConfig: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  data() {
    return {
      formConfig: {
        ruleForm: {
          caseBankType: '1'
        },
        formItemConfig: [
          {
            label: '案例库分类',
            valueKey: 'caseBankType',
            type: 'select',
            labelWidth: '100px',
            optionsConfig: {
              name: 'name',
              value: 'value'
            },
            options: [
              {
                name: '共用',
                value: '1'
              },
              {
                name: '数据处理',
                value: '2'
              },
              {
                name: '数据挖掘',
                value: '3'
              },
              {
                name: '多源数据融合',
                value: '4'
              }
            ]
          }
        ]
      }
    };
  },
  methods: {
    closedDialog() {
      this.$emit('closeDialog');
    },
    saveDialog() {
      const params = {
        id: this.ccbDialogConfig.dataInfo.planId,
        type: this.formConfig.ruleForm.caseBankType
      };
      planManage.addToCaseBank(params).then(({ data }) => {
        if (data.success) {
          this.$message.success(data.result);
          this.$emit('closeDialog');
        } else {
          this.$message.warning('操作失败');
        }
      });
    }
  }
};
</script>

