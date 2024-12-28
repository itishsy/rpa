<template>
  <!-- 控制台-数据汇总 右侧数据统计Item -->
  <div class="right-item" @click="onClickBox()">
    <div class="right-item-c">
      <div style="padding: 20px 10px 0px 10px">
        <div class="top-layout">
          <div>
            <span class="title">{{ title }}</span>

          </div>
          <div class="img-layout">
            <img :src="icon" width="48" height="48"/>
            <span v-if="showIconStatus" :class="iconStatusText ? 'active' : 'active-disabled'">{{
                iconStatusText ||
                '-'
              }}</span>
          </div>

        </div>
        <div class="desc">
          <slot v-if="custom">

          </slot>

          <div v-if="!custom" class="count-layout">
            <countTo class="count-number" :endVal="count" :duration='1000' :easingFn="easingFn"/>
            <span>{{ unit }}</span>
            <img v-if="showStatus && status !== 0" class="ml-10" :src="statusImg" width="12" height="16"/>
          </div>
          <!-- <span v-else-if="!needControl" class="number">{{ count }}</span>{{ unit }} -->

        </div>
      </div>
      <div class="data-layout">
        <el-row type="flex" style="width: 100%;">
          <el-col :span="12">
            <div class="show" v-show="showBottom && lbTitle" @click.stop="onClickBox('lbTitle')">
              <span class="title">{{ lbTitle }}</span>
              <span class="desc">{{ lbCount }}</span>
            </div>
          </el-col>
          <el-divider direction="vertical" v-if="showBottom && rbTitle"></el-divider>
          <div v-else class="seize"></div>
          <el-col :span="12" v-show="showBottom && rbTitle">
            <div class="show" @click.stop="onClickBox('rbTitle')">
              <span class="title">{{ rbTitle }}</span>
              <span class="desc">{{ rbCount }}</span>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>
<script>
import countTo from 'vue-count-to'

export default {
  name: 'StatisticsInfo',
  components: { countTo },
  props: {
    // 展示的title
    title: {
      type: String,
      required: true
    },
    // 是否显示指标
    showStatus: {
      type: Boolean,
      default: false
    },
    // 指标统计
    count: {
      type: Number,
      default: 0
    },
    // 指标单位
    unit: {
      type: String,
      default: ''
    },
    // 指标状态 >0 上升 <0 下降 =0 等于
    status: {
      type: [Number],
      default: 1
    },
    // 图标
    icon: {
      type: String,
      required: true
    },
    // 是否显示底部详情
    showBottom: {
      type: Boolean,
      default: false
    },
    // 左侧底部文本
    lbTitle: {
      type: String
    },
    // 右侧底部文本
    rbTitle: {
      type: String
    },
    // 左侧底部统计
    lbCount: {
      type: Number,
      default: 0
    },
    // 右侧底部统计
    rbCount: {
      type: Number,
      default: 0
    },
    // 是否需要自己控制跳动字数layout
    custom: {
      type: Boolean,
      default: false
    },
    // 是否显示图标下文本
    showIconStatus: {
      type: Boolean,
      default: false
    },
    // 图标下文本
    iconStatusText: {
      type: String,
      default: ''
    }

  },
  data () {
    return {}
  },
  computed: {
    statusImg () {
      return this.status > 0 ? require('@/assets/images/icons/ic_rise.png') : this.status < 0 ? require('@/assets/images/icons/ic_decline.png') : require('@/assets/images/icons/ic_equal.png')
    }
  },
  watch: {},
  created () {
  },
  beforeMount () {
  },
  mounted () {
  },
  methods: {
    easingFn (t, b, c, d) {
      let y = c * t / d + b // 此方法为匀速
      return y
      // return c * (-Math.pow(2, -10 * t / d) + 1) * 1024 / 1023 + b; //原方案为先快后慢
    },
    onClickBox (type) {
      this.$emit('onClick', type)
    }
  }
}
</script>
<style lang='less' scoped>

.right-item {
  // width: 228px;
  // height: 180px;

  // margin: 0 0 6px 6px;
  background-color: #f2f4fa;
  box-shadow: 0px 0px 10px 0px rgba(135, 175, 228, 0.1);
  border-radius: 6px;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  position: relative;

  .right-item-c {
    width: 100%;
    height: 100%;
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    //padding: 10px;
    box-sizing: border-box;
    display: flex;
    flex-flow: column;
  }

  .top-layout {
    display: flex;
    justify-content: space-between;

    .title {
      width: 111px;
      height: 15px;
      font-size: 16px;
      font-family: Alibaba PuHuiTi;
      font-weight: 400;
      color: #303133;
      line-height: 32px;
    }

  }

  .desc {
    position: relative;
    font-size: 16px;
    margin-bottom: 5px;
  }

  .count-number {
    font-size: 32px;
    font-family: Alibaba PuHuiTi;
    font-weight: bold;
    color: #303133;
    line-height: 32px;
  }

  .data-layout {
    flex: 1;
    display: flex;

    .show {
      display: flex;
      flex-direction: column;
      height: 100%;
      padding-left: 20px;
      .title {
        font-size: 16px;
        font-family: Alibaba PuHuiTi;
        font-weight: 400;
        color: #606266;
        line-height: 32px;
      }

      .desc {
        font-size: 20px;
        font-family: Alibaba PuHuiTi;
        font-weight: 400;
        color: #303133;
        line-height: 32px;
      }
    }

    /deep/ .el-divider--vertical {
      height: 50px;
      margin: 10px 0;
    }

    .seize {
      height: 50px;
      margin: 10px;
    }
  }
}

/* 当屏幕宽度达到2K分辨率时应用样式a */
@media screen and (min-width: 2560px) {
  .right-item {
    /*padding: 30px 10px;
    padding-bottom: 0px;
    margin: 23px 22px;
    background-color: #f2f4fa;
    box-shadow: 0px 0px 10px 0px rgba(135, 175, 228, 0.1);
    border-radius: 6px;*/

    .right-item-c {
      padding: 20px;
    }

    .top-layout {
      display: flex;
      justify-content: space-between;

      .title {
        width: 111px;
        height: 15px;
        font-size: 24px;
        font-family: Alibaba PuHuiTi;
        font-weight: 400;
        color: #303133;
        line-height: 32px;
      }

    }

    .desc {
      position: relative;
      font-size: 16px;
    }

    .count-layout {
      margin-top: 20px;

      .count-number {
        font-size: 32px;
        font-family: Alibaba PuHuiTi;
        font-weight: bold;
        color: #303133;
        line-height: 32px;
        margin-top: 20px;
      }
    }

    .data-layout {
      display: flex;
      margin-top: 20px;
      justify-content: space-between;

      .show {
        display: flex;
        flex-direction: column;

        .title {
          font-size: 16px;
          font-family: Alibaba PuHuiTi;
          font-weight: 400;
          color: #606266;
          line-height: 32px;
        }

        .desc {
          font-size: 20px;
          font-family: Alibaba PuHuiTi;
          font-weight: 400;
          color: #303133;
          line-height: 32px;
        }
      }

      /deep/ .el-divider--vertical {
        height: 50px;
        margin: 10px;
      }

      .seize {
        height: 50px;
        margin: 10px;
      }
    }
  }
}

.img-layout {
  display: flex;
  flex-direction: column;
  align-items: center;

}

.active {
  color: #5fa463;
  position: absolute;
  top: 80px;
  right: 10px;
}

.active-disabled {
  color: #f2f4fa;
  position: absolute;
  top: 80px;
  right: 10px;
}
</style>
