<template>
  <el-container
      class="roomContainer bg-[#000000]"
      data-v0-t="card"
  >
    <el-header
        class=" space-y-1.5 flex flex-row items-center justify-between p-6 bg-[#000000] text-[#b3a7df] border-b-2 border-green-500">
      <h3 class="font-semibold tracking-tight text-2xl font-serif text-[#b3a7df]">聊天室大厅</h3>
      <div class="flex space-x-4">
        <el-row>
          <el-radio-group v-model="radio" class="radio" text>
            <el-radio-button :label="0" border>根据用户名</el-radio-button>
            <el-radio-button :label="1" border>根据房间名</el-radio-button>
          </el-radio-group>
        </el-row>
        <input
            v-model="searchText"
            aria-label="Search chat rooms"
            class="flex h-10 rounded-md ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 w-full p-3 border border-[#b3a7df] text-lg mb-4 bg-[#000000] text-[#b3a7df]"
            placeholder="Search for a chat room..."
            type="search"
        />
        <button class="inline-flex items-center justify-center rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 border bg-background hover:bg-accent hover:text-accent-foreground border-green-500"
                @click="submitSearch">
          <svg
              class="w-6 h-6 text-green-500"
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
            <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
            <polyline points="9 22 9 12 15 12 15 22"></polyline>
          </svg>
        </button>
        <button
            class="inline-flex items-center justify-center rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 border bg-background hover:bg-accent hover:text-accent-foreground border-green-500">
          <svg
              class="w-6 h-6 text-green-500"
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
            <path d="M21 8L18.74 5.74A9.75 9.75 0 0 0 12 3C11 3 10.03 3.16 9.13 3.47"></path>
            <path d="M8 16H3v5"></path>
            <path d="M3 12C3 9.51 4 7.26 5.64 5.64"></path>
            <path d="m3 16 2.26 2.26A9.75 9.75 0 0 0 12 21c2.49 0 4.74-1 6.36-2.64"></path>
            <path d="M21 12c0 1-.16 1.97-.47 2.87"></path>
            <path d="M21 3v5h-5"></path>
            <path d="M22 22 2 2"></path>
          </svg>
        </button>
      </div>
    </el-header>
    <el-main class="p-6 border-t flex flex-row-reverse space-x-4">
      <div class="w-2/3 p-6 space-y-8">
        <div v-for="(room,index) in rooms"
             :key="index"
             class="flex flex-row items-start space-x-6 p-6 bg-[#1a202c] rounded-lg shadow-md border-2 border-green-500"
        >
          <div class="flex flex-col space-y-4">
            <h3 class="font-semibold tracking-tight text-xl font-serif text-[#b3a7df]">{{ room.roomId }}
              {{ room.roomName }} {{ room.roomDescription }}</h3>
            <p class="text-md text-[#4a5568] font-serif">
              Founded by +{{ room.founderName }}.
            </p>
          </div>
          <button
              class="inline-flex items-center justify-center rounded-md font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 border bg-background hover:bg-accent hover:text-accent-foreground h-10 px-4 py-2 ml-auto border-[#4c51bf] text-[#4c51bf] text-lg font-serif"
              @click="joinRoom(room.roomId)">
            Apply to Join
          </button>
        </div>
        <el-pagination v-model:current-page="currentPage"
                       :page-size="pageSize"
                       :total="totalRooms"
                       background
                       class="pagination-container"
                       layout="prev, pager, next"
                       @current-change="handlePageChange"
        />
      </div>
      <div class="w-1/3 p-6 bg-[#1a202c] rounded-lg shadow-md border-2 border-green-500">
        <button
            id="95mdbpe1sme"
            class="inline-flex items-center justify-center rounded-md font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 border border-input hover:bg-accent hover:text-accent-foreground h-10 px-4 py-2 w-full bg-purple-200 text-[#4c51bf] text-lg mb-6 font-serif"
        >
          Create Chat Room
        </button>
        <el-card class="mt-6 border rounded-md shadow-sm bg-[#000000] text-[#b3a7df] p-6">
          <input
              v-model="createRoom.name"
              aria-label="Chat room name"
              class="flex h-10 rounded-md ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 w-full p-3 border border-[#b3a7df] text-lg mb-4 bg-[#000000] text-[#b3a7df]"
              placeholder="Enter room name..."
              type="text"
          />
          <input
              v-model="createRoom.description"
              aria-label="Chat room description"
              class="flex h-10 rounded-md ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 w-full p-3 border border-[#b3a7df] text-lg mb-4 bg-[#000000] text-[#b3a7df]"
              placeholder="Enter room description..."
              type="text"
          />
          <input
              v-model="createRoom.maxPeople"
              aria-label="Chat room maxPeople"
              class="flex h-10 rounded-md ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 w-full p-3 border border-[#b3a7df] text-lg mb-4 bg-[#000000] text-[#b3a7df]"
              placeholder="Enter room maxPeople..."
              type="text"
          />
          <button
              class="inline-flex items-center justify-center rounded-md font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 h-10 px-4 py-2 w-full mt-6 bg-[#4c51bf] text-[#b3a7df] text-lg font-serif"
              @click="submitForm">
            Submit
          </button>
        </el-card>
      </div>
    </el-main>
  </el-container>
