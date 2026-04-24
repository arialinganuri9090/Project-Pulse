<template>
  <div>
    <div class="d-flex align-center mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" to="/admin/sections" class="mr-2" />
      <h1 class="text-h4 font-weight-bold">{{ section?.name || 'Section' }}</h1>
      <v-spacer />
      <v-btn color="primary" variant="outlined" :to="`/admin/sections/${$route.params.id}/edit`" class="mr-2">Edit</v-btn>
      <v-btn color="success" variant="outlined" @click="openWeeksDialog">Setup Active Weeks</v-btn>
    </div>

    <v-row v-if="section">
      <v-col cols="12" md="6">
        <v-card rounded="lg" class="mb-4">
          <v-card-title>Details</v-card-title>
          <v-card-text>
            <v-list density="compact">
              <v-list-item title="Start Date" :subtitle="section.startDate" />
              <v-list-item title="End Date" :subtitle="section.endDate" />
              <v-list-item title="Rubric" :subtitle="section.rubric?.name || 'None'" />
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="6">
        <v-card rounded="lg" class="mb-4">
          <v-card-title>Active Weeks</v-card-title>
          <v-card-text>
            <v-chip v-for="w in activeWeeks" :key="w.id" :color="w.active ? 'success' : 'grey'" class="ma-1" size="small">
              {{ w.weekStart }} - {{ w.weekEnd }}
            </v-chip>
            <p v-if="!activeWeeks.length" class="text-medium-emphasis">No weeks configured yet.</p>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12">
        <v-card rounded="lg">
          <v-card-title>Teams</v-card-title>
          <v-card-text>
            <v-chip v-for="t in section.teams" :key="t.id" class="ma-1" :to="`/admin/teams/${t.id}`">{{ t.name }}</v-chip>
            <p v-if="!section.teams?.length" class="text-medium-emphasis">No teams yet.</p>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Setup Active Weeks Dialog -->
    <v-dialog v-model="weeksDialog" max-width="700">
      <v-card rounded="lg">
        <v-card-title class="pa-4">Setup Active Weeks</v-card-title>
        <v-card-text>
          <p class="text-body-2 mb-4 text-medium-emphasis">Check the weeks that should be <strong>INACTIVE</strong> (no WAR/peer eval submissions):</p>
          <v-row dense>
            <v-col v-for="w in allWeeks" :key="w.id" cols="12" sm="6">
              <v-checkbox
                v-model="inactiveWeekStarts"
                :value="w.weekStart"
                :label="`${w.weekStart} - ${w.weekEnd}`"
                density="compact"
                hide-details
              />
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="weeksDialog = false">Cancel</v-btn>
          <v-btn color="primary" :loading="saving" @click="saveWeeks">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { sectionApi } from '@/api/index.js'

const route = useRoute()
const section = ref(null)
const activeWeeks = ref([])
const allWeeks = ref([])
const weeksDialog = ref(false)
const inactiveWeekStarts = ref([])
const saving = ref(false)

onMounted(async () => {
  section.value = (await sectionApi.get(route.params.id)).data
  activeWeeks.value = (await sectionApi.getWeeks(route.params.id)).data
})

async function openWeeksDialog() {
  // If no weeks exist yet, generate them all as active first, then load them
  if (activeWeeks.value.length === 0) {
    await sectionApi.setupWeeks(route.params.id, { inactiveWeekStarts: [] })
    activeWeeks.value = (await sectionApi.getWeeks(route.params.id)).data
  }
  allWeeks.value = activeWeeks.value
  inactiveWeekStarts.value = activeWeeks.value.filter(w => !w.active).map(w => w.weekStart)
  weeksDialog.value = true
}

async function saveWeeks() {
  saving.value = true
  try {
    await sectionApi.setupWeeks(route.params.id, { inactiveWeekStarts: inactiveWeekStarts.value })
    activeWeeks.value = (await sectionApi.getWeeks(route.params.id)).data
    weeksDialog.value = false
  } finally { saving.value = false }
}
</script>
