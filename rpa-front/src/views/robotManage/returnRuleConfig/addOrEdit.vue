<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-40 pr-20">
      <el-form :inline="true" :model="formData" ref="formData" label-width="100px">
        <p class="fw-blod font-16 pt-20">业务信息</p>
        <el-row style="padding: 20px 20px 0px 20px;">
          <el-col :span="8">
            <div style="display: flex">
              <el-form-item label="参保城市：" prop="addrName"
                            :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
                <div>
                  <addrSelector v-model="formData.addrName" :addrArr="options.addrArr"
                                @changeAddrSelect="changeAddrSelect" width="240px" :disabled="isEdit"></addrSelector>
                </div>
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="8">
            <div style="display: flex">
              <el-form-item label="业务类型：" prop="businessType">
                <el-radio-group v-model="formData.businessType" @change="getDeclareWebsite();getCurrentNode()"
                                :disabled="isEdit">
                  <el-radio :label="1">社保</el-radio>
                  <el-radio :label="2">公积金</el-radio>
                </el-radio-group>
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="8">
            <div style="display: flex">
              <el-form-item label="申报类型：" prop="declareType">
                <el-radio-group v-model="formData.declareType" @change="getCurrentNode()" :disabled="isEdit">
                  <el-radio v-for="item in options.declareType" :key="item.id" :label="item.id">{{ item.name }}
                  </el-radio>
                </el-radio-group>
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="8">
            <div style="display: flex">
              <el-form-item label="申报网站：" prop="declareWebsite"
                            :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
                <el-select v-model="formData.declareWebsite" placeholder="请选择" class="input-item" :disabled="isEdit">
                  <el-option
                    v-for="item in options.declareWebsite"
                    :key="item.tplType"
                    :label="item.tplTypeName"
                    :value="item.tplType"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </div>
          </el-col>
          <!--          <el-col :span="8">
                      <el-form-item label="当前节点：" prop="currentNode"
                                    :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
                        <el-select v-model="formData.currentNode" placeholder="请选择" filterable class="input-item" :disabled="isEdit">
                          <el-option
                            v-for="item in options.currentNode"
                            :key="item.code"
                            :label="item.name"
                            :value="item.code">
                          </el-option>
                        </el-select>
                      </el-form-item>
                    </el-col>-->
          <el-col :span="8">
            <div style="display: flex">
              <el-form-item label="启用状态：" prop="status">
                <el-switch v-model="formData.status" :active-value="1" :inactive-value="0"
                           active-color="#13ce66" :disabled="isEdit"></el-switch>
              </el-form-item>
            </div>
          </el-col>
        </el-row>

        <p class="fw-blod font-16 pt-">判断回盘状态</p>
        <div style="padding: 20px;">
          <palTab :tabs="tabs" v-model="tabActive" :type="2" :key="refreshTab"></palTab>
          <div class="mt-10" v-for="(item, index) in formData.ruleSetData" :key="index" v-show="tabActive===index">
            <table class="cust-table-border w-p100">
              <thead>
              <tr>
                <th>匹配规则</th>
                <th>内容</th>
                <th style="width: 450px;">替换信息</th>
                <th>备注/说明</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="(row, rowInd) in item.list" :key="rowInd">
                <td style="position: relative">
                  <span v-if="row.isNew" class="new-sign">新</span>
                  <el-form-item v-if="row.isNew || row.isEdit" label-width="0"
                                :prop="'ruleSetData.'+index+'.list.'+rowInd+'.matchRule'"
                                :rules="[{ required: true, message: '请选择', trigger: 'change' }]" class="w-p100">
                    <el-select v-model="row.matchRule" placeholder="请选择规则" class="w-p100">
                      <el-option label="完全等于" :value="1"></el-option>
                      <el-option label="包含" :value="2"></el-option>
                      <el-option label="以此信息开头" :value="3"></el-option>
                      <el-option label="以此信息结尾" :value="4"></el-option>
                    </el-select>
                  </el-form-item>
                  <p v-else class="row-text">
                    <span v-if="row.matchRule===1">完全等于</span>
                    <span v-else-if="row.matchRule===2">包含</span>
                    <span v-else-if="row.matchRule===3">以此信息开头</span>
                    <span v-else-if="row.matchRule===4">以此信息结尾</span>
                  </p>
                </td>
                <td>
                  <el-form-item v-if="row.isNew || row.isEdit" label-width="0"
                                :prop="'ruleSetData.'+index+'.list.'+rowInd+'.content'"
                                :rules="[{ required: true, message: '请输入', trigger: 'blur' }]" class="w-p100">
                    <el-input v-model="row.content" placeholder="请输入" clearable></el-input>
                  </el-form-item>
                  <p v-else class="row-text">{{ row.content }}</p>
                </td>
                <td>
                  <div v-if="row.isNew || row.isEdit" class="display-flex">
                    <el-form-item label-width="0" :prop="'ruleSetData.'+index+'.list.'+rowInd+'.replaceType'"
                                  :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
                      <el-select v-model="row.replaceType" placeholder="请选择" style="width: 130px">
                        <el-option label="不替换" :value="1"></el-option>
                        <el-option label="申报中的信息" :value="2"></el-option>
                        <el-option label="自定义" :value="3"></el-option>
                        <el-option label="拼接前缀" :value="4"></el-option>
                        <el-option label="追加" :value="5"></el-option>
                      </el-select>
                    </el-form-item>
                    <div class="flex1">
                      <el-form-item v-if="row.replaceType===3 || row.replaceType===4 || row.replaceType===5"
                                    label-width="0" :prop="'ruleSetData.'+index+'.list.'+rowInd+'.replaceContent'"
                                    :rules="[{ required: true, message: '请输入', trigger: 'change' }]" class="w-p100">
                        <el-input v-model="row.replaceContent" placeholder="请输入自定义信息" clearable></el-input>
                      </el-form-item>
                    </div>
                  </div>
                  <p v-else class="row-text">
                    <span v-if="row.replaceType===1">不替换</span>
                    <span v-else-if="row.replaceType===2">申报中的信息</span>
                    <span v-else-if="row.replaceType===3">自定义（{{ row.replaceContent }}）</span>
                    <span v-else-if="row.replaceType===4">拼接前缀（{{ row.replaceContent }}）</span>
                    <span v-else-if="row.replaceType===5">追加（{{ row.replaceContent }}）</span>
                  </p>
                </td>
                <td>
                  <el-form-item v-if="row.isNew || row.isEdit" label-width="0"
                                :prop="'ruleSetData.'+index+'.list.'+rowInd+'.remark'" class="w-p100">
                    <el-input v-model="row.remark" placeholder="请输入"></el-input>
                  </el-form-item>
                  <p v-else class="row-text">{{ handleNextNodeText(row.remark) }}</p>
                </td>
                <td>{{ row.status === 1 ? '启用' : '禁用' }}</td>
                <td>
                  <div v-if="row.isNew || row.isEdit">
                    <el-button type="text" size="small" class="text-blue" @click="doUpdateStatus(index, rowInd, 'status')">
                      {{ row.status === 1 ? '禁用' : '启用' }}
                    </el-button>
                    <el-button type="text" size="small" class="text-red" @click="doRowDel(index, rowInd, 'status')">删除
                    </el-button>
                  </div>
                  <div v-else>
                    <el-button type="text" size="small" class="text-blue" @click="doRowEdit(index, rowInd, 'status')">编辑
                    </el-button>
                  </div>
                </td>
              </tr>
              </tbody>
            </table>
            <div class="text-center pb-20">
              <el-button class="btn--border-blue mt-20 w-200" @click="doRowAdd(index, 'status')"><i class="el-icon-plus"></i> 添加
              </el-button>
            </div>
          </div>
        </div>

        <p class="fw-blod font-16 pt-">判断下一节点</p>
        <div style="padding: 10px 20px;">
          <div class="mt-10">
            <table class="cust-table-border w-p100">
              <thead>
              <tr>
                <th>匹配规则</th>
                <th>内容</th>
                <th style="width: 450px;">替换信息</th>
                <th>备注/说明</th>
                <th>下一节点</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="(row, rowInd) in formData.nextNodeList" :key="rowInd">
                <td style="position: relative">
                  <span v-if="row.isNew" class="new-sign">新</span>
                  <el-form-item v-if="row.isNew || row.isEdit" label-width="0"
                                :prop="'nextNodeList.'+rowInd+'.matchRule'"
                                :rules="[{ required: true, message: '请选择', trigger: 'change' }]" class="w-p100">
                    <el-select v-model="row.matchRule" placeholder="请选择规则" class="w-p100">
                      <el-option label="完全等于" :value="1"></el-option>
                      <el-option label="包含" :value="2"></el-option>
                      <el-option label="以此信息开头" :value="3"></el-option>
                      <el-option label="以此信息结尾" :value="4"></el-option>
                    </el-select>
                  </el-form-item>
                  <p v-else class="row-text">
                    <span v-if="row.matchRule===1">完全等于</span>
                    <span v-else-if="row.matchRule===2">包含</span>
                    <span v-else-if="row.matchRule===3">以此信息开头</span>
                    <span v-else-if="row.matchRule===4">以此信息结尾</span>
                  </p>
                </td>
                <td>
                  <el-form-item v-if="row.isNew || row.isEdit" label-width="0"
                                :prop="'nextNodeList.'+rowInd+'.content'"
                                :rules="[{ required: true, message: '请输入', trigger: 'blur' }]" class="w-p100">
                    <el-input v-model="row.content" placeholder="请输入" clearable></el-input>
                  </el-form-item>
                  <p v-else class="row-text">{{ row.content }}</p>
                </td>
                <td>
                  <div v-if="row.isNew || row.isEdit" class="display-flex">
                    <el-form-item label-width="0" :prop="'nextNodeList.'+rowInd+'.replaceType'"
                                  :rules="[{ required: true, message: '请选择', trigger: 'change' }]">
                      <el-select v-model="row.replaceType" placeholder="请选择" style="width: 130px">
                        <el-option label="不替换" :value="1"></el-option>
                        <el-option label="申报中的信息" :value="2"></el-option>
                        <el-option label="自定义" :value="3"></el-option>
                        <el-option label="拼接前缀" :value="4"></el-option>
                        <el-option label="追加" :value="5"></el-option>
                      </el-select>
                    </el-form-item>
                    <div class="flex1">
                      <el-form-item v-if="row.replaceType===3 || row.replaceType===4 || row.replaceType===5"
                                    label-width="0" :prop="'nextNodeList.'+rowInd+'.replaceContent'"
                                    :rules="[{ required: true, message: '请输入', trigger: 'change' }]" class="w-p100">
                        <el-input v-model="row.replaceContent" placeholder="请输入自定义信息" clearable></el-input>
                      </el-form-item>
                    </div>
                  </div>
                  <p v-else class="row-text">
                    <span v-if="row.replaceType===1">不替换</span>
                    <span v-else-if="row.replaceType===2">申报中的信息</span>
                    <span v-else-if="row.replaceType===3">自定义（{{ row.replaceContent }}）</span>
                    <span v-else-if="row.replaceType===4">拼接前缀（{{ row.replaceContent }}）</span>
                    <span v-else-if="row.replaceType===5">追加（{{ row.replaceContent }}）</span>
                  </p>
                </td>
                <td>
                  <el-form-item v-if="row.isNew || row.isEdit" label-width="0"
                                :prop="'nextNodeList.'+rowInd+'.remark'" class="w-p100">
                    <el-input v-model="row.remark" placeholder="请输入"></el-input>
                  </el-form-item>
                  <p v-else class="row-text">{{ handleNextNodeText(row.remark) }}</p>
                </td>
                <td>
                  <el-form-item v-if="row.isNew || row.isEdit" label-width="0"
                                :prop="'nextNodeList.'+rowInd+'.nextNode'"
                                :rules="[{ required: true, message: '请选择', trigger: 'change' }]" class="w-p100">
                    <el-select v-model="row.nextNode" placeholder="请选择" filterable class="w-p100">
                      <el-option
                        v-for="item in options.currentNode"
                        :key="item.code"
                        :label="item.name"
                        :value="item.code">
                      </el-option>
                    </el-select>
                  </el-form-item>
                  <p v-else class="row-text">{{ handleNextNodeText(row.nextNode) }}</p>
                </td>
                <td>{{ row.status === 1 ? '启用' : '禁用' }}</td>
                <td>
                  <div v-if="row.isNew || row.isEdit">
                    <el-button type="text" size="small" class="text-blue" @click="doUpdateStatus('', rowInd, 'next')">
                      {{ row.status === 1 ? '禁用' : '启用' }}
                    </el-button>
                    <el-button type="text" size="small" class="text-red" @click="doRowDel('', rowInd, 'next')">删除
                    </el-button>
                  </div>
                  <div v-else>
                    <el-button type="text" size="small" class="text-blue" @click="doRowEdit('', rowInd, 'next')">编辑
                    </el-button>
                  </div>
                </td>
              </tr>
              </tbody>
            </table>
            <div class="text-center pb-30">
              <el-button class="btn--border-blue mt-20 w-200" @click="doRowAdd('', 'next')"><i class="el-icon-plus"></i> 添加
              </el-button>
            </div>
          </div>
        </div>

      </el-form>

      <div class="text-center pt-10 pb-30">
        <el-button class="btn--border-blue s-btn" @click="goBack">取消</el-button>
        <el-button type="primary" class="s-btn" style="margin-left: 30px;" @click="handleValid">保存
        </el-button>
      </div>
    </div>

  </div>
