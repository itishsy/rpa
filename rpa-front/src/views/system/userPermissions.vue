<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20" style="padding:20px;">
      <div>
        <i class="el-icon-warning warning-icon"></i>
        <span class="tips-head">温馨提示：</span>
        <p class="tips">1、若是系统管理员，则用户将持所有数据权限，无需分配</p>
        <p class="tips">2、一般用户只需分配【客户、城市】中任意一类数据权限</p>
        <p class="tips">3、若勾选【全部客户】或【全部城市】，则该用户默认有存量及增量的客户/城市权限</p>
      </div>
      <el-row class="mt-20" v-if="userInfo">
        <el-col :span="6">用户姓名：{{ userInfo.username }}</el-col>
        <el-col :span="6">手机号：{{ userInfo.phoneNumber }}</el-col>
        <el-col :span="6">邮箱：{{ userInfo.email }}</el-col>
        <el-col :span="6">分配角色：{{ handleRoleName(userInfo) }}</el-col>
      </el-row>
      <el-row type="flex" align="middle" class="mt-20">
        <span style="width: 10px;background: #3E82FF;height: 20px;display: inline-block;"></span><span
          style="margin-left:10px;">数据权限</span>
        <span style="color:#999999;margin-left: 10px;">以下数据权限二选一</span>
      </el-row>
      <el-row type="flex" class="mt-10">
        <div :class="activeBtn == 0 ? 'btn-active' : 'btn-normal'" @click="changeActiveBtn(0)">
          <i class="el-icon-s-custom icon"></i>
          <span>客户</span>
          <span class="ml-5">{{ statistics.customer }}</span>
        </div>
        <div class="ml-20" :class="activeBtn == 1 ? 'btn-active' : 'btn-normal'" @click="changeActiveBtn(1)">
          <i class="el-icon-location icon"></i>
          <span>城市</span>
          <span class="ml-5">{{ statistics.city }}</span>
        </div>
      </el-row>
      <div v-if="activeBtn == 0">
        <el-row class="mt-20" type="flex" align="middle">
          <el-col :span="4">已选客户：{{ statisticsInfo1.checkedCustomer }}</el-col>
          <el-col :span="4">包含城市：{{ statisticsInfo1.totalCity }}</el-col>
          <el-col :span="4">包含账户：{{ statisticsInfo1.totalAccount }}</el-col>
          <el-col :span="4"><el-checkbox v-model="allPermission.allCust" :true-label="1"
              :false-label="0">全部客户</el-checkbox></el-col>
          <el-col :span="5" :offset="3">
            <div style="display: flex;">
              <el-input @keyup.enter.native="getCustomerTable" v-model.trim="keyword" clearable placeholder="关键字搜索客户名称"
                style="flex: 1;" @clear="getCustomerTable">
                <i @click="getCustomerTable" slot="suffix" class="el-input__icon el-icon-search f-cursor font-16 fw-blod"
                  style="color: #dbdbdb;"></i>
              </el-input>
              <el-button type="primary" size="mini" @click="confrimDistribute" class="ml-20">确定分配</el-button>
            </div>

          </el-col>
        </el-row>
        <div class="mt-10">
          <table class="cust-table-border w-p100" v-loading="tableData.loading" element-loading-text="拼命加载中"
            element-loading-spinner="el-icon-loading">
            <thead>
              <tr>
                <th style="width: 20px;">序号</th>
                <th style="width: 200px;">客户名称</th>
                <th style="width: 50px;">参保城市</th>
                <th style="width: 50px;">社保申报账户</th>
                <th style="width: 50px;">公积金申报账户</th>
                <th style="width: 20px;">勾选客户<el-checkbox class="ml-20" :true-labe="true" :false-labe="false"
                    v-model="checkAll1" @change="val => handleCheckAllChange(val)"></el-checkbox></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in tableData.customer" :key="item.custId">
                <td>{{ index + 1 }}</td>
                <td>{{ item.custName }}</td>
                <td>{{ item.cityNumber || '-' }}</td>
                <td>{{ item.socialAccountNumber || '-' }}</td>
                <td>{{ item.accfundAccountNumber || '-' }}</td>
                <td><el-checkbox v-model="item.checked" :true-labe="true" :false-labe="false"
                    @change="val => handleCheckChange(val, index)"></el-checkbox></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <div v-else>
        <el-row class="mt-20" type="flex" align="middle">
          <el-col :span="4">已选城市：{{ statisticsInfo2.checkedCity }}</el-col>
          <el-col :span="4">包含客户：{{ statisticsInfo2.totalCustomer }}</el-col>
          <el-col :span="4">包含账户：{{ statisticsInfo2.totalAccount }}</el-col>
          <el-col :span="4"><el-checkbox v-model="allPermission.allCity" :true-label="1"
              :false-label="0">全部城市</el-checkbox></el-col>
          <el-col :span="5" :offset="3">
            <div style="display: flex;">
              <el-input @keyup.enter.native="getCityTable" v-model.trim="keyword2" clearable placeholder="关键字搜索省/城市"
                style="flex: 1;" @clear="getCityTable">
                <i @click="getCityTable" slot="suffix" class="el-input__icon el-icon-search f-cursor font-16 fw-blod"
                  style="color: #dbdbdb;"></i>
              </el-input>
              <el-button type="primary" size="mini" @click="confrimDistribute" class="ml-20">确定分配</el-button>
            </div>

          </el-col>
        </el-row>
        <div class="mt-10">
          <table class="cust-table-border w-p100" v-loading="tableData.loading2" element-loading-text="拼命加载中"
            element-loading-spinner="el-icon-loading">
            <thead>
              <tr>
                <th style="width: 20px;">序号</th>
                <th style="width: 50px;">所在省</th>
                <th style="width: 50px;">参保城市</th>
                <th style="width: 50px;">客户数</th>
                <th style="width: 50px;">社保申报账户</th>
                <th style="width: 50px;">公积金申报账户</th>
                <th style="width: 20px;">勾选城市<el-checkbox class="ml-20" :true-labe="true" :false-labe="false"
                    v-model="checkAll2" @change="val => handleCheckAllChange(val)"></el-checkbox></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in tableData.city" :key="item.addrId">
                <td>{{ index + 1 }}</td>
                <td>{{ item.provinceName }}</td>
                <td>{{ item.addrName }}</td>
                <td>{{ item.custNumber || '-' }}</td>
                <td>{{ item.socialAccountNumber || '-' }}</td>
                <td>{{ item.accfundAccountNumber || '-' }}</td>
                <td><el-checkbox v-model="item.checked" :true-labe="true" :false-labe="false"
                    @change="val => handleCityCheckChange(val, index)"></el-checkbox></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

    </div>
  </div>
