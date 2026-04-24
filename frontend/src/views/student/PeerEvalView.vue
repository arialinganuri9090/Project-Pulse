<template>
  <div>
    <h1 class="text-h4 font-weight-bold mb-6">Peer Evaluation</h1>
    <v-alert type="info" density="compact" class="mb-4">
      You can only submit peer evaluations for the previous week. Scores must be integers. Every teammate (including yourself) must be evaluated.
    </v-alert>

    <v-select
      v-model="selectedWeekId"
      :items="pastWeeks"
      :item-title="w => w.weekStart + ' - ' + w.weekEnd"
      item-value="id"
      label="Select Week (previous weeks only)"
      variant="outlined"
      style="max-width: 400px"
      class="mb-4"
      @update:model-value="loadEval"
    />

    <template v-if="selectedWeekId && teammates.length">
      <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>

      <v-card v-for="member in teammates" :key="member.id" rounded="lg" class="mb-4">
        <v-card-title class="pa-4">{{ member.firstName }} {{ member.lastName }}</v-card-title>
        <v-card-text>
          <v-row dense>
            <v-col v-for="criterion in rubricCriteria" :key="criterion.id" cols="12" md="6">
              <v-text-field
                v-model.number="evalData[member.id].scores[criterion.id]"
                :label="criterion.name + ' (max ' + criterion.maxScore + ')'"
                :hint="criterion.description"
                persistent-hint
                type="number"
                variant="outlined"
                density="compact"
                :min="0"
                :max="criterion.maxScore"
              />
            </v-col>
          </v-row>
          <v-textarea v-model="evalData[member.id].publicComment" label="Public Comments (visible to student)" variant="outlined" rows="2" class="mt-3" />
          <v-textarea v-model="evalData[member.id].privateComment" label="Private Comments (instructor only)" variant="outlined" rows="2" class="mt-3" />
        </v-card-text>
      </v-card>

      <div class="d-flex justify-end">
        <v-btn color="primary" size="large" :loading="submitting" @click="submitEval">Submit Peer Evaluation</v-btn>
      </div>
    </template>

    <v-alert v-if="selectedWeekId && !teammates.length" type="warning">
      You are not assigned to a team. Contact your admin.
    </v-alert>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { peerEvalApi, teamApi, rubricApi } from '@/api/index.js'
import { useAuthStore } from '@/stores/auth.js'

const auth = useAuthStore()
const weeks = ref([]); const pastWeeks = ref([]); const selectedWeekId = ref(null)
const teammates = ref([]); const rubricCriteria = ref([])
const evalData = ref({}); const submitting = ref(false); const error = ref('')

onMounted(async () => {
  weeks.value = (await peerEvalApi.getWeeks()).data
  const today = new Date()
  pastWeeks.value = weeks.value.filter(w => new Date(w.weekEnd) < today)
})

async function loadEval() {
  error.value = ''
  try {
    const myTeamRes = await teamApi.myTeam()
    const myTeam = myTeamRes.data
    if (!myTeam) { teammates.value = []; return }
    teammates.value = myTeam.students || []

    // Load rubric criteria
    if (myTeam.section?.rubric?.id) {
      const rubric = (await rubricApi.get(myTeam.section.rubric.id)).data
      rubricCriteria.value = rubric.criteria || []
    }

    // Initialize eval data
    evalData.value = {}
    for (const m of teammates.value) {
      evalData.value[m.id] = { scores: {}, publicComment: '', privateComment: '' }
      for (const c of rubricCriteria.value) {
        evalData.value[m.id].scores[c.id] = null
      }
    }

    // Pre-fill existing evals
    const existing = (await peerEvalApi.getMyEvals(selectedWeekId.value)).data
    for (const e of existing) {
      if (evalData.value[e.evaluatee.id]) {
        evalData.value[e.evaluatee.id].publicComment = e.publicComment || ''
        evalData.value[e.evaluatee.id].privateComment = e.privateComment || ''
        for (const s of e.scores || []) {
          evalData.value[e.evaluatee.id].scores[s.criterion.id] = s.score
        }
      }
    }
  } catch (err) { error.value = 'Failed to load evaluation data' }
}

async function submitEval() {
  submitting.value = true; error.value = ''
  try {
    const evaluations = teammates.value.map(m => ({
      evaluateeId: m.id,
      scores: evalData.value[m.id].scores,
      publicComment: evalData.value[m.id].publicComment,
      privateComment: evalData.value[m.id].privateComment
    }))
    await peerEvalApi.submit(selectedWeekId.value, { evaluations })
    alert('Peer evaluation submitted successfully!')
  } catch (e) { error.value = e.response?.data?.error || 'Failed to submit' }
  finally { submitting.value = false }
}
</script>
