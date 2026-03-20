<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter, onBeforeRouteLeave } from 'vue-router'
import { useArticleStore } from '@/stores'
import { uploadFile } from '@/api/settings'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

const route = useRoute()
const router = useRouter()
const articleStore = useArticleStore()
const isDarkMode = ref(document.documentElement.classList.contains('dark'))
const editorTheme = computed(() => (isDarkMode.value ? 'dark' : 'light'))
const markdownInputRef = ref(null)
let themeObserver = null

const isEdit = computed(() => !!route.params.id)

const form = ref({
  id: null,
  title: '',
  slug: '',
  summary: '',
  coverImage: '',
  categoryId: null,
  tagIds: [],
  contentMarkdown: '',
  contentHtml: '',
  isPublished: 0
})

/* ---- 同步编辑器渲染后的 HTML ---- */
const onHtmlChanged = (html) => {
  form.value.contentHtml = html
}

const saving = ref(false)
const uploadingCover = ref(false)
const importingMarkdown = ref(false)

const generateSlug = (value) =>
  value
    .toLowerCase()
    .replace(/[\u4e00-\u9fff]+/g, '-')
    .replace(/[^a-z0-9-]/g, '-')
    .replace(/-+/g, '-')
    .replace(/^-|-$/g, '')
    .substring(0, 50) || `article-${Date.now()}`

const stripMatchingQuotes = (value) => {
  if (value.length < 2) return value
  const firstChar = value[0]
  const lastChar = value[value.length - 1]
  if (
    (firstChar === '"' && lastChar === '"') ||
    (firstChar === "'" && lastChar === "'")
  ) {
    return value.slice(1, -1)
  }
  return value
}

const parseInlineArrayValue = (value) => {
  const trimmed = value.trim()
  if (!trimmed.startsWith('[') || !trimmed.endsWith(']')) return null
  return trimmed
    .slice(1, -1)
    .split(',')
    .map((item) => stripMatchingQuotes(item.trim()))
    .filter(Boolean)
}

const parseFrontMatter = (text) => {
  const frontMatterMatch = text.match(/^---\r?\n([\s\S]*?)\r?\n---(?:\r?\n|$)/)
  if (!frontMatterMatch) {
    return {
      meta: {},
      content: text
    }
  }

  const meta = {}
  let currentArrayKey = ''
  const arrayLikeKeys = new Set(['tags', 'tag', 'categories', 'category'])

  for (const line of frontMatterMatch[1].split(/\r?\n/)) {
    const trimmed = line.trim()
    if (!trimmed || trimmed.startsWith('#')) continue

    const arrayItemMatch = currentArrayKey && trimmed.match(/^-\s+(.*)$/)
    if (arrayItemMatch) {
      meta[currentArrayKey].push(stripMatchingQuotes(arrayItemMatch[1].trim()))
      continue
    }

    const keyValueMatch = trimmed.match(/^([A-Za-z0-9_-]+)\s*:\s*(.*)$/)
    if (!keyValueMatch) {
      currentArrayKey = ''
      continue
    }

    const key = keyValueMatch[1].trim().toLowerCase()
    const rawValue = keyValueMatch[2].trim()
    const canBeArray = arrayLikeKeys.has(key)

    if (!rawValue) {
      meta[key] = canBeArray ? [] : ''
      currentArrayKey = canBeArray ? key : ''
      continue
    }

    currentArrayKey = ''
    const inlineArrayValue = parseInlineArrayValue(rawValue)
    meta[key] = inlineArrayValue ?? stripMatchingQuotes(rawValue)
  }

  return {
    meta,
    content: text.slice(frontMatterMatch[0].length)
  }
}

const normalizeLookupValue = (value) =>
  String(value ?? '')
    .trim()
    .toLocaleLowerCase()

const toValueList = (value) => {
  if (Array.isArray(value)) {
    return value.map((item) => String(item ?? '').trim()).filter(Boolean)
  }
  if (typeof value !== 'string') return []
  return value
    .split(',')
    .map((item) => stripMatchingQuotes(item.trim()))
    .filter(Boolean)
}

