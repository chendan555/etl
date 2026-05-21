<template>
  <base-dialog v-bind="$attrs" v-on="$listeners" @save="saveConfig">
    <div class="dialogContdiv">
      <base-form ref="form" v-bind="{...formConfig}"></base-form>
      <el-form
        ref="form2"
        :model="formConfig.ruleForm"
        label-width="140px"
        size="mini"
        :rules="formConfig.formItemRuleConfig"
      >
        <el-form-item label="处理方案" prop="cron">
          <el-input placeholder="请输入内容" v-model="getPlanName" disabled>
            <el-button slot="append" icon="el-icon-plus" @click="showDialogPlan"></el-button>
          </el-input>
        </el-form-item>
        <el-form-item label="CRON表达式" prop="cron">
          <el-input placeholder="请输入内容" v-model="formConfig.ruleForm.cronExpression" disabled>
            <el-button slot="append" icon="el-icon-plus" @click="showCronDialog"></el-button>
          </el-input>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" v-model="formConfig.ruleForm.remarks"></el-input>
        </el-form-item>
      </el-form>
    </div>
    <DialogCron
      v-if="cronDialog.visible"
      :dialogConfig="cronDialog"
      :cronExpression="formConfig.ruleForm.cronExpression"
      @closeDialog="closeCronDialog"
    ></DialogCron>
    <DialogPlan v-if="dialogPlan.visible" :dialogConfig="dialogPlan" @closeDialog="closeDialogPlan"></DialogPlan>
  </base-dialog>
</template>
<script >
import BaseDialog from '@/components/BaseDialog/index.vue';
import BaseForm from '@/components/BaseForm/index.vue';
import DialogCron from './DialogCron.vue';
import DialogPlan from './DialogPlan.vue';
import config from '../config';
export default {
  name: 'dialogCron',
  components: { BaseDialog, BaseForm, DialogCron, DialogPlan },
  props: {
    ruleForm: {
      type: Object
    }
  },
  data() {
    return {
      formConfig: {
        ruleForm: this.ruleForm ? this.ruleForm : this.$utils.deepCopy(config.formConfig.ruleForm),
        formItemRuleConfig: config.formConfig.formItemRuleConfig,
        formItemConfig: config.formConfig.formItemConfig
      },
      cronDialog: {
        width: '80%',
        title: 'CRON表达式',
        visible: false
      },
      dialogPlan: {
        width: '100%',
        fullscreen: true,
        title: '处理方案列表',
        visible: false
      }
    };
  },
  computed: {
    getPlanName() {
      let nameList = this.formConfig.ruleForm.detailVoList.map(item => item.name || item.planName);
      return nameList.join(',');
    }
  },
  methods: {
    /**
     * @description 保存并关闭弹窗
     */
    saveConfig() {
      let params = {
        ...this.formConfig.ruleForm,
        detailVoList: this.formConfig.ruleForm.detailVoList.map((item, index) => {
          return {
            planId: item.id || item.planId,
            orderNum: index + 1
          };
        })
      };
      this.$axios.post('/api/quartz/task/save', params).then(({ data }) => {
        if (data.success) {
          this.$message.success(data.result);
          this.$emit('closeDialog', true);
        } else {
          this.$message.warning(data.result);
        }
      });
    },
    /**
     * @description 展示方案选择页面
     */
    showPlanDialog() {
      this.planDialog.visible = true;
    },
    /**
     * @description 获取CRON表达式并关闭弹窗
     * @param list CRON表达式
     */
    closeCronDialog(data) {
      this.$set(this.formConfig.ruleForm, 'cronExpression', data);
      this.cronDialog.visible = false;
    },
    /**
     * @description 展示CRON配置弹窗
     */
    showCronDialog() {
      this.cronDialog.visible = true;
    },
    /**
     * @description 展示获取选择的方案列表弹窗
     */
    showDialogPlan() {
      this.dialogPlan.visible = true;
    },
    /**
     * @description 获取选择的方案列表并关闭弹窗
     * @param list 选择的处理方案列表
     */
    closeDialogPlan(list) {
      this.formConfig.ruleForm.detailVoList = list;
      this.dialogPlan.visible = false;
    }
  }
};
</script>
<style lang="scss" scoped>
.dialogContdiv {
  width: 100%;
  height: calc(100% - 40px);
  ::v-deep .el-input-group__append,
  .el-input-group__prepend {
    background: none;
    border: none;
    color: #fff;
  }
}
</style>
