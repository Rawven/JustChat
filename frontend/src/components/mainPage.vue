<template>
  <el-container class="container">
    <el-main class="main">
      <el-row class="row-bg" justify="center" type="flex">
        <el-col v-if="userInfo" :span="8">
          <img :src="'http://127.0.0.1:8083/ipfs/'+userInfo.profile" alt="User Avatar" class="avatar">
          <h2 class="username">{{ userInfo.username }}</h2>
          <h2 class="signature" v-if="userInfo.signature">{{ userInfo.signature }}</h2>
          <h2 class="signature" v-else>这个用户很懒，没有留下任何东西</h2>
          <router-link to="/profile">更改个人信息</router-link>
        </el-col>
        <el-col :span="16">
          <router-link to="/openRoom">
            <template v-slot:default="{ navigate }">
              <button class="button" @click="navigate">建立个新的聊天室</button>
            </template>
          </router-link>
          <el-input placeholder="请输入搜索内容"></el-input>

          <el-main class="cardContainer">
            <el-card v-for="room in rooms" :key="room.name" class="box-card">
              <div class="room-header">
                <el-avatar :src="room.founderAvatar" size="small"></el-avatar> <!-- 使用 el-avatar 组件显示创建者的头像 -->
                <router-link :to="{path:`/chatRoom/`+Number(room.roomId)}">
                  <h2 class="room-name">{{ room.roomName }}</h2>
                </router-link>
                <p class="room-description">{{ room.roomDescription }}</p>
                <el-tag size="default" class="room-max-people">最大人数: {{ room.maxPeople }}</el-tag> <!-- 使用 el-tag 组件显示最大人数 -->
                <el-tag size="default" class="room-founder">创建者: {{ room.founderName }}</el-tag> <!-- 使用 el-tag 组件显示创建者的名字 -->
              </div>
            </el-card>
            <el-pagination class="pagination-container"
                           background
                           layout="prev, pager, next"
                           :total="totalRooms"
                           v-model:current-page="currentPage"
                           :page-size= "pageSize"
                           @current-change="handlePageChange"
            />
          </el-main>
        </el-col>
      </el-row>
    </el-main>

  </el-container>
</template>

<script>
import {Host} from "@/main";

export default {

  name: 'MainPage',
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  data() {
    return {
      room: {
        roomId: '',
        roomName: '',
        roomDescription: '',
        founderName: '',
        founderAvatar:'',
        maxPeople: 1,
      },
      userInfo:{
        username:'',
        profile:'',
        signature:'这个用户很懒，没有留下任何东西',
      },
      rooms: [],
      currentPage: 1, // 新增属性，用于存储当前的页数
      totalRooms: 0, // 新增属性，用于存储房间总数
      pageSize:5
    };
  },
    created() {
    this.getRooms(this.currentPage);
    let item = localStorage.getItem("userData");
    if (item) {
      this.userInfo = JSON.parse(item);
    }
  },
  methods: {
    getRooms(page) {
      this.realAxios.get(`http://` + Host + `:7000/chat/queryRoomList/${page}`, {
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
    }
  }
};
</script>

<style scoped>

.button {
  display: inline-block;
  padding: 10px 20px;
  font-size: 16px;
  color: #fff;
  background-color: #409EFF;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  text-decoration: none;
  transition: background-color 0.3s ease;
}

.button:hover {
  background-color: #66B3FF;
}

.signature {
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

/* ...existing styles... */

.avatar {
  width: 150px;
  height: 150px;
  border-radius: 50%;
}

.username {
  font-size: 20px;
  margin-top: 10px;
}


.container {
  width: auto;
  height: auto;
  padding: 30px;
}


.main {
  background-color: #F5F5F5;
  color: #333;
  text-align: center;
  margin-left: 10px;
  width: 100%;
  padding: 0;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
.cardContainer {
  width: 100%; /* 设置容器的宽度 */
  height: 500px; /* 设置容器的高度 */
  overflow-y: auto; /* 如果内容超出容器的高度，显示滚动条 */
  margin: 20px; /* 添加外边距 */
}



.row-bg {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.button {
  width: 200px;
  margin: 20px;
  background-color: #FF4081;
  color: #FFF;
}

.pagination-container {
  width: 100%;
  padding: 20px 0;
  text-align: center;
  position: relative; /* 添加 position 属性 */
  z-index: 1000; /* 设置 z-index 为一个较大的值 */
}.box-card {
   width: 100%;
   height: 100px;
   margin-bottom: 20px;
   display: flex;
   flex-direction: column;
   justify-content: space-between;
   padding: 10px; /* 添加内边距 */
 }

.room-header{
  display: flex; /* 设置为弹性盒布局，使其子元素在一行中并列显示 */
  align-items: center; /* 设置在垂直方向上居中对齐 */
  justify-content: space-between; /* 设置在水平方向上均匀分布 */
  margin: 5px 0;
}

.room-name {
  font-size: 16px;
  font-weight: bold;
}

.room-founder,
.room-description,
.room-max-people {
  font-size: 12px;
}
</style>