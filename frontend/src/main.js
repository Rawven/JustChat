import './assets/main.css'

import {createApp,} from 'vue'
import {createPinia} from 'pinia'
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import App from './App.vue'
import router from './router'
import VueParticles from 'vue-particles'
import {axiosFilter} from "@/axios";
import  "@/tailcss.css";
// eslint-disable-next-line no-unused-vars
import Proton from "proton-engine";
// eslint-disable-next-line no-unused-vars
import RAFManager from "raf-manager";
const app = createApp(App)
app.use(VueParticles)
export const Host = "10.24.3.176"
app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.provide('axiosFilter', axiosFilter)
app.mount('#app')

