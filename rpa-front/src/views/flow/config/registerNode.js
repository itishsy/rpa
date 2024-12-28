//画布要注册的节点形状
import { portsConfig } from './port'
export const nodeConfig = {
  rectConfig: {
    //矩形
    inherit: 'rect',
    width: 86,
    height: 36,
    attrs: {
      body: {
        strokeWidth: 1,
        stroke: '#5F95FF',
        fill: '#EFF4FF',
        filter: {
          name: 'dropShadow',
          args: {
            color: '#000000',
            dx: 2,
            dy: 2,
            blur: 6,
            opacity: 0.1,
          },
        },
      },
      text: {
        fontSize: 12,
        fill: '#262626',
      },
    },
    ports: { ...portsConfig },
  },
  polygonConfig: {
    //多边形
    inherit: 'polygon',
    width: 106,
    height: 46,
    attrs: {
      body: {
        strokeWidth: 1,
        stroke: '#5F95FF',
        fill: '#EFF4FF',
        filter: {
          name: 'dropShadow',
          args: {
            color: '#000000',
            dx: 2,
            dy: 2,
            blur: 6,
            opacity: 0.1,
          },
        },
      },
      text: {
        fontSize: 12,
        fill: '#262626',
      },
    },
    ports: {
      ...portsConfig,
      items: [
        {
          group: 'top',
        },
        {
          group: 'left',
        },
        {
          group: 'right',
        },
        {
          group:'bottom'
        }
      ],
    },
  },
  imageConfig: {
    //图片
    inherit: 'rect',
    width: 52,
    height: 52,
    markup: [
      {
        tagName: 'rect',
        selector: 'body',
      },
      {
        tagName: 'image',
      },
      {
        tagName: 'text',
        selector: 'label',
      },
    ],
    attrs: {
      body: {
        stroke: '#5F95FF',
        fill: '#5F95FF',
      },
      image: {
        width: 26,
        height: 26,
        refX: 13,
        refY: 16,
      },
      label: {
        refX: 3,
        refY: 2,
        textAnchor: 'left',
        textVerticalAnchor: 'top',
        fontSize: 12,
        fill: '#fff',
      },
    },
    ports: { ...portsConfig },
  },
  circleConfig: {
    //圆形
    inherit: 'circle',
    width: 45,
    height: 45,
    attrs: {
      body: {
        strokeWidth: 1,
        stroke: '#5F95FF',
        fill: '#EFF4FF',
      },
      text: {
        fontSize: 12,
        fill: '#262626',
      },
    },
    ports: { ...portsConfig },
  },
}
