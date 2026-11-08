<template>
  <TaskListPage :config="config" />
</template>

<script setup lang="ts">
import TaskListPage from './tasks/TaskListPage.vue'

const config = {
  title: '撤机工单',
  hint: '撤机工单管理：创建、修改、取消撤机任务。',
  apiPath: '/vms/api/withdrawTask',
  showRemarks: false,
  showPublish: false,
}
</script>
