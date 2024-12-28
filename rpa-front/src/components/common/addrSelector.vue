<template>
  <div class="addr-main" ref="addrMain">
    <el-popover
      style="margin-top: 0!important;"
      placement="bottom-start"
      width="340"
      popper-class="addr-select"
      :visible-arrow="false"
      trigger="manual" v-model="visible">
      <div @click.stop="showPanel">
      <div class="addr-area" v-show="!isSearch">
        <p class="title">热门城市（支持汉字/拼音）</p>
        <el-tabs type="border-card">
          <el-tab-pane label="热门城市">
            <div v-for="hot in addr.hotCity" :key="hot.id">
              <ul class="addrList">
                <li @click.stop="selectAddr(hot)">{{hot.name}}</li>
              </ul>
              <div style=""></div>
            </div>

          </el-tab-pane>
          <el-tab-pane label="A-H">
            <div v-for="(ahItem, index) in addr.ah" :key="index">
              <div v-if="ahItem" class="display-flex">
                <p class="firstWord">{{ahItem.firstWord}}</p>
                <ul class="addrList clearfix">
                  <li v-for="ahIt in ahItem.addrList" :key="ahIt.id" @click.stop="selectAddr(ahIt)">{{ahIt.name}}</li>
                </ul>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="I-P">
            <div v-for="(ipItem, index) in addr.ip" :key="index">
              <div v-if="ipItem" class="display-flex">
                <p class="firstWord">{{ipItem.firstWord}}</p>
                <ul class="addrList clearfix">
                  <li v-for="ipIt in ipItem.addrList" :key="ipIt.id" @click.stop="selectAddr(ipIt)">{{ipIt.name}}</li>
                </ul>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="Q-Z">
            <div v-for="(qzItem, index) in addr.qz" :key="index">
              <div v-if="qzItem" class="display-flex">
                <p class="firstWord">{{qzItem.firstWord}}</p>
                <ul class="addrList clearfix">
                  <li v-for="qzIt in qzItem.addrList" :key="qzIt.id" @click.stop="selectAddr(qzIt)">{{qzIt.name}}</li>
                </ul>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
      <div v-show="isSearch">
        <ul class="search-list">
          <li v-for="search in searchList" :key="search.id" @click.stop="selectAddr(search)" class="display-flex">
            <span>{{search.name}}</span>
            <span class="flex1 text-right">{{search.word?search.word:''}}</span>
          </li>
        </ul>
      </div>
      </div>
      <div slot="reference" @click.stop="showPanel" :style="{width: inputWidth}">
      <el-input
        :placeholder="placeholder?placeholder:'请选择'"
        suffix-icon="ic-addr"
        v-model="inputVal" style="width: 100%" :disabled="disabled" @input="handleInput" @focus.stop="isSearch=false" @blur="handleBlur">
      </el-input>
      </div>
    </el-popover>
  </div>