const findCategoryId = (value) => {
  const candidates = toValueList(value).map(normalizeLookupValue)
  if (!candidates.length) return null

  const matchedCategory = articleStore.categories.find((item) =>
    candidates.some((candidate) => {
      const name = normalizeLookupValue(item.name)
      const slug = normalizeLookupValue(item.slug)
      return candidate === name || candidate === slug
    })
  )

  return matchedCategory?.id ?? null
}

const findTagIds = (value) => {
  const candidates = toValueList(value).map(normalizeLookupValue)
  if (!candidates.length) return []

  return [
    ...new Set(
      articleStore.tags
        .filter((item) =>
          candidates.some((candidate) => {
            const name = normalizeLookupValue(item.name)
            const slug = normalizeLookupValue(item.slug)
            return candidate === name || candidate === slug
          })
        )
        .map((item) => item.id)
    )
  ]
}

const applyImportedMarkdown = (rawText, fileName) => {
  const normalizedText = rawText.replace(/^\uFEFF/, '')
  const { meta, content } = parseFrontMatter(normalizedText)
  const fallbackTitle = fileName.replace(/\.[^.]+$/, '').trim()
  const importedTitle = String(meta.title ?? meta.name ?? '').trim()
  const importedSlug = String(meta.slug ?? meta.permalink ?? '').trim()
  const importedSummary = String(
    meta.summary ?? meta.description ?? meta.excerpt ?? ''
  ).trim()
  const importedCoverImage = String(
    meta.coverimage ??
      meta.cover_image ??
      meta.cover ??
      meta.banner ??
      meta.thumbnail ??
      ''
  ).trim()
  const importedCategoryId = findCategoryId(meta.category ?? meta.categories)
  const importedTagIds = findTagIds(meta.tags ?? meta.tag)

  if (importedTitle) {
    form.value.title = importedTitle
  } else if (!form.value.title.trim() && fallbackTitle) {
    form.value.title = fallbackTitle
  }

  if (importedSlug) {
    form.value.slug = importedSlug
  } else if (!form.value.slug.trim() && form.value.title.trim()) {
    form.value.slug = generateSlug(form.value.title)
  }

  if (importedSummary) {
    form.value.summary = importedSummary
  }

  if (importedCoverImage) {
    form.value.coverImage = importedCoverImage
  }

  if (importedCategoryId !== null) {
    form.value.categoryId = importedCategoryId
  }

  if (importedTagIds.length) {
    form.value.tagIds = importedTagIds
  }

  form.value.contentMarkdown = content
  form.value.contentHtml = ''
}

const triggerMarkdownImport = () => {
  markdownInputRef.value?.click()
}

const shouldConfirmMarkdownImport = () =>
  Boolean(
    form.value.title.trim() ||
      form.value.summary.trim() ||
      form.value.coverImage.trim() ||
      form.value.categoryId ||
      form.value.tagIds.length ||
      form.value.contentMarkdown.trim()
  )

