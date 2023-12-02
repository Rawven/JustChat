<template>
  <div>
    <Chat :roomId="roomId"/>
  </div>
</template>

<script>
import Chat from '@/components/common/chatRoom.vue';

export default {
  components: {
    Chat
  },
  data() {
    return {
      roomId: this.$route.params.roomId
    }
  }
};
</script>
