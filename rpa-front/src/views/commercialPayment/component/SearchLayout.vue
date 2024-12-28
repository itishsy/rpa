<template>
    <div class="search-row clearfix">
        <el-row>
            <el-col :span="8" class="display-flex">
                <span class="label">申报账户：</span>
                <el-select v-model="searchForms.accountNumber" class="w-300" placeholder="请选择申报账户" clearable filterable>
                    <el-option v-for="it in companyArr" :key="it.itemId" :label="it.name"
                        :value="it.accountNumber"></el-option>
                </el-select>
            </el-col>
            <el-col :span="8" class="display-flex">
                <span class="label">创建时间：</span>
                <div style="display:flex;max-width:300px">
                    <el-date-picker v-model="searchForms.minCreateDate" value-format="yyyy-MM-dd" style="width:50%;"
                        type="date" placeholder="选择时间" clearable align="center"
                        :picker-options="getPickerOption('start', 'searchForms')"></el-date-picker>
                    <span class="lh-com" style="padding: 0 5px;">至</span>
                    <el-date-picker v-model="searchForms.maxCreateDate" value-format="yyyy-MM-dd" style="width:50%;"
                        placeholder="选择时间" type="date" clearable
                        :picker-options="getPickerOption('end', 'searchForms')"></el-date-picker>
                </div>
            </el-col>
            <el-col :span="8" class="display-flex">
                <span class="label">费款所属期：</span>
                <div style="display:flex;max-width:300px">
                    <el-date-picker v-model="searchForms.minPeriodOfPayment" value-format="yyyy-MM" style="width:50%;"
                        type="month" placeholder="选择时间" clearable></el-date-picker>
                    <span class="lh-com" style="padding: 0 5px;">至</span>
                    <el-date-picker v-model="searchForms.maxPeriodOfPayment" value-format="yyyy-MM" style="width:50%;"
                        placeholder="选择时间" type="month" clearable></el-date-picker>
                </div>
            </el-col>
        </el-row>
        <el-row class="mt-15">
            <el-col :span="16" class="display-flex">
                <span class="label">缴费状态：</span>
                <el-checkbox-group v-model="searchForms.statuses" size="medium" @change="testChange"
                    style="display: flex;align-items: center;">
                    <el-checkbox v-for="item in searchOptions.declareTypes" :key="item.id" :label="item.id">
                        {{ item.name }}
                    </el-checkbox>
                </el-checkbox-group>
            </el-col>
            <el-col :span="8" class="text-right">
                <!-- <el-button type="primary" plain class="search-btn w-60 mr-10">重置</el-button> -->
                <el-button type="primary" class="search-btn w-60 mr-10" @click="clickQuery">查询</el-button>
                <el-button v-if="$global.hasPermission('commercialPaymentAdd')" type="primary"
                    class="search-btn w-60 mr-80" @click="addRow">添加</el-button>
            </el-col>
        </el-row>
    </div>
