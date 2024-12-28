<template>
    <div class="search-row clearfix">
        <el-row>
            <el-col :span="8" class="display-flex">
                <span class="label">业务类型：</span>
                <el-radio-group v-model="searchForms.businessType" size="medium" @change="onBusinessTypeChange">
                    <el-radio-button v-for="item in searchOptions.businessTypes" :key="item.id" :label="item.id"> {{
                        item.name }}</el-radio-button>
                </el-radio-group>
            </el-col>
            <el-col :span="8" class="display-flex">
                <span class="label my-required">参保城市：</span>
                <addrSelector v-model="searchForms.addrName" :addrArr="searchOptions.addrArr"
                    @changeAddrSelect="getSocialAddrId" width="100%" class="w-300"></addrSelector>
            </el-col>

            <el-col :span="8" class="display-flex">
                <span class="label">申报账户：</span>
                <el-select v-model="searchForms.selectItem" class="search-row-item w-300" value-key="itemId" collapse-tags clearable
                    filterable @change="onAccountNumberChange">
                    <el-option v-for="it in searchOptions.accountNumberArr" :key="it.itemId" :label="it.name"
                        :value="it"></el-option>
                </el-select>
            </el-col>
        </el-row>
        <el-row class="mt-15">
            <el-col :span="8" class="display-flex">
                <span class="label">申报类型：</span>
                <el-checkbox-group v-model="searchForms.declareTypes" size="medium" @change="testChange">
                    <el-checkbox-button v-for="item in searchOptions.declareTypes" :key="item.id" :label="item.id">
                        {{ item.name }}
                    </el-checkbox-button>
                </el-checkbox-group>
            </el-col>
            <el-col :span="8" class="display-flex" v-if="type === 1">
                <span class="label">创建时间：</span>
                <div style="display:flex;max-width:300px">
                    <el-date-picker v-model="searchForms.insuredStartDate" value-format="yyyy-MM-dd" style="width:50%;"
                        placeholder="选择时间" clearable></el-date-picker>
                    <span class="lh-com" style="padding: 0 5px;">至</span>
                    <el-date-picker v-model="searchForms.insuredEndDate" value-format="yyyy-MM-dd" style="width:50%;"
                        placeholder="选择时间"  clearable></el-date-picker>
                </div>
            </el-col>
            <el-col :span="8" class="display-flex" v-else>
                <span class="label">交互时间：</span>
                <div style="display:flex;max-width:300px">
                    <el-date-picker v-model="searchForms.beginDate" value-format="yyyy-MM-dd" style="width:50%;"
                        type="month" placeholder="选择时间" clearable></el-date-picker>
                    <span class="lh-com" style="padding: 0 5px;">至</span>
                    <el-date-picker v-model="searchForms.endDate" value-format="yyyy-MM-dd" style="width:50%;"
                        placeholder="选择时间" type="month" clearable></el-date-picker>
                </div>
            </el-col>
            <el-col :span="8" class="display-flex" v-if="type === 1">
                <span class="label">关键字：</span>
                <el-input style="width: 100%;max-width: 300px;" v-model="searchForms.keyword" placeholder="输入姓名/证件号码关键字"
                    clearable></el-input>
            </el-col>
            <el-col :span="8" class="display-flex" v-else>
                <span class="label">缴纳起始月：</span>
                <el-date-picker v-model="searchForms.insuredDate" value-format="yyyy-MM" class="w-300" placeholder="选择日期"
                    clearable></el-date-picker>
            </el-col>
        </el-row>
        <el-row class="mt-15" justify="end" type="flex">
            <el-col :span="8" style="display: flex;justify-content: end;">
                <el-button type="primary" plain class="search-btn w-60 mr-10" @click="resetTable">重置</el-button>
                <el-button type="primary" class="search-btn w-60 mr-100" @click="queryTable">查询</el-button>
            </el-col>

        </el-row>
    </div>
