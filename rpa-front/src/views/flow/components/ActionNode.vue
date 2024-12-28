<template>
  <!-- 指令节点 -->
  <div
    class="card"
    :class="{
      'card-success': nodeState === '1',
      'card-error': nodeState === '2',
      'card-quote':quote,
      'card-openEdit-no': isDisabledStepSet
    }"
  >
    <div class="card-body">
      <p class="label">{{ label }}</p>
      <p class="actionName">{{ actionName }}</p>
      <!-- <i class="el-icon-edit cursor" @click="triggerEdit()"></i> -->
    </div>

    <!-- <el-button @click="add()">内部Add: {{ num }} </el-button> -->
  </div>
</template>

<script>
export default {
  name: 'ActionNode',
  inject: ['getGraph', 'getNode'],
  data () {
    return {
      num: 0,
      id: '', // 当前对应节点的id
      label: '', // 对应节点的label
      desc: '', // 加入到节点json中的描述
      nodeState: '0', // 节点状态
      quote: false, // 当前是否为引用状态
      actionName: '', // 命令节点的名称
      isDisabledStepSet: '' // 是否为引用模板且开放编辑为否（不可编辑配置信息）
    }
  },
  mounted () {
    const self = this
    const node = this.getNode()
    const { flowName, desc, nodeState, quote, actionName, isDisabledStepSet } = node.getData()
    this.label = flowName || ''
    this.desc = desc || ''
    this.nodeState = nodeState || '0'
    this.quote = quote
    this.actionName = actionName
    this.isDisabledStepSet = isDisabledStepSet
    // 监听数据改变事件
    node.on('change:data', ({ current }) => {
      //   self.id = current.id
      self.label = current.flowName
      self.desc = current.desc
      self.nodeState = current.nodeState
      self.isDisabledStepSet = current.isDisabledStepSet
    })
  },
  beforeDestroy () {
    // this.$EventBus.$off('nodeTrigger')
  },
  methods: {
    add () {
      const node = this.getNode()
      const { num } = node.getData()
      node.setData({
        num: num + 1
      })
    },
    triggerEdit () {
      const node = this.getNode()
      console.log('triggerEdit--->', this.label, node.getData())
      const graph = this.getGraph()
      console.log('graph------', graph)
      graph.trigger('node:custom-edit', node.getData())
      // console.log('triggerEdit--->', this)
      // this.$eventbus.$emit('nodeTrigger',node.getData())
      // this.$store.commit('updateCurrentNode', node.getData())
    }
  }
}
</script>
<style lang="less" scoped>
.card {
  padding: 0;
  width: 100%;
  height: 100%;
  background-color: #eff4ff;
  border: 1px solid;
  border-color: #5f95ff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1) !important;
  border-radius: 4px;

  &-openEdit-no{
    background-color: #eeeeee;
    border-color: #dbdbdb;
  }

  &-success {
    background-color: #e1f3d8;
    border-color: #67c23a;
  }

  &-error {
    background-color: #fde2e2;
    border-color: #f56c6c;
  }

  &-quote {
    background-color: #fcedd6;
    border-color: #E6A23C;
  }

  &-body {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    // background-color: #F56C6C;
    p {
      flex: 1;
      padding-left: 10px;
    }

    .label {
      flex:2;
    }

    .actionName {
      text-align: end;
      font-size: 12px;
      color: #a9a8a8;
      padding-right: 10px;
    }
  }
}

/deep/ .el-card__body {
  padding: 0;
  height: 100%;
}

.cursor {
  cursor: pointer;
  padding-right: 10px;
}
</style>
