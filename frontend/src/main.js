import './assets/main.css'

import {createApp,} from 'vue'
import {createPinia} from 'pinia'
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import App from './App.vue'
import router from './router'
import VueParticles from 'vue-particles'
import {axiosFilter} from "@/axios";



const app = createApp(App)
app.use(VueParticles)
export const Host = "10.44.59.225"
app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.provide('axiosFilter', axiosFilter)
app.mount('#app')

