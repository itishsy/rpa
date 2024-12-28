const proPlugins = [];
console.log(process.env.NODE_ENV)
if (process.env.NODE_ENV === 'production') {
  proPlugins.push('transform-remove-console');
}
module.exports = {
  "presets": [
    "@vue/app"
  ],
  "plugins": [
    [
      "component",
      {
        "libraryName": "element-ui",
        "styleLibraryName": "theme-chalk"
      }
    ],
    ...proPlugins
  ]
}