</template>
<script>
import addrSelector from 'components/common/addrSelector.vue'
import palTab from 'components/common/pal-tab.vue'

export default {
  components: { palTab, addrSelector },
  data () {
    return {
      pathData: [],
      isEdit: false,
      options: {
        addrArr: [],
        declareWebsite: [],
        declareType: [
          // 变更类型（1增，2减，3调基，5补缴）
          { id: 1, name: '增员' },
          { id: 2, name: '减员' },
          { id: 3, name: '调基' },
          { id: 5, name: '补缴' }
        ],
        currentNode: []
      },
      formData: {
        id: '',
        addrId: '',
        addrName: '',
        businessType: 1,
        declareWebsite: '',
        declareType: 1,
        currentNode: '',
        status: 1,
        ruleSetData: [
          {
            list: []
          },
          {
            list: []
          },
          {
            list: []
          }
        ],
        nextNodeList: []
      },
      /* ruleItem: {
        declaring: [],
        declareSuccess: [],
        declareFail: []
      }, */
      addRuleItem: {
        declareStatus: '', // 申报状态，2：申报中，4：申报成功，5：申报失败
        matchRule: '', // 匹配规则：1-equals 2-contains  3-startsWith 4-endsWith
        content: '', // 内容
        replaceType: '', // 替换类型：1：不替换，2：申报中信息，3：自定义 4：拼接前缀 5：追加
        replaceContent: '', // 替换信息
        remark: '', // 备注
        nextNode: '', // 下一节点
        status: 1, // 状态 0：禁用 1：启用

        isNew: true
      },
      tabActive: 0,
      tabs: ['申报中(0)', '申报成功(0)', '申报失败(0)'],
      refreshTab: new Date().getTime()
    }
  },
  computed: {},
  watch: {},
  created () {
    var id = this.$route.query.id
    this.isEdit = !!id
    if (id) {
      this.getData(id)
    } else {
      this.getAddr()
      this.getCurrentNode('init')
    }

    let pathData = []
    let tabs = this.$store.state.tabs
    if (tabs) {
      pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    pathData.push({ name: this.isEdit ? '编辑' : '添加' })
    this.pathData = pathData
  },
  mounted () {
  },
  methods: {
    // 获取参保地
    getAddr (type) {
      this.$http({
        url: 'policy/declareAddr/addrList',
        method: 'post'
      }).then(({ data }) => {
        this.options.addrArr = data.data
      })
    },
    // 改变参保地
    changeAddrSelect (item) {
      if (this.formData.addrId == item.id) {
        return
      }
      this.formData.addrId = item.id
      this.getDeclareWebsite()
    },
    // 获取申报网站
    getDeclareWebsite (type) {
      let that = this
      if (type != 'init') {
        this.formData.declareWebsite = ''
        this.options.declareWebsite = []
      }
      if (!this.formData.addrId || !this.formData.businessType) {
        return
      }
      this.$http({
        url: '/policy/offerSettings/findAddrTplItems/' + this.formData.addrId + '/' + this.formData.businessType,
        method: 'post'
      }).then(({ data }) => {
        that.options.declareWebsite = data.data.tplItems
      }).catch((data) => {
      })
    },
    // 获取节点
    getCurrentNode (type) {
      let that = this
      if (type != 'init') {
        this.formData.currentNode = ''
        this.options.currentNode = []
        this.formData.nextNodeList.map(item => {
          item.nextNode = ''
        })
      }
      if (!this.formData.declareType || !this.formData.businessType) {
        return
      }
      let changeTypes = []
      changeTypes.push(this.formData.declareType)
      this.$http({
        url: '/policy/offerSettings/getOperationTypes',
        method: 'post',
        params: {
          businessType: this.formData.businessType,
          changeType: changeTypes.join(',')
        }
      }).then(({ data }) => {
        that.options.currentNode = data.data
        /* if (type != 'init' && data.data.length > 0) {
          this.formData.currentNode = data.data[0].code
        } */
      }).catch((data) => {
      })
    },
    getData (id) {
      let that = this
      this.$http({
        url: '/robot/offerRule/page',
        method: 'post',
        data: {
          'page': 1,
          'start': 0,
          'size': 20,
          'query': [{ 'property': 'id', 'value': id }],
          'sidx': '',
          'sort': ''
        }
      }).then(({ data }) => {
        let res = data.data.rows[0]
        that.formData.id = res.id
        that.formData.addrId = res.addrId
        that.formData.addrName = res.addrName
        that.formData.businessType = res.businessType
        that.formData.declareWebsite = res.declareWebsite
        that.formData.declareType = res.declareType
        that.formData.remark = res.remark
        that.formData.currentNode = res.currentNode
        that.formData.status = res.status
        that.formData.nextNodeList = res.nextNodeList ? res.nextNodeList : []
        let list0 = []
        let list1 = []
        let list2 = []
        res.ruleItemList = res.ruleItemList ? res.ruleItemList : []
        res.ruleItemList.map(item => {
          // 申报状态，2：申报中，4：申报成功，5：申报失败
          if (item.declareStatus === 2) {
            list0.push(item)
          } else if (item.declareStatus === 4) {
            list1.push(item)
          } else if (item.declareStatus === 5) {
            list2.push(item)
          }
        })
        that.formData.ruleSetData[0].list = list0
        that.formData.ruleSetData[1].list = list1
        that.formData.ruleSetData[2].list = list2
        that.doUpdateTabText(0)
        that.doUpdateTabText(1)
        that.doUpdateTabText(2)
        that.getDeclareWebsite('init')
        that.getCurrentNode('init')
      }).catch((data) => {
      })
    },

    doUpdateTabText (index) {
      let length = this.formData.ruleSetData[index].list.length
      let tab = index === 0 ? '申报中' : index === 1 ? '申报成功' : '申报失败'
      this.tabs[index] = tab + '(' + length + ')'
      this.refreshTab = new Date().getTime()
    },
    // 添加行
    doRowAdd (index, type) {
      let addRuleItem = { ...this.addRuleItem }
      if (type === 'status') {
      //   判断回盘状态
        // 申报状态，2：申报中，4：申报成功，5：申报失败
        addRuleItem.declareStatus = index === 0 ? 2 : index === 1 ? 4 : 5
        this.formData.ruleSetData[index].list.push(addRuleItem)
        this.doUpdateTabText(index)
      } else {
      // 判断下一节点
        this.formData.nextNodeList.push(addRuleItem)
      }
    },
    // 启用、禁用
    doUpdateStatus (index, rowInd, type) {
      let status = 0
      if (type === 'status') {
        //   判断回盘状态
        status = this.formData.ruleSetData[index].list[rowInd].status === 0 ? 1 : 0
        if (this.formData.ruleSetData[index].list[rowInd].isNew) {
          this.formData.ruleSetData[index].list[rowInd].status = status
          return
        }
      } else {
        // 判断下一节点
        status = this.formData.nextNodeList[rowInd].status === 0 ? 1 : 0
        if (this.formData.nextNodeList[rowInd].isNew) {
          this.formData.nextNodeList[rowInd].status = status
          return
        }
      }

      this.$confirm('是否确定' + (status === 0 ? '禁用' : '启用') + '该规则？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        if (type === 'status') {
          //   判断回盘状态
          this.formData.ruleSetData[index].list[rowInd].status = status
        } else {
          // 判断下一节点
          this.formData.nextNodeList[rowInd].status = status
        }
      }).catch(() => {
      })
    },
    // 删除
    doRowDel (index, rowInd, type) {
      if (type === 'status') {
        //   判断回盘状态
        if (this.formData.ruleSetData[index].list[rowInd].isNew) {
          this.formData.ruleSetData[index].list.splice(rowInd, 1)
          this.doUpdateTabText(index)
          return
        }
      } else {
        // 判断下一节点
        if (this.formData.nextNodeList[rowInd].isNew) {
          this.formData.nextNodeList.splice(rowInd, 1)
          return
        }
      }

      this.$confirm('是否确定删除该规则？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        if (type === 'status') {
          //   判断回盘状态
          this.formData.ruleSetData[index].list.splice(rowInd, 1)
          this.doUpdateTabText(index)
        } else {
          // 判断下一节点
          this.formData.nextNodeList.splice(rowInd, 1)
        }
      }).catch(() => {
      })
    },
    // 编辑
    doRowEdit (index, rowInd, type) {
      console.log("doooooo")
      if (type === 'status') {
        //   判断回盘状态
        this.$set(this.formData.ruleSetData[index].list[rowInd], 'isEdit', true)
      } else {
        // 判断下一节点
        this.$set(this.formData.nextNodeList[rowInd], 'isEdit', true)
      }
    },
    // 获取下一节点的文本
    handleNextNodeText (currentNode) {
      for (let i = 0; i < this.options.currentNode.length; i++) {
        if (this.options.currentNode[i].code === currentNode) {
          return this.options.currentNode[i].name
        }
      }
      return currentNode
    },

    handleValid: function () {
      let that = this
      // this.showLoading()
      that.$refs.formData.validate((valid, errorObj) => {
        if (valid) {
          that.doSave()
        } else {
          console.log(errorObj)
          that.closeLoading()
          for (let field in errorObj) {
            if (field.indexOf('ruleSetData.0.list') > -1) {
              that.tabActive = 0
              return
            } else if (field.indexOf('ruleSetData.1.list') > -1) {
              that.tabActive = 1
              return
            } else if (field.indexOf('ruleSetData.2.list') > -1) {
              that.tabActive = 2
              return
            }
          }
        }
      })
    },
    doSave: function () {
      let that = this
      var setObj = that.formData
      let ruleItemList = setObj.ruleSetData[0].list.concat(setObj.ruleSetData[1].list)
      ruleItemList = ruleItemList.concat(setObj.ruleSetData[2].list)
      setObj.ruleItemList = ruleItemList
      console.log(setObj)
      that.$http({
        url: '/robot/offerRule/save',
        method: 'post',
        data: setObj
      }).then(({ data }) => {
        that.$message.success('操作成功')
        that.goBack()
      }).catch((data) => {
        that.closeLoading()
      })
    },
    // 返回
    goBack () {
      this.$router.push('/returnRuleConfig/index')
    },
    showLoading (msg) {
      this.loading = this.$loading({
        target: document.body,
        lock: true,
        text: msg || '保存中',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)'
      })
    },
    closeLoading () {
      if (this.loading && this.loading.close) {
        this.loading.close()
      }
    }
  }
}
</script>
<style lang="less" scoped>
/deep/ .ic-addr {
  margin-top: 13px !important;
}

.input-item {
  width: 240px;
}

/deep/ table {
  .el-form-item__content {
    width: 100%;
  }

  .el-form-item {
    margin-bottom: 0;
  }

  .new-sign {
    position: absolute;
    color: @greenColor;
    left: 0;
    top: 0;
    font-size: 12px;
  }

  .row-text {
    word-break: break-all;
  }
}
</style>
