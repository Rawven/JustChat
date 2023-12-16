

<template>
 <el-container>
   <el-main>
     <div
         class="flex items-center space-x-4 border rounded-lg p-2 cursor-pointer bg-white list-room"
         v-for="(notice,index) in notices"
         :key="index"
     >
       <el-col>
         <el-col v-if="checkNull(room.lastMsgSender)" >{{ formatDateOrTime(JSON.parse(room.lastMsg).timestamp) }}</el-col>
         <el-col>
           <el-tag>{{ room.roomName }}</el-tag>
           <el-row>
             <el-icon v-if="room.isNew" color="#FF0000">
               <ChatLineRound/>
             </el-icon>
             <el-icon v-else color="#409EFC">
               <ChatRound/>
             </el-icon>
             <el-col v-if="checkNull(room.lastMsgSender)" class="font-medium text-lg font-serif">{{ room.lastMsgSender + "：" + JSON.parse(room.lastMsg).content }}</el-col>
             <el-col v-else class="font-medium text-lg font-serif">暂无消息</el-col>
           </el-row>
         </el-col>
       </el-col>
     </div>
   </el-main>
 </el-container>
</template>
<script>
import {Host} from "@/main";

export default {
  inject: {
    realAxios: {
      from: 'axiosFilter'
    }
  },
  data(){
    return {
      notices: [],
    }
  },
  created(){
      this.realAxios.get('http://'+Host+':7000/notice/getNotice',{
        headers: {
          'token': localStorage.getItem("token")
        }
      }).then(response => {
        this.notices = response.data.data;
      })
  }
}
</script>
<style scoped>

</style>