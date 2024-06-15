<template>
  <el-aside class="asle">
    <el-header class="p-4 border-b border-gray-200">
      <h2 class="text-lg font-semibold">侧边栏</h2>
    </el-header>
    <el-menu class="p-4 space-y-2">
      <div class="flex items-center space-x-4" @click="turnSearch">
        <el-icon>
          <Plus/>
        </el-icon>
        <el-text tag="b">Search</el-text>
      </div>
      <div class="flex items-center space-x-4" @click="turnRooms">
        <el-icon>
          <House/>
        </el-icon>
        <el-text tag="b">Rooms</el-text>
      </div>
      <div class="flex items-center space-x-4" @click="turnFriends">
        <el-icon>
          <User/>
        </el-icon>
        <el-text tag="b">Friends</el-text>
      </div>
      <div class="flex items-center space-x-4" @click="turnNotifications">
        <el-icon>
          <Bell/>
        </el-icon>
        <span>Notifications</span>
      </div>
      <div class="flex items-center space-x-4" @click="turnMoment">
        <el-icon>
          <PictureFilled/>
        </el-icon>
        <el-text tag="b">Moment</el-text>
      </div>
      <div class="flex items-center space-x-4" @click="turnPersonal">
        <el-icon>
          <Avatar/>
        </el-icon>
        <el-text tag="b">Personal</el-text>
      </div>

      <div class="flex items-center space-x-4" @click="logOut">
        <el-icon>
          <Close/>
        </el-icon>
        <span>Logout</span>
      </div>
    </el-menu>
  </el-aside>
</template>

<script>
import {
  Avatar,
  Bell,
  Close,
  House,
  PictureFilled,
  Plus,
  User
} from "@element-plus/icons-vue";
import {defineComponent} from "vue";
import router from "@/router";
import {ElMessage} from "element-plus";

export default defineComponent({
  name: 'Jc-Aside',
  components: {Avatar, Close, Bell, Plus, House, User, PictureFilled},
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  created() {

  },
  activated() {
    let time = localStorage.getItem("expireTime");
    if (time < 1000 * 60 * 60 * 24) {
      this.refreshToken();
    }
  },
  methods: {
    refreshToken() {
      let token = localStorage.getItem("token");
      this.realAxios.post('http://localhost:7000/auth/refreshToken', {
        token: token
      }, {
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(response => {
        this.userInfo = response.data.data;
      })
    },
    log(e) {
      ElMessage.success(e);
    },
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
.asle {
  width: 200px;
  background-color: transparent;
}

.asle * {
  background-color: transparent;
}

</style>