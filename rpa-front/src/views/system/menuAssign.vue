<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>

    <div class="pd-20">
      <el-row>
        <el-col :span="12">
          <el-card class="box-card mr-5">
            <div slot="header" class="clearfix text-left">
              <span>页面菜单</span>
            </div>
            <div class="power" :style="{height: height}">
              <el-tree ref="menuTree"
                       :data="menuList"
                       node-key="id"
                       :props="props"
                       :default-expanded-keys="expandedKeys"
                       :expand-on-click-node="false"
                       @node-contextmenu="rightClick"
                       @node-expand="expandNode"
                       @node-collapse="collapseNode">
                <span class="el-tree-node__label" slot-scope="{ node, data }">
                  <div class="tree-node-text" :class="{'selected': selectKey==data.id, 'disabled': data.status===0}" style="display: inline-block" @click="setCurrentNodeByKey(data.id, true)">
                    <span class="pl-5" :title="node.label">{{ node.label }}</span>
                  </div>
                  <span v-if="menuType===2" style="display: inline-block;vertical-align: top;margin-top: 7px;">（{{data.moduleName}}）</span>
                  <span class="pl-20 sort-buts" v-if="sortKey==data.id">
                    <el-button type="text" icon="el-icon-sort-down" class="font-20 fw-blod" @click="reSort(data,1)" v-if="node.parent.childNodes.indexOf(node)<node.parent.childNodes.length-1"></el-button>
                    <el-button type="text" icon="el-icon-sort-up" class="font-20 fw-blod" @click="reSort(data,-1)" v-if="node.parent.childNodes.indexOf(node)>0"></el-button>
                  </span>
                </span>
              </el-tree>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="box-card ml-5 power-card">
            <div slot="header" class="clearfix text-left">
              <span>按钮</span>
            </div>
            <div class="power" :style="{height: height}">
              <div v-for="tag in buttons" :key="tag.id">
                <el-tag
                  closable @close="deleteButton(tag)">
                  <i class="el-icon-setting"></i>
                  {{tag.name}}
                </el-tag>
              </div>
              <el-empty v-if="buttons.length===0" description="暂无按钮"></el-empty>
            </div>
          </el-card>
        </el-col>
      </el-row>

    </div>

    <div id="contextmenu"
         v-show="menuVisible"
         class="menu">
      <div style="margin: 0;">
        <div class="contextmenu__item">
          <el-button size="mini" type="text" icon="el-icon-edit" class="w-p100 font-14" @click="editMenu(currentData)">修改</el-button>
        </div>
        <div class="contextmenu__item">
          <el-button size="mini" type="text" icon="el-icon-circle-plus-outline" class="w-p100 font-14" @click="addMenu(currentData, 0)">添加子菜单</el-button>
        </div>
        <div class="contextmenu__item">
          <el-button size="mini" type="text" icon="el-icon-circle-plus-outline" class="w-p100 font-14" @click="addMenu(currentData, 1)">添加同级菜单</el-button>
        </div>
        <div class="contextmenu__item">
          <el-button type="text" size="mini" icon="el-icon-delete" class="w-p100 font-14 text-red" @click="del(currentData)">删除</el-button>
        </div>
        <div class="contextmenu__item">
          <el-button type="text" size="mini" icon="el-icon-setting" class="w-p100 font-14" @click="addButton(currentData)">添加按钮</el-button>
        </div>
      </div>
    </div>

    <!--添加、编辑菜单-->
    <el-drawer :title="setForm.id==''?'添加菜单':'修改菜单'" :visible.sync="setForm.showDialog" :wrapperClosable="false" custom-class="spl-filter-drawer" :show-close="true" :before-close="clearSetForm">

      <div class="drawer-body pt-20" style="margin: 0 30px;">
        <el-form ref="setForm" :model="setForm" class="filter-form" label-width="100px">
          <el-form-item label="状态：" prop="status" :rules="[{ required: true, message: '请选择',trigger:'change'}]">
            <el-radio-group v-model="setForm.status">
              <el-radio :label="1">启用</el-radio>
              <el-radio :label="0">停用</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item v-if="menuType===2" label="服务模块：" prop="moduleCode" :rules="[{ required: true, message: '请选择服务模块',trigger:'change'}]">
            <el-select v-model="setForm.moduleCode" ref="moduleCode" placeholder="请选择服务模块" class="w-p100" :disabled="setForm.id!=='' || setForm.parentId">
              <el-option v-for="item in moduleCodeList" :key="item.dictCode" :label="item.dictName" :value="item.dictCode" filterable>
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="菜单名称：" prop="name" :rules="[{ required: true, message: '请输入菜单名称',trigger:'blur'},{validator:validMenuName,trigger:'blur'}]">
            <el-input size="medium" placeholder="请输入菜单名称" maxlength="50" v-model="setForm.name" clearable></el-input>
          </el-form-item>

          <el-form-item label="菜单链接：" prop="url">
            <el-input size="medium" placeholder="请输入菜单链接" maxlength="50" v-model="setForm.url" clearable></el-input>
          </el-form-item>

          <el-form-item label="备注：" prop="comment">
            <el-input size="medium" placeholder="请输入备注" maxlength="100" v-model="setForm.comment" clearable></el-input>
          </el-form-item>

          <el-form-item label="图标样式：" prop="iconClass">
            <el-input size="medium" placeholder="请输入图标样式" maxlength="50" v-model="setForm.iconClass" clearable></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div class="drawer-footer-buts">
        <el-button class="mr-12" size="small" @click="clearSetForm()">取 消</el-button>
        <el-button size="small" type="primary" style="margin-left: 20px;" @click="saveData()" :loading="saveLoading">确定</el-button>
      </div>
    </el-drawer>

    <!--添加按钮-->
    <el-drawer title="添加按钮" :visible.sync="buttonForm.showDialog" :wrapperClosable="false" custom-class="spl-filter-drawer" :show-close="true" :before-close="clearButtonForm">

      <div class="drawer-body pt-20">
        <el-form ref="buttonForm" :model="buttonForm" class="filter-form">
          <label class="required item-label">按钮名称</label>
          <el-form-item label="" prop="name" :rules="[{ required: true, message: '请输入按钮名称',trigger:'blur'}]">
            <el-input size="medium" placeholder="请输入" maxlength="50" v-model="buttonForm.name"></el-input>
          </el-form-item>

          <label class="required item-label">按钮编码</label>
          <el-form-item label="" prop="code" :rules="[{ required: true, message: '请输入按钮编码',trigger:'blur'},{validator:validButtonCode,trigger:'blur'}]">
            <el-input size="medium" placeholder="请输入" maxlength="50" v-model="buttonForm.code"></el-input>
          </el-form-item>

          <label class="required item-label">按钮类型</label>
          <el-form-item label="" prop="type" :rules="[{ required: true, message: '请选择按钮类型',trigger:'change'}]">
            <el-select size="medium" placeholder="请选择" class="w-p100" v-model="buttonForm.type">
              <el-option label="按钮" :value="1"></el-option>
              <el-option label="页面" :value="2"></el-option>
            </el-select>
          </el-form-item>

          <label class="item-label">url</label>
          <el-form-item label="" prop="url">
            <el-input size="medium" placeholder="请输入" maxlength="50" v-model="buttonForm.url"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div class="drawer-footer-buts">
        <el-button class="mr-12" size="small" @click="clearButtonForm()">取 消</el-button>
        <el-button size="small" type="primary" @click="saveButton()" :loading="saveLoading">确定</el-button>
      </div>
    </el-drawer>
  </div>
