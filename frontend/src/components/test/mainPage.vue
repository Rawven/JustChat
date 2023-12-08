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
            v-for="(room, index) in rooms"
            :key="index"
        >
      <span class="relative flex h-10 w-10 shrink-0 overflow-hidden rounded-full">
        <img :src="'http://10.44.59.225:8083/ipfs/'+room.roomProfile" alt="User Avatar" class="avatar">
      </span>
          <el-col>
            <el-col>{{ formatDateOrTime(JSON.parse(room.lastMsg).timestamp) }}</el-col>
            <el-col>
              <el-tag>{{ room.roomName }}</el-tag>
              <el-col>{{ room.lastMsgSender+"："+JSON.parse(room.lastMsg).content }}</el-col>
            </el-col>
          </el-col>
        </div>
      </nav>
    </aside>
    <chat-room v-if="this.nowRoomId !==0" :room="JSON.stringify(this.rooms[this.nowRoomId-1])" :user="this.userInfo.username"></chat-room>
  </el-container>
</template>

<script>
import {Host} from "@/main";
import {ref} from "vue";
import ChatRoom from "@/components/common/chatRoom.vue";

export default {

  name: 'MainPage',
  components: {ChatRoom},
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
        lastMsg: '',
        roomProfile: '',
        roomName: '',
        founderName: '',
        maxPeople: 1,
      },
      userInfo: {
        username: '',
        profile: '',
        signature: '',
      },
      rooms: [],
      currentPage: 1, // 新增属性，用于存储当前的页数
      totalRooms: 0, // 新增属性，用于存储房间总数
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

  },
  methods: {
    Host() {
      return Host
    },
    initWebSocket() {
      let token = localStorage.getItem("token");
      this.websocket = new WebSocket(`ws://` + Host + `:8080/websocket/${token}`);
      this.websocket.onopen = () => {
        console.log('WebSocket is open now.');
      };
      this.websocket.onmessage = (event) => {
        console.log('WebSocket message received:', event.data);
        this.messageCount++;
      };
      this.websocket.onclose = () => {
        console.log('WebSocket is closed now.');
      };
    },
    // 其他方法...
    formatDateOrTime(timestamp) {
      const messageDate = new Date(JSON.parse(timestamp));
      const currentDate = new Date();
      if (messageDate.toDateString() === currentDate.toDateString()) {
        return messageDate.toLocaleTimeString();
      } else {
        return messageDate.toLocaleDateString();
      }
    },

    updateMessageCount() {
      this.realAxios.post(`http://` + Host + `:7000/user/common/queryUnreadMessageCount`, {}, {
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(response => {
        this.messageCount = response.data.data;
      })
    },
    submitSearch(value) {
      // 在这里发送请求到后端
      this.realAxios.get(`http://` + Host + `:7000/chat/common/query/queryRelatedRoomList/${this.searchInput}/${this.radio}/${value}`, {
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(response => {
        // 处理响应
        this.rooms = response.data.data.rooms;
      })
    },
    enterRoom(roomId) {
      this.$router.push({path: `/common/chatRoom/` + Number(roomId)});
    },
    getRooms(page) {
      this.realAxios.get(`http://` + Host + `:7000/chat/common/query/queryUserRoomList/${page}/${5}`, {
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(response => {
        // 将获取的房间数组赋值给 rooms
        this.rooms = response.data.data.rooms;
        // 将获取的房间总数赋值给 totalRooms
        this.totalRooms = parseInt(response.data.data.total);
        this.nowRoomId = this.rooms[0].roomId;
      })
    },
    handlePageChange(page) {
      this.currentPage = page;
      this.getRooms(page);
    }
  }
};
</script>
<style>
body {
  overflow: hidden; /* 阻止整个页面滚动 */
}
.list-room{
  overflow: hidden;
}
.containerM {
  width: 275%;
  height: 72%;
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