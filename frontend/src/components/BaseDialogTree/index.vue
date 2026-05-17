<!--

    选择机构组件

-->

<template>
  <div class="mechanismModel">
    <!--选择机构-->
    <dialog-model v-if="dialog.visible" :dialog="dialog" :trees="true" @on-cancel="modelCancel">
      <div slot="slot-trees" style="height: 100%;">
        <Trees
          :config="treeConfig"
          v-model="tempValue"
          @change="changeValue"
          :treeData="treeData&& treeData.length ? treeData : []"
        ></Trees>
      </div>
      <div slot="slot-footer">
        <el-button size="mini" type="primary" @click="selectedSave">确定</el-button>
        <el-button size="mini" @click="dialog.visible = false" v-if="modelBtnClose">取 消</el-button>
      </div>
    </dialog-model>
  </div>
</template>

<script>
/**
 * Create by Leo
 * 基础组件
 * 基础树形结构弹框组件
 * 用于页面弹框显示进行树形结构数据选择
 */
// 引入Trees组件
import Trees from '@/components/Tree';
// 引入DialogModel组件
import DialogModel from '@/components/DialogModel';
export default {
  props: {
    /**
     * 控制弹框关闭按钮
     */
    modelBtnClose: {
      type: Boolean,
      default: true
    },
    /**
     * 树形组件配置
     */
    treeConfig: {
      type: Object
    },
    /**
     * 树形组件数据
     */
    treeData: {
      type: Array,
      default: () => []
    },
    /**
     * 树形组件回显数据
     */
    value: ''
  },
  components: {
    Trees,
    DialogModel
  },
  name: 'mechanismModel',
  data() {
    return {
      /**
       * 弹框配置信息
       */
      dialog: {
        title: '机构',
        visible: false,
        append: true,
        width: '26%',
        center: false,
        close: this.modelBtnClose
      },
      /**
       * 树形数据选择结果
       */
      checkData: [],
      tempValue: null,
      tempDesc: null
    };
  },
  watch: {
    /**
     * 监听弹框显隐情况
     */
    'dialog.visible': function(v) {
      if (v) {
        this.tempValue = this.value;
      }
    }
  },
  methods: {
    /**
     * 值改变
     * v：值
     * desc：描述
     */
    changeValue(v, desc) {
      this.tempDesc = desc;
      this.$emit('setNameKey', v);
    },
    /**
     * 展示数据
     * data：数据
     */
    show(data) {
      this.dialog.visible = true;
      if (data) {
        this.dialog.title = data.title ? data.title : '';
      }
    },
    /**
     * 清空选择
     */
    resetChecked() {
      this.$refs['tressRef'].resetChecked();
    },
    /**
     * 保存选择
     */
    selectedSave() {
      //验证选中对象是否是所需
      if (this.modelError && !this.checkbox) {
        this.$confirm(this.modelError.title, '系统提示', {
          showConfirmButton: false,
          cancelButtonText: '取消',
          type: 'warning'
        });
        return;
      }
      this.$emit('input', this.tempValue);
      this.$emit('change', this.tempValue, this.tempDesc);
      this.dialog.visible = false;
    },
    /**
     * 关闭弹框
     */
    modelCancel() {
      console.log('cancel');
    }
  }
};
</script>

<style lang="scss">
.mechanismModel {
  .el-dialog__body {
    height: 600px !important;
  }
}
</style>
