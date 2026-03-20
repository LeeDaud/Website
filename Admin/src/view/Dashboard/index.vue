<script setup>
import { ref, onMounted, onBeforeUnmount, shallowRef } from 'vue'
import {
  getOverview,
  getViewStatistics,
  getVisitorStatistics,
  getArticleViewTop10,
  getProvinceDistribution
} from '@/api/report'
import { getConfigByKey } from '@/api/settings'
import * as echarts from 'echarts/core'
import { LineChart, BarChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import dayjs from 'dayjs'

echarts.use([
  LineChart,
  BarChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent,
  CanvasRenderer
])

/* 概览数据 */
const overview = ref({})
const loadingOverview = ref(false)

const statCards = [
  { key: 'totalViewCount', label: '总浏览量', icon: 'icon-eye' },
  { key: 'totalVisitorCount', label: '总访客数', icon: 'icon-user' },
  { key: 'todayViewCount', label: '今日浏览', icon: 'icon-today' },
  { key: 'todayNewVisitorCount', label: '今日新访客', icon: 'icon-new' },
  {
    key: 'totalArticleCount',
    label: '文章总数',
    icon: 'icon-bianjiwenzhang_huaban'
  },
  { key: 'totalCommentCount', label: '评论总数', icon: 'icon-comment' },
  { key: 'pendingCommentCount', label: '待审评论', icon: 'icon-pending' }
]

const fetchOverview = async () => {
  loadingOverview.value = true
  try {
    const res = await getOverview()
    overview.value = res.data ?? {}
  } finally {
    loadingOverview.value = false
  }
}

/* 运行天数 */
const runDays = ref(null)

const parseStartDate = (value) => {
  const raw = (value ?? '').toString().trim()
  if (!raw) return null

  // 兼容 YYYY-MM 输入，默认按当月 1 号计算
  const normalized = /^\d{4}-\d{2}$/.test(raw) ? `${raw}-01` : raw
  const parsed = dayjs(normalized)
  return parsed.isValid() ? parsed.startOf('day') : null
}

const fetchRunDays = async () => {
  runDays.value = null
  try {
    const res = await getConfigByKey('start-time')
    const dateStr = res.data?.configValue ?? ''
    const start = parseStartDate(dateStr)
    if (!start) return

    const diff = dayjs().startOf('day').diff(start, 'day')
    runDays.value = Number.isFinite(diff) ? Math.max(0, diff) : null
  } catch (e) {
    console.error('获取运行天数失败', e)
  }
}

/* 图表配置 */
const viewChartEl = ref(null)
const visitorChartEl = ref(null)
const barChartEl = ref(null)
const pieChartEl = ref(null)
const viewChart = shallowRef(null)
const visitorChart = shallowRef(null)
const barChart = shallowRef(null)
const pieChart = shallowRef(null)

const makeShortcuts = () => [
  {
    text: '最近 7 天',
    value: () => [dayjs().subtract(6, 'day').toDate(), new Date()]
  },
  {
    text: '最近 30 天',
    value: () => [dayjs().subtract(29, 'day').toDate(), new Date()]
  }
]

const defaultRange = () => [
  dayjs().subtract(6, 'day').format('YYYY-MM-DD'),
  dayjs().format('YYYY-MM-DD')
]

const viewDateRange = ref(defaultRange())
const visitorDateRange = ref(defaultRange())

const splitStr = (s) => (s ? s.split(',') : [])
const splitNum = (s) => (s ? s.split(',').map(Number) : [])

const isDarkMode = () => document.documentElement.classList.contains('dark')

const getChartPalette = () => {
  if (isDarkMode()) {
    return {
      axisLine: '#3b4250',
      splitLine: '#2a2f3a',
      axisText: '#aab2bf',
      legendText: '#c3c8d1',
      linePrimary: '#8ab4ff',
      lineSecondary: '#a9b4c6',
      bar: '#9aa6b8',
      pieBorder: '#171a21',
      pie: ['#8ab4ff', '#9aa6b8', '#b8c0cc', '#7f8b9f', '#6d7890', '#5a6478']
    }
  }
  return {
    axisLine: '#e4e7ed',
    splitLine: '#f0f0f0',
    axisText: '#909399',
    legendText: '#606266',
    linePrimary: '#000000',
    lineSecondary: '#606266',
    bar: '#303133',
    pieBorder: '#ffffff',
    pie: ['#303133', '#606266', '#909399', '#a8abb2', '#c0c4cc', '#d4d7de']
  }
}

const makeLineOption = (title, categories, data, color) => {
  const p = getChartPalette()
  return {
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 30, bottom: 30 },
    xAxis: {
      type: 'category',
      data: categories,
      axisLine: { lineStyle: { color: p.axisLine } },
      axisLabel: { color: p.axisText, fontSize: 12 }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: p.splitLine } },
      axisLabel: { color: p.axisText, fontSize: 12 }
    },
    series: [
      {
        name: title,
        type: 'line',
        data,
        smooth: true,
        symbol: 'circle',
        symbolSize: 5,
        lineStyle: { color, width: 2 },
        itemStyle: { color },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: color + '33' },
              { offset: 1, color: color + '00' }
            ]
          }
        }
      }
    ]
  }
}

