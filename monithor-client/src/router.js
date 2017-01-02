import Vue from 'vue'
import Router from 'vue-router'
import Job from './components/Job'
import Jobs from './components/Jobs'
import Monitor from './components/Monitor'

Vue.use(Router)

export default new Router({
  mode: 'hash',
  linkActiveClass: 'active',
  routes: [
    {name: 'new_job', path: '/jobrunner', component: Job},
    {name: 'edit_job', path: '/jobrunner/:id', component: Job},
    {name: 'jobs', path: '/jobs', component: Jobs},
    {name: 'monitor', path: '/', component: Monitor}
  ]
})
