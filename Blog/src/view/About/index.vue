<script setup>
import { inject, onMounted, computed } from 'vue'
import { useBlogStore } from '@/stores'
import SidebarCard from '@/components/SidebarCard.vue'

const blogStore = useBlogStore()
const { articleTitle, articleMeta } = inject('setHero')
const info = computed(() => blogStore.personalInfo)

onMounted(() => {
  articleTitle.value = '关于'
  articleMeta.value = '系统构建的长期实践'
})

const keywords = ['工程能力强化', '自动化与 AI 实验', '系统整合']

const skillGroups = [
  {
    title: '编程语言',
    items: 'Python / JavaScript / TypeScript / C'
  },
  {
    title: '前端',
    items: 'Vue / React 基础 / 组件化开发 / Vite 构建'
  },
  {
    title: '后端与服务',
    items: 'Node.js / REST API 设计 / 简单数据库设计'
  },
  {
    title: '工具与系统',
    items: 'Git / VPS 部署 / Nginx / 自动化脚本'
  },
  {
    title: 'AI 方向',
    items: '基础模型应用 / 自动化流程整合 / Prompt 设计'
  }
]

const stages = [
  {
    title: '早期阶段',
    desc: '基础能力构建，系统学习计算机核心知识。'
  },
  {
    title: '实验阶段',
    desc: '开始独立构建工具与插件，尝试自动化流程。'
  },
  {
    title: '整合阶段',
    desc: '将项目、写作与 AI 实验整合为个人系统。'
  },
  {
    title: '当前阶段',
    desc: '优化结构，提升长期运行能力。'
  }
]
</script>

<template>
  <div class="about-page">
    <div class="about-layout">
      <div class="about-inner">
        <div class="about-card">
          <section class="about-section">
            <h3 class="section-title">关于我</h3>
            <div class="section-body">
              <p>我叫李成，网络昵称 LeeDaud。我是一名计算机专业的学生，也是一名系统构建的实践者。</p>
              <p>
                我对“长期主义”这件事有非常实际的兴趣。不是概念层面的认同，而是工程层面的验证：
                如何让一个人、一个系统，在时间中持续运行，而不是阶段性爆发。
              </p>
              <p>过去几年，我在做几件事：构建个人工具与插件、设计自己的数字花园、尝试用 AI 提升效率、记录每一次思考与阶段变化。</p>
              <p>我不追求快速堆叠成果，更关注结构是否稳固。</p>
              <p>我相信成长不是事件，而是系统。</p>
            </div>
          </section>

          <section class="about-section">
            <h3 class="section-title">当前阶段关键词</h3>
            <div class="keyword-list">
              <span v-for="k in keywords" :key="k" class="keyword-item">{{ k }}</span>
            </div>
            <p class="section-note">持续构建，比短期爆发更重要。</p>
          </section>

          <section class="about-section">
            <h3 class="section-title">技能结构</h3>
            <div class="skill-list">
              <div v-for="g in skillGroups" :key="g.title" class="skill-row">
                <div class="skill-title">{{ g.title }}</div>
                <div class="skill-items">{{ g.items }}</div>
              </div>
            </div>
          </section>

          <section class="about-section">
            <h3 class="section-title">阶段经历</h3>
            <div class="stage-list">
              <div v-for="s in stages" :key="s.title" class="stage-item">
                <div class="stage-title">{{ s.title }}</div>
                <div class="stage-desc">{{ s.desc }}</div>
              </div>
            </div>
          </section>

          <section class="about-section about-section--last">
            <h3 class="section-title">联系</h3>
            <div class="section-body">
              <p v-if="info?.email">邮箱：<a :href="`mailto:${info.email}`" class="inline-link">{{ info.email }}</a></p>
              <p>X：<a href="https://x.com/LeeDaud_0212" target="_blank" rel="noopener noreferrer" class="inline-link">@LeeDaud_0212</a></p>
              <p>留言：可在留言板继续交流。</p>
            </div>
          </section>
        </div>
      </div>

      <SidebarCard />
    </div>
  </div>
</template>

<style scoped>
.about-page {
  width: 100%;
}

.about-layout {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.about-inner {
  flex: 1;
  min-width: 0;
}

.about-card {
  background: var(--blog-card, #fff);
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid var(--blog-border-light, #ebeef5);
  overflow: hidden;
}

.about-section {
  padding: 24px 28px;
  border-bottom: 1px solid var(--blog-border-light, #ebeef5);
}

.about-section--last {
  border-bottom: none;
  background: var(--blog-hover, #fafafa);
}

.section-title {
  margin: 0 0 14px;
  font-size: 15px;
  font-weight: 700;
  color: var(--blog-text, #303133);
}

.section-body {
  font-size: 14px;
  color: var(--blog-text2, #606266);
  line-height: 1.9;
}

.section-body p {
  margin: 0 0 10px;
}

.section-body p:last-child {
  margin-bottom: 0;
}

.keyword-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.keyword-item {
  font-size: 12px;
  color: var(--blog-text2, #606266);
  border: 1px solid var(--blog-border, #e4e7ed);
  border-radius: 999px;
  padding: 4px 10px;
}

.section-note {
  margin-top: 12px;
  margin-bottom: 0;
  font-size: 13px;
  color: var(--blog-text3, #909399);
}

.skill-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.skill-row {
  border: 1px solid var(--blog-border, #e4e7ed);
  border-radius: 8px;
  padding: 10px 12px;
}

.skill-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--blog-text, #303133);
  margin-bottom: 4px;
}

.skill-items {
  font-size: 13px;
  color: var(--blog-text2, #606266);
  line-height: 1.7;
}

.stage-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.stage-item {
  border-left: 2px solid var(--blog-border, #e4e7ed);
  padding-left: 10px;
}

.stage-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--blog-text, #303133);
}

.stage-desc {
  margin-top: 4px;
  font-size: 13px;
  color: var(--blog-text2, #606266);
  line-height: 1.7;
}

.inline-link {
  color: var(--blog-text, #303133);
  text-decoration: none;
  border-bottom: 1px dashed var(--blog-border, #e4e7ed);
}

@media (max-width: 960px) {
  .about-layout {
    flex-direction: column;
  }
}

@media (max-width: 600px) {
  .about-section {
    padding: 16px;
  }
}
</style>
