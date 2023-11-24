import {createRouter, createWebHistory} from 'vue-router'
import loginView from "@/views/LoginView.vue";
import registerView from "@/views/RegisterView.vue";
import AvatarView from "@/views/AvatarView.vue";
import ChatRoomView from "@/views/ChatRoomView.vue";
import MainPageView from "@/views/MainPageView.vue";
import OpenANewRoom from "@/views/OpenANewRoomView.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/login',
            name: 'LoginView',
            component: loginView
        },
        {
            path: '/register',
            name: 'RegisterView',
            component: registerView
        },
        {
            path: '/setAvatar',
            name: 'AvatarView',
            component: AvatarView
        },
        {
            path: '/chatRoom/:roomId',
            name: "RoomView",
            component: ChatRoomView,
            props: true
        },
        {
            path: '/mainPage',
            name: "MainPageView",
            component: MainPageView
        },
        {
            path: '/openRoom',
            name: "OpenRoomView",
            component: OpenANewRoom
        }
    ]
})

export default router
