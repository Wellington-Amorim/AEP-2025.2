import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Simulacao from '../views/Simulacao.vue'
import Dados from '../views/Dados.vue'
import Sobre from '../views/Sobre.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/simulacao',
      name: 'simulacao',
      component: Simulacao
    },
    {
      path: '/dados',
      name: 'dados',
      component: Dados
    },
    {
      path: '/sobre',
      name: 'sobre',
      component: Sobre
    }
  ]
})

export default router
