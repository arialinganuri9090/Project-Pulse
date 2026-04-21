<template>
  <div>
    <div class="d-flex align-center mb-6">
      <v-btn icon="mdi-arrow-left" variant="text" to="/admin/teams" class="mr-2" />
      <h1 class="text-h4 font-weight-bold">Create Team</h1>
    </div>
    <v-card rounded="lg" max-width="600">
      <v-card-text class="pa-6">
        <v-alert v-if="error" type="error" class="mb-4">{{ error }}</v-alert>
        <v-form @submit.prevent="submit" ref="form">
          <v-text-field v-model="name" label="Team Name (must be unique)" variant="outlined" :rules="[v => !!v || 'Required']" class="mb-3" />
          <v-textarea v-model="description" label="Description" variant="outlined" class="mb-3" rows="3" />
          <v-text-field v-model="websiteUrl" label="Team Website URL" variant="outlined" class="mb-3" />
          <v-select v-model="sectionId" :items="sections" item-title="name" item-value="id" label="Senior Design Section" variant="outlined" :rules="[v => !!v || 'Required']" class="mb-4" />
          <div class="d-flex justify-end gap-2">
            <v-btn variant="outlined" to="/admin/teams">Cancel</v-btn>
            <v-btn color="primary" type="submit" :loading="loading">Create Team</v-btn>
          </div>
        </v-form>
      </v-card-text>
    </v-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { teamApi, sectionApi } from '@/api/index.js'

const router = useRouter(); const form = ref(null)
const name = ref(''); const description = ref(''); const websiteUrl = ref(''); const sectionId = ref(null)
const sections = ref([]); const loading = ref(false); const error = ref('')

onMounted(async () => { sections.value = (await sectionApi.list()).data })

async function submit() {
  const { valid } = await form.value.validate()
  if (!valid) return
  loading.value = true; error.value = ''
  try {
    await teamApi.create({ name: name.value, description: description.value, websiteUrl: websiteUrl.value, sectionId: sectionId.value })
    router.push('/admin/teams')
  } catch (e) { error.value = e.response?.data?.error || 'Failed to create team' }
  finally { loading.value = false }
}
</script>
