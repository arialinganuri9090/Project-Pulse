<template>
  <div>
    <h1 class="text-h4 font-weight-bold mb-6">Section Peer Evaluation Report</h1>
    <v-card rounded="lg" class="mb-4">
      <v-card-text>
        <v-row dense>
          <v-col cols="12" md="5">
            <v-select v-model="sectionId" :items="sections" item-title="name" item-value="id" label="Select Section" variant="outlined" @update:model-value="loadWeeks" />
          </v-col>
          <v-col cols="12" md="5">
            <v-select v-model="weekId" :items="weeks" :item-title="w => w.weekStart + ' - ' + w.weekEnd" item-value="id" label="Select Week" variant="outlined" :disabled="!sectionId" />
          </v-col>
          <v-col cols="12" md="2" class="d-flex align-center">
            <v-btn color="primary" block @click="generateReport" :disabled="!weekId" :loading="loading">Generate</v-btn>
          </v-col>
        </v-row>
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
import { sectionApi, reportApi } from '@/api/index.js'

const sections = ref([]); const weeks = ref([]); const sectionId = ref(null); const weekId = ref(null)
const reportHtml = ref(''); const loading = ref(false)

onMounted(async () => { sections.value = (await sectionApi.list()).data })

async function loadWeeks() { weeks.value = (await sectionApi.getWeeks(sectionId.value)).data; weekId.value = null }
async function generateReport() {
  loading.value = true; reportHtml.value = ''
  try { reportHtml.value = (await reportApi.sectionPeerEval(sectionId.value, weekId.value)).data }
  finally { loading.value = false }
}
</script>
