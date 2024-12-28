<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="content-box">
      <div class="content-box-l" :style="{height:mianContentHeight + 'px'}">
        <div style="padding: 0 10px;">
          <div class="tab-div">
            <div class="tab-item" :class="{'active': tabActive===0}" @click="handleUnRead">
              <img v-if="tabActive===0" src="@/assets/images/icons/ic-unread-white.png" width="26px" height="26px" alt="">
              <img v-else src="@/assets/images/icons/ic-unread.png" width="26px" height="26px" alt="">
              <span class="ml-5">未读 {{unReadCount}}</span>
            </div>
            <div class="tab-item" :class="{'active': tabActive===1}" @click="handleRead">
              <img v-if="tabActive===1" src="@/assets/images/icons/ic-readed-white.png" width="22px" height="22px" alt="">
              <img v-else src="@/assets/images/icons/ic-readed.png" width="22px" height="22px" alt="">
              <span class="ml-5">已读 {{readCount}}</span>
            </div>
          </div>
        </div>
        <div class="msg-box">
          <div class="opt-div">
            <div class="flex1">
              <el-checkbox v-model="checkAll" @change="handleCheckAll" v-if="tabActive == 0">全选</el-checkbox>
            </div>
            <el-button class="s-btn" style="margin-right: 0!important;" @click="adjustStatus(1)" v-if="tabActive == 0">标记已读</el-button>
            <!-- <el-button class="s-btn" style="margin-right: 0!important;" @click="adjustStatus(0)" v-if="tabActive == 1">标记未读</el-button> -->
          </div>
          <div style="overflow-y: auto;overflow-x: hidden;padding: 0 10px;" :style="{height:mianContentHeight - 100 + 'px'}"
              v-infinite-scroll="loadList" :infinite-scroll-disabled="loadDisabled" infinite-scroll-distance="20" v-loading="loading">
            <div class="msg-list" v-for="(item,index) in newsList" :key="index" >
              <p class="year-title" v-if="getTime(index,item)">{{$moment(item.createTime).format('YYYY')}}</p>
              <div :class="['msg-item',nowId == item.id ? 'background' : '']" >
                <div>
                  <el-checkbox v-model="item.checked" @change="changeChecked($event,item)" v-if="tabActive == 0"></el-checkbox>
                  <span style="display: inline-block;width: 14px;height: 14px;border-radius: 50%;background: #DCDFE6;" v-if="tabActive == 1"></span>
                </div>
                <p :class="['title',item.status == 0 ? 'bold' : '']" :title="`[${item.messageTypeName}] ${item.theme}`" @click="getDetail(item.id)">
                  <!-- <i class="radius-unRead" v-if="item.status == 0"></i> -->
                  <span class="ml-5">{{`[${item.messageTypeName}] ${item.theme}`}}</span>
                </p>
                <p class="date" @click="getDetail(item.id)">{{$moment(item.createTime).format('MM-DD') }}</p>
              </div>
            </div>
            <p v-if="loading" class="text-center text-gray mt-10 mb-20">加载中...</p>
            <p v-if="noMore" class="text-center text-gray mt-10 mb-20">没有更多了</p>
          </div>
        </div>
      </div>
     <div class="content-box-r" v-loading="loading2">
       <div class="message-content" :style="{height:mianContentHeight + 'px'}">
         <div style="padding: 0 10px;">
           <div class="top-title">
             <div class="msg-title" :title="drawerTile">{{newsDetail.theme}}</div>
             <div style="width: 300px">
               <el-input v-model.trim="messageSearch" @keyup.enter.native="getPage(tabActive,1)" placeholder="输入关键字回车搜索">
                 <i @click="getPage(tabActive,1)" slot="suffix" class="el-input__icon el-icon-search f-cursor font-16 fw-blod"
                    style="color: #dbdbdb;"></i>
               </el-input>
             </div>
           </div>
         </div>
        <el-empty description="暂无内容" v-if="!newsDetail.id" :style="{height:mianContentHeight - 71 + 'px'}"></el-empty>
         <div class="message-detail" :style="{height:mianContentHeight - 71 + 'px'}" v-else>
            <!--           附件区-->
            <div class="file-list">
              <a class="file-item" v-for="(item,index) in newsDetail.files" :key="index" @click="downLoad(item.fileId)">
                <img src="@/assets/images/icons/ic-excel.png" alt="" />
                <p>{{ item.fileName }}</p>
              </a>
           </div>
           <div class="date">{{ newsDetail.createTime }}</div>
           <div class="message" id="message" v-html="newsDetail.emailContent"></div>

         </div>
       </div>
     </div>
    </div>
  </div>
