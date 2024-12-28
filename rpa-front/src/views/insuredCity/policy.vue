<template>
    <div class="spl-container">
        <!-- 申报政策 -->
        <breadcrumb :data="pathData"></breadcrumb>
        <div style="padding: 20px" v-loading="isLoading" element-loading-text="拼命加载中"
            element-loading-spinner="el-icon-loading">
            <palTab :tabs="tabs" v-model="tabActive" :type="2" @change="changeTab"></palTab>

            <!-- 社保面板 -->
            <div v-show="businessType == 1">
                <CommonHeader header="网站申报期"></CommonHeader>
                <p class="tips">用于每月参保数据提交截止做参考。通常该时间设置会比网站实际申报截止早一点，为申报操作预留充足时间</p>
                <!-- 网站申报 - 选择组 -->
                <div class="select-group mt-10 ml-10 mr-10 mb-10">
                    <SelectGroupTemplate :ref="`policy-${item.declareType}`"
                        :label="getDeclarationEnumValue(item.declareType)"
                        v-for="item in policyData.policyAddrDeadlineSettingList" :key="item.id" :sourceItem="item"
                        @onStartChange="onStartGroupChange" @onEndChange="onEndGroupChange"
                        @onWeekendDayBlur="onWeekendDayBlur" />
                    <div class="display-flex select-item">
                        <p class="title">年度调基月:</p>
                        <el-select v-model="policyData.policyAddrDeclareSetting.transferBaseMonth" placeholder="请选择"
                            class="ml-10">
                            <el-option v-for="item in monthOptions" :key="item.value" :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                    </div>
                    <div class="display-flex select-item mt-10 ">
                        <p class="title mr-10">备注:</p>
                        <el-input v-model="policyData.policyAddrDeclareSetting.comment" placeholder="请输入备注"
                            style="width: 60%;" type="textarea" :rows="3"></el-input>
                    </div>
                </div>
                <CommonHeader header="缴费制单计划"></CommonHeader>
                <div class="select-group mt-10 ml-10 mr-10 mb-10">
                    <SelectGroupTemplate :ref="`policy-${item.systemType}`" :label="getSystemEnumValue(item.systemType)"
                        v-for="item in policyData.policyAddrCostSettingList" :key="item.id" :sourceItem="item"
                        @onStartChange="onStartGroupPlanChange" @onEndChange="onEndGroupPlanChange"
                        @onWeekendDayBlur="onWeekendDayBlurPlan" />

                </div>
                <CommonHeader header="网报政策"></CommonHeader>
                <div class="display-flex layout-top  mt-10 ml-10 mr-10 mb-10">
                    <p class="mr-10  required-pre">是否允许同月增减</p>
                    <el-radio-group v-model="policyData.policyAddrDeclareSetting.isMonthIncreaseDecrease">
                        <el-radio :label="1">是</el-radio>
                        <el-radio :label="0">否</el-radio>
                    </el-radio-group>
                    <p class="ml-10">（则同一参保月份允许反复增减）</p>
                </div>
                <!-- 增员 -->
                <div class="add-layout">
                    <div class="title-left">增员</div>
                    <div class="container">
                        <div class="display-flex layout-top  mt-10 ml-10 mr-10 mb-10">
                            <p class="mr-10 required-pre">增补合并申报：</p>
                            <el-radio-group v-model="policyData.policyAddrDeclareSetting.addMergeDeclare">
                                <el-radio :label="1">是</el-radio>
                                <el-radio :label="0">否</el-radio>
                            </el-radio-group>
                            <p class="ml-10">（网站无单独补缴入口，选是；若网站增补申报结果分开，选否）</p>
                        </div>
                        <div class="mt-10 ml-120 mr-10 mb-10" v-if="tplItems[businessType] && tplItems[businessType].tplItems.length > 0">
                          <p v-for="(item, index) in tplItems[businessType].tplItems">
                            <span v-text="'(' + item.tplTypeName + ')'" style="display: inline-block;width: 100px;"></span>
                            <span>增补合并申报：</span>
                            <el-radio-group v-model="item.merge">
                              <el-radio :label="true" disabled>是</el-radio>
                              <el-radio :label="false" disabled>否</el-radio>
                            </el-radio-group>
                            <!--<span class="pl-20" v-text="item.merge?'是':'否'"></span>-->
                          </p>
                        </div>
                        <div class="display-flex select-item">
                            <p class="title required-pre">可往前参保时间:</p>
                            <el-select v-model="policyData.policyAddrDeclareSetting.agoInsuredMonthType" placeholder="请选择"
                                class="ml-10">
                                <el-option v-for="item in insurancePeriodOptions" :key="item.id" :label="item.dictName"
                                    :value="item.dictCode">
                                </el-option>
                            </el-select>
                            <p class="ml-10">（仅针对网报数据限制）</p>
                        </div>

                    </div>
                </div>
                <!-- 保存按钮 -->
                <div class="button-group">
                    <el-button type="primary" :loading="socialLoading" class="save" @click="saveSocial">保存社保</el-button>
                </div>

            </div>

            <!-- 公积金面板 -->
            <div v-show="businessType == 2">
                <CommonHeader header="网站申报期"></CommonHeader>
                <p class="tips">用于每月参保数据提交截止做参考。通常该时间设置会比网站实际申报截止早一点，为申报操作预留充足时间</p>
                <!-- 网站申报 - 选择组 -->
                <div class="select-group mt-10 ml-10 mr-10 mb-10">
                    <SelectGroupTemplate :ref="`fund-${item.declareType}`"
                        :label="getDeclarationEnumValue(item.declareType)"
                        v-for="item in fundData.policyAddrDeadlineSettingList" :key="item.id" :sourceItem="item"
                        @onStartChange="onStartGroupChange" @onEndChange="onEndGroupChange"
                        @onWeekendDayBlur="onWeekendDayBlur" />
                    <div class="display-flex select-item">
                        <p class="title">年度调基月:</p>
                        <el-select v-model="fundData.policyAddrDeclareSetting.transferBaseMonth" placeholder="请选择"
                            class="ml-10">
                            <el-option v-for="item in monthOptions" :key="item.value" :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                    </div>
                    <div class="display-flex select-item mt-10 ">
                        <p class="title mr-10">备注:</p>
                        <el-input v-model="fundData.policyAddrDeclareSetting.comment" placeholder="请输入备注"
                            style="width: 60%;" type="textarea" :rows="3"></el-input>
                    </div>
                </div>
                <CommonHeader header="缴费制单计划"></CommonHeader>
                <div class="select-group mt-10 ml-10 mr-10 mb-10">
                    <SelectGroupTemplate :ref="`fund-${item.systemType}`" label="公积金"
                        v-for="item in fundData.policyAddrCostSettingList" :key="item.id" :sourceItem="item"
                        @onStartChange="onStartGroupPlanChange" @onEndChange="onEndGroupPlanChange"
                        @onWeekendDayBlur="onWeekendDayBlurPlan" />
                </div>
                <CommonHeader header="网报政策"></CommonHeader>

                <div class="display-flex layout-top  mt-10 ml-10 mr-10 mb-10">
                    <p class="mr-10  required-pre">是否允许同月增减</p>

                    <el-radio-group v-model="fundData.policyAddrDeclareSetting.isMonthIncreaseDecrease">
                        <el-radio :label="1">是</el-radio>
                        <el-radio :label="0">否</el-radio>
                    </el-radio-group>


                    <p class="ml-10">（则同一参保月份允许反复增减）</p>
                </div>
                <!-- 增员 -->
                <div class="add-layout">
                    <div class="title-left">增员</div>
                    <div class="container">
                        <div class="display-flex layout-top  mt-10 ml-10 mr-10 mb-10">
                            <p class="mr-10 required-pre">增补合并申报：</p>
                            <el-radio-group v-model="fundData.policyAddrDeclareSetting.addMergeDeclare">
                                <el-radio :label="1">是</el-radio>
                                <el-radio :label="0">否</el-radio>
                            </el-radio-group>
                            <p class="ml-10">（网站无单独补缴入口，选是；若网站增补申报结果分开，选否）</p>
                        </div>

                        <div class="mt-10 ml-120 mr-10 mb-10" v-if="tplItems[businessType] && tplItems[businessType].tplItems.length > 0">
                          <p v-for="(item, index) in tplItems[businessType].tplItems">
                            <span v-text="'(' + item.tplTypeName + ')'" style="display: inline-block;width: 100px;"></span>
                            <span>增补合并申报：</span>
                            <el-radio-group v-model="item.merge">
                              <el-radio :label="true" disabled>是</el-radio>
                              <el-radio :label="false" disabled>否</el-radio>
                            </el-radio-group>
                            <!--<span class="pl-20" v-text="item.merge?'是':'否'"></span>-->
                          </p>
                        </div>

                        <div class="display-flex select-item">
                            <p class="title required-pre">可往前参保时间:</p>
                            <el-select v-model="fundData.policyAddrDeclareSetting.agoInsuredMonthType" placeholder="请选择"
                                class="ml-10">
                                <el-option v-for=" item  in  insurancePeriodOptions " :key="item.id" :label="item.dictName"
                                    :value="item.dictCode">
                                </el-option>
                            </el-select>
                            <p class="ml-10">（仅针对网报数据限制）</p>
                        </div>

                    </div>
                </div>

                <!-- 保存按钮 -->
                <div class="button-group">
                    <el-button type="primary" class="save" :loading="fundLoading" @click="saveFund">保存公积金</el-button>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import palTab from '../../components/common/pal-tab'
