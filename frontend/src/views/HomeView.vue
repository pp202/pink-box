<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { apiRequest } from '../api'
import { useAuthStore } from '../stores/auth'

type HelloPayload = { message: string }
type Note = {
  id: number
  title: string
  body: string
  createdAt: string
  updatedAt: string
}

const authStore = useAuthStore()
const helloMessage = ref('')
const notes = ref<Note[]>([])
const title = ref('')
const body = ref('')
const error = ref('')

const loadData = async () => {
  try {
    const hello = await apiRequest<HelloPayload>('/api/v1/hello')
    helloMessage.value = hello.message
    notes.value = await apiRequest<Note[]>('/api/v1/notes')
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Failed loading data'
  }
}

const createNote = async () => {
  await apiRequest<Note>(
    '/api/v1/notes',
    {
      method: 'POST',
      body: JSON.stringify({ title: title.value, body: body.value }),
    },
    true,
  )
  title.value = ''
  body.value = ''
  await loadData()
}

const logout = async () => {
  await authStore.logout()
  window.location.href = '/login'
}

onMounted(loadData)
</script>

<template>
  <section>
    <h2>Home</h2>
    <p><strong>User:</strong> {{ authStore.user?.username }}</p>
    <p><strong>Backend says:</strong> {{ helloMessage }}</p>
    <button @click="logout">Logout</button>

    <h3>Create note</h3>
    <form @submit.prevent="createNote">
      <label>
        Title
        <input v-model="title" required />
      </label>
      <label>
        Body
        <textarea v-model="body" required />
      </label>
      <button type="submit">Create</button>
    </form>

    <p v-if="error" class="error">{{ error }}</p>

    <h3>Your notes</h3>
    <ul>
      <li v-for="note in notes" :key="note.id">
        <strong>{{ note.title }}</strong>
        <p>{{ note.body }}</p>
      </li>
    </ul>
  </section>
</template>

<style scoped>
form {
  display: grid;
  gap: 0.75rem;
  max-width: 420px;
  margin-bottom: 1rem;
}
label {
  display: grid;
  gap: 0.25rem;
}
.error {
  color: #b00020;
}
ul {
  padding-left: 1rem;
}
</style>
