<template>
  <div>
    <div class="d-flex align-center mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" @click="$router.back()" class="mr-2" />
      <h1 class="text-h4 font-weight-bold">{{ student?.firstName }} {{ student?.lastName }}</h1>
    </div>
    <v-row v-if="student">
      <v-col cols="12" md="5">
        <v-card rounded="lg" class="mb-4">
          <v-card-title>Profile</v-card-title>
          <v-card-text>
            <v-list density="compact">
              <v-list-item title="Email" :subtitle="student.email" />
              <v-list-item title="Status">
                <template #subtitle><v-chip :color="student.status === 'ACTIVE' ? 'success' : 'grey'" size="small">{{ student.status }}</v-chip></template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="7">
        <v-card rounded="lg">
          <v-card-title>Actions</v-card-title>
          <v-card-text>
            <v-btn color="primary" variant="outlined" class="mr-2" @click="showPeerEvalReport">Peer Eval Report</v-btn>
            <v-btn color="success" variant="outlined" @click="showWarReport">WAR Report</v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Reports as dialogs with HTML content -->
    <v-dialog v-model="reportDialog" max-width="1000">
      <v-card rounded="lg">
        <v-card-title class="d-flex justify-space-between align-center pa-4">
          {{ reportTitle }}
          <v-btn icon="mdi-close" variant="text" @click="reportDialog = false" />
        </v-card-title>
        <v-card-text>
          <div v-if="reportLoading" class="text-center pa-8"><v-progress-circular indeterminate /></div>
          <div v-else v-html="reportHtml" />
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { studentApi, reportApi } from '@/api/index.js'

const route = useRoute()
const student = ref(null)
const reportDialog = ref(false); const reportHtml = ref(''); const reportTitle = ref(''); const reportLoading = ref(false)

onMounted(async () => { student.value = (await studentApi.get(route.params.id)).data })

async function showPeerEvalReport() {
  reportTitle.value = 'Peer Evaluation Report'; reportLoading.value = true; reportDialog.value = true
  try { reportHtml.value = (await reportApi.studentPeerEval(route.params.id, 1, 999)).data }
  finally { reportLoading.value = false }
}
async function showWarReport() {
  reportTitle.value = 'WAR Report'; reportLoading.value = true; reportDialog.value = true
  try { reportHtml.value = (await reportApi.studentWar(route.params.id, 1, 999)).data }
  finally { reportLoading.value = false }
}
</script>
