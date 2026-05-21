<template>
    <div class="addData">
        <FrameDemo :title="title">
            <!-- <div class="handle" slot="handle">
                <p class="item" :class="[activeName === index ? 'chsd': '']" v-for="(item, index) in tabList" :key="index" @click="handleClick(index)">
                    <span>{{item.name}}</span>
                </p>
            </div> -->
            <div class="echartsdiv" slot="content">
                <div class="tablediv">
                    <el-table
                        :data="addDataList"
                        style="width: 100%">
                        <el-table-column
                        type="index"
                        width="50">
                        </el-table-column>
                        <el-table-column
                            v-for="(item, index) in labelList"
                            :key="index"
                            :prop="item.prop"
                            :label="item.name"
                            show-overflow-tooltip>
                            <!-- <template slot-scope="scope">
                                <p :title="scope.row[item.prop]">{{item.prop==='connectTime'?scope.row[item.prop]:scope.row[item.prop].length>5?scope.row[item.prop].slice(0, 4)+'...':scope.row[item.prop]}}</p>
                            </template> -->
                        </el-table-column>
                        <el-table-column
                            prop="deal"
                            label="状态">
                            <template slot-scope="scope">
                                <p :style="{color: scope.row.deal==='已处理'?'#4AE16D':'#D4C91A'}">{{scope.row.deal}}</p>
                            </template>
                        </el-table-column>
                        <!-- <el-table-column
                            label="操作">
                            <div class="tableHandle">
                                <p>
                                    <img src="~@/assets/mainPage/iconView.png" alt="">
                                </p>
                                <p>
                                    <img src="~@/assets/mainPage/iconDeal.png" alt="">
                                </p>
                            </div>
                        </el-table-column> -->
                    </el-table>
                </div>
                <div class="morediv" @click="openMore">
                    <p>查看更多></p>
                </div>
            </div>
        </FrameDemo>
    </div>
</template>
<script>
// import axios from '@/api/base/http'
import { dataRules } from '@/api/';
// import charts from '@/components/Echarts/index'
import FrameDemo from './FrameDemo'
export default {
    name: 'addData',
    components: {FrameDemo},
    data() {
        return {
            labelList: [{
                name: '表单名称',
                prop: 'name'
            }, {
                name: '采集点位',
                prop: 'cjdd'
            }, {
                name: '采集时间',
                prop: 'cjsj'
            }],
            title: {
                imgUrl: 'addData',
                name: '补充数据'
            },
            tabList: [{
                name: '补充引接'
            }, {
                name: '补充采集'
            }],
            activeName: 0,
            addDataList: [
                {
                    position: '靖宇',
                    connectUnit: '文件',
                    dataContent: '在线',
                    connectTime: '2020-04-03',
                    status: Math.random() > 0.5 ? '1' : '0'
                }, {
                    position: '贺兰山',
                    connectUnit: '数据库',
                    dataContent: '离线',
                    connectTime: '2020-02-15',
                    status: Math.random() > 0.5 ? '1' : '0'
                }, {
                    position: '阿拉善',
                    connectUnit: 'Excel',
                    dataContent: '文件共享',
                    connectTime: '2020-06-25',
                    status: Math.random() > 0.5 ? '1' : '0'
                }, {
                    position: '双辽',
                    connectUnit: '表单',
                    dataContent: '在线',
                    connectTime: '2020-02-13',
                    status: Math.random() > 0.5 ? '1' : '0'
                }, {
                    position: '景德镇',
                    connectUnit: '音频',
                    dataContent: '文件共享',
                    connectTime: '2020-05-08',
                    status: Math.random() > 0.5 ? '1' : '0'
                }
            ]
        }
    },
    methods: {
        // 补充引接/补充采集切换
        handleClick(index) {
            this.activeName = index;
            // this.getData();
        },
        // 获取数据
        getData() {
            // axios.get('/api/getAddData').then(res=>{
            //     console.log(res.data.data)
            //     this.addDataList = res.data.data;
            // })
            dataRules.apiGetErrorForm({
                pageSize: 10,
                pageIndex: 1
            })
                .then(res => {
                    if(res.data.code == 200) {
                        this.addDataList = res.data.data.records;
                    }
                })
        },
        openMore() {
            this.$router.push({
                path: '/DataReviewReduce',
                query: {
                    showMore: true
                }
            });
            this.$eventBus.$emit('showMore');
        }
    },
    created() {
        this.getData();
    }
}
</script>
<style lang="scss" scoped>
.addData{
    width: 100%;
    height: 100%;
    .handle{
        height: 100%;
        display: flex;
        .item{
            width: 80px;
            height: 100%;
            font-size: 14px;
            display: flex;
            justify-content: flex-end;
            span{
                cursor: pointer;
                height: 100%;
            }
        }
        .chsd span{
            border-bottom: 1px solid #4AE16D;
            color: #4AE16D;
        }
    }
    .tableHandle{
        display: flex;
        justify-content: space-around;
        p{
            cursor: pointer;
        }
    }
    ::v-deep .el-table td, ::v-deep .el-table th{
        padding: 7px 0;
        text-align: center;
    }
    .echartsdiv{
        width: 100%;
        height: 100%;
    }
    .tablediv{
        width: 100%;
        height: calc(100% - 24px);
        overflow: hidden;
        overflow-y: auto;
    }
    .morediv{
        width: 100%;
        display: flex;
        justify-content: flex-end;
        p{
            font-size: 12px;
            color: #4DE9C6;
            line-height: 24px;
            cursor: pointer;
        }
    }
}
</style>
