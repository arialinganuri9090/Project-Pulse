<template>
  <v-app>
    <v-navigation-drawer v-model="drawer" :rail="rail" permanent>
      <v-list-item prepend-icon="mdi-pulse" title="Project Pulse" nav>
        <template #append>
          <v-btn :icon="rail ? 'mdi-chevron-right' : 'mdi-chevron-left'" variant="text" @click="rail = !rail" />
        </template>
      </v-list-item>
      <v-divider />
      <v-list density="compact" nav>
        <v-list-item prepend-icon="mdi-view-dashboard" title="Dashboard" to="/instructor/dashboard" />
        <v-list-item prepend-icon="mdi-account-multiple" title="Students" to="/instructor/students" />
        <v-list-item prepend-icon="mdi-account-group" title="Teams" to="/instructor/teams" />
        <v-list-item prepend-icon="mdi-file-chart" title="Section Peer Eval Report" to="/instructor/section-report" />
        <v-list-item prepend-icon="mdi-clipboard-text-multiple" title="Team WAR Report" to="/instructor/team-war" />
      </v-list>
      <template #append>
        <v-divider />
        <v-list density="compact" nav>
          <v-list-item prepend-icon="mdi-account-circle" :title="auth.user?.firstName + ' ' + auth.user?.lastName" subtitle="Instructor" />
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

function logout() { auth.logout(); router.push('/login') }
</script>
