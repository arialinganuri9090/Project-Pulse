<template>
  <v-app>
    <v-navigation-drawer v-model="drawer" :rail="rail" permanent>
      <v-list-item
        prepend-icon="mdi-pulse"
        title="Project Pulse"
        nav
      >
        <template #append>
          <v-btn :icon="rail ? 'mdi-chevron-right' : 'mdi-chevron-left'" variant="text" @click="rail = !rail" />
        </template>
      </v-list-item>
      <v-divider />
      <v-list density="compact" nav>
        <v-list-item prepend-icon="mdi-view-dashboard" title="Dashboard" to="/admin/dashboard" />
        <v-list-item prepend-icon="mdi-clipboard-list" title="Rubrics" to="/admin/rubrics" />
        <v-list-item prepend-icon="mdi-school" title="Sections" to="/admin/sections" />
        <v-list-item prepend-icon="mdi-account-group" title="Teams" to="/admin/teams" />
        <v-list-item prepend-icon="mdi-account-multiple" title="Students" to="/admin/students" />
        <v-list-item prepend-icon="mdi-account-tie" title="Instructors" to="/admin/instructors" />
      </v-list>
      <template #append>
        <v-divider />
        <v-list density="compact" nav>
          <v-list-item prepend-icon="mdi-account-circle" :title="auth.user?.firstName + ' ' + auth.user?.lastName" subtitle="Admin" />
          <v-list-item prepend-icon="mdi-logout" title="Logout" @click="logout" />
        </v-list>
      </template>
    </v-navigation-drawer>

    <v-main>
      <v-container fluid>
        <router-view />
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'

const auth = useAuthStore()
const router = useRouter()
const drawer = ref(true)
const rail = ref(false)

function logout() {
  auth.logout()
  router.push('/login')
}
</script>
