<template>
  <div>
    <div class="d-flex align-center justify-space-between mb-6">
      <h1 class="text-h4 font-weight-bold">Rubrics</h1>
      <v-btn color="primary" prepend-icon="mdi-plus" to="/admin/rubrics/create">Create Rubric</v-btn>
    </div>
    <v-card rounded="lg">
      <v-data-table
        :headers="headers"
        :items="rubrics"
        :loading="loading"
        item-value="id"
      >
        <template #item.criteria="{ item }">
          {{ item.criteria?.length || 0 }} criteria
        </template>
        <template #item.actions="{ item }">
          <v-btn icon="mdi-eye" variant="text" size="small" @click="viewRubric(item)" />
        </template>
      </v-data-table>
    </v-card>

    <v-dialog v-model="dialog" max-width="600">
      <v-card v-if="selected" rounded="lg">
        <v-card-title class="pa-4">{{ selected.name }}</v-card-title>
        <v-card-text>
          <v-table>
            <thead><tr><th>Criterion</th><th>Description</th><th>Max Score</th></tr></thead>
            <tbody>
              <tr v-for="c in selected.criteria" :key="c.id">
                <td>{{ c.name }}</td>
                <td>{{ c.description }}</td>
                <td>{{ c.maxScore }}</td>
              </tr>
            </tbody>
          </v-table>
        </v-card-text>
        <v-card-actions><v-spacer/><v-btn @click="dialog = false">Close</v-btn></v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { rubricApi } from '@/api/index.js'

const rubrics = ref([])
const loading = ref(false)
const dialog = ref(false)
const selected = ref(null)

const headers = [
  { title: 'Name', key: 'name' },
  { title: 'Criteria', key: 'criteria' },
  { title: 'Actions', key: 'actions', sortable: false }
]

onMounted(async () => {
  loading.value = true
  try { rubrics.value = (await rubricApi.list()).data } finally { loading.value = false }
})

function viewRubric(item) { selected.value = item; dialog.value = true }
</script>
