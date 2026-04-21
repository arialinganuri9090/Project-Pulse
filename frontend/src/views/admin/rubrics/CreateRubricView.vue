<template>
  <div>
    <div class="d-flex align-center mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" to="/admin/rubrics" class="mr-2" />
      <h1 class="text-h4 font-weight-bold">Create Rubric</h1>
    </div>
    <v-card rounded="lg" max-width="800">
      <v-card-text class="pa-6">
        <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>
        <v-form @submit.prevent="submit" ref="form">
          <v-text-field v-model="name" label="Rubric Name" variant="outlined" :rules="[v => !!v || 'Required']" class="mb-4" />

          <div class="d-flex align-center justify-space-between mb-3">
            <h3 class="text-h6">Criteria</h3>
            <v-btn color="primary" variant="tonal" size="small" prepend-icon="mdi-plus" @click="addCriterion">Add Criterion</v-btn>
          </div>

          <v-card v-for="(c, i) in criteria" :key="i" outlined class="mb-3 pa-4" variant="outlined">
            <div class="d-flex align-center justify-space-between mb-3">
              <span class="font-weight-medium">Criterion {{ i + 1 }}</span>
              <v-btn icon="mdi-delete" variant="text" size="small" color="error" @click="removeCriterion(i)" :disabled="criteria.length === 1" />
            </div>
            <v-row dense>
              <v-col cols="12" md="6">
                <v-text-field v-model="c.name" label="Name" variant="outlined" density="compact" :rules="[v => !!v || 'Required']" />
              </v-col>
              <v-col cols="12" md="3">
                <v-text-field v-model.number="c.maxScore" label="Max Score" type="number" variant="outlined" density="compact" :rules="[v => v > 0 || 'Must be positive']" />
              </v-col>
              <v-col cols="12">
                <v-textarea v-model="c.description" label="Description" variant="outlined" density="compact" rows="2" />
              </v-col>
            </v-row>
          </v-card>

          <div class="d-flex justify-end gap-2 mt-4">
            <v-btn variant="outlined" to="/admin/rubrics">Cancel</v-btn>
            <v-btn color="primary" type="submit" :loading="loading">Create Rubric</v-btn>
          </div>
        </v-form>
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { rubricApi } from '@/api/index.js'

const router = useRouter()
const form = ref(null)
const name = ref('')
const criteria = ref([{ name: '', description: '', maxScore: 10 }])
const loading = ref(false)
const error = ref('')

function addCriterion() { criteria.value.push({ name: '', description: '', maxScore: 10 }) }
function removeCriterion(i) { criteria.value.splice(i, 1) }

async function submit() {
  const { valid } = await form.value.validate()
  if (!valid) return
  loading.value = true
  error.value = ''
  try {
    await rubricApi.create({ name: name.value, criteria: criteria.value })
    router.push('/admin/rubrics')
  } catch (e) {
    error.value = e.response?.data?.error || 'Failed to create rubric'
  } finally {
    loading.value = false
  }
}
</script>
