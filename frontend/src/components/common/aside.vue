<template>
  <el-aside class="w-48 border-r border-gray-200 overflow-y-auto">
    <el-header class="p-4 border-b border-gray-200">
      <h2 class="text-lg font-semibold">Navigation</h2>
    </el-header>
    <el-menu class="p-4 space-y-2">
      <div class="flex items-center space-x-4" @click="turnSearch">
        <svg
            class="w-6 h-6"
            fill="none"
            height="24"
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            viewBox="0 0 24 24"
            width="24"
            xmlns="http://www.w3.org/2000/svg"
        >
          <path d="M6 18h8"></path>
          <path d="M3 22h18"></path>
          <path d="M14 22a7 7 0 1 0 0-14h-1"></path>
          <path d="M9 14h2"></path>
          <path d="M9 12a2 2 0 0 1-2-2V6h6v4a2 2 0 0 1-2 2Z"></path>
          <path d="M12 6V3a1 1 0 0 0-1-1H9a1 1 0 0 0-1 1v3"></path>
        </svg>
        <el-text tag="b">Search</el-text>
      </div>
      <div class="flex items-center space-x-4" @click="turnRooms">
        <svg
            class="w-6 h-6"
            fill="none"
            height="24"
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            viewBox="0 0 24 24"
            width="24"
            xmlns="http://www.w3.org/2000/svg"
        >
          <path d="M3 7V5c0-1.1.9-2 2-2h2"></path>
          <path d="M17 3h2c1.1 0 2 .9 2 2v2"></path>
          <path d="M21 17v2c0 1.1-.9 2-2 2h-2"></path>
          <path d="M7 21H5c-1.1 0-2-.9-2-2v-2"></path>
          <rect height="5" rx="1" width="7" x="7" y="7"></rect>
          <rect height="5" rx="1" width="7" x="10" y="12"></rect>
        </svg>
        <el-text tag="b">Rooms</el-text>
      </div>
      <div class="flex items-center space-x-4" @click="turnFriends">
        <el-icon><User /></el-icon>
        <el-text tag="b">Friends</el-text>
      </div>
      <div class="flex items-center space-x-4" @click="turnNotifications">
        <svg
            class="w-6 h-6"
            fill="none"
            height="24"
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            viewBox="0 0 24 24"
            width="24"
            xmlns="http://www.w3.org/2000/svg"
        >
          <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"></path>
          <path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"></path>
        </svg>
        <span>Notifications</span>
      </div>
      <div class="flex items-center space-x-4" @click="turnMoment">
        <el-icon>
          <PictureFilled/>
        </el-icon>
        <el-text tag="b">Moment</el-text>
      </div>
      <div class="flex items-center space-x-4" @click="logOut">
        <svg
            class="w-6 h-6"
            fill="none"
            height="24"
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            viewBox="0 0 24 24"
            width="24"
            xmlns="http://www.w3.org/2000/svg"
        >
          <path d="M13 4h3a2 2 0 0 1 2 2v14"></path>
          <path d="M2 20h3"></path>
          <path d="M13 20h9"></path>
          <path d="M10 12v.01"></path>
          <path
              d="M13 4.562v16.157a1 1 0 0 1-1.242.97L5 20V5.562a2 2 0 0 1 1.515-1.94l4-1A2 2 0 0 1 13 4.561Z"></path>
        </svg>
        <span>Logout</span>
      </div>
    </el-menu>
  </el-aside>
</template>

<script>
import {PictureFilled, User} from "@element-plus/icons-vue";
import {defineComponent} from "vue";
import router from "@/router";

export default defineComponent({
  name: 'Jc-Aside',
  components: {User, PictureFilled},
  methods: {
    turnSearch() {
      router.push('/room')
    },
    turnRooms() {
      router.push('/main')
    },
    turnFriends() {
      router.push('/friend')
    },
    turnNotifications() {
      router.push('/notice')
    },
    turnMoment() {
      router.push('/moment')
    },
    logOut() {
      router.push('logOut')
    }
  }
})


</script>

<style scoped>

</style>