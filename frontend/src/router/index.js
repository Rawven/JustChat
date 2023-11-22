import {createRouter, createWebHistory} from 'vue-router'
import loginView from "@/views/LoginView.vue";
import registerView from "@/views/RegisterView.vue";
import AvatarView from "@/views/AvatarView.vue";
import ChatRoomView from "@/views/ChatRoomView.vue";

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
            path: '/chatRoom',
            name: "RoomView",
            component : ChatRoomView
        }
    ]
})

export default router
