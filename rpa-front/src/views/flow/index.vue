<template>
  <div class="spl-container">
    <breadcrumb :data="pathData">
      <template slot="breadcrumb-btn">
        <!---v-if="editableTabsValue !== 'flow'"--->
        <div class="mr-10" style="display: inline-block;">
          <el-select ref="select" v-model="targetFlowCode" placeholder="请选择复制目标流程" style="width:100%;" value-key="id"
            @change="changeProcess" filterable clearable>
            <el-option :label="item.appNameAndFlowName" :value="item" v-for="(item, index) in processOption"
              :key="item.id"></el-option>
          </el-select>
        </div>
        <el-dropdown class="test123" trigger="click" @command="handleStartTask"
          v-if="isStepErrorMsg == '' && $route.query.template != 1">
          <el-button class="btn--border-blue mr-20" size="small">运行<i
              class="el-icon-arrow-down el-icon--right text-blue"></i></el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item v-for="(item, index) in allTask" :key="index" :command="item">{{ item.companyName }}（{{
              item.accountNumber }}）</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </template>
    </breadcrumb>

    <!-- 增加标签页，第一页流程编辑页不可关闭，后面的为子流程块页（嵌入步骤中） -->
    <el-tabs v-model="editableTabsValue" type="card" @tab-remove="removeTab">
      <el-tab-pane :label="flowEditTitle" name="flow" :closable="false">
        <!-- 主流程、主模板 -->
        <FlowGraph ref="flow" containerId="main" stencilId="main-stencil" minimapId="main-minimap" :nodeList="nodeList"
          :templateType="flowTemplateType" :curFlowCode="$route.query.flowCode" :templateFlowCode="$route.query.templateFlowCode" @save="confirm" @onNodeClick="clickVNode"></FlowGraph>
      </el-tab-pane>
      <el-tab-pane v-for="(item, index) in editableTabs" :key="item.id" :label="item.title" :name="item.name" closable>
        <!-- 步骤设置 -->
        <StepSetting v-if="item.type === 'step'" :id="item.id" :flowCode="item.flowCode" :appCode="item.appCode"
          :stepCode="item.stepCode" :templateFlowCode="item.templateFlowCode">
        </StepSetting>
        <!-- 子流程编辑 -->
        <FlowGraph :ref="`subFlow-${item.id}`" v-if="item.type === 'editor'" :containerId="`container-${item.id}`"
          :mFlowId="item.id" :stencilId="`stencilId-${item.id}`" :minimapId="`minimapId-${item.id}`" :nodeList="nodeList"
          :templateType="item.templateType" :mStepObj="item.stepObj"
          :curFlowCode="item.curFlowCode" :templateFlowCode="item.templateFlowCode" @save="confirm" @onNodeClick="clickVNode">
        </FlowGraph>
      </el-tab-pane>
    </el-tabs>

    <el-dialog title="添加流程" :visible.sync="dialogVisible" width="600px" :close-on-click-modal="false"
      :before-close="cancel">
      <el-form :model="formData" :rules="rules" ref="addProcess" label-width="100px" class="cust-dialog">
        <div>
          <el-form-item label="流程名称：" prop="processName" style="width:80%;">
            <el-input v-model="formData.processName" placeholder="请输入内容" v-filter-special-char></el-input>
          </el-form-item>
        </div>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancel" size="small" class="w-60">取 消</el-button>
        <el-button type="primary" @click="confirm" size="small" class="w-60">确定</el-button>
      </span>
    </el-dialog>

  </div>