</template>
<script>

export default {
  data() {
    return {
      checkedAllCity: false,     //勾选为全部城市
      checkedAllCustomer: false,     //勾选为全部客户
      activeBtn: 0,    //0代表勾选了客户，1代表勾选了城市
      keyword: '',     //搜索关键字
      keyword2: '',     //搜索关键字2
      allPermission: {
        allCust: 0,    //第一个表格是否全部勾选
        allCity: 0,    //第二个表格是否全部勾选
      },
      testCheck: false,
      userInfo: null,      //顶部用户信息
      statistics: {
        city: 0,     //城市统计
        customer: 0    //客户统计
      },
      tableData: {
        loading: false,    //客户表格Loading
        loading2: false,     //城市表格loading
        city: [],      //城市数据
        customer: []     //客户数据
      },
      statisticsInfo1: {
        checkedCustomer: '-',     //所选客户
        totalCity: '-',       //包含城市
        totalAccount: '-',    //包含账户
      },
      statisticsInfo2: {
        checkedCity: '-',     //所选城市
        totalCustomer: '-',       //包含客户
        totalAccount: '-',    //包含账户
      },
      citySelectStorage: [],     //城市勾选缓存
      customerSelectStorage: [],    //客户勾选缓存
      checkAll1: false,
      checkAll2: false,
    }
  },
  computed: {
    userId() {
      return this.$route.query.userId
    },
  },
  watch: {

  },
  created() {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(
        tabs[this.$route.meta.parentPath]
      )
    }
    this.pathData.push({ name: '用户权限' })
    let that = this
    this.$nextTick(() => {
      // that.getTableData()
      that.getUserDetail()
      that.getCustomerCount()
      that.getCustomerTable()
      that.getCityCount()
      that.getCityTable()
      that.getUserAllPermissionSetting()
    })

  },
  mounted() {

  },
  methods: {
    onChangeAllCust(e) {
      console.log(e)
    },
    //运营后台用户管理-用户权限-获取用户全部权限配置信息
    getUserAllPermissionSetting() {
      let that = this
      that.$http({
        url: `policy/sys/user/getUserAllPermissionSetting/${this.userId}`,
        method: 'POST',
      }).then(({ data }) => {
        this.allPermission = data.data
      }).catch(() => {
      })
    },
    //切换客户/城市
    changeActiveBtn(type) {
      let that = this
      that.activeBtn = type
      if (type == 0) {
        that.getCustomerTable()
        that.getCustomerCount()
      } else {
        that.getCityTable()
        that.getCityCount()
      }
      //每次切换都需要清空全部勾选的项目
      that.handleCheckAllChange(false)
      that.checkAll1 = false
      that.checkAll2 = false
    },
    //确定分配
    confrimDistribute() {
      let that = this
      let type = this.activeBtn   //0客户 1城市
      //获取勾选的项目 组成对应的ids
      let ids = []
      if (type == 0) {
        if (this.tableData.customer.filter(item => item.checked).length == 0 && !this.allPermission.allCust) {
          that.$message.warning('请先勾选需要分配的客户权限')
          return
        }
        //如果存在客户权限缓存则使用缓存，否则使用列表选择项（缓存只会在列表不进行过滤查询时刷新）
        ids = this.customerSelectStorage.length > 0 ? this.customerSelectStorage : this.tableData.customer.filter(item => item.checked).map(item => item.custId)
      } else {
        if (this.tableData.city.filter(item => item.checked).length == 0 && !this.allPermission.allCity) {
          that.$message.warning('请先勾选需要分配的城市权限')
          return
        }
        //如果存在城市权限缓存则使用缓存，否则使用列表选择项（缓存只会在列表不进行过滤查询时刷新）
        ids = this.citySelectStorage.length > 0 ? this.citySelectStorage : this.tableData.city.filter(item => item.checked).map(item => item.addrId)
      }
      // ids = type ?  this.tableData.city.filter(item => item.checked).map(item => item.addrId) : this.tableData.customer.filter(item => item.checked).map(item => item.custId)
      let url = type ? `/policy/sys/user/saveUserAddr/${this.userId}/${this.allPermission.allCity}/?addrIds=${ids.toString()}` : `/policy/sys/user/saveUserCust/${this.userId}/${this.allPermission.allCust}/?custIds=${ids.toString()}`
      if (this.statistics.city && type == 1) {
        //当前为城市 而且已经有城市统计数据 直接弹框
        that.$http({
          url: url,
          method: 'post',
        }).then(res => {
          if (res.data.code == 200) {
            that.getCustomerCount()
            that.getCityCount()
            if (type == 0) {
              that.$message.success('客户权限分配成功')
              that.getCustomerTable()
            } else {
              that.$message.success('城市权限分配成功')
              that.getCityTable()
            }
          }
        })
      } else if (this.statistics.customer && type == 0) {
        //当前为客户 而且已经有客户统计数据 直接弹框
        that.$http({
          url: url,
          method: 'post',
        }).then(res => {
          if (res.data.code == 200) {
            that.getCustomerCount()
            that.getCityCount()
            if (type == 0) {
              that.$message.success('客户权限分配成功')
              that.getCustomerTable()
            } else {
              that.$message.success('城市权限分配成功')
              that.getCityTable()
            }
          }
        })
      } else if (!this.statistics.customer && !this.statistics.city) {
        //从未分配过权限
        that.$http({
          url: url,
          method: 'post',
        }).then(res => {
          if (res.data.code == 200) {
            that.getCustomerCount()
            that.getCityCount()
            if (type == 0) {
              that.$message.success('客户权限分配成功')
              that.getCustomerTable()
            } else {
              that.$message.success('城市权限分配成功')
              that.getCityTable()
            }
          }
        })
      }
      else {
        this.$confirm(type ? `一旦切换，${that.userInfo.username}的{客户}权限将被清空。是否确定换成{城市}权限？` : `一旦切换，${that.userInfo.username}的{城市}权限将被清空。是否确定换成{客户}权限？`, {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          showClose: false,
          customClass: 'pal-confirm',
          type: 'warning',
        })
          .then(() => {
            that.$http({
              url: url,
              method: 'post',
            }).then(res => {
              if (res.data.code == 200) {
                that.getCustomerCount()
                that.getCityCount()
                if (type == 0) {
                  that.$message.success('客户权限分配成功，已清空城市权限')
                  that.getCustomerTable()
                } else {
                  that.$message.success('城市权限分配成功，已清空客户权限')
                  that.getCityTable()
                }
              }
            })
          })
          .catch(() => { })
      }

    },
    //根据userId获取权限用户详情
    getUserDetail() {
      let that = this
      that.$http({
        url: `/sys/user/getAdminUser/${this.userId}`,
        method: 'POST',
      }).then(({ data }) => {
        console.log('用户详情', data)
        this.userInfo = data.data
      }).catch(() => {
        // that.addCustomerForm.saveLoading = false
      })
    },
    //处理角色回显
    handleRoleName(row) {
      var arr = row.roles ? row.roles : []
      var res = []
      arr.map(item => {
        res.push(item.name)
      })
      return res.join('，')
    },
    //参保城市数统计
    getCityCount() {
      let that = this
      that.$http({
        url: `/policy/sys/user/addrNumber/${this.userId}`,
        method: 'POST',
      }).then(({ data }) => {
        this.statistics.city = data.data
      }).catch(() => {
      })
    },
    //客户数统计
    getCustomerCount() {
      let that = this
      that.$http({
        url: `/policy/sys/user/custNumber/${this.userId}`,
        method: 'POST',
      }).then(({ data }) => {
        this.statistics.customer = data.data
      }).catch(() => {
      })
    },
    //获取参保城市权限列表
    getCityTable() {
      let that = this
      that.tableData.loading2 = true
      that.$http({
        url: `/policy/sys/user/getUserAddrList/${this.userId}`,
        method: 'POST',
        params: {
          searchText: this.keyword2
        }
      }).then(({ data }) => {
        this.tableData.city = data.data.sort((a, b) => {
          if (a.checked && !b.checked) {
            return -1; // a 排在 b 前面
          } else if (!a.checked && b.checked) {
            return 1; // b 排在 a 前面
          } else {
            return 0; // 保持原有顺序
          }
        })
        if (!this.keyword2) {
          this.citySelectStorage = data.data.map(item => {
            if (item.checked) {
              return item.addrId
            }
          }).filter(item => item !== undefined)
          console.log('初始化城市缓存--------', this.citySelectStorage)
        }

        that.caluCityTotal()
        //最后刷新全部权限
        that.getUserAllPermissionSetting()
        setTimeout(() => {
          that.tableData.loading2 = false
        }, 300);
      }).catch(() => {
        setTimeout(() => {
          that.tableData.loading2 = false
        }, 300);
      })
    },
    //获取客户权限列表
    getCustomerTable() {
      let that = this
      that.tableData.loading = true
      that.$http({
        url: `/policy/sys/user/getUserCustList/${this.userId}`,
        method: 'POST',
        params: {
          searchText: this.keyword
        }
      }).then(({ data }) => {
        that.tableData.customer = data.data.sort((a, b) => {
          if (a.checked && !b.checked) {
            return -1; // a 排在 b 前面
          } else if (!a.checked && b.checked) {
            return 1; // b 排在 a 前面
          } else {
            return 0; // 保持原有顺序
          }
        })
        if (!this.keyword) {
          this.customerSelectStorage = data.data.map(item => {
            if (item.checked) {
              return item.custId
            }
          }).filter(item => item !== undefined)
          console.log('初始化客户缓存--------', this.customerSelectStorage)
        }
        that.caluCustomerTotal()
        //最后刷新全部权限
        that.getUserAllPermissionSetting()
        setTimeout(() => {
          that.tableData.loading = false
        }, 300);

      }).catch(() => {
        setTimeout(() => {
          that.tableData.loading = false
        }, 300);
      })
    },
    //客户权限勾选
    handleCheckChange(val, index) {
      console.log(val, index)
      let item = this.tableData.customer[index]
      item.checked = val
      if (val) {
        //在城市缓存勾选中添加 否则删除
        this.customerSelectStorage.push(item.custId)
      } else {
        if (this.customerSelectStorage.indexOf(item.custId) != -1) {
          this.customerSelectStorage.splice(this.customerSelectStorage.indexOf(item.custId), 1)
        }
      }
      console.log('看看客户缓存------', this.customerSelectStorage)
      // item.checked = val ? this.checkIsAllSelect(item.fieldOptions) : false
      this.caluCustomerTotal()
    },
    //计算客户权限统计
    caluCustomerTotal() {
      //循环计算出客户权限列表中已选客户、包含城市、包含账户
      let checkedArray = this.tableData.customer.filter(item => item.checked)
      this.statisticsInfo1 = {
        checkedCustomer: checkedArray.length,
        totalCity: checkedArray.reduce((acc, item) => acc + item.cityNumber, 0),
        totalAccount: checkedArray.reduce((acc, item) => acc + item.accfundAccountNumber + item.socialAccountNumber, 0)
      }
      // console.log('statisticsInfo1', this.statisticsInfo1)
    },
    //城市权限勾选
    handleCityCheckChange(val, index) {
      let item = this.tableData.city[index]
      item.checked = val
      console.log(val, index, item.addrId)
      if (val) {
        //在城市缓存勾选中添加 否则删除
        this.citySelectStorage.push(item.addrId)
      } else {
        if (this.citySelectStorage.indexOf(item.addrId) != -1) {
          this.citySelectStorage.splice(this.citySelectStorage.indexOf(item.addrId), 1)
        }
      }
      console.log('看看城市缓存------', this.citySelectStorage)
      // item.checked = val ? this.checkIsAllSelect(item.fieldOptions) : false
      this.caluCityTotal()
    },
    //计算城市权限统计
    caluCityTotal() {
      //循环计算出客户权限列表中已选城市、包含客户、包含账户
      let checkedArray = this.tableData.city.filter(item => item.checked)
      this.statisticsInfo2 = {
        checkedCity: checkedArray.length,
        totalCustomer: checkedArray.reduce((acc, item) => acc + item.custNumber, 0),
        totalAccount: checkedArray.reduce((acc, item) => acc + item.accfundAccountNumber + item.socialAccountNumber, 0)
      }
      // console.log('statisticsInfo2', this.statisticsInfo2)
    },
    //客户、城市权限列表 勾选全部、取消勾选全部 
    handleCheckAllChange(val) {
      if (this.activeBtn == 0) {
        for (const iterator of this.tableData.customer) {
          iterator.checked = val
        }
        this.caluCustomerTotal()
      } else {
        for (const iterator of this.tableData.city) {
          iterator.checked = val
        }
        this.caluCityTotal()
      }

    }
  }
}
</script>
<style lang="less" scoped>
.warning-icon {
  font-size: 20px;
  color: #f6891e;
}

.tips-head {
  font-size: 16px;
}

.tips {
  line-height: 22px;
  margin-top: 5px;
}

.btn-mix {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 140px;
  height: 40px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;

  .icon {
    font-size: 18px;
    padding-right: 5px
  }
}

.btn-normal {
  .btn-mix();
  background-color: #eff3fc;
  color: #303133;

  .icon {
    color: #8a8a8a;
  }
}

.btn-active {
  .btn-mix();
  background-color: #3e82ff;
  color: #ffffff;

  .icon {
    color: #e6e6e6;
  }
}

table {
  th {
    text-align: center;
  }

  text-align: center;
}
</style>
