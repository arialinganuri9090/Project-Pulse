<template>
  <div>
    <div class="d-flex align-center justify-space-between mb-6">
      <h1 class="text-h4 font-weight-bold">Senior Design Sections</h1>
      <v-btn color="primary" prepend-icon="mdi-plus" to="/admin/sections/create">Create Section</v-btn>
    </div>
    <v-card rounded="lg" class="mb-4">
      <v-card-text>
        <v-text-field v-model="search" label="Search by name" prepend-inner-icon="mdi-magnify" variant="outlined" density="compact" clearable @keyup.enter="loadSections" />
      </v-card-text>
    </v-card>
    <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>
    <v-card rounded="lg">
      <v-data-table :headers="headers" :items="sections" :loading="loading" item-value="id">
        <template #item.name="{ item }">
          <router-link :to="`/admin/sections/${item.id}`" style="text-decoration:none; color: inherit; font-weight: 500;">
            {{ item.name }}
          </router-link>
        </template>
        <template #item.teams="{ item }">{{ item.teams?.length || 0 }} teams</template>
        <template #item.actions="{ item }">
          <v-btn icon="mdi-eye" variant="text" size="small" :to="`/admin/sections/${item.id}`" />
          <v-btn icon="mdi-pencil" variant="text" size="small" :to="`/admin/sections/${item.id}/edit`" />
        </template>
        <template #no-data>
          <p class="text-medium-emphasis pa-4">No sections found. Create one using the button above.</p>
        </template>
      </v-data-table>
    </v-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { sectionApi } from '@/api/index.js'

const sections = ref([])
const loading = ref(false)
const error = ref('')
const search = ref('')

const headers = [
  { title: 'Section Name', key: 'name' },
  { title: 'Start Date', key: 'startDate' },
  { title: 'End Date', key: 'endDate' },
  { title: 'Teams', key: 'teams' },
  { title: 'Actions', key: 'actions', sortable: false }
]

async function loadSections() {
  loading.value = true
  error.value = ''
  try {
    sections.value = (await sectionApi.list({ name: search.value || undefined })).data
  } catch (e) {
    error.value = e.response?.data?.error || 'Failed to load sections. Check the backend is running.'
  } finally {
    loading.value = false
  }
}
onMounted(loadSections)
</script>
