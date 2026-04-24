<template>
  <div>
    <div class="d-flex align-center justify-space-between mb-6">
      <h1 class="text-h4 font-weight-bold">Senior Design Teams</h1>
      <v-btn color="primary" prepend-icon="mdi-plus" to="/admin/teams/create">Create Team</v-btn>
    </div>
    <v-card rounded="lg" class="mb-4">
      <v-card-text>
        <v-row dense>
          <v-col cols="12" md="6">
            <v-text-field v-model="search" label="Search by name" prepend-inner-icon="mdi-magnify" variant="outlined" density="compact" clearable @keyup.enter="loadTeams" />
          </v-col>
          <v-col cols="12" md="6">
            <v-select v-model="sectionId" :items="sections" item-title="name" item-value="id" label="Filter by Section" variant="outlined" density="compact" clearable @update:model-value="loadTeams" />
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>
    <v-card rounded="lg">
      <v-data-table :headers="headers" :items="teams" :loading="loading" item-value="id">
        <template #item.name="{ item }">
          <router-link :to="`/admin/teams/${item.id}`" style="text-decoration:none; color: inherit; font-weight: 500;">
            {{ item.name }}
          </router-link>
        </template>
        <template #item.members="{ item }">{{ item.students?.length || 0 }} students</template>
        <template #item.instructors="{ item }">{{ item.instructors?.length || 0 }} instructors</template>
        <template #item.actions="{ item }">
          <v-btn icon="mdi-eye" variant="text" size="small" :to="`/admin/teams/${item.id}`" />
        </template>
        <template #no-data>
          <p class="text-medium-emphasis pa-4">No teams found. Create one using the button above.</p>
        </template>
      </v-data-table>
    </v-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { teamApi, sectionApi } from '@/api/index.js'

const teams = ref([]); const sections = ref([]); const loading = ref(false)
const search = ref(''); const sectionId = ref(null); const error = ref('')

const headers = [
  { title: 'Team Name', key: 'name' },
  { title: 'Description', key: 'description' },
  { title: 'Members', key: 'members' },
  { title: 'Instructors', key: 'instructors' },
  { title: 'Actions', key: 'actions', sortable: false }
]

async function loadTeams() {
  loading.value = true
  error.value = ''
  try {
    teams.value = (await teamApi.list({ sectionId: sectionId.value || undefined, name: search.value || undefined })).data
  } catch (e) {
    error.value = e.response?.data?.error || 'Failed to load teams. Check the backend is running.'
  } finally {
    loading.value = false
  }
}
onMounted(async () => {
  sections.value = (await sectionApi.list()).data
  loadTeams()
})
</script>
