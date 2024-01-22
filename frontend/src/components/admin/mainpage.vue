<template>
  <div class="common-layout">
    <el-container class="mainC">
      <el-header>Header</el-header>
      <el-container>
        <el-aside class="aside">
          <el-card class="box-card">
            <el-menu>
              <el-menu-item class="now">查看用户</el-menu-item>
              <el-menu-item @click="goToRoomPage">查看Room</el-menu-item>
            </el-menu>
          </el-card>
        </el-aside>
        <el-main class="main">
          <el-card class="main-card">
            <el-table :data="users" style="width: 100%">
              <el-table-column label="用户id" prop="userId"></el-table-column>
              <el-table-column class="profile" label="头像" prop="profile">
                <template v-slot="scope">
                  <img :src="'http://10.24.3.176:8083/ipfs/'+scope.row.profile" alt="User Avatar"
                       class="small-round-avatar">
                </template>
              </el-table-column>
              <el-table-column label="姓名" prop="username"></el-table-column>
              <el-table-column label="Email" prop="email"></el-table-column>
              <el-table-column label="个性签名" prop="signature"></el-table-column>
              <el-table-column
                  fixed="right"
                  label="操作"
                  width="200">
                <template v-slot="scope">
                  <el-row>
                    <el-col :span="12">
                      <el-button round type="primary" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                    </el-col>
                    <el-col :span="12">
                      <el-button round type="info" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                    </el-col>
                  </el-row>
                </template>
              </el-table-column>
            </el-table>
            <el-pagination v-model:current-page="currentPage"
                           :page-size="pageSize"
                           :total="totalRooms"
                           background
                           class="pagination-container"
                           layout="prev, pager, next"
                           @current-change="handlePageChange"
            />
          </el-card>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import {Host} from "@/main";

export default {
  name: 'admin_mainPage',
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  data() {
    return {
      user: {
        userId: '',
        username: '',
        email: '',
        signature: '',
        profile: '',
      },
      currentPage: 1, // 新增属性，用于存储当前的页数
      totalRooms: 0, // 新增属性，用于存储房间总数
      pageSize: 8,
      users: [] // 这里应该是你的房间数据
    }
  },
  created() {
    this.getUsers(this.currentPage)
  },
  methods: {
    goToRoomPage() {
      this.$router.push('/admin/roomPage');
    },
    getUsers(page) {
      this.realAxios.get(`http://` + Host + `:7000/user/admin/queryAllUser/${page}`, {
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(response => {
        // 将获取的房间数组赋值给 rooms
        this.users = response.data.data.users;
        // 将获取的房间总数赋值给 totalRooms
        this.totalRooms = parseInt(response.data.data.total);
      })
    },
    handlePageChange(page) {
      this.currentPage = page;
      this.getUsers(page);
    },
    handleEdit(index, row) {
      // 这里是你的编辑逻辑
    },
    handleDelete(index, row) {
      // 这里是你的删除逻辑
    }
  }
}
</script>

<style scoped>
.now {
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

.aside {
  margin: 2px;
  background-color: #13ce66;
  color: white; /* 设置文字颜色为白色，以便在黑色背景上看清楚 */
}

html, body {
  height: 100%;
  margin: 0;
  padding: 0;
}

.common-layout {
  width: 100%;
  height: 100%;
}

.main {
  /* 在这里添加你想要的独特布局样式 */
}

.small-round-avatar {
  width: 50px; /* 设置图片宽度 */
  height: 50px; /* 设置图片高度 */
  border-radius: 50%; /* 设置边框半径为50%，使图片变圆 */
  object-fit: cover; /* 保持图片的纵横比 */
}

.box-card {
  width: 100%;
  height: 1000px;
  background-color: #2c3e50;
}

.main-card {
  width: 100%;
  height: 100%;
}
</style>