</template>
<script>
import '@antv/x6-vue-shape'
import FlowGraph from './components/FlowGraph.vue'
import StepSetting from './components/StepSetting.vue'
export default {
  components: { FlowGraph, StepSetting },
  data () {
    return {
      pathData: [],
      allTask: [],
      isStepErrorMsg: '',
      editableTabsValue: 'flow',
      editableTabs: [],
      dialogVisible: false,
      loading: null,
      formData: {},
      id: null, // 需要保存的画布id 用于多次保存
      flowTemplateType: '', // 主flow的templateType 如果当前路由存在引用并且该值为1（模板），不能进行保存
      rules: {
        processName: [
          { required: true, message: '请输入流程名称', trigger: 'change' },
          { required: true, trigger: 'change', validator: this.checkProcessName }
        ]
        // bussinssType:[
        //   { required: true, message: '请选择添加方式', trigger: 'change'},
        // ],
        // processTypeName:[
        //   { required: true,  trigger: 'change',validator:this.checkProcessTypeName},
        // ],
        // flowType:[
        //   { required: true, message: '请选择流程类型', trigger: 'change'},
        // ]
      },
      nodeList: [],
      processOption: [], // 可复制的下拉流程选项
      targetFlowCode: '' // 选中复制目标流程
    }
  },
  computed: {
    flowEditTitle () {
      return this.$route.query.flowName
    }
  },
  watch: {},
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.pathData.push({ name: `${this.$route.query.template ? `RPA流程模板编辑(${this.$route.query.flowName})` : `RPA流程图编辑(${this.$route.query.flowName})`}` })
    // 获取流程图
    // if (this.$route.query.flowCode && this.$route.query.appCode) {
    //   this.testGetProcess(this.$route.query.appCode, this.$route.query.flowCode)
    //   this.getQuoteNodesList()
    // } else if (this.$route.query.template) {
    //   //获取模板
    //   this.testGetTemplate()
    // }

    // getByCode 获取主flow 流程
    if (this.$route.query.templateFlowCode) {
      this.getGraphData(this.$route.query.flowCode, null, this.$route.query.templateFlowCode)
    } else {
      this.getGraphData(this.$route.query.flowCode, null, '')
    }

    // this.$nextTick(() => {
    //   //如果当前路由上有templateFlowCode 冻结主画布
    //   setTimeout(() => {
    //     if (this.$route.query.templateFlowCode) {

    //       if (this.$refs.flow.graph) {
    //         this.$refs.flow.freezeGraph()
    //       }
    //     }
    //   }, 500);

    // })

    this.getListAllTask()
    this.getAllFlow()
  },
  beforeMount () { },
  mounted () {

  },
  beforeDestroy () {
  },
  beforeRouteLeave (to, from, next) {
    console.log('路由即将离开')

    let that = this
    // 判断当前画布上是否有节点 并且主画布已经发生了更改 ，有则弹出框提示 否则跳过
    if (this.$refs.flow.graph.getCells().length > 0 && this.$refs.flow.isGraphChange) {
      setTimeout(() => {
        this.$confirm('当前画布已有节点变更尚未保存，是否返回', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          showClose: false,
          customClass: 'pal-confirm',
          type: 'warning'
        })
          .then(() => {
            next()
          })
          .catch(() => {
            next(false)
            this.bus.$emit('update', '/processSetting/index')
          })
      }, 300)
    } else {
      next()
    }
  },
  methods: {
    // type 'editor' 跳转到编辑器 'step' 跳转到默认的流程步骤
    async addTab ({ flowName, flowId, flowCode, stepCode, parentFlowCode, actionCode }, type, flowData) {
      console.log(flowName, flowId, flowCode, stepCode)
      console.log('addTab跳转----------', parentFlowCode)
      // return
      // 判断当前明细是否已经存在 存在的话 切换 ，否则新增
      if (this.editableTabs.find(tab => tab.stepCode === stepCode)) {
        this.editableTabsValue = flowName + '-' + stepCode
      } else {
        this.editableTabs.push({
          title: `${actionCode === 'subFlow' ? '' : '代码块-'}${flowName}`,
          name: flowName + '-' + stepCode,
          content: 'New Tab content',
          id: flowId || stepCode,
          appCode: this.$route.query.appCode,
          // flowCode: this.$route.query.templateFlowCode ? this.$route.query.flowCode : parentFlowCode, // flowCode主要是stepList请求时用到 需要判断当前是引用模板还是非引用模板，是引用模板的话就需要传入主flowCode，如果是非引用模板就传入parentFlowCOde
          flowCode: flowData ? flowData.flowCode : parentFlowCode,
          type: type,
          stepCode: stepCode,
          stepObj: type === 'editor' ? { stepCode: stepCode, stepName: flowName, subSaveId: null } : {},
          templateType: '',
          curFlowCode: flowData ? flowData.flowCode : '', // 流程图需要
          templateFlowCode: flowData ? flowData.templateFlowCode : ''
        })
        this.editableTabsValue = flowName + '-' + stepCode

        // 如果当前打开的页面是子流程 需要为其graph渲染接口中画布数据 用于编辑的情况
        if (type === 'editor') {
          this.getGraphData(flowData ? flowData.flowCode : '', stepCode, flowData ? flowData.templateFlowCode : '')
          // const { code, data } = await this.$http({
          //   url: `/api/robot/flowDesign/getByCode?flowCode=${parentFlowCode}&stepCode=${stepCode}`,
          //   baseURL: '',
          //   method: 'get'
          // })
          // if (data.data) {
          //   const json = JSON.parse(data.data.data)
          //   this.editableTabs.find(item => item.stepObj.stepCode === stepCode).stepObj.subSaveId = data.data.id
          //   this.$refs[`subFlow-${stepCode}`][0].graph.fromJSON(json)
          // }
        }
      }
    },
    removeTab (panelName) {
      let tabs = this.editableTabs
      let activeName = this.editableTabsValue
      console.log('即将删除的panel name --->', panelName, activeName)
      if (activeName === panelName) {
        tabs.forEach((tab, index) => {
          if (tab.name === panelName) {
            let nextTab = tabs[index + 1] || tabs[index - 1]
            if (nextTab) {
              activeName = nextTab.name
            }
          }
        })
      }

      this.editableTabsValue = activeName
      this.editableTabs = tabs.filter(tab => tab.name !== panelName)
      if (this.editableTabs.length === 0) this.editableTabsValue = 'flow'
      console.log('removeTab ---> ', this.editableTabsValue, this.editableTabs)
    },
    // 获取主流程
    async testGetProcess (appCode, flowCode) {
      const { code, data } = await this.$http({
        url: `/api/robot/flowDesign/getByCode?appCode=${appCode}&flowCode=${flowCode}`,
        baseURL: '',
        method: 'get'
      })
      if (data.data) {
        this.importJSON(JSON.parse(data.data.data))
        // console.log('testGetProcess -- >', data.data.data)
        this.id = data.data.id
      }
    },
    // 主流程- 获取模板 此时画板id为flow
    async testGetTemplate () {
      const { code, data } = await this.$http({
        url: `/api/robot/flowDesign/getByCode?flowCode=${this.$route.query.flowCode}`,
        baseURL: '',
        method: 'get'
      })
      // console.log('testGetProcess -- >', code,data)
      if (data.data) {
        this.importJSON(JSON.parse(data.data.data))

        this.id = data.data.id
      }
    },
    importJSON (json) {
      this.$refs.flow.graph.fromJSON(json)
    },
    // 保存画布(如果mStepObj不为空，代表现在保存的是子流程，否则则为主流程)
    async saveGraph (graphJSON, stepList, mStepObj, templateFlowCode, flowCode) {
      console.log('当前tab页签', this.editableTabsValue)
      // return

      let dataObj = {}
      // 如果为模板引用时 需要多传入一个templateFlowCode
      if (templateFlowCode) {
        dataObj = {
          id: mStepObj ? mStepObj.subSaveId : this.id, // 画布id  存在mStepObj代表当前是子流程
          appCode: this.$route.query.appCode,
          appName: this.$route.query.appName,
          // flowCode: this.$route.query.flowCode || null, // 模板引用时 需要一直传入主flowCode
          flowCode: mStepObj ? flowCode : this.$route.query.flowCode,
          templateFlowCode: templateFlowCode, // 模板引用时需要传入templateFlowCode
          flowName: this.$route.query.flowName || this.formData.processName,
          data: graphJSON,
          steps: stepList,
          flowType: mStepObj ? 2 : 1,
          stepCode: mStepObj ? mStepObj.stepCode : undefined,
          stepName: mStepObj ? mStepObj.stepName : undefined
        }
      } else {
        dataObj = {
          id: mStepObj ? mStepObj.subSaveId : this.id,
          appCode: this.$route.query.appCode,
          appName: this.$route.query.appName,
          // flowCode: templateFlowCode,
          flowCode: flowCode,
          flowName: this.$route.query.flowName || this.formData.processName,
          data: graphJSON,
          steps: stepList,
          flowType: mStepObj ? 2 : 1,
          stepCode: mStepObj ? mStepObj.stepCode : undefined,
          stepName: mStepObj ? mStepObj.stepName : undefined
        }
      }
      try {
        const { data } = await this.$http({
          url: '/api/robot/flowDesign/add',
          method: 'post',
          baseURL: '',
          data: dataObj
        })
        if (data) {
          console.log('saveGraph -- >', data)
          // this.cancel()
          // this.getAllFlow()
          if (mStepObj) {
            this.editableTabs.find(item => item.stepObj.stepCode === mStepObj.stepCode).stepObj.subSaveId = data.data
          } else {
            this.id = data.data
          }

          this.$message.success('操作成功')
          // 根据情况重新获取画布
          // 保存之后要对应刷新tab
          // 当前保存的是flow 主tab
          // if (this.editableTabsValue === 'flow') {
          //   if (this.$route.query.template) {
          //     //获取模板
          //     this.testGetTemplate()
          //   } else {
          //     this.testGetProcess(this.$route.query.appCode, this.$route.query.flowCode)
          //   }
          //   console.log('主tab flow刷新')
          // } else {
          //   //当前保存的不是主tab,需要寻找
          //   let url = ''
          //   if (this.$route.query.template) {
          //     url = `/api/robot/flowDesign/getByCode?flowCode=${parentFlowCode}&stepCode=${mStepObj.stepCode}`
          //   } else {
          //     url = `/api/robot/flowDesign/getByCode?appCode=${this.$route.query.appCode}&flowCode=${parentFlowCode}&stepCode=${mStepObj.stepCode}`
          //   }
          //   const { code, data } = await this.$http({
          //     url: url,
          //     baseURL: '',
          //     method: 'get'
          //   })
          //   if (data.data) {
          //     const json = JSON.parse(data.data.data)
          //     try {
          //       this.editableTabs.find(item => item.stepObj.stepCode === mStepObj.stepCode).stepObj.subSaveId = data.data.id
          //       this.$refs[`subFlow-${mStepObj.stepCode}`][0].graph.fromJSON(json)
          //       console.log('非主tab刷新', `subFlow-${mStepObj.stepCode}`)
          //     } catch (error) {
          //       console.log('非主tab刷新报错', error)
          //     }

          //   }
          // }
          if (this.editableTabsValue === 'flow') {
            this.getGraphData(this.$route.query.flowCode, null, this.$route.query.templateFlowCode)
          } else {
            this.getGraphData(flowCode, mStepObj.stepCode, templateFlowCode)
          }

          this.closeLoading()
        }
      } catch (error) {
        this.closeLoading()
      }
    },
    // 取消添加流程
    cancel () {
      this.dialogVisible = false
      // this.tableEdit = ''
      // this.disabledBussinssType = false
      this.formData = {
        // bussinssType:"add",
        processName: ''
        // processTypeName:'',
        // flowType:"1",
        // processObj:{},
        // flowCode:''
      }
      this.$refs.addProcess.resetFields()
    },
    closeLoading () {
      if (this.loading && this.loading.close) {
        this.loading.close()
      }
    },
    // 确定添加流程
    confirm (data, stepList, mStepObj, parentFlowCode, flowCode) {
      console.log('当前即将保存的画布----', data)
      console.log('当前即将保存的stepList----', stepList)
      console.log('当前即将保存的mStepObj----', mStepObj)
      console.log('当前即将保存的parentFlowCode----', parentFlowCode)
      // return
      this.showLoading()
      this.saveGraph(data, stepList, mStepObj, parentFlowCode, flowCode)
    },
    showLoading (target) {
      this.loading = this.$loading({
        target: target || document.body,
        lock: true,
        text: '加载中',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)'
      })
    },
    // 点击自定义vue节点回调
    clickVNode (data, mapData) {
      console.log('点击自定义vue节点数据', data)
      if (!data.parentFlowCode) {
        this.$message.warning('请先保存画布，才能进行步骤设置～')
        return
      }

      // 如果当前为模板引用 则要根据节点data里面的OpenEdit来判断是否能进入下一级
      if (mapData.templateFlowCode && data.openEdit == 0) {
        this.$message.warning('当前模板节点已设置不开放编辑，不支持跳转')
        return
      }
      if (data.actionCode === 'codeBlock') {
        // 当前为 代码块 跳转至旧的步骤设置
        this.addTab(data, 'step', mapData)
      } else if (data.actionCode === 'subFlow') {
        // 当前为 子流程 跳转至新的子流程编辑器
        // RPA-5010 判断“调用流程”的打开方式，openType=1，以流程图方式打开；openType=0，以流程步骤的方式打开
        this.$http({
          url: `/robot/flowDesign/getFlowOpenType?appCode=${this.$route.query.appCode}&flowCode=${mapData.flowCode}&stepCode=${data.stepCode}`,
          method: 'get'
        }).then((res) => {
          if (res.data.data.flowCode === mapData.flowCode) {
            this.$message.warning('引用流程不存在，请检查')
            return
          }
          if (res.data.data.openType === 1) {
            this.addTab(data, 'editor', res.data.data)
          } else if (res.data.data.openType === 0) {
            this.addTab(data, 'step', res.data.data)
          }
        }).catch(() => {
        })
      } else {
        throw Error('当前点击节点不存在actionCode属性,不支持跳转')
        // 以下为测试代码
        this.addTab(data, 'editor')
      }
    },
    // 请求获取引用节点列表
    async getQuoteNodesList () {
      const { data } = await this.$http({
        url: '/api/robot/flowDesign/getSubByAppCode?appCode=' + this.$route.query.appCode,
        method: 'get',
        baseURL: ''
      }).catch(error => {
        console.log('getQuoteNodesList error----->', error)
      })
      if (data) {
        console.log('请求引用节点列表----->', data)
        this.nodeList = data.data || []
      }
    },
    // getByCode 获取画布图数据（会打开编辑器的才是画布）
    async getGraphData (flowCode, stepCode, templateFlowCode) {
      // 分两种情形会获取画布数据
      // 1.模板引用的情况 路由上会存在templateFlowCode 代表当前为模板引用 需要传flowCode、templateFlowCode、appCode、以及子流程的stepCode
      // 2.非模板引用的情况 则只要对应传节点flowCode当为flowCode，对应子流程的stepCode即可
      let url = ''
      if (templateFlowCode) {
        // 当前为模板引用
        url = `/api/robot/flowDesign/getByCode?appCode=${this.$route.query.appCode}&flowCode=${flowCode}&templateFlowCode=${templateFlowCode}`
      } else {
        // 当前为非模板引用
        if (this.$route.query.template) {
          // 当前为流程模板
          // stepCode 为空 代表当前请求的是主flow,即第一个页签的请求。有stepCode才代表需要请求的是子流程
          url = `/api/robot/flowDesign/getByCode?flowCode=${flowCode}`
        } else {
          // 当前为普通的非模板引用的流程
          // stepCode 为空 代表当前请求的是主flow,即第一个页签的请求。有stepCode才代表需要请求的是子流程
          url = `/api/robot/flowDesign/getByCode?appCode=${this.$route.query.appCode}&flowCode=${flowCode}`
        }
      }

      const { code, data } = await this.$http({
        url: url,
        baseURL: '',
        method: 'get'
      })
      if (data.data) {
        const json = JSON.parse(data.data.data)
        // 根据不同情况对应刷新不同的页签 ，比如当前是主flow请求就加载到主flow中，是哪个页签就加载到哪个页签中去
        if (this.editableTabsValue === 'flow') {
          // 当前为主flow getByCode 需要加载到主flow中
          this.$refs.flow.graphFromJSON2(json)
          this.id = data.data.id
          this.flowTemplateType = data.data.templateType
          console.log('主tab刷新', `flow`)
          console.log(json)
        } else {
          try {
            this.editableTabs.find(item => item.stepObj.stepCode === stepCode).stepObj.subSaveId = data.data.id
            this.editableTabs.find(item => item.stepObj.stepCode === stepCode).templateType = data.data.templateType
            // this.editableTabs.find(item => item.stepObj.stepCode === stepCode).curFlowCode = data.data.flowCode
            this.$refs[`subFlow-${stepCode}`][0].graphFromJSON2(json)
            console.log('非主tab刷新', `subFlow-${stepCode}`)
            console.log(' this.editableTabs.find(item => item.stepObj.stepCode === stepCode)', this.editableTabs.find(item => item.stepObj.stepCode === stepCode))
          } catch (error) {
            console.log('非主tab刷新报错', error)
          }
        }
      } else {
        // data为空的情况 设置templateType
        if (this.editableTabsValue === 'flow') {
          this.flowTemplateType = 0
        } else {
          this.editableTabs.find(item => item.stepObj.stepCode === stepCode).stepObj.templateType = 0
        }
      }
    },
    // 运行某个任务
    handleStartTask (item) {
      let that = this
      this.$http({
        url: 'http://localhost:9090/api/robot/start/startFlow/' + item.taskCode + '/' + this.$route.query.flowCode,
        method: 'get'
      }).then(({ data }) => {
        that.$message.success('运行成功')
      }).catch((data) => {
      })
    },
    // 获取当前应用的所有任务（用于运行）
    getListAllTask () {
      let that = this
      this.$http({
        url: '/robot/app/client/listAllTask?appCode=' + this.$route.query.appCode,
        method: 'post'
      }).then(({ data }) => {
        that.allTask = data.data
      }).catch((data) => {
      })
    },
    // 非主流程下 获取可进行复制的目标流程
    getAllFlow (appCode) {
      var url = appCode ? 'robot/flow/showOneAppFlow?appCode=' + appCode : 'robot/flow/showOneAppFlow'
      this.$http({
        url: url,
        method: 'post'
      }).then(res => {
        // this.closeLoading()
        this.processOption = res.data.data
      }).catch(err => {
        this.closeLoading()
      })
    },
    // 选中复制目标流程
    changeProcess (item) {
      console.log('changeProcess', item, this.targetFlowCode)
      if (item) {
        this.$confirm('此操作将当前画布复制为目标流程, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          // console.log(this.editableTabsValue)
          // this.$message({
          //   type: 'success',
          //   message: '删除成功!'
          // });
          // this.targetFlowCode = item
          if (this.editableTabsValue != 'flow') {
            var findPreCopyTab = this.editableTabs.find(sitem => sitem.name === this.editableTabsValue)
            this.getCopyByCode(item.flowCode, findPreCopyTab.curFlowCode, findPreCopyTab.stepCode, findPreCopyTab.parentFlowCode)
          } else {
            this.getCopyByCode(item.flowCode, this.$route.query.flowCode, null, this.$route.query.flowCode)
          }

          this.$refs.select.blur()
        }).catch(() => {
          // 取消后清空下拉
          this.targetFlowCode = ''
          // this.$message({
          //   type: 'info',
          //   message: '已取消删除'
          // });
        })
      }
    },
    // 请求获取复制目标流程的画布
    // spourceCode 需要复制目标的code
    // targetCode 需要被复制的流程code
    // stepCode 需要被复制的stepCode
    async getCopyByCode (sourceCode, targetFlowCode, stepCode, parentFlowCode) {
      let reqUrl = ''
      if (stepCode) {
        reqUrl = `/api/robot/flowDesign/copyByCode?sourceFlowCode=${sourceCode}&targetFlowCode=${targetFlowCode}&stepCode=${stepCode}&parentFlowCode=${parentFlowCode}`
      } else {
        reqUrl = `/api/robot/flowDesign/copyByCode?sourceFlowCode=${sourceCode}&targetFlowCode=${targetFlowCode}&parentFlowCode=${parentFlowCode}`
      }
      const { data } = await this.$http({
        url: reqUrl,
        baseURL: '',
        method: 'get'
      })
      if (data.data) {
        // 复制替换画布
        console.log('复制替换', data.data)
        const json = JSON.parse(data.data.data)
        // 重新加载对应tab的画布
        if (this.editableTabsValue != 'flow') {
          this.$refs[`subFlow-${stepCode}`][0].graph.fromJSON(json)
          this.$refs[`subFlow-${stepCode}`][0].getStepList()
        } else {
          this.$refs.flow.graph.fromJSON(json)
          this.id = data.data.id
          this.flowTemplateType = data.data.templateType
          this.$refs.flow.getStepList()
        }
      }
    }
  }
}
</script>
<style lang='less' scoped>
/deep/ .el-tabs__header {
  margin: 0px;
}
</style>