</template>
<script>
export default {
  name: 'addrSelector',
  data () {
    return {
      addrVal: '',
      inputVal: '',
      valField: 'name',
      visible: false,
      isSearch: false,
      searchList: [],
      addrArray: ['潜山县|Qianshan|qsx|0|327', '迎江区|Yingjiang|yjq|0|1291907', '大观区|Daguan|dgq|0|1291906', '亳州|Bozhou|bz|0|94', '合肥|Hefei|hf|0|92', '淮南|Huainan|hn|0|175', '六安|Luan|la|0|199', '马鞍山|Maanshan|mass|0|142', '铜陵县|Tongling|tlx|0|361', '芜湖|Wuhu|whs|0|148', '芜湖县|Wuhu|whx|0|1291904', '无为县|Wuwei|wwx|0|1291905', '鸠江区|Jiujiang|jjq|0|1291892', '澳门|aomen|am|0|1291901', '澳门半岛|Aomenbandao|ambd|0|335', '嘉模堂区|Jiamotangqu|jmtq|0|226', '氹仔岛|Dangzaidao|dzd|0|342', '路环岛|Luhuandao|lhd|0|341', '圣方济各堂区|Shengfangjigetangqu|sfjgtq|0|350', '平谷区|Pinggu|pgq|0|707', '延庆县|Yanqing|yqx|0|20824', '北京|Beijing|bj|1|54', '怀柔区|Huairou|hrq|0|10638', '本地市|bendishi|bds|0|20822', '福州|Fuzhou|fz|0|87', '龙岩|Longyan|lys|0|143', '南平|Nanping|nps|0|145', '宁德|Ningde|nds|0|139', '莆田|Putian|pt|0|176', '泉州|Quanzhou|qzs|0|147', '厦门|Xiamen|xm|1|64', '漳州|Zhangzhou|zzs|0|149', '合作市|Hezuo|hzs|0|20917', '甘南|Gannan|gn|0|20825', '嘉峪关|Jiayuguan|jyg|0|1291886', '镜铁区|Jingtie|jtq|0|1291887', '西固区|Xigu|xgq|0|1291890', '兰州|Lanzhou|lzs|0|141', '城关区|Chengguan|cgq|0|1291885', '临夏|Linxia|lx|0|20826', '康乐县|Kangle|klx|0|1291889', '静宁县|Jingning|jnx|0|1291888', '华池县|Huachi|hcx|0|356', '天水|Tianshui|ts|0|1291882', '广东|Guangdong|gd|0|340', '潮州|Chaozhou|cz|0|191', '从化|conghua|ch|0|72', '东莞|Dongguan|dg|0|71', '番禺|fanyu|py|0|108', '佛山|Foshan|fs|0|63', '天河区|Tianhe|thq|0|334', '番禺区|Panyu|fyq|0|1291899', '广州九焱|Guangzhoujiuyan|gzjy|0|53', '广州|Guangzhou|gz|1|61', '河源|Heyuan|hy|0|157', '惠州|Huizhou|hz|0|78', '江门|Jiangmen|jm|0|73', '揭阳|Jieyang|jy|0|180', '开平|kaiping|kp|0|110', '高州市|Gaozhou|gzs|0|344', '茂名|Maoming|mm|0|173', '信宜市|Xinyi|xys|0|331', '电白区|Dianbai|dbq|0|321', '梅州|Meizhou|mz|0|159', '清远|Qingyuan|qy|0|83', '汕头|Shantou|st|0|179', '汕尾|Shanwei|sw|0|172', '韶关|Shaoguan|sg|0|91', '深圳|Shenzhen|sz|1|62', '南山区|Nanshan|nsq|0|1291914', '台山|taishan|ts|0|111', '阳江|Yangjiang|yj|0|170', '云浮|Yunfu|yf|0|100', '湛江|Zhanjiang|zjs|0|122', '肇庆|Zhaoqing|zq|0|82', '中山|Zhongshan|zs|1|79', '中山九焱|zhongshanjiuyan|zsjy|0|84', '珠海|Zhuhai|zh|0|74', '广西|Guangxi|gx|0|1291891', '百色|Baise|bs|0|163', '北海|Beihai|bhs|0|121', '防城港|Fangchenggang|fcg|0|318', '港南区|Gangnan|gnq|0|1291894', '港北区|Gangbei|gbq|0|1291895', '平南县|Pingnan|pnx|0|1291896', '覃塘区|Qintang|ttq|0|1291897', '贵港|Guigang|gg|0|164', '桂林|Guilin|gl|0|77', '河池|Hechi|hc|0|316', '钟山县|Zhongshan|zsx|0|337', '柳州|Liuzhou|lzs|0|128', '南宁|Nanning|nn|0|96', '钦州|Qinzhou|qzs|0|130', '梧州|Wuzhou|wzs|0|131', '兴业县|Xingye|xyx|0|348', '玉林|Yulin|yl|0|165', '平坝区|Pingba|pbq|0|359', '毕节|Bijie|bj|0|1291915', '观山湖区|Guanshanhu|gshq|0|1291911', '贵阳|Guiyang|gy|0|70'],
      VcityReg: {
        reg1: /^[a-h]$/i,
        reg2: /^[i-p]$/i,
        reg3: /^[q-z]$/i
      },
      addr: {
        hotCity: [],
        ah: [],
        ip: [],
        qz: []
      },
      inputWidth: '260px'
    }
  },
  props: ['value', 'width', 'addrArr', 'placeholder', 'valueField', 'disabled'],
  model: {
    prop: 'value',
    event: 'change'
  },
  watch: {
    value (newValue, oldValue) {
      this.addrVal = newValue
      if (this.valField != 'name') {
        this.inputVal = this.getNameByAddrId(this.addrVal)
      } else {
        this.inputVal = newValue
      }
    },
    width (newValue, oldValue) {
      this.inputWidth = newValue
    },
    addrArr (newValue, oldValue) {
      this.addrArray = newValue
      this.initAdrr()
    },
    valueField (newValue, oldValue) {
      this.valField = newValue
    }
  },
  components: {},
  created () {
    this.valField = this.valueField ? this.valueField : 'name'
    this.inputWidth = this.width ? this.width : '260px'
    this.addrArray = this.addrArr ? this.addrArr : []
    this.initAdrr()
    this.addrVal = this.value ? this.value : ''
    if (this.valField != 'name') {
      this.inputVal = this.getNameByAddrId(this.addrVal)
    } else {
      this.inputVal = this.addrVal
    }
  },
  mounted () {
    window.addEventListener('click', this.handleGlobalClick)
  },
  beforeDestroy () { // 在组件生命周期结束的时候销毁。
    window.removeEventListener('click', this.handleGlobalClick)
  },
  methods: {
    // 移除操作
    handleGlobalClick (e) {
      var _this = this
      if (!_this.$refs.addrMain.contains(e.target)) {
        _this.visible = false
      }
    },
    initAdrr () {
      this.addr = {
        hotCity: [],
        ah: [],
        ip: [],
        qz: []
      }
      let that = this
      let item
      let firstWord
      let index
      let ahArr = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'] // 用于记录a-h每个字母是否存在以及索引位置
      let ipArr = ['I', 'J', 'K', 'L', 'M', 'N', 'O', 'P'] // 用于记录i-p每个字母是否存在以及索引位置
      let qzArr = ['Q', 'R', 'S', 'T', 'W', 'X', 'Y', 'Z'] // 用于记录q-z每个字母是否存在以及索引位置
      this.addrArray.forEach(it => {
        item = it.split('|')
        // 第四位数据，1代表热门，0代表非热门
        if (item[3] === '1') {
          that.addr.hotCity.push({ name: item[0], id: item[4] })
        }
        firstWord = item[1][0].toUpperCase()
        /* 格式: [
                {
                  firstWord: 'A',
                  addrList: [
                    {
                      name: '澳门',
                      id: '0'
                    },
                    {
                      name: '鞍山',
                      id: '1'
                    }
                  ]
                }
              ] */
        if (that.VcityReg.reg1.test(firstWord)) {
          //  a-h
          index = ahArr.indexOf(firstWord)
          if (that.addr.ah[index]) {
            //  已存在
            that.addr.ah[index].addrList.push({ name: item[0], id: item[4] })
          } else {
            //  未存在
            that.addr.ah[index] = ({ firstWord: firstWord, addrList: [{ name: item[0], id: item[4] }] })
          }
        } else if (that.VcityReg.reg2.test(firstWord)) {
          //  i-p
          index = ipArr.indexOf(firstWord)
          if (that.addr.ip[index]) {
            //  已存在
            that.addr.ip[index].addrList.push({ name: item[0], id: item[4] })
          } else {
            //  未存在
            that.addr.ip[index] = ({ firstWord: firstWord, addrList: [{ name: item[0], id: item[4] }] })
          }
        } else if (that.VcityReg.reg3.test(firstWord)) {
          //  q-z
          index = qzArr.indexOf(firstWord)
          if (that.addr.qz[index]) {
            //  已存在
            that.addr.qz[index].addrList.push({ name: item[0], id: item[4] })
          } else {
            //  未存在
            that.addr.qz[index] = ({ firstWord: firstWord, addrList: [{ name: item[0], id: item[4] }] })
          }
        }
      })
    },
    selectAddr (item) {
      if (item.isEmpty) return false
      this.addrVal = item[this.valField]
      this.inputVal = item.name
      this.visible = false
      this.$emit('change', item[this.valField])
      this.$emit('changeAddrSelect', item)
    },
    showPanel() {
      if (!this.disabled) {
        this.visible = true
      }
    },
    handleInput (value) {
      this.$emit('change', value)
      let item
      let list = []
      let searchVal = value.toUpperCase()
      this.addrArray.forEach(it => {
        item = it.split('|')
        if (item[0].indexOf(value) > -1 || item[1].toUpperCase().indexOf(searchVal) > -1 || item[2].toUpperCase().indexOf(searchVal) > -1) {
          list.push({ name: item[0], word: item[1].toUpperCase(), id: item[4] })
        }
      })
      if (list.length === 0) {
        list.push({ name: '对不起，没有找到数据"' + value + '"', isEmpty: true })
      }
      this.searchList = list
      this.isSearch = true
      this.visible = true
    },

    handleBlur () {
      // 搜索能匹配上的id
      let item
      let searchVal = this.addrVal
      let obj = { name: '', id: '' }
      let searchRes = false
      for (let i = 0; i < this.addrArray.length; i++) {
        item = this.addrArray[i].split('|')
        if (item[0] === searchVal) {
          obj = { name: item[0], word: item[1].toUpperCase(), id: item[4] }
          searchRes = true
          break
        }
      }
      if (!searchRes) {
        this.addrVal = ''
        this.inputVal = ''
        this.$emit('change', '')
      }
      this.$emit('changeAddrSelect', obj)
    },
    getNameByAddrId (addrId) {
      var addrName = ''
      let item
      if (addrId != undefined && addrId != null && addrId != '') {
        for (let i = 0; i < this.addrArray.length; i++) {
          item = this.addrArray[i].split('|')
          if (item[4] === addrId) {
            addrName = item[0]
            break
          }
        }
      }
      return addrName
    },
    getIdByAddrName (addrName) {
      var addrId = ''
      let item
      if (addrName != undefined && addrName != null && addrName != '') {
        for (let i = 0; i < this.addrArray.length; i++) {
          item = this.addrArray[i].split('|')
          if (item[0] === addrName) {
            addrId = item[4]
            break
          }
        }
      }
      return addrId
    }
  }
}
</script>

