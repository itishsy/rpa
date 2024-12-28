import {  Shape } from '@antv/x6'

//画布的一些配置项
export const graphConfig = {
    background: {
      //   color: '#fffbe6', // 设置画布背景颜色
    },
    grid: {
      size: 10,
      visible: true,
    },
    mousewheel: {
      //滚轮缩放行为
      enabled: true, //开启
      zoomAtMousePosition: true, //使用鼠标位置作为中心缩放
      modifiers: 'ctrl', //修饰键 按下该键并滚动鼠标才会进行画布缩放
      minScale: 0.5, //最小缩放级别
      maxScale: 3, //最大缩放级别
    },
    connecting: {
      //配置画布的连线规则
      router: {
        name: 'manhattan', //智能正交路由，自动避开路径上的其他节点
        args: {
          padding: 1,
        },
      },
      connector: {
        //圆角连接器
        name: 'rounded',
        args: {
          radius: 8,
        },
      },
      anchor: 'center',
      connectionPoint: 'anchor',
      allowBlank: false, //不允许链接到画布的空白处
      snap: {
        radius: 20, //距离链接桩节点 20px会进行自动吸附
      },
      createEdge() {
        return new Shape.Edge({
          attrs: {
            line: {
              stroke: '#A2B1C3',
              strokeWidth: 2,
              targetMarker: {
                name: 'block',
                width: 12,
                height: 8,
              },
            },
          },
          zIndex: 0,
        })
      },
      validateConnection({ targetMagnet }) {
        //判断链接是否有效
        return !!targetMagnet
      },
    },
    highlighting: {
      magnetAdsorbed: {
        //连线过程中，自动吸附到链接桩时被使用 ,进行高亮显示
        name: 'stroke',
        args: {
          attrs: {
            fill: '#5F95FF',
            stroke: '#5F95FF',
          },
        },
      },
    },
    resizing: true,
    rotating: false,
    selecting: {
      enabled: true,
      rubberband: true,
      showNodeSelectionBox: true,
    },
    history: {
      enabled: true,
      // beforeAddCommand(event, args){
      //   // 忽略节点和边tool的历史变更
      //   if (args.key === 'tools' || args.key === 'zIndex') {
      //     return false
      //   }
      //   if(args.key === undefined && graph.isEdge(args.cell)){
      //     args.cell.removeTools();
      //   }
      // }
    }, 
    snapline: true,
    keyboard: true,
    clipboard: true,

  }