import { createApp } from 'vue'

import App from './App.vue'
import router from './router'
import pinia from './stores'
import 'element-plus/theme-chalk/dark/css-vars.css'

import '@/assets/styles/ali-iconfont.css'
import '@/assets/styles/main.scss'

document.documentElement.classList.add('dark')

const app = createApp(App)

app.use(pinia)
app.use(router)

app.mount('#app')
