<template>
  <div>
    <h1 class="text-h4 font-weight-bold mb-2">Welcome, {{ auth.user?.firstName }}!</h1>
    <p class="text-body-1 text-medium-emphasis mb-6">Here's your overview for this week.</p>
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
  { title: 'Weekly Activity Report', subtitle: 'Log your activities for the week', icon: 'mdi-clipboard-text', color: 'primary', to: '/student/war' },
  { title: 'Peer Evaluation', subtitle: 'Evaluate your teammates', icon: 'mdi-star-check', color: 'warning', to: '/student/peer-eval' },
  { title: 'My Peer Eval Report', subtitle: 'View your performance feedback', icon: 'mdi-chart-bar', color: 'success', to: '/student/my-report' },
]
</script>