</template>
<script>
import {Host} from "@/main";
import {ElMessage} from "element-plus";

export default {
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  created() {
    this.getRooms(this.currentPage)

  },
  data() {
    return {
      createRoom: {
        name: '',
        description: '',
        maxPeople: '',
      },
      room: {
        roomId: '',
        lastMsg: '',
        roomProfile: '',
        roomName: '',
        founderName: '',
        roomDescription: '',
        maxPeople: 1,
        lastMsgSender: '',
        isNew: false,
      },
      rooms: [],
      currentPage: 1, // 新增属性，用于存储当前的页数
      totalRooms: 0, // 新增属性，用于存储房间总数
      pageSize: 5,
      searchText: '',
      radio: 1,
    };
  },
  methods: {
    submitForm() {
      this.realAxios.post('http://' + Host + ':7000/chat/common/createRoom', this.createRoom, {
        headers: {
          'token': localStorage.getItem("token")
        }
      })
          .then(() => {
            ElMessage.success('创建成功')
            // 注册成功后清除用户的所有input
            this.createRoom.name = '';
            this.createRoom.description = '';
            this.createRoom.maxPeople = '';

          })
    },
    getRooms(page) {
      this.realAxios.get(`http://` + Host + `:7000/chat/common/queryIdRoomList/${page}/${5}`, {
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(response => {
        // 将获取的房间数组赋值给 rooms
        this.rooms = response.data.data.rooms;
        // 将获取的房间总数赋值给 totalRooms
        this.totalRooms = parseInt(response.data.data.total);
      })
    },
    handlePageChange(page) {
      this.currentPage = page;
      this.getRooms(page);
    },
    joinRoom(id) {
      this.realAxios.post(`http://` + Host + `:7000/chat/common/applyToJoinRoom`, {roomId: id}, {
        headers: {
          'token': localStorage.getItem("token")
        }
        // eslint-disable-next-line no-unused-vars
      }).then(response => {
        ElMessage.success('申请成功')
      })
    },
    submitSearch() {
      // 在这里发送请求到后端
      this.realAxios.get(`http://` + Host + `:7000/chat/common/queryRelatedRoomList/${this.searchText}/${this.radio}/${this.currentPage}`, {
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(response => {
        // 处理响应
        this.rooms = response.data.data.rooms;
      })
    },
  },
}

</script>
<style scoped>
.avatar {
  width: 75px;
  height: 75px;
  border-radius: 50%;
}

.roomContainer {
  width: 300%;
  top: 0;
  margin: 0;
}
</style>