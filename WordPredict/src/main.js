import Vue from 'vue'
import { AutoComplete, Input, Row } from 'ant-design-vue'
import 'ant-design-vue/dist/antd.css'
import App from './App'

Vue.config.productionTip = false

Vue.use(AutoComplete)
Vue.use(Input)
Vue.use(Row)

new Vue({
  render: function (h) { return h(App) }
}).$mount('#app')
