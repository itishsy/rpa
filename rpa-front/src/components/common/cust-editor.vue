<template>
  <div>
    <quill-editor
      ref="myQuillEditor"
      v-model="content"
      :options="editorOption"
      @change="onEditorChange($event)"
      @blur="onEditorBlur($event)"
      @focus="onEditorFocus($event)"
      @ready="onEditorReady($event)"
      :style="{height: height, width: width}" :height="height" style="background-color: #fff;"
    />
  </div>

</template>
<script>
  import 'quill/dist/quill.core.css'
  import 'quill/dist/quill.snow.css'
  import 'quill/dist/quill.bubble.css'
  import { quillEditor } from 'vue-quill-editor'
  import Quill from "quill";
  import { lineHeightStyle } from "@/utils/lineHeight";
  import { ImageDrop } from 'quill-image-drop-module' // 图片拖动组件引用
  import ImageResize from 'quill-image-resize-module' // 图片缩放组件引用
  Quill.register('modules/imageDrop', ImageDrop) // 注册
  Quill.register('modules/imageResize', ImageResize) // 注册

  let fontSizeStyle = Quill.import('attributors/style/size')
  fontSizeStyle.whitelist = ['14px', '12px', '16px', '18px', '20px', '24px']
  Quill.register(fontSizeStyle, true)

  var Link = Quill.import('formats/link');

  class FileBlot extends Link {  // 继承Link Blot

    static create(value) {

      let node = undefined

      if (value&&!value.href){  // 适应原本的Link Blot

        node = super.create(value);

      }

      else{  // 自定义Link Blot

        node = super.create(value.href);

        node.setAttribute('download', true);  // 左键点击即下载

        node.innerText = value.innerText;

      }

      return node;

    }

  }

  FileBlot.blotName = 'link';

  FileBlot.tagName = 'A';

  Quill.register(FileBlot);
  // Quill.register("modules/imageDrop", ImageDrop);
  var color =['#000000','#FFFFFF' ,'#ea5404','#FF0000' ,'#00FF00' ,'#0000FF' ,'#FF00FF' ,'#00FFFF' ,'#FFFF00' , ,'#F0F8FF' ,'#70DB93' ,'#5C3317','#9F5F9F' ,'#B5A642' ,'#D9D919' ,'#A62AA2' ,'#8C7853' ,'#A67D3D' ,'#5F9F9F' ,'#D98719' ,'#B87333' ,'#FF7F00' ,'#42426F' ,'#5C4033' ,'#2F4F2F' ,'#4A766E']
  // 工具栏配置
  const toolbarOptions = [
    ["bold", "italic", "underline", "strike"], // 加粗 斜体 下划线 删除线 -----['bold', 'italic', 'underline', 'strike']
    ["blockquote", "code-block"], // 引用  代码块-----['blockquote', 'code-block']
    [{ header: 1 }, { header: 2 }], // 1、2 级标题-----[{ header: 1 }, { header: 2 }]
    [{ list: "ordered" }, { list: "bullet" }], // 有序、无序列表-----[{ list: 'ordered' }, { list: 'bullet' }]
    [{ script: "sub" }, { script: "super" }], // 上标/下标-----[{ script: 'sub' }, { script: 'super' }]
    [{ indent: "-1" }, { indent: "+1" }], // 缩进-----[{ indent: '-1' }, { indent: '+1' }]
    [{ direction: "rtl" }], // 文本方向-----[{'direction': 'rtl'}]
    [{ size: fontSizeStyle.whitelist  }], // 字体大小-----[{ size: ['small', false, 'large', 'huge'] }]
    [{ lineheight: ["1", "1.5", "1.75", "2", "3", "4", "5"] }], // 对齐方式
    [{ header: [1, 2, 3, 4, 5, 6, false] }], // 标题-----[{ header: [1, 2, 3, 4, 5, 6, false] }]
    [{ color: color }, { background: [] }], // 字体颜色、字体背景颜色-----[{ color: [] }, { background: [] }]
    [{ font: [] }], // 字体种类-----[{ font: [] }]
    [{ align: [] }], // 对齐方式-----[{ align: [] }]
    ["clean"], // 清除文本格式-----['clean']
    ["image", "video", 'link'] // 链接、图片、视频-----['link', 'image', 'video']
  ];
  export default {
    name: 'cust-editor',
    components: {
      quillEditor
    },

    data () {
      return {
        // 富文本编辑器默认内容
        content: '',
        editorOption: {
          //  富文本编辑器配置
          modules: {
            //工具栏定义的
            toolbar: {
              container: toolbarOptions,
              handlers: {
                lineheight: (value) => {
                  if (value) {
                    let quill = this.$refs.myQuillEditor.quill;
                    quill.format("lineHeight", value);
                  }
                },
              },
            },
            imageResize: {          //放大缩小
              displayStyles: {
                backgroundColor: "black",
                border: "none",
                color: "white"
              },
              modules: ["Resize", "DisplaySize", "Toolbar"]
            },
          },
          //主题
          theme: "snow",
          placeholder: "请输入"
        },
        tempArr: ['#test#'],
        savedRangeIndex: ''
      }
    },
    props: {
      value: {
        type: String,
        default: '',
        required: false
      },
      height: {
        default: '300px'
      },
      width: {
        default: '100%'
      },
      showPlaceholder: {
        default: true
      },
    },
    model: {
      prop: 'value',
      event: 'change'
    },
    watch: {
      value(newValue, oldValue) {
        this.content = this.value
      }
    },
    created() {
      this.content = this.$lodash.isEmpty(this.value) ? '': this.value
      if(!this.showPlaceholder){
        this.editorOption.placeholder = ''
      }
    },
    methods: {
      insertTemplate (text) {
        var html = text
        var quill = this.$refs.myQuillEditor.quill
        quill.insertText(this.savedRangeIndex==''?0:this.savedRangeIndex, html, {
          'color': '#3E82FF',
          'italic': false,
          'list': true
        });
        this.$refs.myQuillEditor.quill.selection.savedRange.index = (this.savedRangeIndex==''?0:this.savedRangeIndex) + html.length
        quill.format('color', '#303133')
      },
      delText (data) {
        // var quill = this.$refs.myQuillEditor.quill
        // var detail = quill.deleteText(6, 4);
        // var detail = quill.deleteText(6, 4, 'api');
      },
      //失去焦点事件
      onEditorBlur(quill) {
        this.$emit('change', this.content)
        this.savedRangeIndex = this.$refs.myQuillEditor.quill.selection.savedRange.index
        this.$emit("onEditorBlur", quill)
      },
      //获得焦点事件`
      onEditorFocus(quill) {
        var quill = this.$refs.myQuillEditor.quill
        this.$emit("onEditorFocus", quill)

      },
      // 准备富文本编辑器
      onEditorReady(quill) {
        Quill.register({ "formats/lineHeight": lineHeightStyle }, true);
      },
      //内容改变事件
      onEditorChange({ quill, html, text }) {
        this.$emit('change', this.content)
      },
      // 获取编辑内容时间
      getEditorContent() {
        return this.content
      }
    }
  }
