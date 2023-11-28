<template>
  <div>
    <Avatar/>
  </div>

</template>

<script>
import Avatar from '@/components/common/setAvatar.vue';

export default {
  components: {
    Avatar
  }
};
</script>
<style>
.app-container {
  height: 100vh;
  background-color: #f0f2f5;
}

.sidebar {
  background-color: #545c64;
}

.navbar {
  background-color: #303133;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0px 1px 5px rgba(0, 0, 0, 0.1);
}

.logo {
  font-size: 24px;
  color: #fff;
  font-weight: bold;
}

.nav-menu {
  flex-grow: 1;
}

.user-info {
  margin-left: 20px;
  cursor: pointer;
  color: #fff;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
}

.el-icon-user {
  margin-right: 5px;
}

.footer {
  background-color: #303133;
  text-align: center;
  line-height: 60px;
  color: #fff;
}
</style>