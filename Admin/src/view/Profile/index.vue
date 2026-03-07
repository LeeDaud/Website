<script setup>
import { onMounted, ref } from 'vue'
import { useProfileStore } from '@/stores'
import { getPersonalInfo, updatePersonalInfo, uploadFile } from '@/api/settings'

const profileStore = useProfileStore()

const activeTab = ref('personal')

const personalForm = ref({
  nickname: '',
  tag: '',
  description: '',
  avatar: '',
  email: '',
  website: '',
  github: '',
  location: ''
})
const savingPersonal = ref(false)

const expFilter = ref('')
const expDialogVisible = ref(false)
const expEditing = ref(false)
const expSaving = ref(false)
const uploadingLogo = ref(false)

const expForm = ref({
  id: null,
  type: 0,
  title: '',
  subtitle: '',
  logoUrl: '',
  content: '',
  projectLink: '',
  startDate: '',
  endDate: '',
  isVisible: 1
})

const skillDialogVisible = ref(false)
const skillEditing = ref(false)
const skillSaving = ref(false)
const uploadingSkillIcon = ref(false)
const skillForm = ref({
  id: null,
  name: '',
  description: '',
  icon: '',
  sort: null,
  isVisible: 1
})

const socialDialogVisible = ref(false)
const socialEditing = ref(false)
const socialSaving = ref(false)
const socialForm = ref({
  id: null,
  name: '',
  icon: '',
  link: '',
  sort: null,
  isVisible: 1
})

const normalizeExternalUrl = (url) => {
  const value = (url || '').trim()
  if (!value) return ''
  return /^https?:\/\//i.test(value) ? value : `https://${value}`
}

const normalizeExperienceDate = (value, { required = false, fieldName = '日期' } = {}) => {
  const raw = (value ?? '').toString().trim()
  if (!raw) {
    if (required) {
      return { error: `${fieldName}不能为空` }
    }
    return { value: null }
  }
  if (/^\d{4}-\d{2}-\d{2}$/.test(raw)) {
    return { value: raw }
  }
  if (/^\d{4}-\d{2}$/.test(raw)) {
    return { value: `${raw}-01` }
  }
  return { error: `${fieldName}格式应为 YYYY-MM 或 YYYY-MM-DD` }
}

const getExpTypeLabel = (type) => {
  const value = Number(type)
  if (value === 0) return '教育'
  if (value === 1) return '工作'
  return '项目'
}

const fetchPersonal = async () => {
  const res = await getPersonalInfo()
  Object.assign(personalForm.value, res.data ?? {})
}

const savePersonal = async () => {
  savingPersonal.value = true
  try {
    await updatePersonalInfo({ ...personalForm.value })
    ElMessage.success('个人信息保存成功')
  } finally {
    savingPersonal.value = false
  }
}

const handleAvatarUpload = async (options) => {
  const fd = new FormData()
  fd.append('file', options.file)
  const res = await uploadFile(fd)
  personalForm.value.avatar = res.data
  ElMessage.success('头像上传成功')
}

const loadExperiences = async () => {
  const type = expFilter.value === '' ? undefined : Number(expFilter.value)
  await profileStore.fetchExperiences(type)
}

const openExpDialog = (row = null) => {
  expEditing.value = !!row
  expForm.value = row
    ? {
        id: row.id,
        type: Number(row.type ?? 0),
        title: row.title,
        subtitle: row.subtitle ?? '',
        logoUrl: row.logoUrl ?? '',
        content: row.content ?? '',
        projectLink: row.projectLink ?? '',
        startDate: row.startDate ?? '',
        endDate: row.endDate ?? '',
        isVisible: row.isVisible ?? 1
      }
    : {
        id: null,
        type: 0,
        title: '',
        subtitle: '',
        logoUrl: '',
        content: '',
        projectLink: '',
        startDate: '',
        endDate: '',
        isVisible: 1
      }
  expDialogVisible.value = true
}

const handleLogoUpload = async (options) => {
  uploadingLogo.value = true
  try {
    const fd = new FormData()
    fd.append('file', options.file)
    const res = await uploadFile(fd)
    expForm.value.logoUrl = res.data
    ElMessage.success('Logo 上传成功')
  } finally {
    uploadingLogo.value = false
  }
}

