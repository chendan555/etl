 class CanvasDesign {
   constructor() {
     this.eleList = []
   }

   addNode(node) {
     this.eleList.push(node);
   }

   addTabNodeById(id, node) {
     const curNode = this.findNodeById(id);
     curNode.config.eleList.push(node);
   }

   removeNode(index) {
     this.eleList.splice(index, 1);
   }

   removeNodeById(id) {
     this.removeNode(this.findNodeIndexById(id));
   }

   updateNode(node, index) {
     this.eleList[index] = node;
   }

   insertNode(node, index) {
     this.eleList.splice(index, 0, node);
   }

   findNodeById(id) {
     return this.eleList.find(ele => ele.config.id === id);
   }

   findNodeIndexById(id) {
     return this.eleList.findIndex(ele => ele.config.id === id);
   }

   findTabNodeById(id) {
     let curTabNode;
     this.eleList.forEach(ele => {
       if (ele.key === 'tabs') {
         ele.config.eleList.forEach(tabEle => {
           if (tabEle.config.id === id) {
             curTabNode = tabEle
           }
         })
       }
     });
     return curTabNode;
   }

   findTabNodeIndexById(id) {
     let curIndex;
     this.eleList.forEach(ele => {
       if (ele.key === 'tabs') {
         ele.config.eleList.forEach((tabEle, index) => {
           if (tabEle.config.id === id) {
             curIndex = index
           }
         })
       }
     });
     return curIndex;
   }

   findNodeByIdDeep(id) {
     let curTabNode;
     this.eleList.forEach(ele => {
       if (ele.config.id === id) {
         curTabNode = ele;
       }
       if (ele.key === 'tabs') {
         ele.config.eleList.forEach(tabEle => {
           if (tabEle.config.id === id) {
             curTabNode = tabEle
           }
         })
       }
     });
     return curTabNode;
   }

   updatePos(moveIndex, curEleIndex) {
     if (moveIndex > curEleIndex) {
       moveIndex--;
     }
     this.eleList.splice(moveIndex, 0, this.eleList.splice(curEleIndex, 1)[0])
   }

   updateTabElePos(id, moveIndex, curEleIndex) {
     const curNode = this.findNodeById(id);
     if (moveIndex > curEleIndex) {
       moveIndex--;
     }
     curNode.config.eleList.splice(moveIndex, 0, curNode.config.eleList.splice(curEleIndex, 1)[0])
   }

   batchUpdate(attr, newVal) {
     this.eleList.forEach(ele => {
       ele.config[attr] = newVal;
       if (ele.key === 'tabs') {
         ele.config.eleList.forEach(tabEle => {
           tabEle.config[attr] = newVal;
         })
       }
     });
   }

   // 构造数据结果
   // 将就kettle数据接收格式，将table内容拆分成列组成的多个数组 
   getRuleForm() {
     const ruleForm = {};
     this.eleList.forEach(ele => {
       if (this.getValueKey(ele)) {
         if (ele.key === 'table') {
           ele.config.value.forEach((row) => {
             for (let key in row) {
               if (!ruleForm[key]) ruleForm[key] = []
               ruleForm[key].push(row[key])
             }
           })
         }
         ruleForm[this.getValueKey(ele)] = ele.config.value;
       }
       if (ele.key === 'tabs') {
         ele.config.eleList.forEach((tabEle) => {
           if (tabEle.key === 'table') {
             tabEle.config.value.forEach((row) => {
               for (let key in row) {
                 if (!ruleForm[key]) ruleForm[key] = []
                 ruleForm[key].push(row[key])
               }
             })
           }
           ruleForm[this.getValueKey(tabEle)] = tabEle.config.value;
         })
       }
     });
     return ruleForm;
   }

   // 表单元素key-value数组
   getEleListValueKeyAndName() {
     let result = [];
     this.eleList.forEach(ele => {
       if (this.getValueKey(ele) && ele.config.name) {
         result.push({
           label: ele.config.name,
           value: this.getValueKey(ele)
         });
       }
       if (ele.key === 'tabs') {
         ele.config.eleList.forEach((tabEle) => {
           if (this.getValueKey(tabEle) && tabEle.config.name) {
             result.push({
               label: tabEle.config.name,
               value: this.getValueKey(tabEle)
             });
           }
         })
       }
     });
     return result;
   }

   // 表单元素id name key 用于下拉选择
   getEleListId_Name_key() {
     let result = [];
     this.eleList.forEach(ele => {
       result.push({
         label: ele.config.name,
         value: ele.config.id,
         key: ele.key
       });
       if (ele.key === 'tabs') {
         ele.config.eleList.forEach((tabEle) => {
           result.push({
             label: tabEle.config.name,
             value: tabEle.config.id,
             key: tabEle.key
           });
         })
       }
     });
     return result;
   }

   getValueKey(ele) {
     if (ele.config.valueKey) {
       if (Array.isArray(ele.config.valueKey) && ele.config.valueKey.length >
         0) {
         return ele.config.valueKey[ele.config.valueKey.length - 1];
       } else if (ele.config.valueKey.length > 0) {
         return ele.config.valueKey
       }
     }
     return false;
   }

   updateValueByRuleForm(ruleForm) {
     this.eleList.forEach((ele) => {
       const valueKey = this.getValueKey(ele);
       if (ruleForm[valueKey]) {
         ele.config.value = ruleForm[valueKey]
       }
       if (ele.key === 'tabs') {
         ele.config.eleList.forEach((tabEle) => {
           const valueKey = this.getValueKey(tabEle);
           if (ruleForm[valueKey]) {
             tabEle.config.value = ruleForm[valueKey]
           }
         })
       }
     })
   }
 }

 export default CanvasDesign;
