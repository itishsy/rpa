<template>
  <div class="spl-container cust-container">
    <!-- 导航 -->
    <breadcrumb :data="pathData" isShowBack="true"></breadcrumb>

    <div class="pd-20">
      <el-row>
        <el-col :span="15">
          <el-card class="box-card mr-5">
            <div slot="header" class="clearfix text-left">
              <span>角色列表</span>
            </div>

            <div style="display: flex">
              <el-input size="medium" placeholder="搜索角色名称/描述" @keyup.enter.native="getTableData" class="w-310 mb-20" v-model.trim="searchWord" clearable></el-input>
              <div>
                <el-button type="primary" size="small" class="opt-btn ml-20" @click.prevent="getTableData()">查询</el-button>
              </div>
              <div  style="flex:1;display: inline-block;text-align: right;padding-right:20px;">
                <el-button size="small" type="primary" class="opt-btn" @click="handleAdd">添加角色</el-button>
              </div>
            </div>
            <!--表格-->
            <!-- @row-click="handlePower"-->
            <dTable @fetch="getTableData" ref="empTable" :tableHeight="tableHeight" rowKey="id" @row-click="handlePower">
              <u-table-column prop="name" label="角色名称" min-width="130" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span class="f-cursor text-blue">{{scope.row.name}}</span>
                </template>
              </u-table-column>
              <u-table-column prop="comment" label="角色描述" min-width="200"
                              :show-overflow-tooltip="true"></u-table-column>
              <u-table-column prop="customerName" label="是否启用" min-width="100" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <p v-if="scope.row.id==1">{{scope.row.status==1?'启用':'停用'}}</p>
                  <el-switch v-else
                             v-model="scope.row.status"
                             :active-value="1"
                             active-color="#13ce66"
                             inactive-color="#dbdbdb" @change="(value)=>handleEdit(scope.row, 'switch', value)">
                  </el-switch>
                </template>
              </u-table-column>
              <u-table-column fixed="right" label="操作" width="130px">
                <template slot-scope="scope">
                  <span class="text-blue f-cursor" v-if="scope.row.id!=1" @click="handleEdit(scope.row)">编辑</span>
                </template>
              </u-table-column>
            </dTable>
          </el-card>
        </el-col>
        <el-col :span="9">
          <el-card class="box-card ml-5 power-card" v-loading="treeLoading" element-loading-spinner="el-icon-loading" element-loading-background="rgba(244, 248, 248, 0.5)">
            <div slot="header" class="clearfix text-left">
              <span>菜单分配—<span class="text-blue">{{powerSet.name}}</span></span>
            </div>
            <div style="display: flex">
              <el-input size="medium" placeholder="搜索菜单名称" class="w-240 ml-20 mb-20" @keyup.enter.native="filterTree" v-model.trim="menuName" clearable></el-input>
              <div>
                <el-button type="primary" size="small" class="opt-btn ml-20" @click.prevent="filterTree">查询</el-button>
              </div>
              <div  style="flex:1;display: inline-block;text-align: right;padding-right:20px;">
                <el-button size="small" type="primary" class="opt-btn" v-if="powerSet.id!=1" @click="handlePowerSet()" :loading="saveLoading">保存</el-button>
              </div>
            </div>
            <div class="power" :style="{height: treeHeight}">
              <el-tree ref="roleMenuTree"
                       :data="powerSet.menuList"
                       show-checkbox
                       node-key="perId"
                       :props="props"
                       default-expand-all
                       :check-on-click-node="true"
                       :default-checked-keys="defaultCheckedKeys"
                       :expand-on-click-node="false"
                       :filter-node-method="filterNode">
                <span class="el-tree-node__label" slot-scope="{ node, data }">
                  <i class="ic-tree-node mr-5" v-if="data.perType==1"></i>
                  <i class="el-icon-s-tools mr-5" v-if="data.perType==2"></i>
                  <span>{{ node.label }}</span>
                </span>
              </el-tree>
            </div>
          </el-card>
        </el-col>
      </el-row>

    </div>
    <!--添加、编辑角色-->
    <el-drawer :title="setForm.id==''?'新增角色':'编辑角色'" :visible.sync="setForm.showDialog" :wrapperClosable="false" custom-class="spl-filter-drawer" :show-close="true" :before-close="clearSetForm">

      <div class="drawer-body pt-20">
        <el-form ref="setForm" :model="setForm" class="filter-form">
          <!--<el-form-item label="角色编码：" prop="code" :rules="[{ required: true, validator: validatorRoleCode, trigger:'blur'}]">
            <el-input size="medium" placeholder="请输入" class="input-item" maxlength="50" v-model="setForm.code"></el-input>
          </el-form-item>-->
          <label class="required item-label">角色名称</label>
          <el-form-item label="" prop="name" :rules="[{ required: true, message: '请输入角色名称',trigger:'blur'}]">
            <el-input size="medium" placeholder="请输入" maxlength="25" v-model="setForm.name"></el-input>
          </el-form-item>
          <label class="item-label">角色描述</label>
          <el-form-item label="" prop="comment" style="margin-bottom: 10px;">
            <el-input
              type="textarea"
              :autosize="{ minRows: 10, maxRows: 10}"
              placeholder="请输入"
              show-word-limit
              maxlength="200"
              v-model="setForm.comment">
            </el-input>
          </el-form-item>
          <el-form-item label="" prop="status">
            <el-checkbox :true-label="1" :false-label="0" :disabled="isAdmin==1" v-model="setForm.status">是否启用</el-checkbox>
          </el-form-item>
        </el-form>
      </div>
      <div class="drawer-footer-buts">
        <el-button class="mr-12" size="small" @click="clearSetForm()">取 消</el-button>
        <el-button size="small" type="primary" @click="vaildSetForm()" :loading="saveLoading">确定</el-button>
      </div>
    </el-drawer>
    <!--</el-dialog>-->
  </div>
