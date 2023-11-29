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
            <el-table :data="tableData" style="width: 100%">
              <el-table-column prop="userId" label="用户id"></el-table-column>
              <el-table-column prop="name" label="姓名"></el-table-column>
              <el-table-column prop="email" label="Email"></el-table-column>
              <el-table-column prop="signature" label="个性签名"></el-table-column>
              <el-table-column
                  fixed="right"
                  label="操作"
                  width="120">
                <template v-slot="scope">
                  <el-button @click="handleEdit(scope.$index, scope.row)" type="text" size="small">编辑</el-button>
                  <el-button @click="handleDelete(scope.$index, scope.row)" type="text" size="small">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import {Host} from "@/main";

export default {
  name:'admin_mainPage',
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  data() {
    return {
      tableData: [] // 这里应该是你的用户数据
    }
  },
  created() {
        this.realAxios.get("http://"+Host+":7000/info/getAllUserInfo",{
          headers:{
            'token': localStorage.getItem('token')
          }
        }).then(response => {
          this.tableData = response.data.data;
        });
  },
  methods: {
    goToRoomPage() {
      this.$router.push('/admin/roomPage');
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
.now{
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