<template>
  <div class="spl-container">
    <!-- 导航 -->
    <breadcrumb :data="pathData">
<!--      <template slot="breadcrumb-btn">
        <el-button type="primary" class="mr-15" size="medium" v-if="$global.hasPermission('declareAccountAdd')" @click="handleAddOrUpdateOrView('add')">新增账户</el-button>
      </template>-->
    </breadcrumb>

    <div class="content">
      <el-row>
        <el-col :span="12" v-for="(item, index) in listData" :key="index" style="padding: 0 15px;">
          <div class="list-item">
            <div class="icon-div"><i class="el-icon-office-building text-blue font-30"></i></div>
            <div class="info">
              <p class="name" :title="item.orgName">{{item.orgName}}</p>
              <div class="type-item" v-if="item.socialAccounts.length>0">
                <img class="ic-type-item" src="../../assets/images/icons/ic-social-item2.png" alt="">
                <span class="type">社保</span>
                <span>{{item.addressName}}</span>
                <span class="num" :title="item.socialAccountsStr">{{item.socialAccountsStr}}</span>
              </div>
              <div class="type-item" v-if="item.accfundAccounts.length>0">
                <img class="ic-type-item" src="../../assets/images/icons/ic-accfund-item2.png" alt="">
                <span class="type">公积金</span>
                <span>{{item.addressName}}</span>
                <span class="num" :title="item.accfundAccountsStr">{{item.accfundAccountsStr}}</span>
              </div>
              <div class="type-item-opt">
<!--                <span class="text-blue f-cursor opt-text-btn" v-if="$global.hasPermission('declareAccountEdit')" @click="handleAddOrUpdateOrView('edit', item)">编辑</span>-->
                <span class="text-blue f-cursor ml-40 opt-text-btn" v-if="$global.hasPermission('declareAccountView')" @click="handleAddOrUpdateOrView('view', item)">查看</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!--新增、编辑-->
    <viewEditDeclareAccount ref="viewEditDeclareAccount" :detailData="setForm" @success="viewEditSuccess"></viewEditDeclareAccount>

  </div>
</template>

<script>
import viewEditDeclareAccount from './components/viewEditDeclareAccount'
export default {
  components: { viewEditDeclareAccount },
  data () {
    return {
      pathData: [],
      setForm: null,
      listData: []
    }
  },
  computed: {
  },
  created () {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = tabs[this.$route.path]
    }
    this.getList()
  },
  watch: {

  },
  methods: {
    // 获取列表数据
    getList () {
      let that = this
      this.$http({
        url: '/policy/declareAccount/list',
        method: 'post'
      }).then(({ data }) => {
        let res = data.data
        let socialAccountsArr = []; let accfundAccountsArr = []
        res.map(item => {
          socialAccountsArr = []
          accfundAccountsArr = []
          item.socialAccounts.map(item1 => {
            socialAccountsArr.push(item1.accountNumber)
          })
          item.accfundAccounts.map(item2 => {
            accfundAccountsArr.push(item2.accountNumber)
          })
          item.socialAccountsStr = socialAccountsArr.join('；')
          item.accfundAccountsStr = accfundAccountsArr.join('；')
        })
        that.listData = data.data
      }).catch((data) => {
      })
    },
    // 新增、编辑、查看
    handleAddOrUpdateOrView (type, item) {
      if (type == 'edit' || type == 'view') {
        this.setForm = {
          show: true,
          type: type,
          orgName: item.orgName,
          taxNum: item.taxNumber,
          addrName: item.addressName,
          addrId: item.addressId,
          socialChecked: !(item.socialAccounts && item.socialAccounts.length > 0),
          accfundChecked: !(item.accfundAccounts && item.accfundAccounts.length > 0),
          socialList: item.socialAccounts,
          accfundList: item.accfundAccounts,
          custId: '',
          id: '',
          isDisabled: false
        }
        if (type == 'edit') {
          this.setForm.custId = item.custId
          this.setForm.id = item.id
        } else {
          this.setForm.isDisabled = true
        }
      } else if (type == 'add') {
        this.setForm = {
          show: true,
          type: type,
          orgName: '',
          taxNum: '',
          addrName: '',
          addrId: '',
          socialChecked: false,
          accfundChecked: false,
          socialList: [],
          accfundList: [],
          custId: '',
          id: '',
          isDisabled: false
        }
      }
      this.$refs.viewEditDeclareAccount.setForm.show = true
    },
    viewEditSuccess () {
      this.getList()
    }
  }
}
</script>

<style scoped lang="less">
  .content{
    padding: 0px 10px 30px 10px;
  }
.list-item{
  display: flex;
  border: 1px solid #ebeef5;
  padding: 20px;
  border-radius: 6px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.10);
  margin-top: 30px;
  height: 200px;
  box-sizing: border-box;
  position: relative;
  .icon-div{
    padding-right: 20px;
  }
  .info{
    flex: 1;
    width: calc(100% - 100px);
    .name{
      font-size: 18px;
      font-weight: bold;
      margin-bottom: 35px;
      white-space:nowrap; //不换行
      overflow:hidden;//内容超出隐藏
      text-overflow:ellipsis; //显示省略号
    }
  }
  .type-item{
    display: flex;
    margin-top: 25px;
    width: 100%;
    .type{
      width: 80px;
    }
    .num{
      flex: 1;
      margin-left: 40px;
      white-space:nowrap; //不换行
      overflow:hidden;//内容超出隐藏
      text-overflow:ellipsis; //显示省略号
    }
    .ic-type-item{
      width: 32px;
      height: 32px;
      background-size: 100% 100%;
      vertical-align: middle;
      margin-top: -8px;
      margin-right: 3px;
    }
  }
  .type-item-opt{
    position: absolute;
    bottom: 20px;
    right: 20px;
    .opt-text-btn:hover{
      opacity: 0.8;
    }
  }
}

</style>
