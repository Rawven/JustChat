<template>
  <el-aside class="asle">
    <el-header class="p-4 border-b border-gray-200">
      <h2 class="text-lg font-semibold">侧边栏</h2>
    </el-header>
    <el-menu class="p-4 space-y-2">
      <div class="flex items-center space-x-4" @click="turnSearch">
        <el-icon><Plus /></el-icon>
        <el-text tag="b">Search</el-text>
      </div>
      <div class="flex items-center space-x-4" @click="turnRooms">
        <el-icon><House /></el-icon>
        <el-text tag="b">Rooms</el-text>
      </div>
      <div class="flex items-center space-x-4" @click="turnFriends">
        <el-icon><User /></el-icon>
        <el-text tag="b">Friends</el-text>
      </div>
      <div class="flex items-center space-x-4" @click="turnNotifications">
        <el-icon><Bell /></el-icon>
        <span>Notifications</span>
      </div>
      <div class="flex items-center space-x-4" @click="turnMoment">
        <el-icon>
          <PictureFilled/>
        </el-icon>
        <el-text tag="b">Moment</el-text>
      </div>
      <div class="flex items-center space-x-4" @click="turnPersonal">
        <el-icon><Avatar /></el-icon>
        <el-text tag="b">Personal</el-text>
      </div>

      <div class="flex items-center space-x-4" @click="logOut">
        <el-icon><Close /></el-icon>
        <span>Logout</span>
      </div>
    </el-menu>
  </el-aside>
</template>

<script>
import {Avatar, Bell, Close, House, PictureFilled, Plus, User} from "@element-plus/icons-vue";
import {defineComponent} from "vue";
import router from "@/router";

export default defineComponent({
  name: 'Jc-Aside',
  components: {Avatar, Close, Bell, Plus, House, User, PictureFilled},
  methods: {
    turnSearch() {
      router.push('/search')
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
    turnPersonal() {
      router.push('/personal')
    },
    logOut() {
      router.push('logOut')
    }
  }
})


</script>

<style scoped>
.asle{
  width: 200px;
  background-color: transparent;
}
.asle *{
  background-color: transparent;
}

</style>