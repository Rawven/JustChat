import { createRouter, createWebHistory } from 'vue-router'
import loginView from "@/views/LoginView.vue";
import login from "@/components/login.vue";
import registerView from "@/views/RegisterView.vue";

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
    }
  ]
})

export default router
