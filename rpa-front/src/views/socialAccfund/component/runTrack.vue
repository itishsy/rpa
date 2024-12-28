<template>
  <div>
    <el-drawer title="执行轨迹" :visible.sync="show" :wrapperClosable="false"
               custom-class="spl-filter-drawer add-drawer" :show-close="true" :before-close="doClose">
      <div>
        <div v-if="row" class="drawerForm" style="padding: 15px 20px 20px 20px;">
          <!--            当前状态-->
          <div class="type-title mt-0 clearfix"><span class="text">当前状态</span></div>
          <div style="padding: 10px;" class="display-flex">
            <div class="status-icon"><i :class="'ic-run ' + getRunInfo(row.trackStatus).icon"></i></div>
            <div class="flex1 ml-10">
              <p class="fw-blod">{{row.trackStatusName}}</p>
              <div v-if="row.trackStatus==='6'">
                <p class="mt-5">实际开始：{{row.praStartTime?$moment(row.praStartTime).format('YYYY-MM-DD HH:mm'):''}}</p>
                <p class="mt-5">实际完成：{{row.praEndTime?$moment(row.praEndTime).format('YYYY-MM-DD HH:mm'):''}}</p>
                <p class="mt-5">总耗时：{{row.praTime!=null?row.praTime+'分钟':''}}</p>
              </div>
              <p v-else class="mt-5" v-show="row.preEndTime">预计{{$moment(row.preEndTime).format('YYYY-MM-DD HH:mm')}}完成 （耗时{{row.preTime}}分）</p>
              <div class="mt-5 display-flex"><span>原因：</span><span class="flex1">{{row.reason}}</span></div>
            </div>
          </div>
          <!--            当前状态-->

          <!--整体预期-->
          <div class="type-title clearfix mt-0"><span class="text">整体预期</span></div>
          <div class="ml-15 pt-10">
            <div v-if="row.preStartTime">
              <div class="node-item">
                <div class="spot"></div>
                <p>预计 {{$moment(row.preStartTime).format('MM-DD HH:mm')}} 开始</p>
                <p class="pt-20 pb-20">预计耗时{{row.preTime}}分钟完成</p>
              </div>
              <div class="node-item">
                <div class="spot"></div>
                <p>预计 {{$moment(row.preEndTime).format('MM-DD HH:mm')}} 完成</p>
              </div>
            </div>
            <el-empty v-else description="暂无数据" image-size="80" style="padding-top: 0px;padding-bottom: 10px;"></el-empty>
          </div>
          <!--整体预期-->

          <!--执行线路-->
          <div class="type-title clearfix" style="margin-top: 10px;"><span class="text">执行线路</span></div>
          <div class="ml-15 pt-10 pr-10">
            <div class="node-item" v-for="(item, index) in row.trackDetailList" :key="index">
              <div :class="'spot ' + (item.processTime ? '':'spot-gray')"></div>
              <p class="display-flex"><span class="flex1 mr-10">{{item.processName}}</span><span v-if="item.processTime">{{$moment(item.processTime).format('YYYY-MM-DD HH:mm')}}</span></p>
              <p class="pt-20" v-if="item.processName==='分配机器人'">{{item.machineCode}}</p>
              <p class="pt-20" v-if="item.processName!=='数据提交' && item.processName!=='分配机器人' && item.preStartTime">预计 {{$moment(item.preStartTime).format('MM-DD HH:mm')}}开始 耗时{{item.preTime}}分钟 完成</p>
              <p class="pt-5 pb-20" v-if="item.processName!=='数据提交' && item.processName!=='分配机器人' && item.praStartTime">实际 {{$moment(item.praStartTime).format('MM-DD HH:mm')}}开始 耗时{{item.praTime}}分钟 完成</p>
            </div>
            <el-empty v-if="row.trackDetailList.length===0" description="暂无数据" image-size="80" style="padding-top: 0px;padding-bottom: 10px;"></el-empty>
          </div>
          <!--执行线路-->
        </div>

        <div class="drawer-footer-buts">
          <el-button type="primary" class="s-btn w-100" style="margin-right: 0;" @click="doClose">关闭</el-button>
        </div>
      </div>

    </el-drawer>

  </div>
</template>
<script>

export default {
  components: {},
  props: [],
  data () {
    return {
      show: false,
      row: null
    }
  },
  watch: {},
  filters: {},
  computed: {},
  created () {},
  mounted () {},
  methods: {
    init (businessType, row, obj) {
      this.getDetail(businessType, row, obj)
    },
    doClose () {
      this.show = false
    },
    getDetail (businessType, row, obj) {
      let that = this
      this.$global.showLoading('请稍候...')
      that.$http({
        url: '/robot/loginAuth/queryEmployeeChangeTrackInfo',
        method: 'post',
        data: {
          'addrId': row.addrId,
          'addrName': row.addrName,
          'businessType': businessType,
          'clientId': row.customerId,
          'declareAccount': row.compAccount,
          'declareStatus': row.status,
          'declareType': row.changeType,
          'operationTypes': row.operationTypes,
          'failReason': obj.failReason,
          'submitTime': row.submitTime,
          'uuid': row.uuid
        }
      }).then(({ data }) => {
        that.$global.closeLoading()
        data.data.trackDetailList = data.data.trackDetailList ? data.data.trackDetailList : []
        this.row = data.data
        that.show = true
      }).catch(() => {
        that.$global.closeLoading()
      })
    },
    getRunInfo (status) {
      // 1排队中 el-icon-time text-blue
      // 2离线 el-icon-circle-close text-red
      // 3暂停 el-icon-video-play text-orange
      // 4执行中 el-icon-video-pause text-green
      // 5审核 el-icon-s-check text-yellow
      // 6已完成 el-icon-circle-check text-green
      // 7执行中断 el-icon-remove-outline text-red
      // 8未到申报期 el-icon-date text-blue
      let obj = {
        '1': { text: '排队中', icon: 'el-icon-time text-blue' },
        '2': { text: '离线', icon: 'el-icon-circle-close text-red' },
        '3': { text: '暂停', icon: 'el-icon-video-play text-orange' },
        '4': { text: '执行中', icon: 'el-icon-video-pause text-green' },
        '5': { text: '审核', icon: 'el-icon-s-check text-yellow' },
        '6': { text: '已完成', icon: 'el-icon-circle-check text-green' },
        '7': { text: '执行中断', icon: 'el-icon-remove-outline text-red' },
        '8': { text: '未到申报期', icon: 'el-icon-date text-blue' }
      }
      return obj[status]
    }
  }
}
</script>
<style lang="less" scoped>
.status-icon{
  //width: 60px;
  //height: 60px;
  //border-radius: 50%;
  //background-color: @blueColor;
}
@spotW: 14px;
@spotL: -7px;
.node-item{
  position: relative;
  padding-left: 15px;
  min-height: 80px;
  &::before{
    content: '';
    display: inline-block;
    width: 1px;
    height: 100%;
    background: #dbdbdb;
    position: absolute;
    top: 2px;
    left: 0;
  }
  &:last-of-type{
    min-height: auto;
    &::before {
      display: none;
    }
  }
  .spot{
    display: inline-block;
    width: @spotW;
    height: @spotW;
    border-radius: 50%;
    background-color: @greenColor;
    position: absolute;
    left: @spotL;
    top: 2px;
  }
  .spot-gray{
    background-color: #dbdbdb;
  }
}
.ic-run{
  font-size: 60px;
}
</style>
