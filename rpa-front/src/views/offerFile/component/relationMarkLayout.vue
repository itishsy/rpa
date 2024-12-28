<!-- 关联标记维护 布局组件 -->
<template>
    <div id="upload-dialog">
        <div class="file-content-box">
            <div style="display:flex;font-size:14px;padding-bottom:20px;" v-if="showTopRow">
                <p style="width:50%;"><span style="color:red;">*</span>参保城市：{{ this.rowData.addrName }}</p>
                <p style="width:50%;"><span style="color:red;">*</span>业务类型：{{ this.rowData.bussinssType == 1 ? '社保' :
                    "公积金" }}</p>
            </div>
            <div class="title-top">完善关联标记</div>
            <el-form :model="relationData" :rules="rules" ref="relationForm" label-width="100px" class="demo-ruleForm">
                <el-table :data="relationData.tplItems" border header-cell-class-name="table-header" style="width: 100%"
                    ref="table2">
                    <el-table-column label="关联标记" width="200" align="center" prop="date">
                        <template slot="header">
                            <div>
                                <p>
                                    <span style="color:red;">*</span>关联标记
                                </p>
                            </div>
                        </template>
                        <template scope="scope">
                            <el-form-item label label-width="0" :prop="'tplItems.' + scope.$index + '.tplType'"
                                :rules="rules.relationData.tplType">
                                <el-select v-model="scope.row.tplType" placeholder="请选择" style="width:100%;"
                                    @change="changeTplType($event, scope.$index)">
                                    <el-option v-for="(item, index) in relationData.options[rowData.bussinssType]"
                                        :key="index" :label="item.dictName" :value="item.dictCode">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column label="申报险种" align="center" prop="date">
                        <template slot="header">
                            <div>
                                <p>
                                    <span style="color:red;">*</span>申报险种
                                </p>
                            </div>
                        </template>
                        <template scope="scope">
                            <el-form-item label label-width="0" :prop="'tplItems.' + scope.$index + '.items'"
                                :rules="rules.relationData.items">
                                <el-select v-model="scope.row.items" placeholder="请选择" style="width:100%;" multiple
                                    value-key="itemCode" :disabled="scope.row.disabled" ref="elSelect">
                                    <el-option v-for="(item, index) in relationData[rowData.bussinssType]" :key="index"
                                        :label="item.itemName" :value="item">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column label="增补合并申报" align="center" prop="date" width="400" v-if="showComplex">
                        <template slot="header">
                            <div>
                                <p>
                                    <span style="color:red;">*</span>增补合并申报
                                </p>
                            </div>
                        </template>
                        <template scope="scope">
                            <div style="text-align: left !important;padding-top: 10px !important;">
                                <el-form-item label label-width="0" :prop="'tplItems.' + scope.$index + '.merge'">
                                    <el-radio-group v-model="scope.row.merge" @input="mergeChange(scope.$index)">
                                        <el-radio :label="true">是</el-radio>
                                        <el-radio :label="false">否</el-radio>
                                    </el-radio-group>
                                </el-form-item>

                                <el-row v-if="scope.row.merge">
                                    <el-col :span="7" style="text-align: right">
                                        <label class="required">基数：</label>
                                    </el-col>
                                    <el-col :span="15">
                                        <el-form-item label label-width="0"
                                            :prop="'tplItems.' + scope.$index + '.mergeAddRule.baseType'"
                                            :rules="rules.relationData.baseType">
                                            <el-select v-model="scope.row.mergeAddRule.baseType" placeholder="请选择"
                                                style="width:100%;">
                                                <el-option label="取增员基数" :value="1"></el-option>
                                                <el-option label="取补缴基数" :value="5"></el-option>
                                            </el-select>
                                        </el-form-item>
                                    </el-col>
                                </el-row>

                                <el-row v-if="scope.row.merge">
                                    <el-col :span="7" style="text-align: right">
                                        <label class="required">形式：</label>
                                    </el-col>
                                    <el-col :span="15">
                                        <el-form-item label label-width="0"
                                            :prop="'tplItems.' + scope.$index + '.mergeAddRule.mergeType'"
                                            :rules="rules.relationData.mergeType">
                                            <el-select v-model="scope.row.mergeAddRule.mergeType" placeholder="请选择"
                                                style="width:100%;">
                                                <el-option label="参保日期合并" :value="1"></el-option>
                                                <el-option label="附加补缴日期" :value="2"></el-option>
                                            </el-select>
                                        </el-form-item>
                                    </el-col>
                                </el-row>

                                <el-row v-if="scope.row.merge && scope.row.mergeAddRule.mergeType == 2">
                                    <el-col :span="7" style="text-align: right">
                                        <label class="required">附加补缴信息：</label>
                                    </el-col>
                                    <el-col :span="15">
                                        <el-form-item label label-width="0"
                                            :prop="'tplItems.' + scope.$index + '.mergeAddRule.bjFieldRule'"
                                            :rules="rules.relationData.bjFieldRule">
                                            <el-input v-model="scope.row.mergeAddRule.bjFieldRule"></el-input>
                                        </el-form-item>
                                    </el-col>
                                </el-row>

                                <el-row v-if="scope.row.merge">
                                    <el-col :span="7" style="text-align: right">
                                        <label class="required">其他规则：</label>
                                    </el-col>
                                    <el-col :span="15">
                                        <el-form-item label label-width="0"
                                            :prop="'tplItems.' + scope.$index + '.mergeAddRule.mergeRule'"
                                            :rules="rules.relationData.mergeRule">
                                            <el-select v-model="scope.row.mergeAddRule.mergeRule" placeholder="请选择"
                                                style="width:100%;">
                                                <el-option label="增补险种不同可合并" :value="0"></el-option>
                                                <el-option label="增补险种完全一致才合并" :value="1"></el-option>
                                            </el-select>
                                        </el-form-item>
                                    </el-col>
                                </el-row>

                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" align="center" width="120" fixed="right">
                        <template scope="scope">
                            <div style="display:flex;justify-content: space-around;">
                                <span style="cursor:pointer;color:red;" v-if="relationData.tplItems.length > 1"
                                    @click="removeTplItems(scope.row, scope.$index)">移除</span>
                                <span style="cursor:pointer;color:#3e82ff" @click="addTplItems">添加</span>
                            </div>
                        </template>
                    </el-table-column>
                </el-table>
            </el-form>
            <div class="text-red mt-10">{{ relationError }}</div>
        </div>
    </div>