</template>

<script>
export default {
  components: {},
  data () {
    return {
      pathData: [{ name: '消息中心' }],
      tabActive: 0,
      checkAll: false,
      mianContentHeight: window.innerHeight - 190,
      active: 0,
      drawerTile: '',
      messageSearch: '',
      count: 20,
      loading: false,
      unReadCount:0,
      readCount:0,
      newsList:[],
      newsDetail:{},
      unReadNum:1,
      readNum:1,
      loading2:false,
      checkedList:[],
      nowId:""
    }
  },
  computed: {
    noMore () {
      if(this.loading){
        return false
      }
      if(this.tabActive == 0){
        return this.newsList.length >= this.unReadCount
      }else if(this.tabActive == 1){
        return this.newsList.length >= this.readCount
      }
      return false
    },
    loadDisabled () {
      return this.loading || this.noMore
    },
    getTime(){
      return function(index,item){
        var year = new Date().getFullYear()
        return this.$moment(item.createTime).format('YYYY') != year 
        && (index == 0 || this.$moment(item.createTime).format('YYYY') != this.$moment(this.newsList[index - 1].createTime).format('YYYY'))
      }
    }
  },
  created () {
    this.getPage(0,this.unReadNum)
    this.getPage(1,this.readNum)
  },
  methods: {
    handleUnRead(){
      this.tabActive = 0 
      this.getPage(0)
      this.checkAll = false
      this.checkedList = []
    },
    handleRead(){
      this.tabActive = 1
      this.getPage(1)
      this.checkAll = false
      this.checkedList = []
    },
    loadList () {
      if(this.loading){
        return
      }
      console.log("loadList")
      if(this.tabActive == 0){
        this.getPage(0,++this.unReadNum)
      }else if(this.tabActive == 1){
        this.getPage(1,++this.readNum)
      }
    },
    highlightText () {
      const contentElement = document.getElementById('message')
      const content = contentElement.textContent
      const regex = new RegExp(this.messageSearch, 'gi')
      const highlightedContent = content.replace(regex, match => `<span class="highlight">${match}</span>`)
      console.log(highlightedContent)
      contentElement.innerHTML = highlightedContent
    },
    // 0：未读，1：已读
    getPage(status,num=1){
      console.log(num)
      this.loading = true
      return this.$http({
        url: `/policy/message/list/page`,
        method: 'post',
        data:{
          "page": 1,
          "start": 0,
          "size": num * 50,
          "query": [
            {
              "property": "messageType",
              "value": ""
            },
            {
              "property": "searchText",
              "value": this.messageSearch
            },
            {
              "property": "status",
              "value": status
            }
          ],
          "sidx": "",
          "sort": ""
        }
      }).then((res) => {
        if(status == 0){
          this.unReadCount = res.data.data.records
          this.$store.commit('updateMsgNumber',res.data.data.records)
        }
        if(status == 1){
          this.readCount = res.data.data.records
        }
        if(this.tabActive == status){
          this.newsList = res.data.data.rows
          this.newsList.forEach(item=>{
            this.$set(item,'checked',false)
          })
          this.checkAll = false
        }
        this.loading = false
      })
      .catch((err) => {
        this.loading = false
      })
    },
    getDetail(id){
      this.nowId = id
      this.loading2 = true
      this.$http({
        url: `/policy/message/list/detail/${id}`,
        method: 'post',
      }).then((res) => {
        this.loading2 = false
        this.newsDetail = res.data.data
        if(this.tabActive == 1){
          return
        }
        this.adjustStatus(1,this.newsDetail.id)
        this.newsList.forEach(item=>{
          if(this.newsDetail.id == item.id){
            item.status = 1
          }
        })
      })
      .catch((err) => {
        this.loading2 = false
      })
    },
    adjustStatus(status,id,type){
      if(!id){
        if(this.checkedList.length <= 0){
          return
        }
      }
      if(type != 'notRefresh'){
        this.loading = true
      }
      // this.loading = true
      this.$http({
        url: `/policy/message/list/adjustStatus/${status}`,
        method: 'post',
        params:{
          ids:!id ? this.checkedList.join(',') : id
        },
      }).then((res) => {
        this.loading = false
        var num = 1
        if(this.tabActive == 0){
          num = this.unReadNum
        }else if(this.tabActive == 1){
          num = this.readNum
        }
        this.checkedList = []
        console.log(num)
        if(type != 'notRefresh'){
          this.getPage(0,this.unReadNum)
          this.getPage(1,this.readNum)
        }
      })
      .catch((err) => {
        this.loading = false
      })
    },
    changeChecked(val,item){
      if(val){
        this.checkedList.push(item.id)
        this.checkedList = [...new Set(this.checkedList)]
      }else{
        var index = this.checkedList.indexOf(item.id)
        this.checkedList.splice(index,1)
      }
      console.log(this.checkedList)
    },
    handleCheckAll(val){
      if(val){
        this.newsList.forEach(item=>{
          item.checked = true
          this.checkedList.push(item.id)
          this.checkedList = [...new Set(this.checkedList)]
        })
      }else{
        this.checkedList = []
        this.newsList.forEach(item=>{
          item.checked = false
        })
      }
      
    },
    downLoad(fileID){
      this.$downloadFile(`/policy/sys/file/download/${fileID}`, 'get')
    }
  }
}
</script>
<style lang="less" scoped>
.content-box {
  display: flex;
  padding: 10px;

  .content-box-l {
    width: 30%;
    min-width: 300px;
    max-width: 400px;
    border: 1px solid #dbdbdb;
    margin-right: 20px;
  }

  .tab-div {
    border-bottom: 1px solid #dbdbdb;
    display: flex;
    width: 100%;
    padding: 10px;
    cursor: pointer;
    box-sizing: border-box;
  }

  .tab-item {
    min-width: 120px;
    height: 36px;
    line-height: 36px;
    padding: 0 10px;
    border-radius: 4px;
    background-color: #eff3fc;
    margin-right: 20px;
    display: flex;
    align-items: center;
  }
  .tab-item.active{
    background-color: @blueColor;
    color: #ffffff;
  }

  .opt-div {
    display: flex;
    align-items: center;
    padding: 10px 10px 0 10px;
  }

  .year-title {
    font-weight: bold;
    margin-top: 15px;
  }

  .msg-list {
    margin-top: 10px;
  }

  .msg-item {
    display: flex;
    align-items: center;
    cursor: pointer;
    margin: 5px -5px 0 -5px;
    padding: 5px 5px 5px 5px;

    .title {
      flex: 1;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      margin-left: 5px;
      position: relative;
    }
    .bold{
      font-weight: bold;
    }

    .date {
      color: #909399;
      margin-left: 20px;
    }
  }
  .background{
    background: #e1efff;
  }

  .msg-item:hover {
    background-color: #eff3fc;
  }
  .content-box-r{
    flex: 1;
    border: 1px solid #dbdbdb;
  }
  .message-content {
    width: 100%;
    .top-title {
      width: 100%;
      height: 58px;
      line-height: 58px;
      font-size: 16px;
      border-bottom: 1px solid #ddd;
      display: flex;
      padding: 0 10px;
      box-sizing: border-box;

      .msg-title{
        flex: 1;
        width: 300px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        margin-right: 30px;
      }
    }

    .date {
      color: #7F7F7F;
      font-size: 14px;
      padding: 5px;
    }

    .message-detail {
      padding: 10px 15px;
      box-sizing: border-box;
      overflow: auto;
    }

    .message {
      white-space: pre-wrap;
    }

    .file-list{
      display: flex;
      flex-wrap: wrap;
      img{
        margin-right: 5px;
      }
      .file-item{
        display: flex;
        padding: 3px 10px 3px 3px;
        border: 1px solid #dbdbdb;
        align-items: center;
        cursor: pointer;
        border-radius: 4px;
        margin-right: 20px;
        margin-top: 10px;
        color: #3d4245;
      }
      .file-item:hover{
        background-color: #eff3fc;
      }
    }
  }
}
/deep/ .highlight {
  background-color: yellow;
  font-weight: bold;
}
.home-container{
  padding-bottom: 10px;
}
.radius-unRead{
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #FD6154;
  position: absolute;
  top: 50%;
  left: 0;
  transform: translateY(-50%);
}

table {
  border: 1px solid #e4e4e4;
  border-right: none;
  border-bottom: none;
  th {
    border-bottom: 1px solid #e4e4e4;
    border-right: 1px solid #e4e4e4;
    font-weight: 400;
  }
  td {
    padding: 15px 10px;
    box-sizing: border-box;
    border-bottom: 1px solid #e4e4e4;
    border-right: 1px solid #e4e4e4;
    font-weight: 400;
  }
  .table-cell {
    margin-left: 35%;
  }
}
</style>
<style>
  .spl-container{
    margin-top:78px;
  }
</style>
