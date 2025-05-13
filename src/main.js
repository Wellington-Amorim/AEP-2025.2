import { createApp } from 'vue'
import App from './App.vue'
import router from './router'      // <<< importe o router

import 'cesium/Build/Cesium/Widgets/widgets.css' // se ainda não importou o CSS do Cesium

createApp(App).use(router).mount('#app')
