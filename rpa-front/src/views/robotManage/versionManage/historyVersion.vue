<template>
  <div class="content spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="main-box">
      <!-- 步骤条 -->
      <el-steps align-center direction="vertical" class="steps-box" v-if="historyData.length > 0">
        <i class="el-icon-top icon-top"></i>
        <el-step :class="item.status == 1
          ? 'step-item blue'
          : 'step-item opopp'
          " v-for="(item, i) in historyData" :key="i">
          <template slot="icon">
            <!--状态（1 运行中，0 停止， 2 历史版本）-->
            <span v-if="item.status == 0">停止</span>
            <span v-if="item.status == 1">运行中</span>
            <span v-if="item.status == 2">历史<br />版本</span>
          </template>
          <template slot="title">
            <span style="margin-left: 10px" v-if="item.status == 2" class="click-pointer"
              @click="handleVersion(item)">使用此版本</span>
          </template>
          <template slot="description">
            <div style="display: flex;">
              <p style="white-space: nowrap" class="version-line">
                版本号：{{ item.version }}
              </p>
              <el-divider class="step-line"></el-divider>
              <div class="step-left-time" :style="getStyleObject(item.createName || '-')">
                <el-row type='flex' justify="space-between">
                  <span>{{ $moment(item.releaseTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
                  <el-tooltip class="item" effect="dark" :content="item.createName || '-'"
                    placement="top">
                    <span class="name">&nbsp;&nbsp;{{ item.createName || '-' }}</span>
                  </el-tooltip>

                </el-row>
                <p class="text-center text-blue mt-5 f-cursor" @click="downloadFile(item.filePath, item.fileName)">下载升级包
                </p>
              </div>
              <div class="right-icon">
                <i v-if="item.status == 1 || item.status == 2" @click="seeContent(item)"
                  class="el-icon-s-order click-pointer" style="font-size: 20px"></i>
                <p class="comment" :title="item.comment">{{ item.comment }}</p>
              </div>
            </div>


          </template>
        </el-step>
      </el-steps>
      <el-empty description="暂无历史版本" v-else style="position: absolute;top: 40%;transform: translateY(-50%);"></el-empty>

      <!--更新内容-->
      <el-dialog title="本次发布更新内容：" :close-on-click-modal="false" :visible.sync="seeVisible" width="600px"
        class="cust-dialog" :show-close="false">
        <div style="min-height: 150px">
          <p v-html="seeComtent"></p>
        </div>
        <div class="text-right mt-30">
          <el-button type="primary" @click="nowContent()">我了解！</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
export default {
  name: 'historyVersion',
  components: {},
  data() {
    return {
      pathData: [],
      component: '',
      versionObject: {
        version: ''
      },
      seeComtent: undefined,
      seeVisible: false,
      historyData: []
    }
  },
  computed: {
    getStyleObject() {
      return function (str) {
        if (str.length >= 10) {
          return {
            left: '-260px'
          };
        } else if (str.length == 1) {
          return {
            left: '-180px'
          }
        }
        else {
          return {
            left: '-200px'
          };
        }
      }

    }
  },
  created() { },
  watch: {},
  mounted() {
    let tabs = this.$store.state.tabs
    if (tabs) {
      this.pathData = this.$global.deepcopyArray(tabs[this.$route.meta.parentPath])
    }
    this.pathData.push({ name: '历史版本' })
    if (this.$route.query.component) {
      this.component = this.$route.query.component
    }
    this.seeVesion()
  },
  methods: {
    nowContent() {
      this.seeVisible = false
      this.seeComtent = undefined
    },
    seeContent(item) {
      this.seeComtent = item.comment
      this.seeVisible = true
    },
    seeVesion() {
      let that = this
      this.showLoading()
      this.$http({
        url: '/robot/version/history',
        method: 'post',
        params: {
          component: that.component
        }
      }).then(({ data }) => {
        that.historyData = data.data
        that.closeLoading()
      }).catch(err => {
        that.closeLoading()
      })
    },

    // 确定使用此版本
    handleCheck() {
      let that = this
      this.showLoading()
      this.$http({
        url: '/robot/version/activate',
        method: 'post',
        params: {
          component: this.component,
          version: this.versionObject.version
        }
      }).then(({ data }) => {
        that.seeVesion()
        that.closeLoading()
        that.$message.success('发布成功')
      }).catch(err => {
        that.closeLoading()
      })
    },
    handleVersion(item) {
      let that = this
      this.$confirm('确定使用此版本？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        showClose: false,
        customClass: 'pal-confirm',
        type: 'warning'
      }).then(() => {
        that.versionObject.version = item.version
        that.handleCheck()
      }).catch(() => {

      })
    },
    showLoading(target) {
      this.loading = this.$loading({
        target: target || document.body,
        lock: true,
        text: '加载中',
        spinner: 'el-icon-loading',
        background: 'rgba(255, 255, 255, 0.7)'
      })
    },
    closeLoading() {
      if (this.loading && this.loading.close) {
        this.loading.close()
      }
    },
    downloadFile(filePath, fileName) {
      var nameArr = fileName.split('.')
      var fileType = nameArr[nameArr.length - 1]
      fileType = fileType.toLowerCase()
      if (fileType == 'jar') {
        this.$downloadFile(filePath, 'get', this.$global.JAR, '', fileName)
      } else {
        this.$downloadFile(filePath, 'get', {}, '', fileName)
      }
    }
  }
}
</script>

<style scoped lang="less">
.icon-top {
  font-size: 30px;
  color: #c1c4cb;
  margin-left: -4px;
}

.click-pointer {
  color: @blueColor;
  cursor: pointer;
  font-size: 14px;
  margin-bottom: 5px;
}

.main-box {
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 15px;
}

/deep/ .el-step.is-vertical .el-step__head {
  width: 39px;
  height: 168px;
}

/deep/.opopp .el-step__icon.is-text {
  border-radius: 50%;
  border: 2px solid;
  border-color: inherit;
  width: 50px;
  height: 50px;
  position: relative;
  left: -13px;
  font-size: 13px;
}

/deep/.blue .el-step__icon.is-text {
  border-radius: 50%;
  border: 2px solid;
  border-color: #5a9cf8;
  width: 50px;
  height: 50px;
  position: relative;
  left: -13px;
  font-size: 13px;
  color: #5a9cf8;
}

/deep/.redNo .el-step__icon.is-text {
  border-radius: 50%;
  border: 2px solid;
  border-color: red;
  width: 50px;
  height: 50px;
  position: relative;
  left: -13px;
  font-size: 13px;
  color: red;
}

/deep/ .el-step.is-vertical .el-step__line {
  margin-top: -18px;
}

/deep/ .el-step__line {
  height: 291px;
}

.step-item {
  position: relative;

  .step-line {
    width: 141px;
    position: absolute;
    top: 0px;
    left: 40px;
  }

  .right-icon {
    position: absolute;
    top: 14px;
    left: 186px;
    display: flex;

    .comment {
      width: 400px;
      margin-left: 5px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      color: #606266;
    }
  }

  .step-left-time {
    position: absolute;
    top: 17px;
    left: -260px;
    font-size: 14px;
    color: #606266;
    // margin-left: -200px;

    .name {
      max-width: 100px;
      text-overflow: ellipsis;
      white-space: nowrap;
      overflow: hidden;
    }
  }

  .version-line {
    white-space: nowrap;
    position: absolute;
    top: 32px;
    font-size: 14px;
    color: #606266;
  }
}
</style>
