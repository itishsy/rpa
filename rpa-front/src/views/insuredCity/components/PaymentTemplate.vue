<template>
    <!-- 参保城市 - 申报政策 - 缴费制单计划选择组模板 -->
    <!-- 社保下有 社保系统 、 医保系统 、社保三险系统 -->
    <!-- 公积金下有 公积金 、 补充公积金 -->
    <div class="mb-20 display-flex select-item">
        <!-- <p class="title  mr-10" :class="{'required-pre':label !='调基'}">{{ label }}:</p> -->
        <el-select v-if="type" v-model="sourceItem.socilalSelect" placeholder="请选择" class="ml-10">
            <el-option v-for="item in socialOptions" :key="item.value" :label="item.label" :value="item.value" disabled>
            </el-option>
        </el-select>
        <el-select v-else v-model="sourceItem.fundSelect" placeholder="请选择" class="ml-10">
            <el-option v-for="item in fundOptions" :key="item.value" :label="item.label" :value="item.value" disabled>
            </el-option>
        </el-select>
        <select-group :options="defaultoptions" :default-value="sourceItem.defaultStartValue"
            @input="selectStartGroupChange($event, sourceItem)"></select-group>
        <span class="ml-10 mr-10">至</span>
        <select-group :options="defaultoptions" :default-value="sourceItem.defaultEndValue"
            @input="selectEndGroupChange($event, sourceItem)"></select-group>

        <el-select v-model="sourceItem.isWeekend" placeholder="请选择" class="ml-10">
            <el-option v-for="item in optionDecs" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
        </el-select>

        <el-input v-if="sourceItem.isWeekend === 0 || sourceItem.isWeekend === 1" v-model="sourceItem.isWeekendDay"
            class="my-select" maxlength="1" placeholder="请输入"
            onKeypress="return(/^[1-7]*$/.test(String.fromCharCode(event.keyCode)))"
            onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')"
            @blur="onWeekendDayBlur">
            <template #suffix>天</template>
        </el-input>
    </div>
</template>
<script>
import SelectGroup from '../components/SelectGroup.vue'
export default {
    name: 'PaymentTemplate',
    components: { SelectGroup },
    props: {
        // label: {
        //     type: String,
        //     required: true,
        //     default: ''
        // },
        // 1 为社保 2为公积金
        type: {
            type: Number,
            default: 1
        },
        sourceItem: {
            type: Object,
            required: true,
            default: () => {
                // "defaultStartValue":[0, 1, 0],   //申报开始默认值
                // "defaultEndValue": [0, 1, 0];    //申报结束默认值
            }
        }
    },
    data() {
        return {
            defaultoptions: [

                {
                    //申报月开始/结束
                    label: '',      //用于placehodler显示
                    items: [
                        { label: '上月', value: 2 },
                        { label: '当月', value: 0 },
                        { label: '下月', value: 1 }
                    ]
                },
                {
                    //申报日开始/结束
                    label: '',
                    items: [
                        { label: '1日', value: 1 },
                        { label: '2日', value: 2 },
                        { label: '3日', value: 3 },
                        { label: '4日', value: 4 },
                        { label: '5日', value: 5 },
                        { label: '6日', value: 6 },
                        { label: '7日', value: 7 },
                        { label: '8日', value: 8 },
                        { label: '9日', value: 9 },
                        { label: '10日', value: 10 },
                        { label: '11日', value: 11 },
                        { label: '12日', value: 12 },
                        { label: '13日', value: 13 },
                        { label: '14日', value: 14 },
                        { label: '15日', value: 15 },
                        { label: '16日', value: 16 },
                        { label: '17日', value: 17 },
                        { label: '18日', value: 18 },
                        { label: '19日', value: 19 },
                        { label: '20日', value: 20 },
                        { label: '21日', value: 21 },
                        { label: '22日', value: 22 },
                        { label: '23日', value: 23 },
                        { label: '24日', value: 24 },
                        { label: '25日', value: 25 },
                        { label: '26日', value: 26 },
                        { label: '27日', value: 27 },
                        { label: '28日', value: 28 },
                        { label: '29日', value: 29 },
                        { label: '30日', value: 30 },
                        { label: '31日', value: 31 },
                    ]
                },
                //申报时开始/结束
                {
                    label: '',
                    items: [
                        { label: '00时', value: 0 },
                        { label: '01时', value: 1 },
                        { label: '02时', value: 2 },
                        { label: '03时', value: 3 },
                        { label: '04时', value: 4 },
                        { label: '05时', value: 5 },
                        { label: '06时', value: 6 },
                        { label: '07时', value: 7 },
                        { label: '08时', value: 8 },
                        { label: '09时', value: 9 },
                        { label: '10时', value: 10 },
                        { label: '11时', value: 11 },
                        { label: '12时', value: 12 },
                        { label: '13时', value: 13 },
                        { label: '14时', value: 14 },
                        { label: '15时', value: 15 },
                        { label: '16时', value: 16 },
                        { label: '17时', value: 17 },
                        { label: '18时', value: 18 },
                        { label: '19时', value: 19 },
                        { label: '20时', value: 20 },
                        { label: '21时', value: 21 },
                        { label: '22时', value: 22 },
                        { label: '23时', value: 23 },
                    ]
                }

            ],
            defaultStartValue: [0, 1, 0],   //申报开始默认值
            defaultEndValue: [0, 1, 0],     //申报结束默认值
            optionDecs: [
                {
                    value: null,
                    label: '请选择'
                },
                {
                    value: 0,
                    label: '遇节假日提前'
                }, {
                    value: 1,
                    label: '遇节假日延后'
                }
            ],
            isWeekendDay: '',    //延后天数  当isWeekend存在时显示，然后只能输入1~7
            isWeekend: '',      //遇节假日提前？遇节假日延后
            socialOptions: [            //社保选择
                // {
                //     value: null,
                //     label:'请选择'
                // },
                {
                    value: 0,
                    label: '社保系统'
                }, {
                    value: 1,
                    label: '医保系统'
                }, {
                    value: 2,
                    label: '社保三险系统'
                }
            ],
            fundOptions: [            //公积金选择
                // {
                //     value: null,
                //     label:'请选择'
                // },
                {
                    value: 0,
                    label: '公积金'
                }, {
                    value: 1,
                    label: '补充公积金'
                }
            ],
        };
    },
    computed: {},
    watch: {},
    created() { },
    beforeMount() { },
    mounted() { },
    methods: {
        selectStartGroupChange(e, sourceItem) {
            // console.log('selectGroupChange----start----', e,sourceItem)
            //当前开始选择组进行了变更 要同步修改sourceItem
            const returnItem = {
                ...sourceItem,
                monthBegin: e[0],
                dayBegin: e[1],
                hourBegin: e[2]
            }
            this.$emit('onStartChange', returnItem)
        },
        selectEndGroupChange(e, sourceItem) {
            // console.log('selectGroupChange----end----', e)
            const returnItem = {
                ...sourceItem,
                monthEnd: e[0],
                dayEnd: e[1],
                hourEnd: e[2]
            }
            this.$emit('onEndChange', returnItem)
        },
        onWeekendDayBlur(e) {
            // console.log('isWeekendDay', e,this.sourceItem.isWeekendDay)
            this.$emit('onWeekendDayBlur', this.sourceItem)
        }
    },
}
</script>
<style lang='less' scoped>
.select-item {
    align-items: center;

    .title {
        line-height: 30px;
        min-width: 100px;
        text-align: right;
    }

    .my-select {
        width: 100px;
    }
}
</style>