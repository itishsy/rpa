<template>
  <div>
    <!--新增、编辑-->
    <el-drawer
      title="本月概况"
      :visible.sync="localVisible"
      :wrapperClosable="false"
      custom-class="spl-filter-drawer add-drawer"
      :show-close="true"
      :before-close="doClose"
      size="100%"
    >
      <div>
        <div class="top-det">
          <div>应用名称:{{ accumuFundForm.appName }}</div>
          <div style="display: flex">
            <span style="white-space: nowrap">应用编码:</span
            ><span style="word-wrap: break-word; word-break: break-all">{{
              accumuFundForm.appCode
            }}</span>
          </div>
          <div>创建时间:{{ accumuFundForm.createTime }}</div>
          <div>最新执行时间: {{ accumuFundForm.lastExecutionTime }}</div>
        </div>
        <p class="poi">
          <span>本月执行情况</span>
          <span @click="toQuery()" class="opinter">></span>
        </p>
        <div class="robotTa">
          <div
            class="situation"
            v-for="(item, index) in accumuFundForm.robotTaskVOS"
            :key="index"
          >
            <!-- v-show="accumuFundForm.robotTaskVOS.length !== index + 1" -->
            <p>任务{{ index + 1 }}：{{ item.taskName }}</p>
            <div class="there-center">
              <!-- <p v-show="accumuFundForm.robotTaskVOS.length == index + 1">合计</p> -->
              <p>成功：{{ item.successNumber }}次</p>
              <p>失败：{{ item.failureNumber }}次</p>
              <p>操作数据：{{ item.dataNumber }}条</p>
            </div>
          </div>
        </div>
      </div>
      <div class="there-center22">
        <p style="width:39px">合计</p>
        <p>成功：{{ successTimes }}次</p>
        <p>失败：{{ loserTimes }}次</p>
        <p>操作数据：{{ allTimes }}条</p>
      </div>
    </el-drawer>
  </div>
</template>
  <script>
export default {
  name: 'applicationsOfDrawer',
  components: {},
  props: {
    payOfoptionsDanger: {
      type: Array,
      default: () => [],
    },
    payOfoptions: {
      type: Array,
      default: () => [],
    },
    visible: {
      type: Boolean,
      default: false,
    },
    applicationSurveyFrom: {
      type: String,
      default: '',
    },
  },
  computed: {
    localVisible: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      },
    },
  },
  watch: {
    applicationSurveyFrom: {
      handler(val) {
        if (val) {
          Object.assign(this.accumuFundForm, JSON.parse(val))
          this.echeaData(this.accumuFundForm.robotTaskVOS)
        }
      },
      deep: true,
      immediate: true,
    },
  },
  data() {
    return {
      successTimes: 0,
      loserTimes: 0,
      allTimes: 0,
      situationList: [
        { name: '增员' },
        { name: '减员' },
        { name: '减员' },
        { name: '减员' },
        { name: '减员' },
      ],
      accumuFundForm: {
        appName: undefined,
        appCode: undefined,
        createTime: undefined,
        latelyExecTime: undefined,
        robotTaskVOS: [],
      },
    }
  },
  created() {},
  mounted() {},

  methods: {
    echeaData(val) {
      // <p>成功：{{ item.successNumber }}次</p>
      //         <p>失败：{{ item.failureNumber }}次</p>
      //         <p>操作数据：{{ item.dataNumber }}条</p>
      let successNumberList = []
      let sumsuccess = 0
      let failureNumberList = []
      let sumfail = 0
      let dataNumberList = []
      let sumData = 0
      val &&
        val.forEach((item) => {
          successNumberList.push(item.successNumber)
          failureNumberList.push(item.failureNumber)
          dataNumberList.push(item.dataNumber)
        })
      successNumberList.forEach((sumNn) => {
        sumsuccess += sumNn
      })
      this.successTimes = sumsuccess

      failureNumberList.forEach((sumNn2) => {
        sumfail += sumNn2
      })
      this.loserTimes = sumfail

      dataNumberList.forEach((sumNn3) => {
        sumData += sumNn3
      })
      this.allTimes = sumData
    },
    toQuery() {
      this.$emit('to-query')
      this.doClose()
    },
    doClose() {
      this.localVisible = false
      this.handleNullFrom()
      this.$emit('handleNull-newFrom')
    },
    handleNullFrom() {
      this.accumuFundForm = {
        appName: undefined,
        appCode: undefined,
        createTime: undefined,
        latelyExecTime: undefined,
        robotTaskVOS: [],
      }
    },
  },
}
</script>
  <style lang="less" scoped>
.top-det {
  display: flex;
  justify-content: space-between;
  flex-flow: wrap;
  padding: 0 19px;
  div {
    width: 50%;
    // white-space: nowrap;
    margin-top: 20px;
  }
}
.poi {
  background: #f2f2f2;
  padding: 10px;
  margin: 10px;
  color: #5688df;
  display: flex;
  justify-content: space-between;
}
.robotTa {
  position: relative;
  // bottom: 50px;
  margin-bottom: 60px;
}
.situation {
  margin-left: 14px;
  overflow-y: scroll;
  p {
    margin-top: 10px;
  }
  .there-center {
    display: flex;
    justify-content: center;
    p {
      width: 100px;
      margin-left: 55px;
    }
  }
}
// .situation:last-child > .there-center {
//   color: #5688df;
//   margin-top: 100px;
//   position: absolute;
//   bottom: 20px;
// }
// .bottom-footer {
//   margin-top: 100px;
//   position: absolute;
//   bottom: 20px;
// }
.there-center22 {
  display: flex;
  justify-content: space-around;
  color: #5688df;
  position: absolute;
  bottom: 0px;
  background: white;
  padding-bottom: 16px;
  padding-top: 10px;
}
.there-center22 > p {
  width: 135px;
  margin-left: 20px;
}
.opinter {
  cursor: pointer;
  font-weight: 900;
  font-size: 16px;
}
</style>