const fetchViewChart = async () => {
  const [begin, end] = viewDateRange.value
  const res = await getViewStatistics({ begin, end })
  const vo = res.data ?? {}
  const p = getChartPalette()
  viewChart.value?.setOption(
    makeLineOption(
      '浏览量',
      splitStr(vo.dateList),
      splitNum(vo.viewCountList),
      p.linePrimary
    )
  )
}

const fetchVisitorChart = async () => {
  const [begin, end] = visitorDateRange.value
  const res = await getVisitorStatistics({ begin, end })
  const vo = res.data ?? {}
  const p = getChartPalette()
  visitorChart.value?.setOption(
    makeLineOption(
      '访客数',
      splitStr(vo.dateList),
      splitNum(vo.newVisitorCountList),
      p.lineSecondary
    )
  )
}

const fetchBarChart = async () => {
  const res = await getArticleViewTop10()
  const vo = res.data ?? {}
  const titles = (vo.titleList ?? []).slice(0, 10).reverse()
  const counts = (vo.viewCountList ?? []).slice(0, 10).reverse()
  const p = getChartPalette()

  barChart.value?.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 130, right: 20, top: 16, bottom: 24 },
    xAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: p.splitLine } },
      axisLabel: { color: p.axisText, fontSize: 11 }
    },
    yAxis: {
      type: 'category',
      data: titles.map((t) => (t && t.length > 14 ? t.slice(0, 14) + '…' : t)),
      axisLabel: { color: p.legendText, fontSize: 12 },
      axisLine: { lineStyle: { color: p.axisLine } }
    },
    series: [
      {
        name: '阅读量',
        type: 'bar',
        data: counts,
        barMaxWidth: 20,
        itemStyle: { color: p.bar, borderRadius: [0, 4, 4, 0] }
      }
    ]
  })
}

const fetchPieChart = async () => {
  const res = await getProvinceDistribution()
  const vo = res.data ?? {}
  const provinces = splitStr(vo.provinceList)
  const counts = splitNum(vo.countList)
  const pieData = provinces.map((name, i) => ({
    name: name || '未知',
    value: counts[i] ?? 0
  }))
  const p = getChartPalette()

  pieChart.value?.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    color: p.pie,
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      textStyle: { color: p.legendText, fontSize: 12 },
      icon: 'circle'
    },
    series: [
      {
        name: '访客省份',
        type: 'pie',
        radius: ['40%', '68%'],
        center: ['38%', '50%'],
        avoidLabelOverlap: true,
        label: { show: false },
        labelLine: { show: false },
        data: pieData,
        itemStyle: { borderColor: p.pieBorder, borderWidth: 2 }
      }
    ]
  })
}

