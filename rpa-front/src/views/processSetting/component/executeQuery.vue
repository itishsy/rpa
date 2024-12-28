<template>
  <div>
    <el-drawer title="查看执行明细" :visible.sync="show" :wrapperClosable="true"
               custom-class="spl-filter-drawer detail-drawer">
      <div class="pt-20 pl-20 pr-20">
        <div class="view-content">
          <div class="step-item" v-for="item in implementationDetailsArr" :key="item.id">
            <div style="display: flex; justify-content: space-between">
              <p class="step-title">
                <span class="step-name">{{ item.stepName }}</span>
                {{$moment(item.startTime).format('YYYY-MM-DD HH:mm:ss') }}
              </p>
              <p style="cursor: pointer; color: #3e82ff" v-if="item.files &&
                        item.files.length > 0
                        " @click="screenshots(item.files)">
                查看截屏
              </p>
            </div>
            <p class="reason" v-if="item.error">原因：{{ item.error }}</p>
            <p class="step-dis" v-if="item.flowType == 2" @click="seeDeatil(item.detailVOS)">
              <span style="cursor: pointer; color: #3e82ff">执行步骤</span>
            </p>
          </div>
        </div>
      </div>
    </el-drawer>

    <el-dialog title="执行步骤" :visible.sync="visible2" width="40%" :close-on-click-modal="false" class="cust-dialog">
      <div class="visi-box">
        <div class="step-item" v-for="item in setpList" :key="item.id">
          <div style="display: flex; justify-content: space-between">
            <p class="step-title">
              <span class="step-name">{{ item.stepName }}</span>
              {{ $moment(item.startTime).format('YYYY-MM-DD HH:mm:ss') }}
            </p>
          </div>
        </div>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="visible2 = false">确 定</el-button>
      </span>
    </el-dialog>
    <el-image-viewer v-if="showViewer" :on-close="showViewer = false" :url-list="srcList" />
  </div>
</template>
<script>
export default {
  props: {},
  computed: {},
  data () {
    return {
      show: false,
      implementationDetailsArr: [],
      screenshotsUrl: '',
      srcList: [],
      showViewer: false,
      visible2: false,
      setpList: []
    }
  },
  created () {},
  mounted () {},
  watch: {},
  methods: {
    init (list) {
      this.implementationDetailsArr = list
      this.show = true
    },
    screenshots (robotExecutionFileInfos) {
      this.screenshotsUrl =
        robotExecutionFileInfos && robotExecutionFileInfos.length > 0
          ? robotExecutionFileInfos[0].fileFullPath
          : ''
      this.srcList = [this.screenshotsUrl]
      this.$nextTick((item) => {
        this.showViewer = true
      })
    },
    seeDeatil (val) {
      this.setpList = JSON.parse(JSON.stringify(val))
      this.visible2 = true
    }
  }
}
</script>
<style lang="less" scoped>
.view-content {
  padding: 0 10px;
  overflow-y: auto;
}

.step-item {
  color: #151515;
  /*border-left: 1px solid #DDDDDD;*/
  padding-left: 15px;
  padding-bottom: 25px;
  position: relative;
}

.step-item:before {
  display: inline-block;
  content: '';
  width: 10px;
  height: 10px;
  background: #dddddd;
  border-radius: 50%;
  position: absolute;
  top: 3px;
  left: -5px;
}

.step-item:after {
  display: inline-block;
  content: '';
  width: 1px;
  height: 100%;
  background: #dddddd;
  position: absolute;
  top: 3px;
  left: 0px;
}

.step-item:last-of-type:after {
  display: none;
}

.step-title {
  /*margin-top: -10px;*/
}

.step-name {
  display: inline-block;
  color: #458ae6;
  margin-right: 10px;
}

.reason {
  color: #e15e63;
  margin-top: 10px;
  word-break: break-all;
  word-wrap: break-word;
  max-width: 1800px;
}
.visi-box {
  padding: 10px 29px;
}
.step-item {
  color: #151515;
  /*border-left: 1px solid #DDDDDD;*/
  padding-left: 15px;
  padding-bottom: 25px;
  position: relative;
}

.step-item:before {
  display: inline-block;
  content: '';
  width: 10px;
  height: 10px;
  background: #dddddd;
  border-radius: 50%;
  position: absolute;
  top: 3px;
  left: -5px;
}

.step-item:after {
  display: inline-block;
  content: '';
  width: 1px;
  height: 100%;
  background: #dddddd;
  position: absolute;
  top: 3px;
  left: 0px;
}

.step-item:last-of-type:after {
  display: none;
}

.stepName:hover {
  color: #3E82FF;
}
</style>
