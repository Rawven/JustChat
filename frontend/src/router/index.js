import {createRouter, createWebHistory} from 'vue-router'
import loginView from "@/views/common/LoginView.vue";
import registerView from "@/views/common/RegisterView.vue";
import MainPageView from "@/views/common/MainPageView.vue";
import UpdateInfoView from "@/views/common/UpdateInfoView.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/test',
            name: 'test',
            component: () => import('@/components/common/main/moment.vue')
        },
        {
            path: '/login',
            name: 'LoginView.vue',
            component: loginView
        },
        {
            path: '/register',
            name: 'RegisterView',
            component: registerView
        },
        {
            path: '/main',
            name: "MainPageView",
            component: MainPageView
        },
        {
            path: '/search',
            name: 'RoomPageView',
            component: () => import('@/components/common/main/search.vue')
        },
        {
            path: '/personal',
            name: 'updateInfoView',
            component: UpdateInfoView
        },
        {
            path: '/notice',
            name: 'NoticeView',
            component: () => import('@/views/common/Notice.vue')
        },
        {
            path: '/friend',
            name: 'FriendView',
            component: () => import('@/views/common/FriendPageView.vue'),
        },
        {
            path: '/moment',
            name: 'MomentView',
            component: () => import('@/views/common/MomentView.vue')
        },
    ]
})

export default router

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token');
    if (!token && (to.path !== '/login' && to.path !== '/register')) {
        next('/login');
    } else {
        next();
    }
});
