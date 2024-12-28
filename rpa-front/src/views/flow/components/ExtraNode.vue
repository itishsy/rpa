<template>
  <div class="card" :class="{

    'card-quote': (actionCode === 'subFlow' || type === 'flow') && !openEdit,
    'card-codeBlock': actionCode === 'codeBlock' && !openEdit,
    'card-quote-edit': (actionCode === 'subFlow' || type === 'flow') && openEdit,
    'card-codeBlock-edit': actionCode === 'codeBlock' && openEdit,
    'card-success':nodeState === '1',
    'card-error': nodeState === '2',
    'card-openEdit-no': isDisabledStepSet,

  }">
    <div class="card-body">
      <p>{{ label }}</p>
      <p class="actionName">{{actionName}}</p>
      <i class="el-icon-edit cursor" v-if="openEdit" @click="triggerEdit()"></i>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Extra',
  inject: ['getGraph', 'getNode'],
  data () {
    return {
      num: 0,
      id: '', // 当前对应节点的id
      label: '', // 对应节点的label
      desc: '', // 加入到节点json中的描述
      nodeState: '0', // 节点状态
      quote: false, // 当前是否为引用状态
      actionCode: '',
      type: '',
      openEdit: '', // 当前节点是否可编辑 1可编辑 0不可编辑
      actionName: '', // 当前节点右侧描述文字
      isDisabledStepSet: '' // 是否为引用模板且开放编辑为否（不可编辑配置信息）
    }
  },
  mounted () {
    const self = this
    const node = this.getNode()
    const { flowName, desc, nodeState, quote, actionCode, type, openEdit, actionName, isDisabledStepSet } = node.getData()
    // console.log('node.getData', node.getData())
    this.label = flowName || ''
    this.desc = desc || ''
    this.nodeState = nodeState || openEdit || '0'
    this.quote = quote
    this.actionCode = actionCode
    this.type = type
    this.openEdit = openEdit
    this.actionName = actionName
    this.isDisabledStepSet = isDisabledStepSet
    // console.log('this.openEdit ', this.openEdit)
    // 监听数据改变事件
    node.on('change:data', ({ current }) => {
      //   self.id = current.id
      self.label = current.flowName
      self.desc = current.desc
      self.nodeState = current.nodeState
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

  &-quote {       //子流程
    background-color: #fcedd6;
    border-color: #E6A23C;
  }

  &-quote-edit {    //子流程开放编辑
    // background-color: #fcedd6;
    // border-color: #67c23a;

    background-color: #fcedd6;
    border-color: #409EFF;
  }

  &-codeBlock {   //代码块
    background-color: #fffbe6;
    border-color: #f5de6a;
  }

  &-codeBlock-edit {    //代码块开放编辑
    // background-color: #fffbe6;
    // border-color: #67c23a;

    background-color: #fffbe6;
    border-color: #409EFF;
  }

  &-openEdit-no{
    background-color: #eeeeee;
    border-color: #dbdbdb;
  }

  &-success {   //成功节点
    background-color: #e1f3d8;
    border-color: #67c23a;
  }

  &-error {     //失败节点
    background-color: #fde2e2;
    border-color: #f56c6c;
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
