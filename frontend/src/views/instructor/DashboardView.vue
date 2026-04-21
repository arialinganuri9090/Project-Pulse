<template>
  <div>
    <h1 class="text-h4 font-weight-bold mb-2">Instructor Dashboard</h1>
    <p class="text-body-1 text-medium-emphasis mb-6">Welcome, {{ auth.user?.firstName }}!</p>
    <v-row>
      <v-col v-for="card in cards" :key="card.title" cols="12" sm="6" md="4">
        <v-card :to="card.to" rounded="lg" hover elevation="2">
          <v-card-text class="text-center pa-6">
            <v-icon :color="card.color" size="48" class="mb-3">{{ card.icon }}</v-icon>
            <div class="text-h6 font-weight-bold">{{ card.title }}</div>
            <div class="text-body-2 text-medium-emphasis mt-1">{{ card.subtitle }}</div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth.js'
const auth = useAuthStore()
const cards = [
  { title: 'Students', subtitle: 'Find and view students', icon: 'mdi-account-multiple', color: 'primary', to: '/instructor/students' },
  { title: 'Teams', subtitle: 'View teams and assignments', icon: 'mdi-account-group', color: 'success', to: '/instructor/teams' },
  { title: 'Section Peer Eval Report', subtitle: 'View full section evaluations', icon: 'mdi-file-chart', color: 'warning', to: '/instructor/section-report' },
  { title: 'Team WAR Report', subtitle: 'View weekly activity reports', icon: 'mdi-clipboard-text-multiple', color: 'info', to: '/instructor/team-war' },
]
</script>