<style lang="less">
  .addr-select {
    padding: 0;
    top: 36px;
    margin-top: 0 !important;
    .el-tabs--border-card > .el-tabs__content{
      max-height: 300px;
      overflow: auto;
    }
  }
</style>
<style lang="less" scoped>
  /deep/.el-tabs__content{
      max-height: 300px;
      overflow: auto;
  }
  .addr-main{
    position: relative;
    display: inline-block;
  }
  /deep/.ic-addr{
    margin-right: 5px;
  }
  .addr-select {
    .addr-area {
      font-size: 12px;
      .title {
        height: 30px;
        line-height: 30px;
        padding-left: 10px;
        background: rgba(248, 248, 248, 1);
      }
      /deep/.el-tabs__item{
        min-width: 60px;
        text-align: center;
        padding: 0 10px!important;
        font-size: 12px;
        height: 30px;
        line-height: 30px;
      }
      .firstWord{
        width: 20px;
        padding-top: 5px;
        color: @blueColor;
      }
      .addrList{
        flex: 1;
        padding: 0;
        margin: 0;
        li{
          width: 25%;
          float: left;
          margin: 5px 0;
          cursor: pointer;
          padding-right: 10px;
          box-sizing: border-box;
        }
        li:hover{
          color: @blueColor;
        }
      }
    }
    .search-list{
      padding: 0;
      margin: 5px 0;
      max-height: 300px;
      overflow: auto;
      li{
        padding: 5px 10px;
        cursor: pointer;
      }
      li:hover{
        color: #fff;
        background-color: @blueColor;
      }
    }
  }
</style>
