<script setup>
import { computed, onMounted, ref, watch, nextTick } from 'vue'
import { getSocialMediaAPI } from '@/api/socialMedia'
import { getPersonalInfoAPI } from '@/api/personalInfo'
import { recordVisitorAPI } from '@/api/visitor'
import { getSystemConfigAPI } from '@/api/systemConfig'

const FALLBACK_AVATAR = '/favicon.png'
const REQUIRED_SOCIAL_LINKS = [
  { name: 'Blog', icon: 'boke', link: 'https://blog.licheng.website' },
  { name: 'CV', icon: 'jianli', link: 'https://cv.licheng.website' },
  { name: 'TimelineJournal', icon: 'code', link: 'https://timelinejournal.licheng.website' },
  { name: 'LongevityHabits', icon: 'code', link: 'https://longevityhabits.licheng.website' },
  { name: 'Virtuals-Launch-Hunter', icon: 'code', link: 'https://launch.licheng.website' },
  { name: 'GitHub', icon: 'github', link: 'https://github.com/LeeDaud' },
  { name: 'X', icon: '', link: 'https://x.com/LeeDaud_0212' }
]

const startYear = ref(0)
const currentYear = ref(0)

const personalInfo = ref({
  nickname: 'LeeDaud',
  avatar: FALLBACK_AVATAR
})
const socialMedia = ref([])
const icpBeian = ref('')
const gonganBeian = ref('')
const isDataLoaded = ref(false)

const isDarkMode = ref(false)

const applyTheme = () => {
  const root = document.documentElement
  if (isDarkMode.value) {
    root.style.setProperty('--bg-light', '#1a1a1a')
    root.style.setProperty('--card-light', '#242424')
    root.style.setProperty('--border-light', '#333333')
    root.style.setProperty('--text-light', '#f0f0f0')
    root.style.setProperty('--muted-light', '#9e9e9e')
    root.style.setProperty('--hover-light', '#333333')
  } else {
    root.style.setProperty('--bg-light', '#fafafa')
    root.style.setProperty('--card-light', '#ffffff')
    root.style.setProperty('--border-light', '#e0e0e0')
    root.style.setProperty('--text-light', '#121212')
    root.style.setProperty('--muted-light', '#666666')
    root.style.setProperty('--hover-light', '#f0f0f0')
  }
}

const initTheme = () => {
  const media = window.matchMedia('(prefers-color-scheme: dark)')
  isDarkMode.value = media.matches
  applyTheme()
  media.addEventListener('change', (e) => {
    isDarkMode.value = e.matches
    applyTheme()
  })
}

