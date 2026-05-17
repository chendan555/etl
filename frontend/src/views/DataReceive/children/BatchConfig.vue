<template>
  <div class="batchConfig">
    <base-form v-bind="{...formConfig}"></base-form>
  </div>
</template>
<script>
import BaseForm from '@/components/BaseForm/index.vue';
import API from '@/api/index';
export default {
  name: 'batchConfig',
  components: { BaseForm },
  data() {
    return {
      formConfig: {
        ruleForm: {
          createBatch: '0',
          currentBatch: ''
        },
        formItemConfig: [
          {
            label: '新建批次',
            type: 'radio',
            labelWidth: '100px',
            valueKey: 'createBatch',
            options: [
              {
                name: '是',
                value: '0'
              },
              {
                name: '否',
                value: '1'
              }
            ],
            optionsConfig: {
              name: 'name',
              value: 'value'
            },
            span: 24
          },
          {
            label: '历史批次',
            type: 'singleSelect',
            labelWidth: '100px',
            valueKey: 'currentBatch',
            options: 'getBatchNumList',
            optionsConfig: {
              name: 'NAME',
              value: 'VALUE'
            },
            span: 24,
            showCondition: {
              key: 'createBatch',
              value: '1'
            }
          },
          {
            label: '当前批次',
            type: 'input',
            labelWidth: '100px',
            valueKey: 'currentBatch',
            span: 24,
            disabled: true,
            showCondition: {
              key: 'createBatch',
              value: '0'
            }
          }
        ],
        formItemRuleConfig: {
          createBatch: [
            {
              required: true,
              message: '请选择是否新建批次号',
              trigger: 'change'
            }
          ],
          currentBatch: [
            {
              required: true,
              message: '请设置批次号',
              trigger: 'change'
            }
          ]
        }
      }
    };
  },
  watch: {
    'formConfig.ruleForm.createBatch': {
      deep: true,
      handler(val) {
        if (val === '0') {
          this.getCreateBatchNum();
        } else {
          this.formConfig.ruleForm.currentBatch = '';
        }
      }
    }
  },
  created() {
    this.getCreateBatchNum();
  },
  methods: {
    saveDialog(resolve) {
      if (this.formConfig.ruleForm.currentBatch) {
        this.$emit('submitBatch', this.formConfig.ruleForm.currentBatch, Number(this.formConfig.ruleForm.createBatch));
        this.$nextTick(() => {
          resolve();
        });
      } else {
        this.$utils.setMessage(false, '请完成批次号配置');
      }
    },
    getCreateBatchNum() {
      API.dataReceive.getCreatedBatchNum().then(({ data }) => {
        this.formConfig.ruleForm.currentBatch = data.result;
      });
    }
  }
};
</script>
<style lang="scss" scoped>
.batchConfig {
  width: 100%;
  height: 100%;
}
</style>
