import Vue from 'vue'
import App from '@/App.vue'
import router from '@/router'
import vuetify from '@/vuetify'
import VueNoty from "@/noty"
import '@/config.scss'
import '@fortawesome/fontawesome-free/css/all.min.css'

Vue.config.productionTip = false

Vue.use(VueNoty)

new Vue({
  router,
  vuetify,
  render: h => h(App),
}).$mount('#app')
