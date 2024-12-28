<template>
  <div :class="'scrollView__nav-wrap' + (scrollable ? ' is-scrollable' : '')" :style="{width: width}">
    <span v-show="scrollable" :class="'scrollView__nav-prev' + (scrollable.prev ? '' : ' is-disabled')" @click="scrollPrev">
      <i :style="{'line-height': height + 'px'}" class="el-icon-caret-left"></i>
    </span>
    <span v-show="scrollable" :class="'scrollView__nav-next' + (scrollable.next ? '' : ' is-disabled')" @click="scrollNext">
      <i :style="{'line-height': height + 'px'}" class="el-icon-caret-right"></i>
    </span>
    <div class="scrollView__nav-scroll" ref="navScroll" id="navScroll">
      <div class="scrollView__nav" ref="nav" :style="navStyle">
        <slot></slot>
      </div>
    </div>
  </div>
</template>
<script>
  export default {
    props: {
      width: {
        type: String,
        default: '100%'
      }
    },
    data () {
      return {
        scrollable: false,
        navOffset: 0,
        height: 0
      }
    },
    computed: {
      navStyle () {
        return {
          transform: `translateX(-${this.navOffset}px)`
        }
      }
    },
    methods: {
      scrollPrev () {
        const containerSize = this.$refs.navScroll.offsetWidth
        const currentOffset = this.navOffset
        if (!currentOffset)
            return
        const newOffset = currentOffset > containerSize ? currentOffset - containerSize : 0
        this.navOffset = newOffset
        this.update()
      },
      scrollNext () {
        const navSize = this.$refs.nav.offsetWidth
        const containerSize = this.$refs.navScroll.offsetWidth
        const currentOffset = this.navOffset
        if (navSize - currentOffset <= containerSize) return
        const newOffset =
          navSize - currentOffset > containerSize * 2
            ? currentOffset + containerSize
            : navSize - containerSize
        this.navOffset = newOffset
        this.update()
      },
      update () {
        if (!this.$refs.nav) return
        const navSize = this.$refs.nav.offsetWidth
        this.height = this.$refs.nav.offsetHeight
        const containerSize = this.$refs.navScroll.offsetWidth
        const currentOffset = this.navOffset
        if (containerSize < navSize) {
          const currentOffset = this.navOffset
          this.scrollable = this.scrollable || {}
          this.scrollable.prev = currentOffset
          this.scrollable.next = currentOffset + containerSize < navSize
          if (navSize - currentOffset < containerSize) {
            this.navOffset = navSize - containerSize
          }
        } else {
          this.scrollable = false
          if (currentOffset > 0) {
            this.navOffset = 0
          }
        }
      }
    },
    updated () {
      this.update()
    },
    mounted () {
      let that = this
      var elementResizeDetectorMaker = require("element-resize-detector");//导入
      const erd = elementResizeDetectorMaker()
      erd.listenTo(document.getElementById("navScroll"),(element)=>{
        that.update()
      })
    }
  }
</script>

<style lang="less">
  .scrollView__nav-wrap {
    display: inline-block;
    overflow: hidden;
    margin-bottom: -1px;
    position: relative;
    vertical-align: middle;
  }

  .scrollView__nav-wrap.is-scrollable {
    padding: 0 30px;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
  }

  .scrollView__nav-wrap::after {
    display: none;
  }

  .scrollView__nav-scroll {
    overflow: hidden;
  }

  .scrollView__nav {
    white-space: nowrap;
    position: relative;
    transition: transform 0.3s, -webkit-transform 0.3s;
    float: left;
    z-index: 2;
  }

  .scrollView__nav-prev {
    left: 0;
  }
  .scrollView__nav-next {
    right: 0;
  }
  .scrollView__nav-next,
  .scrollView__nav-prev {
    position: absolute;
    cursor: pointer;
    line-height: 44px;
    font-size: 25px;
    color: #303133;
  }
  .is-disabled > i {
    color: #C0C4CC !important;
    cursor: not-allowed;
  }
</style>