import CommonHeader from './components/CommonHeader'
import SelectGroupTemplate from './components/SelectGroupTemplate'
import PaymentTemplate from './components/PaymentTemplate'
import _ from 'lodash'
export default {
    name: '',
    components: { palTab, CommonHeader, SelectGroupTemplate, PaymentTemplate },
    props: [''],
    data() {
        return {
            isLoading: false,
            tabs: ['社保', '公积金'],
            businessType: '1',
            tabActive: 0,
            pathData: [{ name: '参保城市' }, { name: '申报政策' }],
            monthOptions: [
                {
                    value: 1,
                    label: '1月'
                },
                {
                    value: 2,
                    label: '2月'
                },
                {
                    value: 3,
                    label: '3月'
                },
                {
                    value: 4,
                    label: '4月'
                },
                {
                    value: 5,
                    label: '5月'
                },
                {
                    value: 6,
                    label: '6月'
                },
                {
                    value: 7,
                    label: '7月'
                },
                {
                    value: 8,
                    label: '8月'
                },
                {
                    value: 9,
                    label: '9月'
                },
                {
                    value: 10,
                    label: '10月'
                },
                {
                    value: 11,
                    label: '11月'
                },
                {
                    value: 12,
                    label: '12月'
                },
            ],
            insurancePeriodOptions: [], //可往前申报时间下拉值
            policyData: {   //社保data
                policyAddrDeadlineSettingList: [],
                policyAddrDeclareSetting: {
                    transferBaseMonth: '',
                    agoInsuredMonthType: undefined,
                    isMonthIncreaseDecrease: undefined,
                    addMergeDeclare: undefined
                }
            },
            fundData: { //公积金data
                policyAddrDeadlineSettingList: [],
                policyAddrDeclareSetting: {
                    transferBaseMonth: '',
                    agoInsuredMonthType: undefined,
                    isMonthIncreaseDecrease: undefined,
                    addMergeDeclare: undefined
                }
            },
            policySource: {      //社保data source

            },
            fundSource: {        //公积金data source

            },
            isFirstLoad: true,       //当前是第一次切换加载
            fundLoading: false,          //当前正在保存公积金
            socialLoading: false,          //当前正在保存社保
            defaultSocialCostItem: {            //当前城市社保申报item 用于查询时返回时空的情况 根据接口自己生成
                "addrId": 321400,
                "addrName": "昆山",
                "businessType": 1,
                "systemType": "10007001",
                "monthBegin": 0,
                "dayBegin": 1,
                "hourBegin": 0,
                "monthEnd": 0,
                "dayEnd": 1,
                "hourEnd": 0,
                "isWeekend": null,
                "isWeekendDay": null
            },
            tplItems: {
              "1":null,
              "2":null
            }
        }
    },
    computed: {
        tableHeight: function () {
            return this.$global.bodyHeight - 360 + 'px'
        },
        declareTimeStr() {
            return `待申报（>${this.declareTime}小时）人数`
        },
        declareTime2Str() {
            return `申报中（>${this.declareTime2}小时）人数`
        },

    },
    watch: {},
    created() {
        if (this.$route.query.addrName) {
            this.pathData[1].name = `申报政策（${this.$route.query.addrName}）`
        }
        this.getInsurancePeriod()
        this.queryPolicy()
        this.findAddrTplItems()
        // this.getCitySocialSystem()
    },
    beforeMount() { },
    mounted() { },
    methods: {
        // 改变tab
        changeTab(index) {
            this.businessType = parseInt(index + 1)
            console.log('changeTab', this.businessType)
            if (this.businessType == 2 && this.isFirstLoad && !this.isLoading) {
                this.queryPolicy()      //设置只有一次切换到'公积金'，才请求
                this.findAddrTplItems()
                this.isFirstLoad = false
            }
        },
        //报盘文件设置-获取关联标记信息
        findAddrTplItems(){
          let taht = this
          let addrId = this.$route.query.addrId
          let bussinssType = this.businessType
          return this.$http({
            url:`policy/offerSettings/findAddrTplItems/${addrId}/${bussinssType}`,
            method:'post',
          }).then(({data})=>{
            if(data.data){
              taht.tplItems[bussinssType] = data.data
            }
          }).catch(()=>{
          })
        },
        //保存社保
        saveSocial() {
            let returnData = this.putDataIntoJson(this.policyData, 1)
            console.log('看看社保', returnData)
            //校验社保
            if (this.checkRequiredItem(returnData)) {
                this.socialLoading = true
                this.updatePolicy(returnData, 1)
            }
        },
        //保存公积金
        saveFund() {
            let returnData = this.putDataIntoJson(this.fundData, 2)
            console.log('看看公积金', this.fundData)
            //校验公积金
            if (this.checkRequiredItem(returnData)) {
                this.fundLoading = true
                this.updatePolicy(returnData, 2)
            }

        },
        //将数据项设置到数据体内
        putDataIntoJson(data, type) {
            let returnData = _.cloneDeep(data)
            for (const item of returnData.policyAddrDeadlineSettingList) {
                item.monthBegin = item.defaultStartValue[0]
                item.dayBegin = item.defaultStartValue[1]
                item.hourBegin = item.defaultStartValue[2]

                item.monthEnd = item.defaultEndValue[0]
                item.dayEnd = item.defaultEndValue[1]
                item.hourEnd = item.defaultEndValue[2]

                item.addrId = Number(this.$route.query.addrId),
                    item.addrName = this.$route.query.cityName,
                    item.businessType = type
            }

            for (const item of returnData.policyAddrCostSettingList) {
                item.monthBegin = item.defaultStartValue[0]
                item.dayBegin = item.defaultStartValue[1]
                item.hourBegin = item.defaultStartValue[2]

                item.monthEnd = item.defaultEndValue[0]
                item.dayEnd = item.defaultEndValue[1]
                item.hourEnd = item.defaultEndValue[2]

                item.addrId = Number(this.$route.query.addrId),
                    item.addrName = this.$route.query.cityName,
                    item.businessType = type
            }
            //多复制一条调基的item ， 内容与增员一样
            // const sourceData = type === 1 ? this.policySource.policyAddrDeadlineSettingList : this.fundSource.policyAddrDeadlineSettingList
            // const copyItem = returnData.policyAddrDeadlineSettingList.find(item => item.declareType === 1)
            // returnData.policyAddrDeadlineSettingList.push({ ...copyItem, declareType: 3, id: sourceData.find(item => item.declareType === 3).id })
            return returnData
        },
        //检查必填项
        checkRequiredItem(data) {
            if (!data.policyAddrDeclareSetting.isMonthIncreaseDecrease && data.policyAddrDeclareSetting.isMonthIncreaseDecrease !== 0) {
                this.$message.warning('请先完善是否允许同月增减！')
                return false
            }
            if (!data.policyAddrDeclareSetting.addMergeDeclare && data.policyAddrDeclareSetting.addMergeDeclare !== 0) {
                this.$message.warning('请先完善增补合并申报！')
                return false
            }
            if (!data.policyAddrDeclareSetting.agoInsuredMonthType && data.policyAddrDeclareSetting.agoInsuredMonthType !== '') {
                this.$message.warning('请先完善可往前参保时间！')
                return false
            }
            return true
        },
        //新增、更新申报政策
        async updatePolicy(jsonData, type) {
            const { data: { code, data: result } } = await this.$http({
                url: '/policy/declareAddr/declarePolicyInsertOrUpdate',
                method: 'post',
                data: {
                    ...jsonData,
                    addrId: this.$route.query.addrId,
                    addrName: this.$route.query.addrName,
                    businessType: type
                }
            })
            if (code === 200) {
                if (type === 1) {
                    //社保保存
                    this.$message.success('保存社保政策信息成功')
                    this.socialLoading = false
                } else {
                    //公积金保存
                    this.$message.success('保存公积金政策信息成功')
                    this.fundLoading = false
                }
                this.queryPolicy()
            }
        },
        //查询申报政策
        async queryPolicy() {
            this.isLoading = true
            const { data: { code, data: result } } = await this.$http({
                url: '/policy/declareAddr/declarePolicyQuery',
                method: 'post',
                data: {
                    addrId: Number(this.$route.query.addrId),
                    businessType: Number(this.businessType)
                }
            })
            if (code === 200) {
                if (result.policyAddrDeadlineSettingList.length > 0) {
                    let array = [...result.policyAddrDeadlineSettingList]; // 创建数组的副本，避免直接修改原始数组
                    //交换后两个元素的位置
                    [array[array.length - 2], array[array.length - 1]] = [array[array.length - 1], array[array.length - 2]];
                    // 更新 原始数据
                    result.policyAddrDeadlineSettingList = array;

                    //为网站申报期里面设置默认值
                    result.policyAddrDeadlineSettingList = result.policyAddrDeadlineSettingList.map(item => {
                        return {
                            ...item,
                            defaultStartValue: [item.monthBegin || 0, item.dayBegin || 1, item.hourBegin || 0],
                            defaultEndValue: [item.monthEnd || 0, item.dayEnd || 1, item.hourEnd || 0],
                        }
                    })

                    //为缴费制单计划里面设置默认值
                    result.policyAddrCostSettingList = result.policyAddrCostSettingList.map(item => {
                        return {
                            ...item,
                            defaultStartValue: [item.monthBegin || 0, item.dayBegin || 1, item.hourBegin || 0],
                            defaultEndValue: [item.monthEnd || 0, item.dayEnd || 1, item.hourEnd || 0],
                        }
                    })
                }
                if (!result.policyAddrDeclareSetting) {
                    result.policyAddrDeclareSetting = {
                        transferBaseMonth: ''
                    }
                }

                //缴费制单计划 社保/公积金系统如果返回的的是空 需要请求该城市的社保申报系统 获取对应城市下有多少条记录 对应生成才行
                if (result.policyAddrCostSettingList.length === 0) {
                    this.getCitySystem(result)
                } else {

                    if (this.businessType == 1 && result.policyAddrDeadlineSettingList.length > 0) {
                        //将data设置到社保数据data中
                        this.policySource = result
                        const filterList = result.policyAddrDeadlineSettingList.filter(sitem =>
                            sitem.declareType != 3
                        )
                        // this.policyData = { ...result, policyAddrDeadlineSettingList: filterList }       //过滤掉一条 调基
                        this.policyData = result
                        console.log('申报政策data-----policyData----->', this.policyData)
                    } else if (this.businessType == 2 && result.policyAddrDeadlineSettingList.length > 0) {
                        //将data设置到公积金数据data中
                        this.fundSource = result
                        const filterList = result.policyAddrDeadlineSettingList.filter(sitem =>
                            sitem.declareType != 3
                        )
                        // this.fundData = { ...result, policyAddrDeadlineSettingList: filterList }         //过滤掉一条调基
                        this.fundData = result
                        console.log('看看fundData', this.fundData)
                    }
                }
                //将源头保留一份
                console.log('申报政策data---------->', result)


                setTimeout(() => {
                    this.isLoading = false
                }, 500);
            }
        },
        //获取可往前参保时间
        async getInsurancePeriod() {
            const { data: { code, data: result } } = await this.$http({
                url: '/policy/sys/dict/getDictByKey?dataKey=10020',
                method: 'get',
            })
            if (code === 200) {
                console.log('获取字典值--- 可往前参保时间：', code, result)
                this.insurancePeriodOptions = result
            }

        },
        //获取对应的申报类型枚举值
        getDeclarationEnumValue(declareType) {
            const DeclarationEnum = Object.freeze({
                ADD_MEMBER: 1,          //增员
                REMOVE_MEMBER: 2,          //减员
                ADJUST_SALARY: 3,           //调基
                COMPLEX: 4,         //4为复合类型 寓意为增员/调基的复合 无论是增员还是调基现在都是等价的 因此1/3/4为等价的枚举值
                MAKE_UP_PAYMENT: 5          //补缴
            })
            switch (declareType) {
                case DeclarationEnum.ADD_MEMBER:
                    return '增员'
                case DeclarationEnum.REMOVE_MEMBER:
                    return '减员'
                case DeclarationEnum.ADJUST_SALARY:
                    return '调基'
                case DeclarationEnum.MAKE_UP_PAYMENT:
                    return '补缴'
                case DeclarationEnum.COMPLEX:
                    return '增员/调基'
                default:
                    break;
            }
        },
        //获取社保缴费系统类型枚举值
        getSystemEnumValue(systemType) {
            switch (systemType) {
                case "10007001":
                    return '社保系统'
                case "10007002":
                    return '养老系统'
                case "10007003":
                    return '医疗系统'
                case "10007004":
                    return '单工伤'
                case "10007005":
                    return '工伤系统'
                default:
                    break;
            }
        },
        // ----------------------------------------- 网站申报期 Start ----------------------------------------------
        //开始组选择进行了变更，进行回调
        onStartGroupChange(returnItem) {
            const data = returnItem.businessType == 1 ? this.policyData : this.fundData
            const preChangeIndex = data.policyAddrDeadlineSettingList.findIndex(item => item.id === returnItem.id)
            if (preChangeIndex > -1) {
                //找到了需要修改的item
                const preChangeItem = data.policyAddrDeadlineSettingList[preChangeIndex]
                this.$set(preChangeItem, 'monthBegin', returnItem.monthBegin)
                this.$set(preChangeItem, 'dayBegin', returnItem.dayBegin)
                this.$set(preChangeItem, 'hourBegin', returnItem.hourBegin)
                // console.log('开始组发生变更:', data.policyAddrDeadlineSettingList)
            }
        },
        //结束组选择进行了变更，进行回调
        onEndGroupChange(returnItem) {
            const data = returnItem.businessType == 1 ? this.policyData : this.fundData
            const preChangeIndex = data.policyAddrDeadlineSettingList.findIndex(item => item.id === returnItem.id)
            if (preChangeIndex > -1) {
                //找到了需要修改的item
                const preChangeItem = data.policyAddrDeadlineSettingList[preChangeIndex]
                this.$set(preChangeItem, 'monthEnd', returnItem.monthEnd)
                this.$set(preChangeItem, 'dayEnd', returnItem.dayEnd)
                this.$set(preChangeItem, 'hourEnd', returnItem.hourEnd)
                // console.log('结束组发生变更:', data.policyAddrDeadlineSettingList)
            }
        },
        //延后天数 输入失去焦点时 需要将里面的值转换为数字而不是字符串
        onWeekendDayBlur(returnItem) {
            const data = returnItem.businessType == 1 ? this.policyData : this.fundData
            const preChangeIndex = data.policyAddrDeadlineSettingList.findIndex(item => item.id === returnItem.id)
            if (preChangeIndex > -1) {
                const preChangeItem = data.policyAddrDeadlineSettingList[preChangeIndex]
                this.$set(preChangeItem, 'isWeekendDay', returnItem.isWeekendDay ? parseInt(returnItem.isWeekendDay) : '')
            }
        },
        // ----------------------------------------- 网站申报期 End ----------------------------------------------
        // ----------------------------------------- 缴费制单计划 Start ----------------------------------------------
        //开始组选择进行了变更，进行回调
        onStartGroupPlanChange(returnItem) {
            const data = returnItem.businessType == 1 ? this.policyData : this.fundData
            // console.log('onStartGroupPlanChange', data)
            if(!data.policyAddrCostSettingList)return
            const preChangeIndex = data.policyAddrCostSettingList.findIndex(item => item.id === returnItem.id)
            if (preChangeIndex > -1) {
                //找到了需要修改的item
                const preChangeItem = data.policyAddrCostSettingList[preChangeIndex]
                this.$set(preChangeItem, 'monthBegin', returnItem.monthBegin)
                this.$set(preChangeItem, 'dayBegin', returnItem.dayBegin)
                this.$set(preChangeItem, 'hourBegin', returnItem.hourBegin)
                // console.log('开始组发生变更:', data.policyAddrDeadlineSettingList)
            }
        },
        //结束组选择进行了变更，进行回调
        onEndGroupPlanChange(returnItem) {
            const data = returnItem.businessType == 1 ? this.policyData : this.fundData
            // console.log('onEndGroupPlanChange', data)
            if(!data.policyAddrCostSettingList)return
            const preChangeIndex = data.policyAddrCostSettingList.findIndex(item => item.id === returnItem.id)
            if (preChangeIndex > -1) {
                //找到了需要修改的item
                const preChangeItem = data.policyAddrCostSettingList[preChangeIndex]
                this.$set(preChangeItem, 'monthEnd', returnItem.monthEnd)
                this.$set(preChangeItem, 'dayEnd', returnItem.dayEnd)
                this.$set(preChangeItem, 'hourEnd', returnItem.hourEnd)
                // console.log('结束组发生变更:', data.policyAddrDeadlineSettingList)
            }
        },
        //延后天数 输入失去焦点时 需要将里面的值转换为数字而不是字符串
        onWeekendDayBlurPlan(returnItem) {
            const data = returnItem.businessType == 1 ? this.policyData : this.fundData
            // console.log('onWeekendDayBlurPlan', data)
            if(!data.policyAddrCostSettingList)return
            const preChangeIndex = data.policyAddrCostSettingList.findIndex(item => item.id === returnItem.id)
            if (preChangeIndex > -1) {
                const preChangeItem = data.policyAddrCostSettingList[preChangeIndex]
                this.$set(preChangeItem, 'isWeekendDay', returnItem.isWeekendDay ? parseInt(returnItem.isWeekendDay) : '')
            }
        },
        // ----------------------------------------- 缴费制单计划 End ----------------------------------------------
        //如果是新增城市 缴费制单计划没有返回对应社保/公积金 就需要请求接口获取该城市所存在的申报系统 然后对应设置
        async getCitySystem(tempResult) {
            const { data: { code, data: result } } = await this.$http({
                url: `policy/offerSettings/findAddrTplItems/${this.$route.query.addrId}/${this.businessType}`,
                method: 'post'
            })
            if (code === 200) {
                if (result) {
                    let tempSocialArray = []
                    for (const item of result.tplItems) {
                        tempSocialArray.push({
                            ...this.defaultSocialCostItem,
                            addrId: result.addrId,
                            addrName: result.addrName,
                            businessType: this.bussinessType,
                            systemType: item.tplType,
                            defaultStartValue: [this.defaultSocialCostItem.monthBegin || 0, this.defaultSocialCostItem.dayBegin || 1, this.defaultSocialCostItem.hourBegin || 0],
                            defaultEndValue: [this.defaultSocialCostItem.monthEnd || 0, this.defaultSocialCostItem.dayEnd || 1, this.defaultSocialCostItem.hourEnd || 0],
                        })
                    }
                    tempResult.policyAddrCostSettingList = tempSocialArray
                }

                if (this.businessType == 1) {
                    //将data设置到社保数据data中
                    this.policySource = tempResult
                    this.policyData = tempResult
                    console.log('getCitySystem', this.policyData)

                } else if (this.businessType == 2) {
                    //将data设置到公积金数据data中
                    this.fundSource = tempResult
                    this.fundData = tempResult

                }
            }

        }
    },
}
</script>
<style lang="less" scoped>
.tips {
    padding-left: 10px;
}

.select-group {
    padding: 20px 16px;
    border: 1px solid #e4e4e4;
    border-radius: 3px;

    .select-item {
        align-items: center;

        .title {
            line-height: 30px;
            min-width: 100px;
            text-align: right;
        }
    }



    .my-select {
        width: 100px;
    }
}

.layout-top {
    align-items: center;
}

//增员布局
.add-layout {
    display: flex;
    border: 1px solid #e4e4e4;
    margin: 10px;

    .title-left {
        width: 100px;
        /*height: 100%;*/
        background-color: #f2f2f2;
        text-align: center;
        display: flex;
        justify-content: center;
        align-items: center;
        /*line-height: 100px;*/
    }

    .container {
        flex: 1;
        margin: 10px;
        align-items: center;


        .select-item {
            align-items: center;

            .title {
                line-height: 30px;
                min-width: 100px;
                text-align: right;
            }
        }
    }
}

.button-group {
    display: flex;
    justify-content: center;
    margin-top: 20px;
}
</style>
