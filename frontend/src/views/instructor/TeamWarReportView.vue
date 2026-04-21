<template>
  <div>
    <h1 class="text-h4 font-weight-bold mb-6">Team WAR Report</h1>
    <v-card rounded="lg" class="mb-4">
      <v-card-text>
        <v-row dense>
          <v-col cols="12" md="4">
            <v-select v-model="sectionId" :items="sections" item-title="name" item-value="id" label="Section" variant="outlined" @update:model-value="loadTeamsAndWeeks" />
          </v-col>
          <v-col cols="12" md="4">
            <v-select v-model="teamId" :items="teams" item-title="name" item-value="id" label="Team" variant="outlined" :disabled="!sectionId" />
          </v-col>
          <v-col cols="12" md="4">
            <v-select v-model="weekId" :items="weeks" :item-title="w => w.weekStart + ' - ' + w.weekEnd" item-value="id" label="Week" variant="outlined" :disabled="!sectionId" />
          </v-col>
        </v-row>
        <v-btn color="primary" class="mt-2" @click="generateReport" :disabled="!teamId || !weekId" :loading="loading">Generate Report</v-btn>
      </v-card-text>
    </v-card>
    <div v-if="loading" class="text-center pa-8"><v-progress-circular indeterminate /></div>
    <v-card v-else-if="reportHtml" rounded="lg">
      <v-card-text><div v-html="reportHtml" /></v-card-text>
    </v-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { sectionApi, teamApi, reportApi } from '@/api/index.js'

const sections = ref([]); const teams = ref([]); const weeks = ref([])
const sectionId = ref(null); const teamId = ref(null); const weekId = ref(null)
const reportHtml = ref(''); const loading = ref(false)

onMounted(async () => { sections.value = (await sectionApi.list()).data })

async function loadTeamsAndWeeks() {
  const [t, w] = await Promise.all([teamApi.list({ sectionId: sectionId.value }), sectionApi.getWeeks(sectionId.value)])
  teams.value = t.data; weeks.value = w.data; teamId.value = null; weekId.value = null
}
async function generateReport() {
  loading.value = true; reportHtml.value = ''
  try { reportHtml.value = (await reportApi.teamWar(teamId.value, weekId.value)).data }
  finally { loading.value = false }
}
</script>