const handleMarkdownImport = async (event) => {
  const input = event.target
  const [file] = Array.from(input.files || [])
  if (!file) return

  try {
    if (shouldConfirmMarkdownImport()) {
      await ElMessageBox.confirm(
        '导入 .md 会替换当前正文，并尝试用 Front Matter 填充标题、摘要、封面、分类和标签，是否继续？',
        '确认导入',
        {
          confirmButtonText: '继续导入',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
    }

    importingMarkdown.value = true
    applyImportedMarkdown(await file.text(), file.name)
    ElMessage.success('Markdown 导入成功')
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error('Markdown 导入失败，请确认文件编码为 UTF-8')
    }
  } finally {
    importingMarkdown.value = false
    input.value = ''
  }
}

/* ---- 图片上传（md-editor-v3 回调格式） ---- */
const onUploadImg = async (files, callback) => {
  try {
    const urls = await Promise.all(
      files.map(async (file) => {
        const fd = new FormData()
        fd.append('file', file)
        const res = await uploadFile(fd)
        return res.data
      })
    )
    callback(urls)
    ElMessage.success('图片上传成功')
  } catch {
    ElMessage.error('图片上传失败')
    callback([])
  }
}

/* ---- 封面上传 ---- */
const handleCoverUpload = async (options) => {
  uploadingCover.value = true
  try {
    const fd = new FormData()
    fd.append('file', options.file)
    const res = await uploadFile(fd)
    form.value.coverImage = res.data
    ElMessage.success('封面上传成功')
  } finally {
    uploadingCover.value = false
  }
}

/* ---- 标题失焦自动生成 slug ---- */
const autoSlug = () => {
  if (form.value.title && !form.value.slug) {
    form.value.slug = generateSlug(form.value.title)
  }
}

/* ---- 保存 / 发布 ---- */
const isSaved = ref(false)
const handleSave = async (isPublished) => {
  if (!form.value.title.trim()) return ElMessage.warning('请输入文章标题')
  if (!form.value.slug.trim())
    return ElMessage.warning('请输入 URL 标识 (Slug)')
  if (!form.value.contentMarkdown.trim())
    return ElMessage.warning('请输入文章内容')
  if (!form.value.categoryId) return ElMessage.warning('请选择文章分类')
  saving.value = true
  try {
    form.value.isPublished = isPublished
    await articleStore.saveArticle({ ...form.value })
    isSaved.value = true
    ElMessage.success(isPublished ? '发布成功' : '保存草稿成功')
    router.push('/article/list')
  } finally {
    saving.value = false
  }
}

/* ---- 内容变更检测 & 离开提示 ---- */
const initialSnapshot = ref('')
const hasUnsavedChanges = () => {
  if (isSaved.value) return false
  const current = JSON.stringify({
    title: form.value.title,
    slug: form.value.slug,
    summary: form.value.summary,
    coverImage: form.value.coverImage,
    categoryId: form.value.categoryId,
    tagIds: form.value.tagIds,
    contentMarkdown: form.value.contentMarkdown
  })
  return current !== initialSnapshot.value
}

const takeSnapshot = () => {
  initialSnapshot.value = JSON.stringify({
    title: form.value.title,
    slug: form.value.slug,
    summary: form.value.summary,
    coverImage: form.value.coverImage,
    categoryId: form.value.categoryId,
    tagIds: form.value.tagIds,
    contentMarkdown: form.value.contentMarkdown
  })
}

onBeforeRouteLeave(async () => {
  if (!hasUnsavedChanges()) return true
  try {
    await ElMessageBox.confirm('你有未保存的内容，是否保存为草稿？', '提示', {
      confirmButtonText: '保存草稿',
      cancelButtonText: '不保存',
      distinguishCancelAndClose: true,
      type: 'warning'
    })
    // 用户点击保存草稿
    await handleSave(0)
    return true
  } catch (action) {
    if (action === 'cancel') {
      // 用户点击不保存，直接离开
      return true
    }
    // 用户点击关闭按钮，留在当前页面
    return false
  }
})

onMounted(async () => {
  themeObserver = new MutationObserver(() => {
    isDarkMode.value = document.documentElement.classList.contains('dark')
  })
  themeObserver.observe(document.documentElement, {
    attributes: true,
    attributeFilter: ['class']
  })

  await Promise.all([articleStore.fetchCategories(), articleStore.fetchTags()])
  if (isEdit.value) {
    const res = await articleStore.fetchDetail(route.params.id)
    if (res) {
      Object.assign(form.value, {
        id: res.id,
        title: res.title ?? '',
        slug: res.slug ?? '',
        summary: res.summary ?? '',
        coverImage: res.coverImage ?? '',
        categoryId: res.categoryId,
        tagIds: res.tagIds ?? [],
        contentMarkdown: res.contentMarkdown || res.contentHtml || '',
        isPublished: res.isPublished ?? 0
      })
    }
  }
  takeSnapshot()
})

onUnmounted(() => {
  if (themeObserver) {
    themeObserver.disconnect()
    themeObserver = null
  }
})
</script>

<template>
  <div class="article-edit">
    <input
      ref="markdownInputRef"
      type="file"
      accept=".md,.markdown,text/markdown,text/plain"
      hidden
      @change="handleMarkdownImport"
    />

    <!-- 顶部操作栏 -->
    <div class="edit-topbar">
      <span class="edit-title">{{ isEdit ? '编辑文章' : '新建文章' }}</span>

      <div class="edit-actions">
        <el-button
          size="small"
          :loading="importingMarkdown"
          @click="triggerMarkdownImport"
          >导入 .md</el-button
        >
        <el-button size="small" @click="router.push('/article/list')"
          >取消</el-button
        >
        <el-button size="small" :loading="saving" @click="handleSave(0)"
          >保存草稿</el-button
        >
        <el-button
          size="small"
          type="primary"
          :loading="saving"
          @click="handleSave(1)"
          >发布</el-button
        >
      </div>
    </div>

    <!-- 标题行 -->
    <div class="title-row">
      <el-input
        v-model="form.title"
        placeholder="请输入文章标题…"
        class="title-input"
        size="large"
        @blur="autoSlug"
      />
    </div>

    <!-- 主体区域 -->
    <div class="edit-body">
      <!-- Markdown 编辑器 -->
      <div class="editor-panel">
        <MdEditor
          v-model="form.contentMarkdown"
          :theme="editorTheme"
          preview-theme="github"
          :toolbars-exclude="['mermaid', 'katex', 'github']"
          class="md-editor-fill"
          @on-upload-img="onUploadImg"
          @on-html-changed="onHtmlChanged"
        />
      </div>

      <!-- 元数据侧边栏 -->
      <aside class="edit-aside">
        <div class="aside-section">
          <div class="aside-label">Slug <span class="req">*</span></div>
          <el-input
            v-model="form.slug"
            placeholder="URL 路径标识"
            clearable
            size="small"
          />
        </div>

        <div class="aside-section">
          <div class="aside-label">摘要</div>
          <el-input
            v-model="form.summary"
            type="textarea"
            :rows="3"
            placeholder="文章摘要（选填）"
            size="small"
          />
        </div>

        <div class="aside-section">
          <div class="aside-label">分类 <span class="req">*</span></div>
          <el-select
            v-model="form.categoryId"
            placeholder="选择分类"
            style="width: 100%"
            size="small"
          >
            <el-option
              v-for="c in articleStore.categories"
              :key="c.id"
              :label="c.name"
              :value="c.id"
            />
          </el-select>
        </div>

        <div class="aside-section">
          <div class="aside-label">标签</div>
          <el-select
            v-model="form.tagIds"
            multiple
            placeholder="选择标签"
            style="width: 100%"
            size="small"
          >
            <el-option
              v-for="t in articleStore.tags"
              :key="t.id"
              :label="t.name"
              :value="t.id"
            />
          </el-select>
        </div>

        <div class="aside-section">
          <div class="aside-label">封面图</div>
          <el-upload
            :show-file-list="false"
            :http-request="handleCoverUpload"
            accept="image/*"
            class="cover-uploader"
          >
            <img
              v-if="form.coverImage"
              :src="form.coverImage"
              class="cover-preview"
            />
            <div v-else class="cover-placeholder">
              <span class="iconfont icon-image-fill" />
              <span>点击上传</span>
            </div>
          </el-upload>
          <el-input
            v-model="form.coverImage"
            placeholder="或直接输入图片 URL"
            clearable
            size="small"
            style="margin-top: 6px"
          />
        </div>
      </aside>
    </div>
  </div>
</template>

<style scoped>
.article-edit {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: var(--admin-bg);
}

/* ---- 顶栏 ---- */
.edit-topbar {
  background: var(--admin-card);
  border-bottom: 1px solid var(--admin-border);
  padding: 8px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
}
.edit-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--admin-text);
}
.edit-actions {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* ---- 标题行 ---- */
.title-row {
  background: var(--admin-card);
  padding: 10px 16px;
  border-bottom: 1px solid var(--admin-border);
  flex-shrink: 0;
}
.title-input :deep(.el-input__inner) {
  font-size: 20px;
  font-weight: 600;
  border: none;
  box-shadow: none;
  padding-left: 0;
}

/* ---- 主体 ---- */
.edit-body {
  display: flex;
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

/* 编辑器面板 */
.editor-panel {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* md-editor-v3 填满面板高度 */
.md-editor-fill {
  flex: 1;
  height: 100% !important;
}

/* 去除编辑器默认边框，融入整体风格 */
.editor-panel :deep(.md-editor) {
  border: none;
  border-radius: 0;
  height: 100%;
  background: var(--admin-card);
  --md-color: var(--admin-text);
  --md-hover-color: var(--admin-text);
  --md-bk-color: var(--admin-card);
  --md-bk-color-outstand: var(--admin-soft-bg);
  --md-bk-hover-color: var(--admin-hover);
  --md-border-color: var(--admin-border);
  --md-border-hover-color: var(--admin-text3);
  --md-border-active-color: #2f6cff;
  --md-scrollbar-bg-color: var(--admin-soft-bg);
  --md-scrollbar-thumb-color: var(--admin-border);
  --md-scrollbar-thumb-hover-color: var(--admin-text3);
  --md-scrollbar-thumb-active-color: var(--admin-text2);
}
.editor-panel :deep(.md-editor-toolbar-wrapper) {
  border-bottom: 1px solid var(--admin-border);
  background: var(--admin-soft-bg);
}
.editor-panel :deep(.md-editor-input-wrapper),
.editor-panel :deep(.md-editor-preview-wrapper) {
  background: var(--admin-card);
}
.editor-panel :deep(.md-editor-input),
.editor-panel :deep(.md-editor-preview) {
  background: transparent;
  color: var(--admin-text);
}
.editor-panel :deep(.md-editor-content) {
  font-family: 'PingFang SC', 'Microsoft YaHei', 'Noto Sans SC', sans-serif;
}
.editor-panel :deep(.md-editor-footer) {
  background: var(--admin-soft-bg);
  border-top: 1px solid var(--admin-border);
}
.editor-panel :deep(.md-editor-preview blockquote) {
  background: var(--admin-soft-bg);
  border-inline-start-color: var(--admin-border);
  color: var(--admin-text2);
}
.editor-panel :deep(.md-editor-preview pre),
.editor-panel :deep(.md-editor-preview code) {
  background: var(--admin-soft-bg);
}

/* 侧边栏 */
.edit-aside {
  width: 230px;
  flex-shrink: 0;
  overflow-y: auto;
  background: var(--admin-card);
  border-left: 1px solid var(--admin-border);
  padding: 14px 12px;
}
.aside-section {
  margin-bottom: 18px;
}
.aside-label {
  font-size: 12px;
  font-weight: 600;
  color: var(--admin-text2);
  margin-bottom: 5px;
}
.req {
  color: #f56c6c;
}

/* 封面上传 */
.cover-uploader {
  width: 100%;
}
.cover-preview {
  width: 100%;
  border-radius: 6px;
  object-fit: cover;
  cursor: pointer;
  max-height: 110px;
}
.cover-placeholder {
  width: 100%;
  height: 78px;
  border: 1px dashed var(--admin-border);
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  color: var(--admin-text3);
  font-size: 12px;
  cursor: pointer;
  transition:
    border-color 0.2s,
    color 0.2s;
}
.cover-placeholder:hover {
  border-color: var(--admin-text2);
  color: var(--admin-text);
}
.cover-placeholder .iconfont {
  font-size: 22px;
}

:global(html.dark) .article-edit :deep(.md-editor-preview),
:global(html.dark) .article-edit :deep(.md-editor-input-wrapper),
:global(html.dark) .article-edit :deep(.md-editor-preview-wrapper),
:global(html.dark) .article-edit :deep(.md-editor-input) {
  background: var(--admin-card);
  color: var(--admin-text);
}
</style>
