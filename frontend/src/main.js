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
import global from "./global.js";
// eslint-disable-next-line no-unused-vars
// eslint-disable-next-line no-unused-vars

const app = createApp(App)
app.use(VueParticles)
export const Host = "localhost"

app.config.globalProperties.$global = global;
export const ipfsHost = "http://localhost:8083/ipfs/"
export const MSG_DELIVERED_ACK = "msg_delivered_ack"
export const MSG_READ_ACK = "msg_read_ack"
app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.provide('axiosFilter', axiosFilter)
app.mount('#app')

