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
import {ElMessage} from "element-plus";
import {Host} from "@/main";

export default defineComponent({
  name: 'Jc-Aside',
  components: {Avatar, Close, Bell, Plus, House, User, PictureFilled},
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  created() {
    this.initSseNotice();
  },
  activated() {
    let time = localStorage.getItem("expireTime");
    if(time < 1000 * 60 * 60 * 24){
      this.refreshToken();
    }
  },
  methods: {
    refreshToken() {
      let token = localStorage.getItem("token");
      this.realAxios.post('http://localhost:7000/auth/refreshToken', {
        token:token
      }, {
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(response => {
        this.userInfo = response.data.data;
      })
      },
    initSseNotice() {
      let token = localStorage.getItem("token");
      let source ;
      if (window.EventSource) {
        source = new EventSource(`http://` + Host + `:7000/user/sse/connect/${token}`);
        source.addEventListener('open', () => {
        }, false);
        source.addEventListener('message', (event) => {
          let data = JSON.parse(event.data)
          if (data.type === "FRIEND_APPLY") {
            ElMessage.success('您有新的好友申请');
          } else if (data.type === "ROOM_APPLY") {
            ElMessage.success('您有新的群聊申请');
          } else if (data.type === "RECORD_MOMENT_FRIEND" || data.type === "RECORD_MOMENT") {
            ElMessage.success('您有新的朋友圈消息');
          }else {
            ElMessage.info('未知消息类型');
          }
        });
        source.addEventListener('error', (e) => {
          if (e.readyState === EventSource.CLOSED) {
            ElMessage.error("连接关闭");
          } else {
            console.log(e);
          }
        }, false);
      }
    },
    log(e){
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
.asle{
  width: 200px;
  background-color: transparent;
}
.asle *{
  background-color: transparent;
}

</style>