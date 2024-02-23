import './assets/main.css'

import {createApp,} from 'vue'
import {createPinia} from 'pinia'
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import App from './App.vue'
import router from './router'
import VueParticles from 'vue-particles'
import {axiosFilter} from "@/axios";
import "@/tailcss.css";
// eslint-disable-next-line no-unused-vars
// eslint-disable-next-line no-unused-vars

const app = createApp(App)
app.use(VueParticles)
export const Host = "10.44.59.225"
export const ipfsHost = "http://10.44.59.225:8083/ipfs/"
app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.provide('axiosFilter', axiosFilter)
app.mount('#app')

