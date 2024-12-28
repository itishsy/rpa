<template>
    <div class="content spl-container">
        <!-- 导航 -->
        <breadcrumb :data="pathData">
            <template slot="breadcrumb-btn">
                <el-button type="primary" class="search-btn mr-20" size="small" @click="addRules">添加规则</el-button>
            </template>
        </breadcrumb>
        <!-- 表格 -->
        <div style="padding: 20px 20px 0 20px">
            <dTable @fetch="getRulesTable" ref="table" :tableHeight="tableHeight" :cancelMinHeight="false" :paging="true"
                :showSelection="false" :showIndex="true" rowKey="id" :rowHeight="45">
                <u-table-column prop="initialStatus" label="初始状态" min-width="100" :show-overflow-tooltip="true"
                    fixed="left">
                    <template slot-scope="scope">
                        <span>{{ getStatusText(scope.row.initialStatus) }}</span>
                    </template>
                </u-table-column>
                <u-table-column prop="triggerConditions" label="触发机制" min-width="100" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <span>{{ scope.row.triggerConditions === 1 ? '超时匹配' : '规则匹配' }}</span>
                    </template>
                </u-table-column>
                <u-table-column prop="rules" label="规则" min-width="300" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <span>{{ scope.row.rules }}小时</span>
                    </template>
                </u-table-column>
                <u-table-column prop="changeStatus" label="移交状态" min-width="100" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <span>{{ scope.row.changeStatus === 1 ? '待处理' : '-' }}</span>
                    </template>
                </u-table-column>
                <u-table-column prop="useStatus" label="是否启用" min-width="100" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <el-switch style="display: block" v-model="scope.row.useStatus" active-color="#13ce66"
                            inactive-color="#eceef1" active-text="" inactive-text="" :active-value="1" :inactive-value="0"
                            @change="switchStatus($event, scope.row)">
                        </el-switch>
                    </template>
                </u-table-column>


                <u-table-column fixed="right" label="操作" width="100">
                    <template slot-scope="scope">
                        <el-button type="text" size="small" @click="editRow(scope.row)">编辑</el-button>
                    </template>
                </u-table-column>
            </dTable>
        </div>

        <!-- 规则新增dialog -->
        <el-dialog :title="title" :visible.sync="addVisible" width="30%">
            <div class="add-container">
                <el-form label-position="top" label-width="80px" ref="addForm" :model="ruleForm" :rules="addRule">
                    <el-form-item label="初始状态：" prop="initialStatus">
                        <el-select v-model="ruleForm.initialStatus" style="width: 100%;">
                            <el-option v-for="item in initialStatusSelect" :key="item.key" :label="item.key"
                                :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="移交状态：" prop="changeStatus">
                        <el-select v-model="ruleForm.changeStatus" style="width: 100%;">
                            <el-option v-for="item in changeStatusSelect" :key="item.key" :label="item.key"
                                :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    <div >
                        <p class="mb-10 mt-10">触发机制：</p>
                        <el-radio-group v-model="radioType">
                            <el-radio-button label="1">超时匹配</el-radio-button>
                            <!-- <el-radio-button label="2">规则匹配</el-radio-button> -->
                        </el-radio-group>

                        <div v-if="radioType == 1">
                            <el-form-item label="触发条件：" prop="rules">
                                <el-input v-model.number="ruleForm.rules">
                                    <span slot="suffix" style="color:#999">小时</span>
                                </el-input>
                            </el-form-item>
                        </div>
                        <div v-else>
                            <el-form-item label="添加条件：">
                                <el-select v-model="ruleForm.region2" style="width: 93%;">
                                    <el-option label="区域一" value="shanghai"></el-option>
                                    <el-option label="区域二" value="beijing"></el-option>
                                </el-select>
                                <i class="el-icon-circle-plus-outline plus-btn"></i>
                            </el-form-item>
                            <el-form-item label="触发条件：">
                                <div class="error-layout">
                                    <el-row type="flex" class="error-layout-item" align="middle" v-for="item in 3">
                                        <span>错误原因</span>
                                        <i class="el-icon-remove" />
                                    </el-row>
                                </div>
                            </el-form-item>
                        </div>
                    </div>
                </el-form>


            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="addVisible = false">取消</el-button>
                <el-button type="primary" @click="confirm">确定添加</el-button>
            </span>
        </el-dialog>
    </div>
