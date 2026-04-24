<template>
  <div>
    <h1 class="text-h4 font-weight-bold mb-6">Weekly Activity Report</h1>

    <v-select
      v-model="selectedWeekId"
      :items="weeks"
      :item-title="w => w.weekStart + ' - ' + w.weekEnd"
      item-value="id"
      label="Select Week"
      variant="outlined"
      class="mb-4"
      style="max-width: 400px"
      @update:model-value="loadWar"
    />

    <template v-if="selectedWeekId">
      <div class="d-flex align-center justify-space-between mb-4">
        <h2 class="text-h6">Activities</h2>
        <v-btn color="primary" prepend-icon="mdi-plus" @click="openAddDialog">Add Activity</v-btn>
      </div>

      <v-card rounded="lg">
        <v-data-table :headers="headers" :items="activities" :loading="loading" item-value="id">
          <template #item.status="{ item }">
            <v-chip :color="statusColor(item.status)" size="small">{{ item.status }}</v-chip>
          </template>
          <template #item.actions="{ item }">
            <v-btn icon="mdi-pencil" variant="text" size="small" @click="openEditDialog(item)" />
            <v-btn icon="mdi-delete" variant="text" size="small" color="error" @click="deleteActivity(item)" />
          </template>
          <template #no-data>
            <p class="text-medium-emphasis pa-4">No activities yet. Add your first one!</p>
          </template>
        </v-data-table>
      </v-card>
    </template>

    <!-- Add/Edit Dialog -->
    <v-dialog v-model="activityDialog" max-width="600">
      <v-card rounded="lg">
        <v-card-title class="pa-4">{{ editingActivity ? 'Edit' : 'Add' }} Activity</v-card-title>
        <v-card-text>
          <v-alert v-if="dialogError" type="error" class="mb-3" density="compact">{{ dialogError }}</v-alert>
          <v-form ref="actForm">
            <v-select v-model="form.category" :items="categories" label="Category" variant="outlined" :rules="[v => !!v || 'Required']" class="mb-3" />
            <v-text-field v-model="form.activity" label="Activity Name" variant="outlined" :rules="[v => !!v || 'Required']" class="mb-3" />
            <v-textarea v-model="form.description" label="Description" variant="outlined" rows="3" class="mb-3" />
            <v-row dense>
              <v-col cols="6"><v-text-field v-model.number="form.plannedHours" label="Planned Hours" type="number" variant="outlined" /></v-col>
              <v-col cols="6"><v-text-field v-model.number="form.actualHours" label="Actual Hours" type="number" variant="outlined" /></v-col>
            </v-row>
            <v-select v-model="form.status" :items="statuses" label="Status" variant="outlined" :rules="[v => !!v || 'Required']" class="mt-3" />
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer /><v-btn @click="activityDialog = false">Cancel</v-btn>
          <v-btn color="primary" :loading="saving" @click="saveActivity">{{ editingActivity ? 'Update' : 'Add' }}</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { warApi } from '@/api/index.js'

const weeks = ref([]); const selectedWeekId = ref(null); const activities = ref([]); const loading = ref(false)
const activityDialog = ref(false); const editingActivity = ref(null); const saving = ref(false); const dialogError = ref('')
const actForm = ref(null)
const form = ref({ category: '', activity: '', description: '', plannedHours: null, actualHours: null, status: '' })

const categories = ['DEVELOPMENT', 'TESTING', 'BUGFIX', 'COMMUNICATION', 'DOCUMENTATION', 'DESIGN', 'PLANNING', 'LEARNING', 'DEPLOYMENT', 'SUPPORT', 'MISCELLANEOUS']
const statuses = ['IN_PROGRESS', 'UNDER_TESTING', 'DONE']
const headers = [
  { title: 'Category', key: 'category' }, { title: 'Activity', key: 'activity' },
  { title: 'Planned Hrs', key: 'plannedHours' }, { title: 'Actual Hrs', key: 'actualHours' },
  { title: 'Status', key: 'status' }, { title: 'Actions', key: 'actions', sortable: false }
]

onMounted(async () => {
  const all = (await warApi.getWeeks()).data
  const today = new Date()
  weeks.value = all.filter(w => new Date(w.weekStart) <= today)
})

async function loadWar() {
  loading.value = true
  try {
    const war = (await warApi.getWar(selectedWeekId.value)).data
    activities.value = war?.activities || []
  } catch { activities.value = [] } finally { loading.value = false }
}

function openAddDialog() {
  editingActivity.value = null
  form.value = { category: '', activity: '', description: '', plannedHours: null, actualHours: null, status: '' }
  activityDialog.value = true
}
function openEditDialog(item) {
  editingActivity.value = item
  form.value = { ...item }
  activityDialog.value = true
}

async function saveActivity() {
  const { valid } = await actForm.value.validate()
  if (!valid) return
  saving.value = true; dialogError.value = ''
  try {
    if (editingActivity.value) {
      await warApi.updateActivity(selectedWeekId.value, editingActivity.value.id, form.value)
    } else {
      await warApi.addActivity(selectedWeekId.value, form.value)
    }
    activityDialog.value = false
    await loadWar()
  } catch (e) { dialogError.value = e.response?.data?.error || 'Failed to save' }
  finally { saving.value = false }
}

async function deleteActivity(item) {
  await warApi.deleteActivity(selectedWeekId.value, item.id)
  await loadWar()
}

function statusColor(s) {
  return { IN_PROGRESS: 'warning', UNDER_TESTING: 'info', DONE: 'success' }[s] || 'grey'
}
</script>
