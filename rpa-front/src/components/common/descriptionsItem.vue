<script>
export default {
  components: {},
  props: {
    column: {
      type: [Number, String],
      default: 8,
    },
    label: {
      default: '',
    },
    value: {
      default: '',
    },
    labelClass: {
      type: String,
      default: 'lab',
    },
    valClass: {
      type: String,
      default: 'val',
    },
  },
  data() {
    return {}
  },
  computed: {
    getCol() {
      return Number(this.column) == Number(this.column) ? this.column : 8
    },
  },
  mounted() {},
  methods: {
    setColumn(num) {
      this.column = num
    },
  },
  render(h) {
    return (
      <el-col span={this.column}>
        <div class="detail-item" >
          {this.$slots.label || h('span', { class: this.labelClass }, this.label + '：')}
          {this.$slots.value || h('span', { class: this.valClass }, this.$slots.default)}
        </div>
      </el-col>
    )
  },
}
</script>
<style lang="less" scoped>
.detail-item {
  display: flex;
  margin-bottom: 20px;
  .lab {
    width: 160px;
    text-align: right;
    flex-shrink: 0;
  }
  .val {
    flex: 1;
    text-align: left;
  }
}
</style>