</template>
<script>
  import dTable from '../../components/common/table'

  export default {
    components: {dTable},
    data() {
      return {
        searchWord: '',
        menuName: '',
        saveLoading: false,
        treeLoading: false,
        pathData: [],
        value: '',
        setForm: {
          showDialog: false,
          id: '',
          code: '',
          name: '',
          comment: '',
          status: 1// 是否启用 1：启用，0：未启用，默认启用
        },
        isAdmin: 0,
        powerSet: {
          showDrawer: false,
          id: '',
          name: '',
          menuList: []
        },
        defaultCheckedKeys: [],
        props: {
          label: 'name',
          children: 'subRoleResourceVOS',
        }
      }
    },
    computed: {
      tableHeight: function () {
        return this.$global.bodyHeight - 400 + 'px'
      },
      treeHeight: function () {
        return this.$global.bodyHeight - 310 + 'px'
      }
    },
    created() {
      let tabs = this.$store.state.tabs
      if (tabs) {
        this.pathData = tabs[this.$route.path]
      }
      this.$nextTick(() => {
        this.getTableData()
      })
    },
    watch: {
      'powerSet.menuList': {
        handler: function (nv, ov) {
          var keys = []
          nv.forEach((item) => {
            if (item.subRoleResourceVOS && item.subRoleResourceVOS.length>0) {
              this.parseCheckedKey(keys, item.subRoleResourceVOS)
            } else {
              if (item.checked) {
                keys.push(item.perId)
              }
            }
          })
          this.defaultCheckedKeys = keys
        },
        deep: true
      }
    },
    methods: {
      parseCheckedKey(keys, menus) {
        menus.forEach((item) => {
          if (item.subRoleResourceVOS && item.subRoleResourceVOS.length>0) {
            this.parseCheckedKey(keys, item.subRoleResourceVOS)
          } else {
            if (item.checked) {
              keys.push(item.perId)
            }
          }
        })
      },
      getTableData(params = []) {
        this.powerSet.name = ''
        this.powerSet.menuList = []
        let _self = this
        params = [{property: 'searchText', value: this.searchWord}]
        this.$refs.empTable.fetch({
          query: params,
          method: 'post',
          url: '/sys/role/page',
          callback: function (res) {
            let rows = res.rows
            // if (rows!=null && rows.length > 0) {
            //   _self.handlePower(rows[0])
            // }
          }
        })
      },
      validatorRoleCode (rule, value, callback) {
        if (this.$global.isBlank(value)) {
          callback(new Error('请输入角色编码'))
        } else {
          this.$http({
            method: 'post',
            url: '/sys/role/validatorRoleCode',
            data: this.$qs.stringify({roleId: this.setForm.id, roleCode: this.setForm.code })
          }).then(({data}) => {
            if (this.$global.isNotBlank(data.data)) {
              callback(new Error(data.data))
            } else {
              callback()
            }
          }).catch(() => {
            callback(new Error("系统异常"))
          })
        }
      },
      handleAdd() {
        this.isAdmin = false
        this.setForm.showDialog = true
      },
      handleEdit(row, type) {
        console.log(row)
        this.setForm.id = row.id
        this.setForm.code = row.code
        this.setForm.name = row.name
        this.setForm.comment = row.comment
        this.setForm.status = row.status
        if (type == 'switch') {
          this.saveSetForm('switch')
        } else {
          this.isAdmin = row.id==1
          this.setForm.showDialog = true
        }
      },

      clearSetForm(tab) {
        this.setForm = {
          showDialog: false,
          id: '',
          code: '',
          name: '',
          comment: '',
          status: 1// 是否启用 1：启用，0：未启用，默认启用
        }
        this.$refs.setForm.resetFields()
      },
      vaildSetForm() {
        this.$refs.setForm.validate((valid) => {
          if (valid) {
            this.saveSetForm()
          }
        })
      },
      saveSetForm(type) {
        if (type != 'switch') {
          this.saveLoading = true
        }
        let url = ''
        if (this.setForm.id == '') {
          //  新增
          url = '/sys/role/saveAdd'
        } else {
          //  编辑
          url = '/sys/role/updateRole'
        }
        this.$http({
          url: url,
          method: 'POST',
          data: {
            id: this.setForm.id,
            code: this.setForm.code,
            name: this.setForm.name,
            comment: this.setForm.comment,
            status: Number(this.setForm.status)
          }
        }).then(({data}) => {
          if (type != 'switch') {
            this.saveLoading = false
            this.$message.success('操作成功')
            this.getTableData()
            this.clearSetForm()
          }
        }).catch(() => {
          if (type != 'switch') {
            this.saveLoading = false
          }
        });
      },

      // 删除
      handleDel(row) {
        if(row.users.length>0){
          this.$message.warning('该角色正在被使用，若要删除，请先去人员设置中删除该角色对应的人员！')
          return false
        }
        this.$confirm('<p class="title">确认删除角色？</p><p class="text">确定要删除该条角色信息吗？</p>', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: '',
          customClass: 'cust-confirm-msg',
          dangerouslyUseHTMLString: true,
          closeOnClickModal: false,
          closeOnPressEscape: false
        }).then(() => {
          this.doDel(row)
        }).catch(() => {

        });
      },
      doDel(row){
        this.$http({
          url: '/sys/role/delete/'+row.id,
          method: 'POST',
          data: {
          }
        }).then(({data}) => {
          this.$message.success('操作成功')
          this.getTableData()
        }).catch(() => {

        });
      },

      //  授权
      handlePower(row, column, event) {
        this.treeLoading = true
        this.$http({
          url: '/sys/role/getRoleResource/'+row.id,
          method: 'POST',
          data: {
          }
        }).then(({data}) => {
          this.treeLoading = false
          this.powerSet.id = row.id
          this.powerSet.name = row.name
          this.powerSet.admin = row.id==1
          let menuList = data.data
          if (row.id == 1) {
            this.setDisabled(menuList)
          }
          this.powerSet.menuList = menuList
          this.menuName = ''
        }).catch(() => {
          this.treeLoading = false
        });
      },
      setDisabled(menuList) {
        menuList.forEach(item => {
          item.disabled = true
          let subRoleResourceVOS = item.subRoleResourceVOS
          if (subRoleResourceVOS != null && subRoleResourceVOS.length > 0) {
            this.setDisabled(subRoleResourceVOS)
          }
        })
      },
      filterTree() {
        this.$refs.roleMenuTree.filter(this.menuName)
      },
      filterNode(value, data) {
        if (!value) return true;
        return data.name.indexOf(value) !== -1;
      },
      handlePowerSet () {
        this.saveLoading = true
        let menuIds = []
        let buttonIds = []
        var checkedNodes = this.$refs.roleMenuTree.getCheckedNodes(false, true)
        checkedNodes.forEach((item, index) => {
          if (item.perType == 1) {
            menuIds.push(item.id)
          } else {
            buttonIds.push(item.id)
          }
        })
        this.$http({
          url: '/sys/role/saveRoleResource',
          method: 'POST',
          data: this.$qs.stringify({
            roleId: this.powerSet.id,
            resourceIds: menuIds.join(","),
            buttonIds: buttonIds.join(",")
          })
        }).then(({data}) => {
          this.saveLoading = false
          this.powerSet.showDrawer = false
          this.$message.success('操作成功')
        }).catch(() => {
          this.saveLoading = false
        });
      }
    },
  }
</script>
<style lang="less" scoped>
  .input-item {
    width: 310px;
  }
  .required:before {
    content: '*';
    color: #F56C6C;
    margin-right: 6px;
  }

  .required:after {
    content: '';
  }
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

  .search-but {
    background: #3E82FF !important;
    color: #ffffff !important;
    border-radius: 0 3px 3px 0;
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
  /deep/.pal-table .el-table__body-wrapper .current-row{
    background-color: #e8effc;
  }
  /deep/.el-table__fixed-right .current-row{
    background-color: #e8effc;
  }
</style>