const handleExpSave = async () => {
  const title = expForm.value.title.trim()
  const content = expForm.value.content.trim()
  if (!title) return ElMessage.warning('标题不能为空')
  if (!content) return ElMessage.warning('内容不能为空')

  const normalizedStartDate = normalizeExperienceDate(expForm.value.startDate, {
    required: true,
    fieldName: '开始时间'
  })
  if (normalizedStartDate.error) return ElMessage.warning(normalizedStartDate.error)

  const normalizedEndDate = normalizeExperienceDate(expForm.value.endDate, {
    required: false,
    fieldName: '结束时间'
  })
  if (normalizedEndDate.error) return ElMessage.warning(normalizedEndDate.error)

  expSaving.value = true
  try {
    const payload = {
      ...expForm.value,
      type: Number(expForm.value.type),
      title,
      content,
      startDate: normalizedStartDate.value,
      endDate: normalizedEndDate.value
    }
    if (payload.type !== 2) {
      payload.projectLink = ''
    } else {
      payload.projectLink = normalizeExternalUrl(payload.projectLink)
    }
    await profileStore.saveExperience(payload)
    ElMessage.success(expEditing.value ? '经历更新成功' : '经历创建成功')
    expDialogVisible.value = false
    await loadExperiences()
  } finally {
    expSaving.value = false
  }
}

const deleteExp = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除「${row.title}」？`, '警告', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  await profileStore.removeExperiences([row.id])
  ElMessage.success('删除成功')
  await loadExperiences()
}

const openSkillDialog = (row = null) => {
  skillEditing.value = !!row
  skillForm.value = row
    ? {
        id: row.id,
        name: row.name,
        description: row.description ?? '',
        icon: row.icon ?? '',
        sort: row.sort ?? null,
        isVisible: row.isVisible ?? 1
      }
    : {
        id: null,
        name: '',
        description: '',
        icon: '',
        sort: null,
        isVisible: 1
      }
  skillDialogVisible.value = true
}

const handleSkillIconUpload = async (options) => {
  uploadingSkillIcon.value = true
  try {
    const fd = new FormData()
    fd.append('file', options.file)
    const res = await uploadFile(fd)
    skillForm.value.icon = res.data
    ElMessage.success('图标上传成功')
  } finally {
    uploadingSkillIcon.value = false
  }
}

const handleSkillSave = async () => {
  const name = skillForm.value.name.trim()
  if (!name) return ElMessage.warning('技能名称不能为空')

  skillSaving.value = true
  try {
    await profileStore.saveSkill({
      ...skillForm.value,
      name
    })
    ElMessage.success(skillEditing.value ? '技能更新成功' : '技能创建成功')
    skillDialogVisible.value = false
    await profileStore.fetchSkills()
  } finally {
    skillSaving.value = false
  }
}

const deleteSkill = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除技能「${row.name}」？`, '警告', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  await profileStore.removeSkills([row.id])
  ElMessage.success('删除成功')
  await profileStore.fetchSkills()
}

const openSocialDialog = (row = null) => {
  socialEditing.value = !!row
  socialForm.value = row
    ? {
        id: row.id,
        name: row.name,
        icon: row.icon ?? '',
        link: row.link ?? '',
        sort: row.sort ?? null,
        isVisible: row.isVisible ?? 1
      }
    : {
        id: null,
        name: '',
        icon: '',
        link: '',
        sort: null,
        isVisible: 1
      }
  socialDialogVisible.value = true
}

const handleSocialSave = async () => {
  const name = socialForm.value.name.trim()
  if (!name) return ElMessage.warning('平台名称不能为空')

  socialSaving.value = true
  try {
    await profileStore.saveSocialMedia({
      ...socialForm.value,
      name,
      link: normalizeExternalUrl(socialForm.value.link)
    })
    ElMessage.success(socialEditing.value ? '平台更新成功' : '平台创建成功')
    socialDialogVisible.value = false
    await profileStore.fetchSocialMedias()
  } finally {
    socialSaving.value = false
  }
}