</template>
<script>
export default {
    name: 'relationMarkLayout',
    components: {},
    props: {
        rowData: {
            type: Object,
            default: function () {
                return {
                    addrId: '',
                    addrName:'',
                    bussinssType: '1'
                }
            }
        },
        formData: {
            type: Object,
            default: function () {
                return {
                    bussinssType: ['1', '2'],
                    status: '1',
                    addrId: '',
                    addrName: ""
                }
            }
        },
        //是否展示头部
        showTopRow: {
            type: Boolean,
            default: false
        },
        //是否显示增补合并申报
        showComplex:{
            type:Boolean,
            default:true
        },
        //双向绑定数据
        relationData: {
            type: Object,
            default: function () {
                return {
                    tplItems: [
                        {
                            items: [],
                            tplType: "",
                            tplTypeName: "",
                            disabled: false,
                            merge: false,
                            mergeAddRule: {
                                id: null,
                                addrId: null,
                                addrName: null,
                                businessType: null,
                                tplTypeCode: null,
                                baseType: null,
                                mergeType: null,
                                bjFieldRule: null,
                                mergeRule: null
                            }
                        }
                    ],
                    '1': [],
                    '2': [],
                    options: {}
                }
            }
        },
        //错误信息
        relationError:{
            type:String,
            default:''
        }

    },
    data() {
        return {
            loading: null,
            defaultRelationData: [{
                items: [],
                tplType: "",
                tplTypeName: "",
                merge: false,
                mergeAddRule: {
                    id: null,
                    addrId: null,
                    addrName: null,
                    businessType: null,
                    tplTypeCode: null,
                    baseType: null,
                    mergeType: null,
                    bjFieldRule: null,
                    mergeRule: null
                }
            }],
            rules: {
                uploadFileName: [
                    { required: true, message: '请先上传文件', trigger: 'change' },
                ],
                tplType: [
                    { required: true, message: '请选择关联标记', trigger: 'change' },
                ],
                operationType: [
                    { required: true, message: '请选择子节点', trigger: 'change' },
                ],
                relationData: {
                    tplType: [
                        { required: true, message: '请选择关联标记', trigger: 'change' },
                        {
                            required: true,
                            trigger: 'change',
                            validator: this.checkTplType,
                        },
                    ],
                    items: [
                        { type: 'array', required: true, message: '请选择申报险种', trigger: 'change' }
                    ],
                    baseType: [
                        { required: true, message: '请选择基数规则', trigger: 'change' },
                    ],
                    mergeType: [
                        { required: true, message: '请选择合并形式', trigger: 'change' },
                    ],
                    bjFieldRule: [
                        { required: true, message: '请输入附加补缴信息', trigger: 'change' },
                    ],
                    mergeRule: [
                        { required: true, message: '请选择其他规则', trigger: 'change' },
                    ]
                }
            },
        };
    },
    computed: {},
    watch: {},
    created() { },
    beforeMount() { },
    mounted() { },
    methods: {
        //改变设置关联标记
        changeTplType(val, index) {
            // this.$emit('changeTplType',val,index)
            let newData = Object.assign({}, this.relationData);
            if (val == '10007004') {
                newData.tplItems[index].items = []
                newData.tplItems[index].disabled = true
                var check = false
                //检测是否有工伤类型
                newData[this.rowData.bussinssType].forEach(item => {
                    if (item.itemCode == '10003002') {
                        check = true
                    }
                })
                if (check) {
                    newData.tplItems[index].items.push({ itemCode: "10003002", itemName: "工伤" })
                } else {
                    this.$message.error('此地区未设置工伤险种，不可设置单工伤关联标记')
                }
            } else {
                newData.tplItems[index].disabled = false
            }
            newData.tplItems[index].mergeAddRule.tplTypeCode = val
            this.$emit('update:relationData', newData);
        },
        //
        mergeChange(index) {
            let newData = Object.assign({}, this.relationData);
            // this.$emit('mergeChange',index)
            if (newData.tplItems[index].merge) {
                if (newData.tplItems[index].mergeAddRule.mergeType == null) {
                    newData.tplItems[index].mergeAddRule.mergeType = 1
                }
                if (newData.tplItems[index].mergeAddRule.baseType == null) {
                    newData.tplItems[index].mergeAddRule.baseType = 1
                }
            }
        },
        //添加关联标记
        addTplItems() {
            let newData = Object.assign({}, this.relationData);
            newData.tplItems.push(
                {
                    items: [],
                    tplType: "",
                    tplTypeName: "",
                    merge: false,
                    mergeAddRule: {
                        id: null,
                        addrId: this.rowData.addrId,
                        addrName: this.rowData.addrName,
                        businessType: this.rowData.bussinssType,
                        tplTypeCode: null,
                        baseType: null,
                        mergeType: null,
                        bjFieldRule: null,
                        mergeRule: null
                    }
                })
            this.$emit('update:relationData', newData);
        },
        //移除关联标记
        removeTplItems(row, index) {
            let newData = Object.assign({}, this.relationData);
            if (!row.tplType) {
                newData.tplItems.splice(index, 1)
                return;
            }
            this.showLoading()
            console.log(this.formData)
            this.checkSelectTplCount(this.formData.addrId, row.tplType).then(res => {
                this.closeLoading()
                if (res.data && res.data.code == 200) {
                    if (res.data.data > 0) {
                        this.$message.error('此标记已被文件关联，不得删除')
                    } else if (res.data.data <= 0) {
                        newData.tplItems.splice(index, 1)
                        this.$emit('update:relationData', newData);
                    }
                }
            }).catch(err => {
                this.closeLoading()
            })
        },
        //报盘文件设置-根据参保地id，模板类型获取关联的文件个数
        checkSelectTplCount(addrId, tplTypeCode) {
            return this.$http({
                url: `policy/offerSettings/selectTplCount/${addrId}/${tplTypeCode}`,
                method: 'post',
                headers: {
                    customSuccess: true
                }
            })
        },
        showLoading(target) {
            this.loading = this.$loading({
                target: target ? target : document.body,
                lock: true,
                text: '加载中',
                spinner: 'el-icon-loading',
                background: 'rgba(255, 255, 255, 0.7)'
            });
        },
        closeLoading() {
            if (this.loading && this.loading.close) {
                this.loading.close()
            }
        },
        checkTplType(rule, value, callback) {
            let newData = Object.assign({}, this.relationData);
            var arr = []
            newData.tplItems.forEach(item => {

                if (item.tplType == value) {
                    const optionNameFunc = this.getOptionName();        //匿名函数需要先调用
                    arr.push(optionNameFunc(value, newData.options[this.rowData.bussinssType]))
                }
            })

            if (arr.length >= 2) {
                return callback(new Error(`关联标记${arr[0]}重复添加，请更正`))
            }
            return callback()
        },
        getOptionName() {
            return function (key, option) {
                var str = ''
                option.forEach(item => {
                    if (item.dictCode == key) {
                        str = item.dictName
                    }
                })
                return str

            }
        },
    },
}
</script>
<style lang='less' scoped>
.title-top {
    font-size:14px;
    font-weight:bold;
    margin:10px 0;
}
.file-content-box {
    font-size: 12px;

    .file-list {
        display: flex;
        padding: 10px 0;
        border-bottom: 1px dashed #ddd;

        .file {
            background: #f1f8ff;
            border-radius: 10px;
            padding: 3px 10px;
            position: relative;
            cursor: pointer;
            margin-right: 15px;
            font-size: 12px;

            &:hover {
                color: #3e82ff;
                text-decoration: underline;
            }

            .file-remove {
                position: absolute;
                right: -10px;
                top: -10px;
                width: 20px;
                height: 20px;
                background: red;
                border-radius: 50%;
                display: none;

                &::before {
                    content: '';
                    display: inline-block;
                    width: 10px;
                    height: 2px;
                    background: white;
                    position: absolute;
                    top: 50%;
                    left: 50%;
                    transform: translate(-50%, -50%);
                }
            }

            &:hover .file-remove {
                display: block;
            }
        }
    }

    .upload-tips {
        margin-top: 20px;
        font-size: 12px;
        color: #797979;
    }
}


.file-content-box /deep/.table-header {
    background: #f5f5f5 !important;
    color: #444;
}

.file-content-box /deep/.el-table__cell {
    border-bottom: 1px solid #ddd;
    border-right: 1px solid #ddd;
}

.file-content-box /deep/.el-table__row {
    border-bottom: 1px solid #ddd;
    border-right: 1px solid #ddd;
}

.file-content-box /deep/.el-table--border {
    border: 1px solid #ddd;
}

/deep/.table-header {
    background-color: #f2f2f2 !important;
    padding: 6px 0;
    border-bottom: 1px solid #e2e2e2 !important;
    border-top: 1px solid #e2e2e2 !important;
    border-right: 1px solid #e2e2e2 !important;
    border-right: transparent;
    text-align: left;
}

/deep/.table-header .cell {
    font-weight: bold;
    color: #303133;
}
</style>