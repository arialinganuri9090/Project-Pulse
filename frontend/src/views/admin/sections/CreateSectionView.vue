<template>
  <div>
    <div class="d-flex align-center mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" to="/admin/sections" class="mr-2" />
      <h1 class="text-h4 font-weight-bold">Create Section</h1>
    </div>
    <v-card rounded="lg" max-width="700">
      <v-card-text class="pa-6">
        <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>
        <v-form @submit.prevent="submit" ref="form">
          <v-text-field v-model="sectionName" label="Section Name (must be unique)" variant="outlined" :rules="[v => !!v || 'Required']" class="mb-3" />
          <v-row dense class="mb-3">
            <v-col cols="6">
              <v-text-field v-model="startDate" label="Start Date" type="date" variant="outlined" :rules="[v => !!v || 'Required']" />
            </v-col>
            <v-col cols="6">
              <v-text-field v-model="endDate" label="End Date" type="date" variant="outlined" :rules="[v => !!v || 'Required']" />
            </v-col>
          </v-row>

          <v-select
            v-model="rubricId"
            :items="rubrics"
            item-title="name"
            item-value="id"
            label="Select Rubric for Peer Evaluation"
            variant="outlined"
            class="mb-4"
            :rules="[v => !!v || 'Please select a rubric']"
          />

          <div class="d-flex justify-end gap-2">
            <v-btn variant="outlined" to="/admin/sections">Cancel</v-btn>
            <v-btn color="primary" type="submit" :loading="loading">Create Section</v-btn>
          </div>
        </v-form>
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { sectionApi, rubricApi } from '@/api/index.js'

const router = useRouter()
const form = ref(null)
const sectionName = ref('')
const startDate = ref('')
const endDate = ref('')
const rubricId = ref(null)
const rubrics = ref([])
const loading = ref(false)
const error = ref('')

onMounted(async () => { rubrics.value = (await rubricApi.list()).data })

async function submit() {
  const { valid } = await form.value.validate()
  if (!valid) return
  loading.value = true; error.value = ''
  try {
    await sectionApi.create({ name: sectionName.value, startDate: startDate.value, endDate: endDate.value, rubricId: rubricId.value })
    router.push('/admin/sections')
  } catch (e) { error.value = e.response?.data?.error || 'Failed to create section' }
  finally { loading.value = false }
}
</script>
