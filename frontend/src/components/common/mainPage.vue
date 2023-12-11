<template>
  <el-container class="containerM">
    <aside class="w-64 border-r border-gray-200 overflow-y-auto">
      <header class="p-4 border-b border-gray-200">
        <el-row>
          <img :src="'http://10.44.59.225:8083/ipfs/'+userInfo.profile" alt="User Avatar" class="avatar">
          <h2 class="logo"> Just Chat </h2>
        </el-row>
      </header>
      <section class="p-4 border-b border-gray-200">
        <div class="flex items-center space-x-4">
        <span class="relative flex h-10 w-10 shrink-0 overflow-hidden rounded-full">
          <span class="flex h-full w-full items-center justify-center rounded-full bg-muted">CN</span>
        </span>
          <div>
            <h3 class="text-lg font-semibold">{{ userInfo.username }}</h3>
            <p class="text-sm text-gray-400">{{ userInfo.signature }}</p>
          </div>
        </div>
      </section>
      <header class="p-4 border-b border-gray-200">
        <h2 class="text-lg font-semibold">我的群聊</h2>
      </header>
      <nav class="p-4 space-y-2">
        <div
            class="flex items-center space-x-4 border rounded-lg p-2 cursor-pointer bg-white list-room"
            v-for="(room) in rooms"
            :key="room.isNew"
            @click="checkOut(room.roomId)"
        >
      <span class="relative flex h-10 w-10 shrink-0 overflow-hidden rounded-full">
        <img :src="'http://10.44.59.225:8083/ipfs/'+room.roomProfile" alt="User Avatar" class="avatar">
      </span>
          <el-col>
            <el-col>{{ formatDateOrTime(JSON.parse(room.lastMsg).timestamp) }}</el-col>
            <el-col>
              <el-tag>{{ room.roomName }}</el-tag>
              <el-row>
                <el-icon v-if="room.isNew" color="#FF0000">
                  <ChatLineRound/>
                </el-icon>
                <el-icon v-else color="#409EFC">
                  <ChatRound/>
                </el-icon>
                <el-col>{{ room.lastMsgSender + "：" + JSON.parse(room.lastMsg).content }}</el-col>
              </el-row>
            </el-col>
          </el-col>
        </div>
      </nav>
    </aside>
    <chat-room v-if="nowRoomId >=1" :key="nowRoomId" :room="JSON.stringify(this.rooms[this.roomIndex.get(nowRoomId)])"
               :user="this.userInfo.username"></chat-room>
  </el-container>
</template>

<script>
import {Host} from "@/main";
import {reactive, ref} from "vue";
import ChatRoom from "@/components/common/chatRoom.vue";
import {ChatLineRound, ChatRound} from "@element-plus/icons-vue";

export default {
  watch: {
    nowRoomId(newVal, oldVal) {
      if (newVal !== oldVal) {
        // nowRoomId has changed. You can add your logic here to re-render the chat-room component
        this.nowRoomId = newVal;
      }
    },
  },

  name: 'MainPage',
  components: {ChatLineRound, ChatRound, ChatRoom},
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  data() {
    return {
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
      rooms: reactive( []),
      roomIndex: new Map(),
      pageSize: 5,
      nowRoomId: 0,
    };
  },
  created() {
    this.getRooms(this.currentPage);
    //this.updateMessageCount();
    let item = localStorage.getItem("userData");
    if (item) {
      this.userInfo = JSON.parse(item);
    }
    this.initWebSocket();

  },

  methods: {
    ref,
    Host() {
      return Host
    },
    checkOut(roomId) {
      this.nowRoomId = roomId;
      this.rooms[this.roomIndex.get(roomId)].isNew = false;
    },
    initWebSocket() {
      let token = localStorage.getItem("token");
      this.websocket = new WebSocket(`ws://` + Host + `:8080/websocket/${token}`);
      this.websocket.onopen = () => {
        console.log('WebSocket is open now.');
      };
      this.websocket.onmessage = (event) => {
        console.log('WebSocket message received:', event.data);
        let data = JSON.parse(event.data)
        console.log(this.roomIndex.get(data.roomId))
        let index = this.roomIndex.get(Number(data.roomId));
        this.rooms[index] ={
          ...this.rooms[index],

          lastMsg: data.msg,
          lastMsgSender: data.username,
          isNew: true
        };
        console.log(this.rooms[index])
      };
      this.websocket.onclose = () => {
        console.log('WebSocket is closed now.');
      };
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
    getRooms(page) {
      this.realAxios.get(`http://` + Host + `:7000/chat/common/query/initUserMainPage/${page}/${5}`, {
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(response => {
        // 将获取的房间数组赋值给 rooms
        this.rooms = response.data.rooms;
        // 将获取的房间总数赋值给 totalRooms
        this.nowRoomId = this.rooms[0].roomId;
        this.rooms.forEach((room, index) => {
          console.log(room.roomId)
          this.roomIndex.set(Number(room.roomId), index);
          console.log(this.roomIndex.get(room.roomId))
        });
      })
    },
  }
};
</script>
<style>
body {
  overflow: hidden; /* 阻止整个页面滚动 */
}

.list-room {
  overflow: hidden;
}

.containerM {
  width: 323%;
  height: 62%;
  margin: 0;
}

.avatar {
  width: 75px;
  height: 75px;
  border-radius: 50%;
}

.logo {
  font-size: 24px;
  color: #409EFF;
  font-weight: bold;
  text-align: center;
  margin-bottom: 20px;
  animation: fadeIn 2s;
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