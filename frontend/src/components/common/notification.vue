<template xmlns:el-col="http://www.w3.org/1999/html">
  <el-container class="Cont">
    <el-header class="flex items-center justify-between p-6 bg-white dark:bg-gray-800 shadow-md">
      <a class="flex items-center gap-2 font-semibold text-gray-900 dark:text-gray-100" href="#">
        <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="h-6 w-6"
        >
          <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
          <polyline points="9 22 9 12 15 12 15 22"></polyline>
        </svg>
        <span>My Website</span>
      </a>
      <el-menu class="flex gap-4" mode="horizontal" background-color="transparent" text-color="text-gray-900" active-text-color="hover:underline">
        <el-menu-item index="1" class="text-gray-900 dark:text-gray-100">Friends</el-menu-item>
        <el-menu-item index="2" class="text-gray-900 dark:text-gray-100">Groups</el-menu-item>
      </el-menu>
    </el-header>
    <el-main class="elN">
      <el-row :gutter="16">
        <el-col :span="24">
          <el-card shadow="hover" class="h-full">
            <el-row class="text-2xl font-semibold text-gray-900 dark:text-gray-100">Friend Requests</el-row>
            <el-table :data="tableData" style="width: 100%">
              <el-table-column label="Date" width="180">
                <template #default="scope">
                  <div style="display: flex; align-items: center">
                    <el-icon><timer /></el-icon>
                    <span style="margin-left: 10px">{{ scope.row.date }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="Name" width="180">
                <template #default="scope">
                  <el-popover effect="light" trigger="hover" placement="top" width="auto">
                    <template #default>
                      <div>name: {{ scope.row.name }}</div>
                      <div>address: {{ scope.row.address }}</div>
                    </template>
                    <template #reference>
                      <el-tag>{{ scope.row.name }}</el-tag>
                    </template>
                  </el-popover>
                </template>
              </el-table-column>
              <el-table-column label="Operations">
                <template #default="scope">
                  <el-button size="small" @click="handleEdit(scope.$index, scope.row)"
                  >Edit</el-button
                  >
                  <el-button
                      size="small"
                      type="danger"
                      @click="handleDelete(scope.$index, scope.row)"
                  >Delete</el-button
                  >
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="24">
          <el-card shadow="hover" class="h-full">
            <el-row class="text-2xl font-semibold text-gray-900 dark:text-gray-100">Friend Requests</el-row>
            <el-table :data="tableData" style="width: 100%">
              <el-table-column label="Date" width="180">
                <template #default="scope">
                  <div style="display: flex; align-items: center">
                    <el-icon><timer /></el-icon>
                    <span style="margin-left: 10px">{{ scope.row.date }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="Name" width="180">
                <template #default="scope">
                  <el-popover effect="light" trigger="hover" placement="top" width="auto">
                    <template #default>
                      <div>name: {{ scope.row.name }}</div>
                      <div>address: {{ scope.row.address }}</div>
                    </template>
                    <template #reference>
                      <el-tag>{{ scope.row.name }}</el-tag>
                    </template>
                  </el-popover>
                </template>
              </el-table-column>
              <el-table-column label="Operations">
                <template #default="scope">
                  <el-button size="small" @click="handleEdit(scope.$index, scope.row)"
                  >Edit</el-button
                  >
                  <el-button
                      size="small"
                      type="danger"
                      @click="handleDelete(scope.$index, scope.row)"
                  >Delete</el-button
                  >
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
    <el-footer class="flex items-center justify-between p-6 bg-white dark:bg-gray-800 shadow-md">
      <p class="text-sm text-gray-500 dark:text-gray-400">© 2024 My Website</p>
      <div class="flex gap-4">
        <a href="#">
          <svg
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
              class="h-5 w-5 text-gray-500 dark:text-gray-400"
          >
            <path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"></path>
          </svg>
        </a>
        <a href="#">
          <svg
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
              class="h-5 w-5 text-gray-500 dark:text-gray-400"
          >
            <path d="M22 4s-.7 2.1-2 3.4c1.6 10-9.4 17.3-18 11.6 2.2.1 4.4-.6 6-2C3 15.5.5 9.6 3 5c2.2 2.6 5.6 4.1 9 4-.9-4.2 4-6.6 7-3.8 1.1 0 3-1.2 3-1.2z"></path>
          </svg>
        </a>
        <a href="#">
          <svg
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
              class="h-5 w-5 text-gray-500 dark:text-gray-400"
          >
            <rect width="20" height="20" x="2" y="2" rx="5" ry="5"></rect>
            <path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"></path>
            <line x1="17.5" x2="17.51" y1="6.5" y2="6.5"></line>
          </svg>
        </a>
      </div>
    </el-footer>
  </el-container>
</template>
<script>
export default {
  name: 'MainPage',
  data() {
    return {
      search: '',
      tableData: [
        {
          date: '2016-05-02',
          name: 'Tom',
        },
        {
          date: '2016-05-04',
          name: 'Tom',
        },
        {
          date: '2016-05-01',
          name: 'Tom',
        },
        {
          date: '2016-05-03',
          name: 'Tom',
        },
      ],
    };
  },
};
</script>
<style>
.Cont {
  height: 100%;
  width: 50cm;
  margin: 0;
}
.elN {
  height: 100%;
  width: 100%;
  margin: 0;
}
</style>