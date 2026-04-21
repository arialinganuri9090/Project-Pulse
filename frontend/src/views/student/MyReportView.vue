<template>
  <div>
    <h1 class="text-h4 font-weight-bold mb-6">My Peer Evaluation Report</h1>
    <v-select
      v-model="selectedWeekId"
      :items="weeks"
      :item-title="w => w.weekStart + ' - ' + w.weekEnd"
      item-value="id"
      label="Select Week"
      variant="outlined"
      style="max-width: 400px"
      class="mb-4"
      @update:model-value="loadReport"
    />
    <div v-if="loading" class="text-center pa-8"><v-progress-circular indeterminate /></div>
    <v-card v-else-if="reportHtml" rounded="lg">
      <v-card-text>
        <div v-html="reportHtml" />
      </v-card-text>
    </v-card>
    <v-alert v-if="noData" type="info">No peer evaluation data available for this week.</v-alert>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { peerEvalApi } from '@/api/index.js'

const weeks = ref([]); const selectedWeekId = ref(null); const reportHtml = ref(''); const loading = ref(false); const noData = ref(false)

onMounted(async () => { weeks.value = (await peerEvalApi.getWeeks()).data })

async function loadReport() {
  loading.value = true; noData.value = false; reportHtml.value = ''
  try { reportHtml.value = (await peerEvalApi.getMyReport(selectedWeekId.value)).data; if (!reportHtml.value) noData.value = true }
  catch { noData.value = true } finally { loading.value = false }
}
</script>
