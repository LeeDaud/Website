<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores'

const route = useRoute()
const userStore = useUserStore()

const collapsed = ref(false)
const activeMenu = computed(() => route.path)
const isEditorPage = computed(() => route.path.startsWith('/article/edit'))

const navItems = [
  { path: '/dashboard', icon: 'icon-yibiaopan', label: '仪表盘' },
  {
    path: '/article/list',
    icon: 'icon-bianjiwenzhang_huaban',
    label: '文章管理'
  },
  { path: '/category', icon: 'icon-folder', label: '分类 / 标签' },
  { path: '/comment', icon: 'icon-comment', label: '评论管理' },
  { path: '/message', icon: 'icon-liuyan', label: '留言管理' },
  { path: '/friend-link', icon: 'icon-link', label: '项目管理' },
  { path: '/music', icon: 'icon-music', label: '音乐管理' },
  { path: '/rss', icon: 'icon-rss', label: 'RSS 订阅' },
  { path: '/visitor', icon: 'icon-user', label: '访客管理' },
  { path: '/view-record', icon: 'icon-eye', label: '浏览记录' },
  { path: '/operation-log', icon: 'icon-wj-rz', label: '操作日志' },
  { path: '/profile', icon: 'icon-iconfontprofile', label: '个人资料' },
  { path: '/settings', icon: 'icon-setting', label: '系统设置' }
]

const handleLogout = () => {
  ElMessageBox.confirm('确认退出登录？', '提示', {
    confirmButtonText: '退出',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logoutAction()
  })
}

const toggleSidebar = () => {
  collapsed.value = !collapsed.value
}
</script>

<template>
  <div class="admin-shell">
    <aside :class="['sidebar', { collapsed }]">
      <div class="sidebar-logo">
        <span v-if="!collapsed" class="logo-text">管理控制台</span>
        <span v-else class="logo-icon">
          <span class="iconfont icon-guanliduan" />
        </span>
      </div>

      <nav class="sidebar-nav">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          :class="['nav-item', { active: activeMenu.startsWith(item.path) }]"
        >
          <span :class="['iconfont', item.icon]" />
          <span v-if="!collapsed" class="nav-label">{{ item.label }}</span>
        </router-link>
      </nav>

      <button type="button" class="collapse-btn" @click="toggleSidebar">
        <span
          :class="[
            'iconfont',
            collapsed ? 'icon-arrow-right-bold' : 'icon-arrow-left-bold'
          ]"
        />
      </button>
    </aside>

    <div class="main-wrapper">
      <header class="topbar">
        <div class="topbar-breadcrumb">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }"
              >首页</el-breadcrumb-item
            >
            <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="topbar-right">
          <span class="user-name">
            <span class="iconfont icon-user" />
            {{ userStore.userInfo?.nickname || '管理员' }}
          </span>
          <button class="logout-btn" @click="handleLogout">
            <span class="iconfont icon-logout" />
            退出
          </button>
        </div>
      </header>

      <main :class="['page-main', { 'editor-page': isEditorPage }]">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" :key="$route.fullPath" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<style scoped>
.admin-shell {
  display: flex;
  height: 100vh;
  background-color: var(--admin-bg);
  isolation: isolate;
}

.sidebar {
  width: 220px;
  background-color: var(--admin-card);
  border-right: 1px solid var(--admin-border);
  display: flex;
  flex-direction: column;
  transition: width 0.25s ease;
  flex-shrink: 0;
  position: relative;
  z-index: 30;
}

.sidebar.collapsed {
  width: 60px;
}

.sidebar-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid var(--admin-border);
  font-size: 16px;
  font-weight: 700;
  color: var(--admin-text);
  letter-spacing: 1px;
  white-space: nowrap;
  overflow: hidden;
}

.logo-icon .iconfont {
  font-size: 22px;
}

.sidebar-nav {
  flex: 1;
  padding: 12px 0;
  overflow-y: auto;
  position: relative;
  z-index: 1;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  color: var(--admin-text2);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition:
    background 0.15s,
    color 0.15s;
  text-decoration: none;
  white-space: nowrap;
  overflow: hidden;
  position: relative;
  z-index: 1;
}

.nav-item:hover {
  background-color: var(--admin-hover);
  color: var(--admin-text);
}

.nav-item.active {
  background-color: var(--admin-hover);
  color: var(--admin-text);
  font-weight: 600;
}

.nav-item .iconfont {
  font-size: 18px;
  flex-shrink: 0;
}

.collapse-btn {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-top: 1px solid var(--admin-border);
  background: transparent;
  cursor: pointer;
  color: var(--admin-text3);
  transition: color 0.15s;
  position: relative;
  z-index: 2;
}

.collapse-btn:hover {
  color: var(--admin-text);
}

.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
  position: relative;
  z-index: 1;
}

.topbar {
  height: 60px;
  background-color: var(--admin-card);
  border-bottom: 1px solid var(--admin-border);
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
  position: relative;
  z-index: 20;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-name {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--admin-text2);
}

.user-name .iconfont {
  font-size: 16px;
}

.logout-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  background: transparent;
  border: 1px solid var(--admin-border);
  border-radius: 6px;
  padding: 6px 12px;
  font-size: 13px;
  color: var(--admin-text2);
  cursor: pointer;
  transition: all 0.15s;
}

.logout-btn:hover {
  border-color: var(--admin-text);
  color: var(--admin-text);
  background: var(--admin-hover);
}

.page-main {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  min-width: 0;
  position: relative;
  z-index: 1;
}

.page-main.editor-page {
  padding: 0;
  overflow: hidden;
}

.page-fade-enter-active,
.page-fade-leave-active {
  transition:
    opacity 0.22s ease,
    transform 0.22s ease;
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>
