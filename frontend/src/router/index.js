import {createRouter, createWebHistory} from 'vue-router'
import loginView from "@/views/common/LoginView.vue";
import registerView from "@/views/common/RegisterView.vue";
import AvatarView from "@/views/common/AvatarView.vue";
import ChatRoomView from "@/views/common/ChatRoomView.vue";
import MainPageView from "@/views/common/MainPageView.vue";
import UpdateInfoView from "@/views/common/UpdateInfoView.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
          path: '/test',
          name : 'test',
          component: () => import('@/components/common/roomPage.vue')
        },
        {

            path: '/common',
            children: [

                {
                    path: 'login',
                    name: 'LoginView.vue',
                    component: loginView
                },
                {
                    path: 'register',
                    name: 'RegisterView',
                    component: registerView
                },
                {
                    path: 'setAvatar',
                    name: 'AvatarView',
                    component: AvatarView
                },
                {
                    path: 'chatRoom/:roomId',
                    name: "RoomView",
                    component: ChatRoomView,
                    props: true
                },
                {
                    path: 'mainPage',
                    name: "MainPageView",
                    component: MainPageView
                },
                {
                    path: 'roomPage',
                    name: 'RoomPageView',
                    component: () => import('@/components/common/roomPage.vue')
                },
                {
                    path: 'updateInfo',
                    name: 'updateInfoView',
                    component: UpdateInfoView
                },
                {
                    path: 'notice',
                    name: 'NoticeView',
                    component: () => import('@/views/common/Notice.vue')
                }

            ]
        },
        {
            path: '/admin',
            children: [
                {
                    path: 'login',
                    name: 'AdminLoginView',
                    component: () => import('@/views/admin/LoginView.vue')
                },
                {
                    path: 'mainPage',
                    name: 'AdminMainPageView',
                    component: () => import('@/views/admin/MainView.vue')
                },
                {

                    path: 'roomPage',
                    name: 'AdminRoomPageView',
                    component: () => import('@/views/admin/RoomView.vue')
                },
                {
                    path: 'register',
                    name: 'AdminRegisterView',
                    component: () => import('@/views/admin/RegisterView.vue')
                }
                // {
                //     path: 'mainPage',
                //     name: 'AdminMainPageView',
                //     component: () => import('@/views/admin/AdminMainPageView.vue')
                // },
                // {
                //     path: 'roomManage',
                //     name: 'AdminRoomManageView',
                //     component: () => import('@/views/admin/AdminRoomManageView.vue')
                // },
                // {
                //     path: 'userManage',
                //     name: 'AdminUserManageView',
                //     component: () => import('@/views/admin/AdminUserManageView.vue')
                // },
                // {
                //     path: 'roomManage/:roomId',
                //     name: 'AdminRoomInfoView',
                //     component: () => import('@/views/admin/AdminRoomInfoView.vue'),
                //     props: true
                // },
                // {
                //     path: 'userManage/:userId',
                //     name: 'AdminUserInfoView',
                //     component: () => import('@/views/admin/AdminUserInfoView.vue'),
                //     props: true
                // }
            ]
        }
    ]
})

export default router

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token');
    if (!token && to.path !== '/common/login') {
        next('/common/login');
    } else {
        next();
    }
});
