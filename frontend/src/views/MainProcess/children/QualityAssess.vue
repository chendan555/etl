<template>
    <div class="qualityAssess">
        <FrameDemo :title="title">
            <div class="echartsdiv" slot="content">
                <charts :options="options"></charts>
            </div>
        </FrameDemo>
    </div>
</template>
<script>
import charts from '@/components/Echarts/index'
import FrameDemo from './FrameDemo'
import {qaOptions} from './config.js'
import { dataRules } from '@/api/';
export default {
    name: 'qualityAssess',
    components: {charts, FrameDemo},
    data() {
        return {
            title: {
                imgUrl: 'qualityAssess',
                name: '数据质量评价（%）'
            },
            options: qaOptions
        }
    },
    methods: {
        // 初始化数据
        initData() {
            // let data = [3300, 9000, 24560, 25340, 14600, 13000]
            // this.options.series[0].data[0].value = data;
            dataRules.apiGetQuality()
                .then(res => {
                    if(res.data.code == 200) {
                        this.options.series[0].data[0].value = [
                            res.data.data.yizhi, 
                            res.data.data.weiyi,
                            res.data.data.wanzheng,
                            res.data.data.jishi,
                            res.data.data.youxiao,
                            res.data.data.zhunque,
                        ];
                    }
                })
        }
    },
    mounted() {
        this.initData();
    }
}
</script>
<style lang="scss" scoped>
.qualityAssess{
    width: 100%;
    height: 100%;
    .handle{
        display: flex;
        .item{
            width: 60px;
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
    .echartsdiv{
        width: 100%;
        height: 100%;
    }
}
</style>
