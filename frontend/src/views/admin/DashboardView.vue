<template>
  <div>
    <h1 class="text-h4 font-weight-bold mb-6">Admin Dashboard</h1>
    <v-row>
      <v-col v-for="card in cards" :key="card.title" cols="12" sm="6" md="4" lg="3">
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
const cards = [
  { title: 'Rubrics', subtitle: 'Manage peer evaluation rubrics', icon: 'mdi-clipboard-list', color: 'primary', to: '/admin/rubrics' },
  { title: 'Sections', subtitle: 'Manage senior design sections', icon: 'mdi-school', color: 'success', to: '/admin/sections' },
  { title: 'Teams', subtitle: 'Manage senior design teams', icon: 'mdi-account-group', color: 'warning', to: '/admin/teams' },
  { title: 'Students', subtitle: 'Manage students', icon: 'mdi-account-multiple', color: 'info', to: '/admin/students' },
  { title: 'Instructors', subtitle: 'Manage instructors', icon: 'mdi-account-tie', color: 'secondary', to: '/admin/instructors' },
]
</script>