</template>
<script>
import dTable from '@/components/common/table'
import _ from 'lodash'
export default {
    name: '',
    components: { dTable },
    props: [''],
    data() {
        return {
            pathData: [],         //导航数据
            addVisible: false,       //规则新增visible
            radioType: 1,           //所选触发条件 1超时时长 2规则匹配
            ruleForm: {           //添加规则的表单
                initialStatus: '',
                changeStatus: '',
                rules: ''      //规则
            },
            initialStatusSelect: [           //初始状态可选
                {
                    key: '待申报',
                    value: 1
                },
                {
                    key: '申报中',
                    value: 2
                },
            ],
            changeStatusSelect: [            //移交状态可选
                {
                    key: '待处理',
                    value: 1
                }
            ],
            title: null,      //弹框的标题 添加与编辑
            addRule:        //添加规则弹框的校验规则
            {
                rules: [
                    { required: true, message: '请输入触发规则', trigger: 'blur' },
                    { type: 'number', message: '触发条件：必须为数字值' }
                ],
                initialStatus: [
                    { required: true, message: '请选择初始状态', trigger: 'change' }
                ],
                changeStatus: [
                    { required: true, message: '请选择移交状态', trigger: 'change' }
                ],
            }
        };
    },
    computed: {
        tableHeight: function () {
            return this.$global.bodyHeight - 250 + 'px'
        },
    },
    watch: {},
    created() {
        let tabs = this.$store.state.tabs
        if (tabs) {
            // this.pathData = tabs[this.$route.path]
        }
        this.pathData.push({ name: '控制台' })
        this.pathData.push({ name: '运维数据汇总' })
        this.pathData.push({ name: '规则设置' })


    },
    beforeMount() { },
    mounted() {
        this.$nextTick(() => {
            this.getRulesTable()
        })
    },
    methods: {
        addRules() {
            this.ruleForm = {}
            this.addVisible = true
            this.title = '增加规则'
        },
        //获取规则设置
        getRulesTable() {
            var params = [
            ]
            this.$refs.table.fetch({
                query: params,
                method: 'post',
                url: 'policy/matchRules/ruleList'
            })
        },
        //编辑规则
        editRow(row) {
            this.ruleForm = _.cloneDeep(row)
            this.addVisible = true
            this.title = '编辑规则'
        },
        //获取初始状态文本
        getStatusText(status) {
            switch (status) {
                case 1:
                    return '待申报'
                case 2:
                    return '申报中'
                case 3:
                    return '部分成功'
                case 4:
                    return '申报成功'
                case 5:
                    return '申报失败'
                case 6:
                    return '待提交'
                default:
                    break;
            }
        },
        //修改启用禁用开关
        async switchStatus(event, row) {
            // console.log(event,row)
            const { data: { code, data: result } } = await this.$http({
                url: `policy/matchRules/updateStatus?id=${row.id}&useStatus=${event}`,
                method: 'post'
            })
        },
        //提交或修改规则
         confirm() {
            this.$refs['addForm'].validate(async (valid) => {
                if (valid) {
                    const { data: { code, data: result } } = await this.$http({
                        url: `policy/matchRules/addOrUpdate`,
                        method: 'post',
                        data: {
                            ...this.ruleForm,
                            triggerConditions:this.radioType
                        }
                    })
                    if (code === 200) {
                        this.$message.success('操作成功~')
                        this.getRulesTable()
                    }
                    this.addVisible = false
                } else {
                    console.log('error submit!!');
                    return false;
                }
            });


        }
    },
}
</script>
<style lang='less' scoped>
/deep/ .el-dialog__header {
    border-bottom: none !important;
}

/deep/ .el-dialog__body {
    padding: 10px 30px;
}

.add-container {
    display: flex;
    flex-direction: column;

    /deep/ .el-form-item {
        margin-bottom: 5px;
    }

    /deep/ .el-form--label-top .el-form-item__label {
        padding: 0;
    }

    .plus-btn {
        color: @blueColor;
        margin-left: 10px;
        font-size: 20px;
    }

    .error-layout {
        display: flex;
        flex-direction: column;
        max-height: 200px;
        overflow: auto;
        border: 1px solid #E4E6EB;
        border-radius: 3px;
        padding: 0 10px;

        &-item {
            width: 100%;
            padding-bottom: 3px;
            border-bottom: 1px solid #999;

            span {
                flex: 1;

            }

            i {
                color: @redColor;
                margin-left: 10px;
                font-size: 18px;
            }
        }
    }
}
</style>