const deleteSocial = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除平台「${row.name}」？`, '警告', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  await profileStore.removeSocialMedias([row.id])
  ElMessage.success('删除成功')
  await profileStore.fetchSocialMedias()
}

onMounted(async () => {
  await Promise.all([
    fetchPersonal(),
    loadExperiences(),
    profileStore.fetchSkills(),
    profileStore.fetchSocialMedias()
  ])
})
</script>

<template>
  <div class="profile-page">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="个人信息" name="personal">
        <div class="personal-wrap">
          <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :http-request="handleAvatarUpload"
          >
            <img
              v-if="personalForm.avatar"
              :src="personalForm.avatar"
              class="avatar-preview"
              alt="avatar"
            />
            <div v-else class="avatar-placeholder">
              <span class="iconfont icon-user" />
            </div>
          </el-upload>

          <el-form
            :model="personalForm"
            label-width="96px"
            class="personal-form"
          >
            <el-form-item label="昵称">
              <el-input
                v-model="personalForm.nickname"
                placeholder="请输入昵称"
                clearable
              />
            </el-form-item>
            <el-form-item label="标签" required>
              <el-input
                v-model="personalForm.tag"
                placeholder="例如：全栈开发 / 系统构建"
                clearable
              />
            </el-form-item>
            <el-form-item label="自我介绍">
              <el-input
                v-model="personalForm.description"
                type="textarea"
                :rows="3"
                placeholder="一句话介绍自己"
              />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input
                v-model="personalForm.email"
                placeholder="联系邮箱"
                clearable
              />
            </el-form-item>
            <el-form-item label="个人网站">
              <el-input
                v-model="personalForm.website"
                placeholder="https://..."
                clearable
              />
            </el-form-item>
            <el-form-item label="GitHub">
              <el-input
                v-model="personalForm.github"
                placeholder="https://github.com/..."
                clearable
              />
            </el-form-item>
            <el-form-item label="所在地">
              <el-input
                v-model="personalForm.location"
                placeholder="例如：中国 · 广州"
                clearable
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                :loading="savingPersonal"
                @click="savePersonal"
              >
                保存
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>

      <el-tab-pane label="经历管理" name="experience">
        <div class="tab-toolbar">
          <el-radio-group v-model="expFilter" @change="loadExperiences">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button :label="0">教育经历</el-radio-button>
            <el-radio-button :label="1">工作经历</el-radio-button>
            <el-radio-button :label="2">项目经历</el-radio-button>
          </el-radio-group>
          <el-button type="primary" @click="openExpDialog()">
            <span class="iconfont icon-plus" />
            新增经历
          </el-button>
        </div>
        <el-table
          :data="profileStore.experiences"
          border
          stripe
          v-loading="profileStore.loading"
        >
          <el-table-column prop="title" label="标题 / 名称" min-width="170" />
          <el-table-column
            prop="subtitle"
            label="副标题 / 角色"
            min-width="170"
          />
          <el-table-column label="类型" width="100" align="center">
            <template #default="{ row }">
              {{ getExpTypeLabel(row.type) }}
            </template>
          </el-table-column>
          <el-table-column label="时间" width="210" align="center">
            <template #default="{ row }">
              {{ row.startDate || '-' }} ~ {{ row.endDate || '至今' }}
            </template>
          </el-table-column>
          <el-table-column
            label="项目链接"
            min-width="230"
            show-overflow-tooltip
          >
            <template #default="{ row }">
              <a
                v-if="Number(row.type) === 2 && row.projectLink"
                :href="normalizeExternalUrl(row.projectLink)"
                target="_blank"
                rel="noopener"
                class="link-href"
              >
                {{ row.projectLink }}
              </a>
              <span v-else class="muted">-</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="content"
            label="内容"
            min-width="220"
            show-overflow-tooltip
          />
          <el-table-column
            label="操作"
            width="160"
            align="center"
            fixed="right"
          >
            <template #default="{ row }">
              <div class="row-actions">
                <el-button link size="small" @click="openExpDialog(row)"
                  >编辑</el-button
                >
                <el-divider direction="vertical" />
                <el-button
                  link
                  size="small"
                  type="danger"
                  @click="deleteExp(row)"
                >
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="技能管理" name="skill">
        <div class="tab-toolbar">
          <div />
          <el-button type="primary" @click="openSkillDialog()">
            <span class="iconfont icon-plus" />
            新增技能
          </el-button>
        </div>
        <el-table
          :data="profileStore.skills"
          border
          stripe
          v-loading="profileStore.loading"
        >
          <el-table-column prop="name" label="技能名称" min-width="160" />
          <el-table-column
            prop="description"
            label="描述"
            min-width="230"
            show-overflow-tooltip
          />
          <el-table-column label="图标" min-width="220" show-overflow-tooltip>
            <template #default="{ row }">
              <div class="icon-cell">
                <img
                  v-if="row.icon"
                  :src="row.icon"
                  class="skill-icon-preview"
                  alt="icon"
                />
                <span class="link-href">{{ row.icon || '-' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="sort" label="排序" width="90" align="center" />
          <el-table-column
            label="操作"
            width="160"
            align="center"
            fixed="right"
          >
            <template #default="{ row }">
              <div class="row-actions">
                <el-button link size="small" @click="openSkillDialog(row)"
                  >编辑</el-button
                >
                <el-divider direction="vertical" />
                <el-button
                  link
                  size="small"
                  type="danger"
                  @click="deleteSkill(row)"
                >
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="社交媒体" name="social">
        <div class="tab-toolbar">
          <div />
          <el-button type="primary" @click="openSocialDialog()">
            <span class="iconfont icon-plus" />
            新增平台
          </el-button>
        </div>
        <el-table
          :data="profileStore.socialMedias"
          border
          stripe
          v-loading="profileStore.loading"
        >
          <el-table-column prop="name" label="平台名称" min-width="180" />
          <el-table-column label="链接" min-width="280">
            <template #default="{ row }">
              <a
                :href="row.link"
                target="_blank"
                rel="noopener"
                class="link-href"
              >
                {{ row.link || '-' }}
              </a>
            </template>
          </el-table-column>
          <el-table-column
            prop="icon"
            label="图标标识"
            min-width="160"
            show-overflow-tooltip
          />
          <el-table-column prop="sort" label="排序" width="90" align="center" />
          <el-table-column
            label="操作"
            width="160"
            align="center"
            fixed="right"
          >
            <template #default="{ row }">
              <div class="row-actions">
                <el-button link size="small" @click="openSocialDialog(row)"
                  >编辑</el-button
                >
                <el-divider direction="vertical" />
                <el-button
                  link
                  size="small"
                  type="danger"
                  @click="deleteSocial(row)"
                >
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog
      v-model="expDialogVisible"
      :title="expEditing ? '编辑经历' : '新增经历'"
      width="560px"
      :close-on-click-modal="false"
    >
      <el-form :model="expForm" label-width="96px">
        <el-form-item label="类型">
          <el-radio-group v-model="expForm.type">
            <el-radio :label="0">教育经历</el-radio>
            <el-radio :label="1">工作经历</el-radio>
            <el-radio :label="2">项目经历</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="标题" required>
          <el-input
            v-model="expForm.title"
            placeholder="学校 / 公司 / 项目名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="副标题">
          <el-input
            v-model="expForm.subtitle"
            placeholder="专业 / 职位 / 角色"
            clearable
          />
        </el-form-item>
        <el-form-item label="Logo">
          <div class="upload-row">
            <el-upload
              :show-file-list="false"
              :http-request="handleLogoUpload"
              accept="image/*"
            >
              <el-button size="small" :loading="uploadingLogo">
                <span class="iconfont icon-upload" />
                上传 Logo
              </el-button>
            </el-upload>
            <el-input
              v-model="expForm.logoUrl"
              placeholder="Logo URL（可选）"
              clearable
              class="upload-url-input"
            />
          </div>
          <img
            v-if="expForm.logoUrl"
            :src="expForm.logoUrl"
            class="logo-preview"
            alt="logo"
          />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-input
            v-model="expForm.startDate"
            placeholder="例如：2024-01"
            clearable
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-input
            v-model="expForm.endDate"
            placeholder="例如：2026-03，留空表示至今"
            clearable
          />
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input
            v-model="expForm.content"
            type="textarea"
            :rows="3"
            placeholder="请输入经历描述"
          />
        </el-form-item>
        <el-form-item v-if="Number(expForm.type) === 2" label="项目链接">
          <el-input
            v-model="expForm.projectLink"
            placeholder="https://..."
            clearable
          />
        </el-form-item>
        <el-form-item label="可见">
          <el-switch
            v-model="expForm.isVisible"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="expDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="expSaving" @click="handleExpSave">
          确认
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="skillDialogVisible"
      :title="skillEditing ? '编辑技能' : '新增技能'"
      width="460px"
      :close-on-click-modal="false"
    >
      <el-form :model="skillForm" label-width="84px">
        <el-form-item label="名称" required>
          <el-input
            v-model="skillForm.name"
            placeholder="例如：Vue 3 / Python"
            clearable
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="skillForm.description"
            type="textarea"
            :rows="2"
            placeholder="技能描述（可选）"
          />
        </el-form-item>
        <el-form-item label="图标">
          <div class="upload-row">
            <el-upload
              :show-file-list="false"
              :http-request="handleSkillIconUpload"
              accept="image/*"
            >
              <el-button size="small" :loading="uploadingSkillIcon">
                <span class="iconfont icon-upload" />
                上传图标
              </el-button>
            </el-upload>
            <el-input
              v-model="skillForm.icon"
              placeholder="图标 URL（可选）"
              clearable
              class="upload-url-input"
            />
          </div>
          <img
            v-if="skillForm.icon"
            :src="skillForm.icon"
            class="skill-icon-preview"
            alt="skill icon"
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number
            v-model="skillForm.sort"
            :min="0"
            :precision="0"
            controls-position="right"
            style="width: 130px"
          />
        </el-form-item>
        <el-form-item label="可见">
          <el-switch
            v-model="skillForm.isVisible"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="skillDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="skillSaving"
          @click="handleSkillSave"
        >
          确认
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="socialDialogVisible"
      :title="socialEditing ? '编辑社交平台' : '新增社交平台'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="socialForm" label-width="96px">
        <el-form-item label="平台名称" required>
          <el-input
            v-model="socialForm.name"
            placeholder="例如：GitHub / X"
            clearable
          />
        </el-form-item>
        <el-form-item label="主页链接">
          <el-input
            v-model="socialForm.link"
            placeholder="https://..."
            clearable
          />
        </el-form-item>
        <el-form-item label="图标标识">
          <el-input
            v-model="socialForm.icon"
            placeholder="图标类名或 URL（可选）"
            clearable
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number
            v-model="socialForm.sort"
            :min="0"
            :precision="0"
            controls-position="right"
            style="width: 130px"
          />
        </el-form-item>
        <el-form-item label="可见">
          <el-switch
            v-model="socialForm.isVisible"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="socialDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="socialSaving"
          @click="handleSocialSave"
        >
          确认
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.profile-page {
  background: var(--admin-card);
  border: 1px solid var(--admin-border);
  border-radius: 8px;
  padding: 20px;
  color: var(--admin-text);
}

.tab-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  gap: 10px;
  flex-wrap: wrap;
}

.tab-toolbar .iconfont {
  font-size: 14px;
  margin-right: 4px;
}

.row-actions {
  display: flex;
  align-items: center;
  justify-content: center;
}

.personal-wrap {
  display: flex;
  gap: 28px;
  align-items: flex-start;
  padding: 8px 0;
}

.avatar-uploader {
  flex-shrink: 0;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid var(--admin-border);
}

.avatar-placeholder {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: var(--admin-soft-bg);
  border: 1px dashed var(--admin-border);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.avatar-placeholder .iconfont {
  font-size: 36px;
  color: var(--admin-text3);
}

.personal-form {
  flex: 1;
  max-width: 540px;
}

.link-href {
  color: var(--admin-text2);
  text-decoration: none;
  font-size: 13px;
}

.link-href:hover {
  color: var(--admin-text);
  text-decoration: underline;
}

.muted {
  color: var(--admin-text3);
}

.upload-row {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.upload-url-input {
  flex: 1;
}

.logo-preview {
  width: 56px;
  height: 56px;
  border-radius: 6px;
  object-fit: contain;
  margin-top: 8px;
  border: 1px solid var(--admin-border);
}

.icon-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.skill-icon-preview {
  width: 34px;
  height: 34px;
  object-fit: contain;
  border: 1px solid var(--admin-border);
  border-radius: 4px;
  background: var(--admin-soft-bg);
}

@media (max-width: 900px) {
  .personal-wrap {
    flex-direction: column;
  }
}
</style>
