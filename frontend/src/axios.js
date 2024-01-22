// 创建一个axios实例
import axios from "axios";
import {ElMessage} from "element-plus";
import {Host} from "@/main";
import {provide} from "vue";

const axiosFilter = axios.create();
// 添加请求拦截器
axiosFilter.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    console.log("开始调用,URL" + config.url + ",method:" + config.method + ",data:" + JSON.stringify(config.data))
    return config;
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});
// 添加响应拦截器
axiosFilter.interceptors.response.use(function (response) {
    // 对响应数据做点什么
    if (response.data.code !== 200) {
        if (response.data.code === 402) {
            ElMessage.info("令牌过期，自动刷新");
            axiosFilter.post('http://' + Host + ':7000/auth/refreshToken', {'token': localStorage.getItem("token")}).then(response1 => {
                localStorage.setItem("token", response1.data.data);
            })
            this.$router.go(0);
        }
        ElMessage.error(response.data.message);
        return Promise.reject(new Error(response.data.message));
    }

    console.log("调用成功,URL" + response.config.url + ",data:" + JSON.stringify(response.data))
    return response;
}, function (error) {
    // 对响应错误做点什么
    return Promise.reject(error);
});
// 提供axiosFilter
provide('axiosFilter', axiosFilter);
export {axiosFilter};