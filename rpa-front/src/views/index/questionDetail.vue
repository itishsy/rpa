<template>
  <div class="spl-container">
    <breadcrumb :data="pathData"></breadcrumb>
    <div class="pl-20 pr-20 mb-20">
      <!--<div class="question"><i class="el-icon-question text-blue mr-10 font-20"></i>怎么添加参保数据？</div>-->
      <div class="question"><i class="el-icon-question text-blue mr-10 font-20"></i>{{setForm.title}}</div>
      <div class="answer">
        <i class="el-icon-chat-dot-round text-blue font-20"></i>
        <div class="answer-content" v-html="setForm.content">

        </div>
      </div>
    </div>
  </div>
</template>
<script>

  export default {
    components: {},
    data() {
      return {
        pathData: [{name:'常见问题'}],
        id: '',
        setForm: {
          title: '',
          content: ''
        }
      }
    },
    computed: {},
    watch: {},
    created() {
      let that = this
      /* let tabs = this.$store.state.tabs
       if (tabs) {
         this.pathData = tabs[this.$route.path]
       }*/
      this.id = this.$route.query.id
    },
    mounted() {
      this.getData()
    },
    methods: {
      getData(){
        let that = this
        let setForm = that.setForm
        this.$http({
          url: '/policy/viewOneProblem?id=' + this.id,
          method: 'post',
          data: {}
        }).then(({data}) => {
          let res = data.data
          setForm.title = res.question
          setForm.content = res.answer
        }).catch((data) => {
        })
      },
    }
  }
</script>
<style lang="less" scoped>
  .question {
    font-size: 18px;
    padding: 20px 0;
  }

  .answer {
    border: 1px solid #dbdbdb;
    border-radius: 4px;
    padding: 20px;
    display: flex;
    /deep/.answer-content {
      flex: 1;
      margin-left: 10px;
    }
  }
</style>
