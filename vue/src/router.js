import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'root',
    redirect: '/valves',
  },
  {
    path: '/valves',
    name: 'valves',
    component: () => import('./views/Valves.vue')
  },
  {
    path: '/valve/history',
    name: 'valveHistory',
    component: () => import('./views/ValveHistory.vue'),
    props: true,
  },
  {
    path: '/weather',
    name: 'weather',
    component: () => import('./views/Weather.vue'),
  },
  {
    path: '/settings',
    name: 'settings',
    component: () => import('./views/Settings.vue')
  },
]

const router = new VueRouter({
  routes
})

export default router
