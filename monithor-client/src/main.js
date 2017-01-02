import Vue from 'vue'
import VueResource from 'vue-resource'
import VeeValidate from 'vee-validate'

import App from './components/App'
import router from './router'

Vue.use(VueResource)
Vue.use(VeeValidate, {fieldsBagName: 'formFields'})

/* eslint-disable no-new */
new Vue({
  router,
  el: '#app',
  template: '<App/>',
  components: {App}
})