</template>
<script>
export default {
    name: 'SearchLayout',
    components: {},
    props: {
        //业务类型 1社保 2公积金
        type: {
            type: Number,
            default: 1
        },
        //是否具有管理员权限 管理员才有“添加”
        hasRolePermission: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            searchOptions: {
                declareTypes: [
                    {
                        name: '未核定',
                        id: 0
                    },
                    {
                        name: '已核定',
                        id: 1
                    },
                    {
                        name: '已缴费',
                        id: 2
                    },
                    {
                        name: '缴费异常',
                        id: 3
                    }
                ]
            },
            searchForms:
            {
                accountNumber: null,              //申报账户
                /*minCreateDate: this.$moment().startOf('month').format('YYYY-MM-DD'),      //最小创建时间
                maxCreateDate: this.$moment().format('YYYY-MM-DD'),        //最大创建时间*/
                minCreateDate: '',        //最大创建时间
                maxCreateDate: '',        //最大创建时间
                minPeriodOfPayment: this.$moment().format('YYYY-MM'),       //最小费款所属期
                maxPeriodOfPayment: this.$moment().format('YYYY-MM'),       //最大费款所属期
                statuses: []                 //缴费状态
            },
            companyArr: []               //申报账户选择

        };
    },
    computed: {
        getPickerOption() {
            return function (time, key) {
                var obj = {}
                var self = this
                if (time == 'start') {
                    return {
                        disabledDate(time) {
                            if (!self[key].maxCreateDate) {
                                return false
                            }
                            let curDate = new Date(self[key].maxCreateDate).getTime()
                            let three = new Date(
                                self
                                    .$moment(self[key].maxCreateDate)
                                    .subtract(6, 'months')
                                    .format('YYYY-MM-DD')
                            ).getTime()
                            // let threeMonths = three - curDate
                            return time.getTime() > curDate || time.getTime() < three
                        },
                    }
                } else {
                    return {
                        disabledDate(time) {
                            if (!self[key].minCreateDate) {
                                return false
                            }
                            let curDate = new Date(self[key].minCreateDate).getTime()
                            let three = new Date(
                                self
                                    .$moment(self[key].minCreateDate)
                                    .add(6, 'months')
                                    .format('YYYY-MM-DD')
                            ).getTime()
                            // let threeMonths = curDate + three
                            return (
                                time.getTime() <= curDate - 86400000 ||
                                time.getTime() > three - 86400000
                            )
                        },
                    }
                }
            }
        },
    },
    watch: {},
    created() {
      if (this.$route.query.status) {
        let statuses = []
        statuses.push(Number(this.$route.query.status))
        this.searchForms.statuses = statuses
      }
      if (this.$route.query.createStartTime) {
        this.searchForms.minCreateDate = this.$route.query.createStartTime
      }
      if (this.$route.query.createEndTime) {
        this.searchForms.maxCreateDate = this.$route.query.createEndTime
      }
      if(this.$route.query.status || this.$route.query.createStartTime || this.$route.query.createEndTime){
        this.searchForms.minPeriodOfPayment = ''
        this.searchForms.maxPeriodOfPayment = ''
      }
        this.getCompanyList(this.type)
    },
    beforeMount() { },
    mounted() { },
    methods: {
        getSocialAddrId() {

        },
        testChange(val) {
            // console.log(val)
        },
        //管理员视角下 添加缴费记录
        addRow() {
            this.$emit('add')
        },
        // 获取参保主体
        async getCompanyList(type) {
            let that = this,
                addressId = ''
            // if (type == 1) {
            //     addressId = that.socialSearch.addrId
            // } else {
            //     addressId = that.accfundSearch.addrId
            // }
            return this.$http({
                url: '/policy/declareAccount/seeMainBody',
                method: 'post',
                params: {
                    bussinessType: type,
                    addressId: addressId,
                    custLimit:2
                },
            })
                .then(({ data }) => {
                    that.companyArr = data.data
                    // if (type == 1) {
                    //     that.socialSearch.companyArr = data.data
                    //     that.socialSearch.company = ''
                    //     that.socialSearch.accountNumber = ''
                    //     if (data.data.length == 1) {
                    //         that.socialSearch.itemId = data.data[0].itemId
                    //         this.changeCompany(1)
                    //     }
                    // } else {
                    //     that.accfundSearch.companyArr = data.data
                    //     that.accfundSearch.company = ''
                    //     that.accfundSearch.accountNumber = ''
                    //     if (data.data.length == 1) {
                    //         that.accfundSearch.itemId = data.data[0].itemId
                    //         this.changeCompany(2)
                    //     }
                    // }
                })
                .catch((data) => { })
        },
        //点击查询
        clickQuery() {
            // console.log('看看查询参数', this.searchForms)
            this.$emit('query', this.searchForms)
        },
    },
}
</script>
<style lang='less' scoped>
.search-row {
    .label {
        width: 90px;
        text-align: right;
    }

    /deep/ .el-col {
        // display: flex;
        // justify-content: center;
    }
}
</style>
