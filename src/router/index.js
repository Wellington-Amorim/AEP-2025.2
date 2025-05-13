import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Mapa from '../views/Mapa.vue'
import Simulador from '../views/Simulador.vue'

const routes = [
  { path: '/',           name: 'Home',      component: Home },
  { path: '/mapa',       name: 'Mapa',      component: Mapa },
  { path: '/simulador',  name: 'Simulador', component: Simulador }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