const normalizeExternalUrl = (url) => {
  const value = (url || '').trim()
  if (!value) return ''
  if (/^https?:\/\//i.test(value)) return value
  if (/^\/\//.test(value)) return `https:${value}`
  return `https://${value}`
}

const normalizeSocialMedia = (list) => {
  const items = Array.isArray(list) ? list : []
  const normalized = []
  const seenNames = new Set()

  items.forEach((item, index) => {
    const name = (item?.name || '').trim()
    const link = normalizeExternalUrl(item?.link)
    if (!name || !link) return
    const key = name.toLowerCase()
    if (seenNames.has(key)) return
    seenNames.add(key)
    normalized.push({
      ...item,
      id: item?.id ?? `api-${index}`,
      name,
      link
    })
  })

  REQUIRED_SOCIAL_LINKS.forEach((item, index) => {
    const key = item.name.toLowerCase()
    if (seenNames.has(key)) return
    seenNames.add(key)
    normalized.push({
      id: `default-${index}`,
      ...item,
      link: normalizeExternalUrl(item.link)
    })
  })

  return normalized
}

const avatarUrl = computed(() => normalizeExternalUrl(personalInfo.value.avatar) || FALLBACK_AVATAR)

const handleAvatarError = (event) => {
  if (event?.target) {
    event.target.src = FALLBACK_AVATAR
  }
}

const fetchData = async () => {
  try {
    const [personalRes, socialRes, icpRes, gonganRes, startTimeRes] =
      await Promise.all([
        getPersonalInfoAPI(),
        getSocialMediaAPI(),
        getSystemConfigAPI('icp-beian').catch(() => null),
        getSystemConfigAPI('gongan-beian').catch(() => null),
        getSystemConfigAPI('start-time').catch(() => null)
      ])

    const personalData = personalRes?.data?.data || {}
    personalInfo.value = {
      ...personalInfo.value,
      ...personalData,
      avatar: normalizeExternalUrl(personalData.avatar) || FALLBACK_AVATAR
    }
    socialMedia.value = normalizeSocialMedia(socialRes?.data?.data || [])
    icpBeian.value = icpRes?.data?.data?.configValue || ''
    gonganBeian.value = gonganRes?.data?.data?.configValue || ''

    const startTime = startTimeRes?.data?.data?.configValue || ''
    const parsed = Number((startTime || '').split('-')[0])
    startYear.value = Number.isFinite(parsed) && parsed > 0 ? parsed : currentYear.value
  } catch (error) {
    console.error('数据获取失败:', error)
    socialMedia.value = normalizeSocialMedia([])
    personalInfo.value = {
      ...personalInfo.value,
      avatar: FALLBACK_AVATAR
    }
  } finally {
    isDataLoaded.value = true
  }
}

const setupLinkAnimations = () => {
  nextTick(() => {
    const links = document.querySelectorAll('.link')
    links.forEach((link, index) => {
      link.style.setProperty('--link-index', index + 1)
    })
  })
}

onMounted(async () => {
  currentYear.value = new Date().getFullYear()
  initTheme()
  await fetchData()

  recordVisitorAPI().catch((err) => {
    console.error('访客记录失败:', err)
  })

  await nextTick()
  setupLinkAnimations()
})

watch([() => socialMedia.value, isDataLoaded], () => {
  if (isDataLoaded.value && socialMedia.value.length > 0) {
    setupLinkAnimations()
  }
})
</script>

<template>
  <div class="home-container">
    <main class="card" role="main">
      <div class="avatar-container">
        <img :src="avatarUrl" alt="头像" @error="handleAvatarError" />
      </div>

      <h1 data-name>{{ personalInfo.nickname || 'LeeDaud' }}</h1>
      <p class="tagline">构建个人系统，践行长期主义</p>
      <p class="subtagline">长期主义者、技术探索者、持续写作者。</p>

      <nav class="links" aria-label="站点导航" v-if="socialMedia.length > 0">
        <a
          v-for="item in socialMedia"
          :key="item.id"
          class="link"
          :href="item.link"
          target="_blank"
          :title="item.name"
        >
          <span v-if="item.icon" :class="`iconfont icon-${item.icon}`"></span>
          <svg
            v-else-if="item.name === 'X'"
            class="x-brand-icon"
            viewBox="0 0 24 24"
            aria-hidden="true"
          >
            <path
              fill="currentColor"
              d="M18.901 1.153h3.68l-8.04 9.19 9.46 12.504h-7.406l-5.8-7.584-6.64 7.584H.48l8.6-9.83L0 1.154h7.594l5.243 6.932L18.901 1.153Zm-1.29 19.47h2.04L6.486 3.77H4.298L17.61 20.623Z"
            />
          </svg>
          {{ item.name }}
        </a>
      </nav>

      <footer>
        <div class="beian-info">
          <template v-if="gonganBeian">
            <span>{{ gonganBeian }}</span>
            <span class="beian-divider">|</span>
          </template>

          <template v-if="icpBeian">
            <a href="https://beian.miit.gov.cn/" target="_blank" rel="noopener noreferrer">
              {{ icpBeian }}
            </a>
            <span class="beian-divider">|</span>
          </template>

          <span>© 2024-{{ currentYear }} {{ personalInfo.nickname || 'LeeDaud' }}</span>
        </div>
      </footer>
    </main>
  </div>
</template>

<style>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

html,
body {
  height: 100%;
  width: 100%;
}

:root {
  --bg-light: #fafafa;
  --card-light: #ffffff;
  --border-light: #e0e0e0;
  --text-light: #121212;
  --muted-light: #666666;
  --hover-light: #f0f0f0;
}

body {
  background-color: var(--bg-light);
  color: var(--text-light);
  font: 16px/1.6 system-ui, -apple-system, Segoe UI, Roboto, Helvetica, Arial, sans-serif;
  transition: background-color 0.3s ease, color 0.3s ease;
}

.home-container {
  width: 100vw;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
}

.card {
  width: 100%;
  max-width: 1200px;
  margin-inline: auto;
  padding: clamp(2.5rem, 7vw, 4.5rem);
  border-radius: 20px;
  background-color: var(--card-light);
  border: 1px solid var(--border-light);
  box-shadow: 0 4px 25px rgba(0, 0, 0, 0.06);
  text-align: center;
  transition: all 0.3s ease;
  max-height: 95vh;
  overflow-y: auto;
  animation: fadeIn 0.6s ease-out forwards;
}

.avatar-container {
  width: 150px;
  height: 150px;
  margin: 0 auto 2rem;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid var(--border-light);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.12);
  transition: transform 0.3s ease, border-color 0.3s ease;
  animation: fadeIn 0.5s ease-out 0.1s forwards;
  opacity: 0;
}

.avatar-container:hover {
  transform: scale(1.05);
}

.avatar-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  filter: contrast(1.1) brightness(1.05);
  transition: filter 0.3s ease;
}

