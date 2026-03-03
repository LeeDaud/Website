<script setup>
import { onMounted, ref } from 'vue'
import { useCategoryStore } from '@/stores'

const categoryStore = useCategoryStore()

const activeTab = ref('category')

const catDialogVisible = ref(false)
const catEditing = ref(false)
const catForm = ref({
  id: null,
  name: '',
  slug: '',
  description: '',
  sort: null
})

const tagDialogVisible = ref(false)
const tagEditing = ref(false)
const tagForm = ref({ id: null, name: '', slug: '' })

const openCatDialog = (row = null) => {
  catEditing.value = !!row
  catForm.value = row
    ? {
        id: row.id,
        name: row.name,
        slug: row.slug ?? '',
        description: row.description ?? '',
        sort: row.sort ?? null
      }
    : { id: null, name: '', slug: '', description: '', sort: null }
  catDialogVisible.value = true
}

const saveCat = async () => {
  const name = catForm.value.name.trim()
  const slug = catForm.value.slug.trim()
  if (!name) return ElMessage.warning('分类名称不能为空')
  if (!slug) return ElMessage.warning('Slug 不能为空')

  await categoryStore.saveCategory({
    ...catForm.value,
    name,
    slug
  })
  ElMessage.success(catEditing.value ? '分类更新成功' : '分类创建成功')
  catDialogVisible.value = false
}

const deleteCat = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除分类「${row.name}」？`, '警告', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  await categoryStore.removeCategories([row.id])
  ElMessage.success('分类删除成功')
}

const openTagDialog = (row = null) => {
  tagEditing.value = !!row
  tagForm.value = row
    ? {
        id: row.id,
        name: row.name,
        slug: row.slug ?? ''
      }
    : { id: null, name: '', slug: '' }
  tagDialogVisible.value = true
}

const saveTag = async () => {
  const name = tagForm.value.name.trim()
  const slug = tagForm.value.slug.trim()
  if (!name) return ElMessage.warning('标签名称不能为空')
  if (!slug) return ElMessage.warning('Slug 不能为空')

  await categoryStore.saveTag({
    ...tagForm.value,
    name,
    slug
  })
  ElMessage.success(tagEditing.value ? '标签更新成功' : '标签创建成功')
  tagDialogVisible.value = false
}

const deleteTag = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除标签「${row.name}」？`, '警告', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  await categoryStore.removeTags([row.id])
  ElMessage.success('标签删除成功')
}

onMounted(() => {
  categoryStore.fetchCategories()
  categoryStore.fetchTags()
})
</script>

<template>
  <div class="category-page">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="分类管理" name="category">
        <div class="tab-toolbar">
          <el-button type="primary" @click="openCatDialog()">
            <span class="iconfont icon-plus" />
            新建分类
          </el-button>
        </div>
        <el-table
          :data="categoryStore.categories"
          border
          stripe
          v-loading="categoryStore.loading"
        >
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="name" label="分类名称" min-width="140" />
          <el-table-column prop="slug" label="Slug" min-width="140" />
          <el-table-column
            prop="description"
            label="描述"
            min-width="200"
            show-overflow-tooltip
          />
          <el-table-column prop="sort" label="排序" width="90" align="center" />
          <el-table-column label="操作" width="160" align="center">
            <template #default="{ row }">
              <el-button link size="small" @click="openCatDialog(row)"
                >编辑</el-button
              >
              <el-divider direction="vertical" />
              <el-button
                link
                size="small"
                type="danger"
                @click="deleteCat(row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="标签管理" name="tag">
        <div class="tab-toolbar">
          <el-button type="primary" @click="openTagDialog()">
            <span class="iconfont icon-plus" />
            新建标签
          </el-button>
        </div>
        <el-table
          :data="categoryStore.tags"
          border
          stripe
          v-loading="categoryStore.loading"
        >
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="name" label="标签名称" min-width="180" />
          <el-table-column prop="slug" label="Slug" min-width="220" />
          <el-table-column label="操作" width="160" align="center">
            <template #default="{ row }">
              <el-button link size="small" @click="openTagDialog(row)"
                >编辑</el-button
              >
              <el-divider direction="vertical" />
              <el-button
                link
                size="small"
                type="danger"
                @click="deleteTag(row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog
      v-model="catDialogVisible"
      :title="catEditing ? '编辑分类' : '新建分类'"
      width="460px"
      :close-on-click-modal="false"
    >
      <el-form :model="catForm" label-width="84px">
        <el-form-item label="名称" required>
          <el-input
            v-model="catForm.name"
            placeholder="请输入分类名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="Slug" required>
          <el-input
            v-model="catForm.slug"
            placeholder="请输入 URL 标识，如 tech"
            clearable
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="catForm.description"
            placeholder="分类描述（可选）"
            clearable
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number
            v-model="catForm.sort"
            :min="0"
            :precision="0"
            controls-position="right"
            style="width: 130px"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="catDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCat">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="tagDialogVisible"
      :title="tagEditing ? '编辑标签' : '新建标签'"
      width="420px"
      :close-on-click-modal="false"
    >
      <el-form :model="tagForm" label-width="84px">
        <el-form-item label="名称" required>
          <el-input
            v-model="tagForm.name"
            placeholder="请输入标签名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="Slug" required>
          <el-input
            v-model="tagForm.slug"
            placeholder="请输入 URL 标识，如 vue-js"
            clearable
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="tagDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTag">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.category-page {
  background: var(--admin-card);
  border: 1px solid var(--admin-border);
  border-radius: 8px;
  padding: 20px;
  color: var(--admin-text);
}

.tab-toolbar {
  margin-bottom: 14px;
}

.tab-toolbar .iconfont {
  font-size: 14px;
  margin-right: 4px;
}
</style>
