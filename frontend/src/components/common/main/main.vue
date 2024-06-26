<template>
  <el-container class="JcContainer">
    <JcAside></JcAside>
    <el-aside class="theAside w-64 border-r border-gray-200 overflow-y-auto">
      <el-row>
        <img :src="ipfsHost()+userInfo.profile" alt="User Avatar"
             class="avatar">
      </el-row>
      <br>
      <el-header class="p-4 border-b border-gray-200">
        <h2 class="text-lg font-semibold">我的群聊</h2>
      </el-header>
      <el-menu class="p-4 space-y-2 menu">
        <div
            v-for="(room) in rooms"
            :key="room.isNew"
            class="flex items-center space-x-4 border rounded-lg p-2 cursor-pointer bg-white list-room"
            @click="checkOut(room.roomId)"
        >
    <span class="relative flex h-10 w-10 shrink-0 overflow-hidden rounded-full">
      <img :src="ipfsHost()+room.roomProfile" alt="User Avatar" class="avatar">
    </span>
          <el-col>
            <el-col v-if="checkNull(room.lastMsgSender)">{{
                formatDateOrTime(JSON.parse(room.lastMsg).timestamp)
              }}
            </el-col>
            <el-col>
              <el-tag>{{ room.roomName }}</el-tag>
              <el-row>
                <el-icon v-if="room.isNew" color="#FF0000">
                  <ChatLineRound/>
                </el-icon>
                <el-icon v-else color="#409EFC">
                  <ChatRound/>
                </el-icon>
                <el-col v-if="checkNull(room.lastMsgSender)"
                        class="font-medium text-lg font-serif">
                  {{
                    room.lastMsgSender + "：" + JSON.parse(room.lastMsg).content
                  }}
                </el-col>
                <el-col v-else class="font-medium text-lg font-serif">暂无消息
                </el-col>
              </el-row>
            </el-col>
          </el-col>
        </div>
      </el-menu>
    </el-aside>
    <chat-room v-if="nowRoomId >=1" :key="nowRoomId"
               :room="JSON.stringify(this.rooms[this.roomIndex.get(nowRoomId)])"
               :user="this.userInfo.username"></chat-room>
  </el-container>
</template>

<script>
import {Host, ipfsHost} from "@/main";
import {reactive, ref} from "vue";
import ChatRoom from "@/components/common/chatRoom.vue";
import {ChatLineRound, ChatRound} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import JcAside from "@/components/common/aside.vue";

export default {
  watch: {
    nowRoomId(newVal, oldVal) {
      if (newVal !== oldVal) {
        // nowRoomId has changed. You can add your logic here to re-render the chat-room component
        this.nowRoomId = newVal;
      }
    },
    applyNoticeIsNew(newVal, oldVal) {
      if (newVal !== oldVal) {
        // nowRoomId has changed. You can add your logic here to re-render the chat-room component
        this.applyNoticeIsNew = newVal;
      }
    },
    momentNoticeIsNew(newVal, oldVal) {
      if (newVal !== oldVal) {
        // nowRoomId has changed. You can add your logic here to re-render the chat-room component
        this.momentNoticeIsNew = newVal;
      }
    },
  },

  name: 'MainPage',
  // eslint-disable-next-line vue/no-reserved-component-names
  components: {JcAside, ChatLineRound, ChatRound, ChatRoom},
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  data() {
    return {
      host: Host,
      radio: ref(0),
      searchInput: '',
      websocket: null,
      messageCount: 0,
      room: {
        roomId: '',
        roomName: '',
        roomDescription: '',
        founderName: '',
        maxPeople: 1,
      },
      userInfo: {
        username: '',
        profile: '',
        signature: '',
      },
      rooms: reactive([]),
      roomIndex: new Map(),
      pageSize: 5,
      nowRoomId: 0,
      applyNoticeIsNew: false,
      momentNoticeIsNew: false,
    };
  },
  created() {
    //this.updateMessageCount();
    this.getUserData();
  },
  activated() {
    ElMessage.success('群聊页面激活');
    this.getRooms();
  },
  methods: {
    ipfsHost() {
      return ipfsHost
    },
    ref,
    Host() {
      return Host
    },
    getUserData() {
      let item = localStorage.getItem("userData");
      if (item) {
        this.userInfo = JSON.parse(item);
      }
    },
    logOut() {
      let token = localStorage.getItem("token");
      this.realAxios.get(`http://` + Host + `:7000/auth/logout/${token}`, {})
      localStorage.removeItem("token");
      localStorage.removeItem("userData");
      this.$router.push('/login');
      ElMessage.success('登出成功');
    },
    turnSearch() {
      this.$router.push('/roomPage');
    },
    turnFriends() {
      this.$router.push('/friend');
    },
    checkOut(roomId) {
      this.nowRoomId = roomId;
      this.rooms[this.roomIndex.get(roomId)].isNew = false;
    },
    // 其他方法...
    formatDateOrTime(timestamp) {
      const messageDate = new Date(timestamp);
      const currentDate = new Date();
      if (messageDate.toDateString() === currentDate.toDateString()) {
        return messageDate.toLocaleTimeString();
      } else {
        return messageDate.toLocaleDateString();
      }
    },
    turnNotifications() {
      this.applyNoticeIsNew = false;
      this.$router.push('notice');
    },
    checkNull(name) {
      return name !== "";
    },
    turnMoment() {
      this.$router.push('/moment');
    },
    getRooms() {
      this.realAxios.get(`http://` + Host + `:7000/chat/room/initUserMainPage`, {
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(response => {
        // 将获取的房间数组赋值给 rooms
        this.rooms = response.data.data;
        console.log(this.rooms)
        // 将获取的房间总数赋值给 totalRooms
        if (this.rooms.length > 0) {
          this.nowRoomId = this.rooms[0].roomId;
          this.rooms.forEach((room, index) => {
                this.roomIndex.set(Number(room.roomId), index);
              }
          );
        }
      })
    },
  }
};
</script>
<style>
body {
  overflow: hidden;
  font-family: 'Roboto', sans-serif;
  font-weight: bold;
}

.menu {
  background-color: transparent;
}


.list-room {
  overflow: hidden;
  padding: 10px;
  margin-bottom: 10px;
}

.avatar {
  width: 75px;
  height: 75px;
  border-radius: 50%;
}

.theAside {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
  top: 0;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

</style>