h1 {
  margin: 0 0 0.8rem;
  font-size: clamp(2.2rem, 6vw, 3.2rem);
  letter-spacing: -0.02em;
  animation: fadeIn 0.5s ease-out 0.3s forwards;
  opacity: 0;
}

.tagline {
  margin: 0 0 0.6rem;
  color: var(--muted-light);
  font-size: clamp(1.2rem, 3vw, 1.4rem);
  font-weight: 500;
  animation: fadeIn 0.5s ease-out 0.4s forwards;
  opacity: 0;
}

.subtagline {
  margin: 0 0 1.2rem;
  color: var(--muted-light);
  font-size: clamp(1rem, 2.4vw, 1.08rem);
  animation: fadeIn 0.5s ease-out 0.45s forwards;
  opacity: 0;
}

.brief {
  margin: 0 0 2.4rem;
  color: var(--muted-light);
  font-size: 0.96rem;
  line-height: 1.85;
  animation: fadeIn 0.5s ease-out 0.5s forwards;
  opacity: 0;
}

.iconfont {
  font-size: 22px;
  width: 26px;
  text-align: center;
}

.x-brand-icon {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

.links {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 12px;
  margin-top: 1.5rem;
}

.link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 14px 12px;
  border-radius: 12px;
  border: 1px solid var(--border-light);
  background-color: transparent;
  color: inherit;
  text-decoration: none;
  transition: all 0.2s ease;
  font-weight: 500;
  font-size: 15px;
  animation: fadeIn 0.6s ease-out forwards;
  animation-delay: calc(var(--link-index) * 0.05s + 0.5s);
  opacity: 0;
}

.link:hover {
  background-color: var(--hover-light);
  border-color: currentColor;
  transform: translateY(-2px);
}

footer {
  margin-top: 3.2rem;
  color: var(--muted-light);
  font-size: 0.9rem;
  line-height: 1.6;
  animation: fadeIn 0.6s ease-out 0.8s forwards;
  opacity: 0;
}

.beian-info {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 0.5rem 1rem;
  margin-top: 0.5rem;
}

.beian-info a {
  color: inherit;
  text-decoration: none;
  border-bottom: 1px dotted currentColor;
  transition: border-color 0.2s ease;
}

.beian-info a:hover {
  border-bottom-style: solid;
}

.beian-divider {
  opacity: 0.5;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 900px) {
  .links {
    grid-template-columns: repeat(auto-fit, minmax(130px, 1fr));
  }
}

@media (max-width: 768px) {
  .card {
    padding: 3rem 2rem;
  }
}

@media (max-width: 600px) {
  .card {
    padding: 2.5rem 1.5rem;
    max-height: none;
    overflow: visible;
  }

  .links {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }

  .tagline {
    font-size: 1rem;
  }

  .avatar-container {
    width: 120px;
    height: 120px;
  }
}
</style>