</template>
<script>
export default {
  components: {},
  props: {
    menuType: { // 菜单类型，1：运营后台菜单，2：客户端菜单
      type: Number,
      default: function () {
        return 1
      }
    }
  },
  data () {
    return {
      pathData: [/* {name: '菜单管理'} */],
      menuList: [],
      buttons: [],
      selectKey: null,
      sortKey: null,
      menuVisible: false,
      currentData: {},
      props: {
        label: 'name',
        children: 'childTrees',
        moduleName: 'moduleName'
      },
      saveLoading: false,
      setForm: {
        showDialog: false,
        parentId: '',
        id: '',
        status: 1, // 菜单状态，1:启用，0：停用，默认启用
        moduleCode: '',
        name: '',
        url: '',
        comment: '',
        iconClass: ''
      },
      expandedKeys: [],
      buttonForm: {
        showDialog: false,
        name: '',
        code: '',
        url: '',
        type: null,
        menuId: null
      },
      moduleCodeList: []
    }
  },
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    // this.menuType = Number(this.$route.query.menuType)
    this.getMenu()
    this.getModuleCodeList()
  },
  computed: {
    height: function () {
      return this.$global.bodyHeight - 250 + 'px'
    }
  },
  methods: {
    getMenu () {
      this.$http({
        url: '/sys/menu/getAll/' + this.menuType,
        method: 'POST',
        data: {}
      }).then(({ data }) => {
        this.menuList = data.data
      }).catch(() => {
        this.$message.error('系统异常')
      })
    },
    getModuleCodeList () {
      this.$http({
        url: `policy/sys/dict/getDictByKey`,
        method: 'get',
        params: {
          dataKey: '10024'
        }
      }).then(res => {
        this.moduleCodeList = res.data.data
      }).catch(() => { })
    },
    setCurrentNodeByKey (key, isSort) {
      this.selectKey = key
      this.getButtonsByMenu(key)
      if (isSort) {
        this.menuVisible = false
        this.currentData = {}
        this.sortKey = key
      } else {
        this.sortKey = null
      }
    },
    getButtonsByMenu (menuId) {
      this.$http({
        url: '/sys/menu/buttonsByMenuId',
        method: 'POST',
        data: this.$qs.stringify({ menuId: menuId })
      }).then(({ data }) => {
        this.buttons = data.data
      }).catch(() => {
        this.$message.error('系统异常')
      })
    },
    addMenu (menu, type) {
      this.setForm.showDialog = true
      if (type == 0) {
        // 添加子菜单
        this.setForm.parentId = menu.id
        this.setForm.moduleCode = menu.moduleCode
      } else {
        // 添加同级菜单
        this.setForm.parentId = menu.parentId
      }
    },
    editMenu (menu) {
      this.setForm.showDialog = true
      this.setForm.parentId = menu.parentId
      this.setForm.id = menu.id
      this.setForm.status = menu.status
      this.setForm.moduleCode = menu.moduleCode
      this.setForm.name = menu.name
      this.setForm.url = menu.url
      this.setForm.comment = menu.comment
      this.setForm.iconClass = menu.iconClass
    },
    expandNode (data, node, dataArr) {
      if (this.expandedKeys.indexOf(data.id) < 0) {
        this.expandedKeys.push(data.id)
      }
    },
    collapseNode (data, node, dataArr) {
      let index = this.expandedKeys.indexOf(data.id)
      if (index >= 0) {
        this.expandedKeys.splice(index, 1)
      }
    },
    validMenuName (rule, value, callback) {
      if (this.$global.isBlank(value)) {
        callback(new Error('请输入菜单名称'))
      } else {
        // 校验手机号是否已经被用户占用
        this.$http({
          url: '/sys/menu/validateMenuName',
          method: 'POST',
          data: this.$qs.stringify({
            menuType: this.menuType,
            name: value,
            id: this.setForm.id
          })
        }).then(({ data }) => {
          if (this.$global.isNotBlank(data.data)) {
            callback(new Error(data.data))
          } else {
            callback()
          }
        }).catch(() => {
          callback()
        })
      }
    },
    clearSetForm () {
      this.$refs.setForm.resetFields()
      this.setForm = {
        showDialog: false,
        parentId: '',
        id: '',
        status: 1,
        moduleCode: '',
        name: '',
        url: '',
        comment: '',
        iconClass: ''
      }
    },
    saveData () {
      this.$refs.setForm.validate((valid) => {
        if (valid) {
          this.saveLoading = true
          let url = ''
          if (this.setForm.id == '') {
            //  新增
            url = '/sys/menu/saveAdd'
          } else {
            //  编辑
            url = '/sys/menu/saveEdit'
          }
          if (this.menuType === 2) {
            this.setForm.moduleName = this.$refs.moduleCode.selectedLabel
          }
          this.setForm.menuType = this.menuType
          this.$http({
            url: url,
            method: 'POST',
            data: this.setForm
          }).then(({ data }) => {
            this.saveLoading = false
            this.$message.success('操作成功')
            if (this.expandedKeys.indexOf(this.setForm.parentId) < 0) {
              this.expandedKeys.push(this.setForm.parentId)
            }
            this.getMenu()
            this.clearSetForm()
          }).catch(() => {
            this.saveLoading = false
          })
        }
      })
    },
    del (menu) {
      let that = this
      let ids = []
      this.getAllMenuIdByNode(menu, ids)
      let msg = '确认删除该菜单？'
      if (ids.length > 1) {
        msg = '删除该菜单，该菜单下的子菜单也一并删除，确认删除？'
      }
      this.$confirm(msg, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/sys/menu/delete',
          method: 'post',
          data: this.$qs.stringify({
            ids: ids.join(',')
          })
        }).then(({ data }) => {
          this.$message.success('删除成功')
          this.getMenu()
        }).catch(() => {
        })
      }).catch(() => {

      })
    },
    getAllMenuIdByNode (nodeData, ids) {
      ids.push(nodeData.id)
      let child = nodeData.childTrees
      if (child != null && child.length > 0) {
        child.forEach(item => {
          this.getAllMenuIdByNode(item, ids)
        })
      }
    },
    validButtonCode (rule, value, callback) {
      if (this.$global.isBlank(value)) {
        callback(new Error('请输入按钮编码'))
      } else {
        // 校验按钮编码是否已经被用户占用
        this.$http({
          url: '/sys/menu/validButtonCode/' + this.menuType,
          method: 'POST',
          data: this.$qs.stringify({
            code: value
          })
        }).then(({ data }) => {
          if (this.$global.isNotBlank(data.data)) {
            callback(new Error(data.data))
          } else {
            callback()
          }
        }).catch(() => {
          callback()
        })
      }
    },
    addButton (node) {
      this.buttonForm.showDialog = true
      this.buttonForm.menuId = node.id
    },
    clearButtonForm () {
      this.$refs.buttonForm.resetFields()
      this.buttonForm = {
        showDialog: false,
        name: '',
        code: '',
        url: '',
        type: null,
        menuId: null
      }
    },
    saveButton () {
      this.$refs.buttonForm.validate((valid) => {
        if (valid) {
          this.saveLoading = true
          this.$http({
            url: '/sys/menu/addButton',
            method: 'POST',
            data: this.buttonForm
          }).then(({ data }) => {
            this.saveLoading = false
            this.$message.success('操作成功')
            this.setCurrentNodeByKey(this.buttonForm.menuId, false)
            this.clearButtonForm()
          }).catch(() => {
            this.saveLoading = false
          })
        }
      })
    },
    deleteButton (button) {
      let that = this
      this.$confirm('确认删除该按钮？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/sys/menu/deleteButton',
          method: 'post',
          data: this.$qs.stringify({
            buttonId: button.id
          })
        }).then(({ data }) => {
          this.$message.success('删除成功')
          this.getButtonsByMenu(button.menuId)
        }).catch(() => {
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    rightClick (event, data, node, self) {
      //        this.testModeCode = row.testModeCode
      this.setCurrentNodeByKey(data.id, false)
      this.menuVisible = false // 先把模态框关死，目的是 第二次或者第n次右键鼠标的时候 它默认的是true
      this.menuVisible = true // 显示模态窗口，跳出自定义菜单栏
      event.preventDefault() // 关闭浏览器右键默认事件
      this.currentData = data
      var menu = document.querySelector('.menu')
      this.styleMenu(menu)
    },
    foo () {
      // 取消鼠标监听事件 菜单栏
      this.menuVisible = false
      document.removeEventListener('click', this.foo) // 关掉监听，
    },
    styleMenu (menu) {
      if (event.clientX > 1800) {
        menu.style.left = event.clientX - 100 + 'px'
      } else {
        menu.style.left = event.clientX - 45 + 'px'
      }
      document.addEventListener('click', this.foo) // 给整个document新增监听鼠标事件，点击任何位置执行foo方法
      console.log(event.clientY)
      if (event.clientY > 700) {
        menu.style.top = event.clientY - 270 + 'px'
      } else {
        menu.style.top = event.clientY - 160 + 'px'
      }
    },
    // 菜单调整排序，sortType：1：向下，-1：向上
    reSort (data, sortType) {
      this.$http({
        url: '/sys/menu/reSort/' + sortType,
        method: 'post',
        data: data
      }).then(({ data }) => {
        if (this.expandedKeys.indexOf(data.parentId) < 0) {
          this.expandedKeys.push(data.parentId)
        }
        this.getMenu()
      }).catch(() => {
      })
    }
  }
}
</script>
<style lang="less" scoped>
  /deep/.el-card {
    border: 1px solid #DDDDDD;
    border-radius: 6px;
    box-shadow: none !important;
    .el-card__header {
      font-size: 16px;
      padding-left: 0;
      padding-right: 0;
      margin: 0 20px 0 20px;
      font-family: Microsoft YaHei;
      border-bottom: 1px solid #DDDDDD;
      font-weight: 400;
      color: #333333;
    }
    /deep/.el-card__body{
      box-shadow: none;
    }
  }

  /deep/.power-card {
    .el-card__body{
      padding-right: 0 !important;
    }
  }
  /deep/ .el-tree-node {
    width: 100%;
    .el-tree-node__content {
      height: 40px;
      width: 100%;
      .el-tree-node__label{
        width: 90%;
      }
      .tree-node-text {
        line-height: 30px;
        min-width: 120px;
        max-width: 90%;
        border: 1px solid #c9d4e3;
        text-align: left;
        background-color: #EAF1FB;
        white-space:nowrap;
        overflow:hidden;
        text-overflow:ellipsis;
      }
      .tree-node-text.disabled{
        background-color: #eeeeee !important;
        border: 1px solid #dbdbdb !important;
      }
      .tree-node-text.selected {
        background-color: #BFDAFF !important;
        border: 2px solid #60a1f8 !important;
      }
    }
  }

  .power {
    overflow: auto;
    box-sizing: border-box;
    background: #FFFFFF;
    border-radius: 2px;
    padding-right: -20px;
    .li {
      margin-top: 30px;
    }
    .li:first-of-type {
      margin-top: 0;
    }
    /deep/.el-tree-node__label {
      padding-left: 5px;
    }
  }

  /deep/.el-tag {
    margin: 10px 0 10px 0;
    display: inline-block;
    width: auto;
    min-width: 160px;
    position: relative;
    .el-tag__close{
      position: absolute;
      right: 10px;
      top: 8px;
    }
  }

  .sort-buts {
    display: inline-block;
    vertical-align: top;
    margin-top: 8px;
    /deep/ .el-button{
      padding-top: 0px;
      padding-bottom: 0px;
    }
  }
  .contextmenu__item {
    display: block;
    text-align: left;
    line-height: 34px;
    padding-left: 10px;
    /deep/.el-button {
      text-align: left;
    }
  }
  .contextmenu__item:not(:last-child) {
    border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  }
  .menu {
    position: absolute;
    background-color: #fff;
    width: 135px;
    /*height: 106px;*/
    font-size: 12px;
    color: #444040;
    border-radius: 4px;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    border-radius: 3px;
    border: 1px solid rgba(0, 0, 0, 0.15);
    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.175);
    white-space: nowrap;
    z-index: 1000;
  }
  .contextmenu__item:hover {
    cursor: pointer;
    background: #66b1ff;
    border-color: #66b1ff;
    color: #ffffff;
    /deep/.el-button{
      background: #66b1ff;
      color: #ffffff;
    }
  }
</style>