</template>
<script>
import addrSelector from '@/components/common/addrSelector'
export default {
    name: 'SearchLayout',
    components: { addrSelector },
    props: {
        //layout表单的类型 1为默认（'待处理','待提交','待申报','申报失败','已处理'）2为 '校验失败'
        type: {
            type: Number,
            default: 1
        },
    },
    data() {
        return {
            searchOptions: {
                businessTypes: [
                    {
                        name: '社保',
                        id: 1
                    },
                    {
                        name: '公积金',
                        id: 2
                    }
                ],
                addrArr: [],            //参保城市
                declareTypes: [
                    {
                        name: '增员',
                        id: 1
                    },
                    {
                        name: '减员',
                        id: 2
                    },
                    {
                        name: '调基',
                        id: 3
                    },
                    {
                        name: '补缴',
                        id: 5
                    }
                ],
                accountNumberArr: []
            },
            searchForms:
            {
                selectItem:{},
                businessType: 1,           //业务类型 默认为社保
                company:null,               //申报账户的公司
                changeType: [],            //申报类型
                addrName: null,             //参保城市
                addrId: null,                //城市id
                accountNumber: null,              //申报账户
                insuredStartDate: null,      //开始创建时间
                insuredEndDate: null,        //结束创建时间
                keyword: null,              //关键字
                beginDate: null,       //开始交互时间
                endDate: null,          //开始交互时间
                insuredDate: null,       //缴纳起始月
                declareTypes: [],             //校验失败时所需申报类型
                validateStauts: [0]          //校验失败的 校验结果
            }

        };
    },
    computed: {},
    watch: {},
    created() {
        this.getAddrArr()
        this.getCompanyList(1)
    },
    beforeMount() { },
    mounted() { },
    methods: {
        // 获取参保地
        getAddrArr() {
            this.$http({
                url: '/policy/declareAddr/addrList',
                method: 'post',
            })
                .then(({ data }) => {
                    this.searchOptions.addrArr = data.data
                })
                .catch((data) => { })
        },
        // 获取参保主体
        getCompanyList(type) {
            let that = this, addressId = ''
            if (type == 1) {
                addressId = that.searchForms.addrId
            } else {
                addressId = that.searchForms.addrId
            }
            this.$http({
                url: '/policy/declareAccount/seeMainBody',
                method: 'post',
                params: {
                    bussinessType: type,
                    custLimit: 0,
                    addressId: addressId
                }
            }).then(({ data }) => {
                // if (type == 1) {
                //     that.socialSearch.accountNumberArr = data.data
                //     that.socialSearch.accountNumbers = []
                // } else {
                //     that.accfundSearch.accountNumberArr = data.data
                //     that.accfundSearch.accountNumbers = []
                // }
                that.searchOptions.accountNumberArr = data.data
                that.searchForms.accountNumber = null
            }).catch((data) => {
            })
        },
        // 改变社保-参保地
        getSocialAddrId(item) {
            if (this.searchForms.addrId == item.id) {
                return false
            }
            this.searchForms.addrId = item.id
            this.getCompanyList(1)
        },
        //申报账户 选择回调
        onAccountNumberChange(e){
            this.searchForms.company =  e.id
            this.searchForms.accountNumber = e.accountNumber
            console.log('onAccountNumberChange', e )
        },
        testChange(val) {
            console.log(val)
        },
        //业务类型变更
        onBusinessTypeChange(e){
            //需要重新获取申报账户
            console.log('onBusinessTypeChange',e)
            this.getCompanyList(e)
            this.$emit('change-tab',e)
        },
        //重置表单
        resetTable() {
            this.searchForms = {
                businessType: 1,           //业务类型 默认为社保
                company:null,               //申报账户的公司
                changeType: [],            //申报类型
                addrName: null,             //参保城市
                addrId: null,                //城市id
                accountNumber: null,              //申报账户
                insuredStartDate: null,      //开始创建时间
                insuredEndDate: null,        //结束创建时间
                keyword: null,              //关键字
                beginDate: null,       //开始交互时间
                endDate: null,          //开始交互时间
                insuredDate: null,       //缴纳起始月
                declareTypes: [],             //校验失败时所需申报类型
                validateStauts: [0]          //校验失败的 校验结果
            }
            this.queryTable
        },
        //查询表单
        queryTable() {
            this.$emit('query',this.searchForms)
        }
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