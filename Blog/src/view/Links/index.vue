<script setup>
import { ref, inject, onMounted } from 'vue'
import { getFriendLinks } from '@/api/friendLink'
import SidebarCard from '@/components/SidebarCard.vue'

const { articleTitle, articleMeta } = inject('setHero')
const loading = ref(false)

const normalizeUrl = (url) => {
  const value = (url || '').trim()
  if (!value) return ''
  return /^https?:\/\//i.test(value) ? value : `https://${value}`
}

const projects = ref([])

const mapProject = (item) => ({
  id: item.id,
  name: item.name || '未命名项目',
  url: normalizeUrl(item.url),
  avatarUrl: item.avatarUrl || '',
  description: item.description || '暂无描述'
})

const loadProjects = async () => {
  loading.value = true
  try {
    const res = await getFriendLinks()
    const list = res?.data?.data ?? []
    const projectList = list
      .filter((item) => item?.url && String(item.url).trim())
      .map(mapProject)
    projects.value = projectList
  } catch {
    projects.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  articleTitle.value = '项目'
  articleMeta.value = '已完成并部署的项目'
  loadProjects()
})
</script>

<template>
  <div class="links-page">
    <div class="links-layout">
      <div class="links-main">
        <div class="content-card">
          <div class="card-header">
            <i class="iconfont icon-lianjie" />
            <span>共 {{ projects.length }} 个项目</span>
          </div>

          <div v-if="loading" class="placeholder-grid">
            <div v-for="i in 6" :key="i" class="sk-card" />
          </div>

          <div v-else-if="projects.length" class="link-grid">
            <a
              v-for="project in projects"
              :key="project.id"
              :href="normalizeUrl(project.url)"
              target="_blank"
              rel="noopener noreferrer"
              class="link-card"
            >
              <img
                v-if="project.avatarUrl"
                :src="project.avatarUrl"
                :alt="project.name"
                class="link-avatar"
                loading="lazy"
              />
              <span v-else class="link-avatar-letter">{{
                project.name ? project.name.charAt(0) : '?'
              }}</span>
              <div class="link-body">
                <p class="link-name">{{ project.name }}</p>
                <p class="link-desc">{{ project.description }}</p>
                <p class="link-url">{{ normalizeUrl(project.url) }}</p>
              </div>
            </a>
          </div>

          <p v-else class="empty">暂无项目，请在后台项目管理中添加</p>
        </div>
      </div>

      <SidebarCard />
    </div>
  </div>
</template>

<style scoped>
.links-page {
  width: 100%;
}
.links-layout {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}
.links-main {
  flex: 1;
  min-width: 0;
}
.content-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid #ebeef5;
  padding: 24px 28px;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #909399;
  margin-bottom: 20px;
  padding-bottom: 14px;
  border-bottom: 1px solid #ebeef5;
}
.card-header .iconfont {
  font-size: 16px;
  color: #606266;
}

.placeholder-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 14px;
}
.sk-card {
  height: 80px;
  background: #ebeef5;
  border-radius: 8px;
}

.link-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 14px;
}
.link-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  text-decoration: none;
  color: inherit;
  background: #fafafa;
  transition:
    transform 0.2s,
    box-shadow 0.2s,
    border-color 0.15s;
}
.link-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  border-color: #c0c4cc;
}
.link-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  border: 2px solid #ebeef5;
}
.link-avatar-letter {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e4e7ed;
  color: #606266;
  font-size: 20px;
  font-weight: 700;
  user-select: none;
}
.link-body {
  min-width: 0;
}
.link-name {
  font-size: 15px;
  font-weight: 600;
  margin: 0 0 3px;
  color: #303133;
}
.link-desc {
  font-size: 12px;
  color: #909399;
  margin: 0;
  line-height: 1.5;
}
.link-url {
  margin: 4px 0 0;
  font-size: 12px;
  color: #606266;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.empty {
  text-align: center;
  color: #909399;
  padding: 40px 0;
  font-size: 14px;
  margin: 0;
}

@media (max-width: 960px) {
  .links-layout {
    flex-direction: column;
  }
}
@media (max-width: 600px) {
  .content-card {
    padding: 16px;
  }
  .link-card {
    overflow: hidden;
    max-width: 100%;
  }
  .link-body {
    overflow: hidden;
  }
  .link-desc {
    white-space: normal;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }
}
</style>