const refreshAllCharts = () => {
  fetchViewChart()
  fetchVisitorChart()
  fetchBarChart()
  fetchPieChart()
}

const resizeCharts = () => {
  viewChart.value?.resize()
  visitorChart.value?.resize()
  barChart.value?.resize()
  pieChart.value?.resize()
}

const initCharts = () => {
  viewChart.value = echarts.init(viewChartEl.value)
  visitorChart.value = echarts.init(visitorChartEl.value)
  barChart.value = echarts.init(barChartEl.value)
  pieChart.value = echarts.init(pieChartEl.value)
  refreshAllCharts()
}

onMounted(() => {
  fetchOverview()
  fetchRunDays()
  initCharts()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  viewChart.value?.dispose()
  visitorChart.value?.dispose()
  barChart.value?.dispose()
  pieChart.value?.dispose()
})
</script>

<template>
  <div class="dashboard">
    <div v-loading="loadingOverview" class="stat-grid">
      <div v-for="card in statCards" :key="card.key" class="stat-card">
        <div class="stat-icon">
          <span :class="['iconfont', card.icon]" />
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ overview[card.key] ?? '-' }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </div>
      </div>
    </div>

    <div class="chart-row">
      <div class="chart-card">
        <div class="chart-header">
          <span class="chart-title">浏览量趋势</span>
          <el-date-picker
            v-model="viewDateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :shortcuts="makeShortcuts()"
            size="small"
            @change="fetchViewChart"
          />
        </div>
        <div ref="viewChartEl" class="chart-body" />
      </div>

      <div class="chart-card">
        <div class="chart-header">
          <span class="chart-title">访客数趋势</span>
          <el-date-picker
            v-model="visitorDateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :shortcuts="makeShortcuts()"
            size="small"
            @change="fetchVisitorChart"
          />
        </div>
        <div ref="visitorChartEl" class="chart-body" />
      </div>
    </div>

    <div class="chart-row">
      <div class="chart-card">
        <div class="chart-header">
          <span class="chart-title">阅读量 TOP 10</span>
        </div>
        <div ref="barChartEl" class="chart-body" />
      </div>

      <div class="chart-card">
        <div class="chart-header">
          <span class="chart-title">访客省份分布</span>
        </div>
        <div ref="pieChartEl" class="chart-body" />
      </div>
    </div>

    <div v-if="runDays !== null" class="run-banner">
      <div class="run-banner-inner">
        <span class="run-icon iconfont icon-time" />
        <span class="run-text">本站已稳定运行</span>
        <span class="run-days">{{ runDays }}</span>
        <span class="run-unit">天</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 16px;
}

.stat-card {
  background: var(--admin-card);
  border: 1px solid var(--admin-border);
  border-radius: 8px;
  padding: 20px 16px;
  display: flex;
  align-items: center;
  gap: 14px;
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  background-color: var(--admin-soft-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon .iconfont {
  font-size: 22px;
  color: var(--admin-text);
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: var(--admin-text);
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: var(--admin-text3);
  margin-top: 4px;
}

.run-banner {
  background: var(--admin-card);
  border: 1px solid var(--admin-border);
  border-radius: 8px;
  padding: 10px 28px;
}

.run-banner-inner {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.run-icon {
  font-size: 20px;
  color: var(--admin-text);
  margin-right: 2px;
}

.run-text {
  font-size: 15px;
  color: var(--admin-text2);
}

.run-days {
  font-size: 20px;
  font-weight: 700;
  color: var(--admin-text);
  line-height: 1;
  margin: 0 2px;
}

.run-unit {
  font-size: 15px;
  color: var(--admin-text2);
}

.chart-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

@media (max-width: 900px) {
  .chart-row {
    grid-template-columns: 1fr;
  }
}

.chart-card {
  background: var(--admin-card);
  border: 1px solid var(--admin-border);
  border-radius: 8px;
  padding: 20px;
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 8px;
}

.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--admin-text);
}

.chart-body {
  height: 260px;
}
</style>
