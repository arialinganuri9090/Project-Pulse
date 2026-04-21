<template>
  <div>
    <div class="d-flex align-center mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" @click="$router.back()" class="mr-2" />
      <h1 class="text-h4 font-weight-bold">Student Reports - {{ student?.firstName }} {{ student?.lastName }}</h1>
    </div>

    <v-tabs v-model="tab" class="mb-4">
      <v-tab value="peerEval">Peer Eval Report</v-tab>
      <v-tab value="war">WAR Report</v-tab>
    </v-tabs>

    <v-card rounded="lg" class="mb-4">
      <v-card-text>
        <v-row dense>
          <v-col cols="6">
            <v-select v-model="startWeekId" :items="allWeeks" :item-title="w => w.weekStart + ' - ' + w.weekEnd" item-value="id" label="Start Week" variant="outlined" />
          </v-col>
          <v-col cols="6">
            <v-select v-model="endWeekId" :items="allWeeks" :item-title="w => w.weekStart + ' - ' + w.weekEnd" item-value="id" label="End Week" variant="outlined" />
          </v-col>
        </v-row>
        <v-btn color="primary" class="mt-2" @click="generateReport" :loading="loading">Generate</v-btn>
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
import { useRoute } from 'vue-router'
import { studentApi, reportApi } from '@/api/index.js'

const route = useRoute()
const student = ref(null); const tab = ref('peerEval')
const allWeeks = ref([]); const startWeekId = ref(null); const endWeekId = ref(null)
const reportHtml = ref(''); const loading = ref(false)

onMounted(async () => {
  student.value = (await studentApi.get(route.params.id)).data
})

async function generateReport() {
  if (!startWeekId.value || !endWeekId.value) return
  loading.value = true; reportHtml.value = ''
  try {
    if (tab.value === 'peerEval') {
      reportHtml.value = (await reportApi.studentPeerEval(route.params.id, startWeekId.value, endWeekId.value)).data
    } else {
      reportHtml.value = (await reportApi.studentWar(route.params.id, startWeekId.value, endWeekId.value)).data
    }
  } finally { loading.value = false }
}
</script>