</script>

<style lang='less' scoped>
  /deep/.quill-editor{
    display: flex;
    flex-flow: column;

    .ql-toolbar.ql-snow{
      background-color: #f5f5f5;
      border-color: @inputBorderColor;
    }
    .ql-container.ql-snow{
      border-color: @inputBorderColor;
      flex: 1;
      overflow: auto;
    }
    .ql-tooltip{
      left: 0!important;
    }
    .ql-snow .ql-tooltip[data-mode="link"]::before {
      content: "请输入链接地址:";
    }
    .ql-snow .ql-tooltip.ql-editing a.ql-action::after {
      border-right: 0px;
      content: "保存";
      padding-right: 0px;
    }

    .ql-snow .ql-tooltip[data-mode="video"]::before {
      content: "请输入视频地址:";
    }
    .ql-snow .ql-color-picker .ql-picker-label svg, .ql-snow .ql-icon-picker .ql-picker-label svg{
      position: relative;
      top: -5px;
    }
    .ql-snow .ql-picker-label::before{
      position: relative;
      top: -9px;
    }
    .ql-snow .ql-picker.ql-size .ql-picker-label::before,
    .ql-snow .ql-picker.ql-size .ql-picker-item::before {
      content: "14px";
    }
    .ql-snow .ql-picker.ql-size .ql-picker-label[data-value="12px"]::before,
    .ql-snow .ql-picker.ql-size .ql-picker-item[data-value="12px"]::before {
      content: "12px";
    }
    .ql-snow .ql-picker.ql-size .ql-picker-label[data-value="14px"]::before,
    .ql-snow .ql-picker.ql-size .ql-picker-item[data-value="14px"]::before {
      content: "14px";
    }
    .ql-snow .ql-picker.ql-size .ql-picker-label[data-value="16px"]::before,
    .ql-snow .ql-picker.ql-size .ql-picker-item[data-value="16px"]::before {
      content: "16px";
    }
    .ql-snow .ql-picker.ql-size .ql-picker-label[data-value="18px"]::before,
    .ql-snow .ql-picker.ql-size .ql-picker-item[data-value="18px"]::before {
      content: "18px";
    }
    .ql-snow .ql-picker.ql-size .ql-picker-label[data-value="20px"]::before,
    .ql-snow .ql-picker.ql-size .ql-picker-item[data-value="20px"]::before {
      content: "20px";
    }
    .ql-snow .ql-picker.ql-size .ql-picker-label[data-value="24px"]::before,
    .ql-snow .ql-picker.ql-size .ql-picker-item[data-value="24px"]::before {
      content: "24px";
    }

    .ql-snow .ql-picker.ql-header .ql-picker-label::before,
    .ql-snow .ql-picker.ql-header .ql-picker-item::before {
      content: "文本";
    }
    .ql-snow .ql-picker.ql-header .ql-picker-label[data-value="1"]::before,
    .ql-snow .ql-picker.ql-header .ql-picker-item[data-value="1"]::before {
      content: "标题1";
    }
    .ql-snow .ql-picker.ql-header .ql-picker-label[data-value="2"]::before,
    .ql-snow .ql-picker.ql-header .ql-picker-item[data-value="2"]::before {
      content: "标题2";
    }
    .ql-snow .ql-picker.ql-header .ql-picker-label[data-value="3"]::before,
    .ql-snow .ql-picker.ql-header .ql-picker-item[data-value="3"]::before {
      content: "标题3";
    }
    .ql-snow .ql-picker.ql-header .ql-picker-label[data-value="4"]::before,
    .ql-snow .ql-picker.ql-header .ql-picker-item[data-value="4"]::before {
      content: "标题4";
    }
    .ql-snow .ql-picker.ql-header .ql-picker-label[data-value="5"]::before,
    .ql-snow .ql-picker.ql-header .ql-picker-item[data-value="5"]::before {
      content: "标题5";
    }
    .ql-snow .ql-picker.ql-header .ql-picker-label[data-value="6"]::before,
    .ql-snow .ql-picker.ql-header .ql-picker-item[data-value="6"]::before {
      content: "标题6";
    }

    .ql-snow .ql-picker.ql-font .ql-picker-label::before,
    .ql-snow .ql-picker.ql-font .ql-picker-item::before {
      content: "标准字体";
    }
    .ql-snow .ql-picker.ql-font .ql-picker-label[data-value="serif"]::before,
    .ql-snow .ql-picker.ql-font .ql-picker-item[data-value="serif"]::before {
      content: "衬线字体";
    }
    .ql-snow .ql-picker.ql-font .ql-picker-label[data-value="monospace"]::before,
    .ql-snow .ql-picker.ql-font .ql-picker-item[data-value="monospace"]::before {
      content: "等宽字体";
    }
    .ql-toolbar.ql-snow .ql-picker.ql-expanded .ql-picker-options{
      line-height: 1;
    }

    .ql-snow .ql-picker.ql-lineheight .ql-picker-label::before {
      content: "行高";
    }
    .ql-snow .ql-picker.ql-lineheight .ql-picker-item[data-value="1"]::before {
      content: "1";
    }
    .ql-snow .ql-picker.ql-lineheight .ql-picker-item[data-value="1.5"]::before {
      content: "1.5";
    }
    .ql-snow .ql-picker.ql-lineheight .ql-picker-item[data-value="1.75"]::before {
      content: "1.75";
    }
    .ql-snow .ql-picker.ql-lineheight .ql-picker-item[data-value="2"]::before {
      content: "2";
    }
    .ql-snow .ql-picker.ql-lineheight .ql-picker-item[data-value="3"]::before {
      content: "3";
    }
    .ql-snow .ql-picker.ql-lineheight .ql-picker-item[data-value="4"]::before {
      content: "4";
    }
    .ql-snow .ql-picker.ql-lineheight .ql-picker-item[data-value="5"]::before {
      content: "5";
    }
    .ql-snow .ql-picker.ql-lineheight {
      width: 70px;
    }
  }

  /*.editor {*/
    /*line-height: normal !important;*/
    /*height: 500px;*/
  /*}*/

</style>
