<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import {
  getConfigList,
  createConfig,
  updateConfig,
  deleteConfigs
} from '@/api/settings'
import {
  changePassword,
  changeNickname,
  changeEmail,
  sendCode
} from '@/api/auth'
import { useUserStore } from '@/stores'

const userStore = useUserStore()

const activeTab = ref('config')

const configs = ref([])
const loadingConfig = ref(false)
const configDialogVisible = ref(false)
const configEditing = ref(false)
const configForm = ref({
  id: null,
  configKey: '',
  configValue: '',
  configType: '',
  description: ''
})

const fetchConfigs = async () => {
  loadingConfig.value = true
  try {
    const res = await getConfigList()
    configs.value = res.data ?? []
  } finally {
    loadingConfig.value = false
  }
}

const openConfigDialog = (row = null) => {
  configEditing.value = !!row
  configForm.value = row
    ? {
        id: row.id,
        configKey: row.configKey,
        configValue: row.configValue,
        configType: row.configType ?? '',
        description: row.description ?? ''
      }
    : {
        id: null,
        configKey: '',
        configValue: '',
        configType: '',
        description: ''
      }
  configDialogVisible.value = true
}

const saveConfig = async () => {
  const key = configForm.value.configKey.trim()
  if (!key) return ElMessage.warning('配置键不能为空')

  const payload = {
    ...configForm.value,
    configKey: key
  }
  if (configEditing.value) {
    await updateConfig(payload)
  } else {
    await createConfig(payload)
  }
  ElMessage.success(configEditing.value ? '配置更新成功' : '配置创建成功')
  configDialogVisible.value = false
  await fetchConfigs()
}

const deleteConfig = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除配置「${row.configKey}」？`, '警告', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  await deleteConfigs([row.id])
  ElMessage.success('删除成功')
  await fetchConfigs()
}

const pwdForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmNewPassword: ''
})
const savingPwd = ref(false)

const handleChangePassword = async () => {
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword) {
    return ElMessage.warning('请填写完整密码信息')
  }
  if (pwdForm.value.newPassword !== pwdForm.value.confirmNewPassword) {
    return ElMessage.warning('两次新密码输入不一致')
  }

  savingPwd.value = true
  try {
    await changePassword({ ...pwdForm.value })
    ElMessage.success('密码修改成功，即将退出登录')
    await userStore.logoutAction()
  } finally {
    savingPwd.value = false
  }
}

const nicknameVal = ref('')
const savingNickname = ref(false)

const handleChangeNickname = async () => {
  const nickname = nicknameVal.value.trim()
  if (!nickname) return ElMessage.warning('昵称不能为空')

  savingNickname.value = true
  try {
    await changeNickname({ nickname })
    ElMessage.success('昵称修改成功')
    await userStore.fetchUserInfo()
  } finally {
    savingNickname.value = false
  }
}

const emailForm = ref({ email: '', code: '' })
const savingEmail = ref(false)
const sendingEmailCode = ref(false)
const emailCounting = ref(false)
const emailCountdown = ref(60)
let emailTimer = null

const clearEmailTimer = () => {
  if (emailTimer) {
    clearInterval(emailTimer)
    emailTimer = null
  }
}

const sendEmailCode = async () => {
  const username = userStore.userInfo?.username ?? ''
  if (!username) {
    return ElMessage.warning('未获取到当前用户名，请重新登录后重试')
  }

  sendingEmailCode.value = true
  try {
    await sendCode({ username })
    ElMessage.success('验证码已发送')
    clearEmailTimer()
    emailCounting.value = true
    emailCountdown.value = 60
    emailTimer = setInterval(() => {
      emailCountdown.value -= 1
      if (emailCountdown.value <= 0) {
        clearEmailTimer()
        emailCounting.value = false
      }
    }, 1000)
  } finally {
    sendingEmailCode.value = false
  }
}

const handleChangeEmail = async () => {
  const email = emailForm.value.email.trim()
  const code = emailForm.value.code.trim()
  if (!email || !code) {
    return ElMessage.warning('请填写新邮箱和验证码')
  }

  savingEmail.value = true
  try {
    await changeEmail({ email, code })
    ElMessage.success('邮箱更换成功')
    emailForm.value = { email: '', code: '' }
  } finally {
    savingEmail.value = false
  }
}

onMounted(() => {
  fetchConfigs()
})

onBeforeUnmount(() => {
  clearEmailTimer()
})
</script>

<template>
  <div class="settings-page">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="系统配置" name="config">
        <div class="tab-toolbar">
          <el-button type="primary" @click="openConfigDialog()">
            <span class="iconfont icon-plus" />
            新增配置
          </el-button>
        </div>
        <el-table :data="configs" border stripe v-loading="loadingConfig">
          <el-table-column prop="configKey" label="配置键" min-width="170" />
          <el-table-column
            prop="configValue"
            label="配置值"
            min-width="200"
            show-overflow-tooltip
          />
          <el-table-column
            prop="configType"
            label="类型"
            width="110"
            align="center"
          />
          <el-table-column
            prop="description"
            label="描述"
            min-width="220"
            show-overflow-tooltip
          />
          <el-table-column label="操作" width="160" align="center">
            <template #default="{ row }">
              <el-button link size="small" @click="openConfigDialog(row)"
                >编辑</el-button
              >
              <el-divider direction="vertical" />
              <el-button
                link
                size="small"
                type="danger"
                @click="deleteConfig(row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="账号安全" name="security">
        <div class="security-wrap">
          <div class="security-section">
            <h3 class="section-title">修改密码</h3>
            <el-form :model="pwdForm" label-width="110px" class="security-form">
              <el-form-item label="当前密码">
                <el-input
                  v-model="pwdForm.oldPassword"
                  type="password"
                  show-password
                  placeholder="输入当前密码"
                  style="max-width: 340px"
                />
              </el-form-item>
              <el-form-item label="新密码">
                <el-input
                  v-model="pwdForm.newPassword"
                  type="password"
                  show-password
                  placeholder="输入新密码"
                  style="max-width: 340px"
                />
              </el-form-item>
              <el-form-item label="确认新密码">
                <el-input
                  v-model="pwdForm.confirmNewPassword"
                  type="password"
                  show-password
                  placeholder="再次输入新密码"
                  style="max-width: 340px"
                />
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  :loading="savingPwd"
                  @click="handleChangePassword"
                >
                  保存密码
                </el-button>
              </el-form-item>
            </el-form>
          </div>

          <el-divider />

          <div class="security-section">
            <h3 class="section-title">修改昵称</h3>
            <div class="inline-action">
              <el-input
                v-model="nicknameVal"
                placeholder="输入新昵称"
                clearable
                style="max-width: 340px"
              />
              <el-button
                type="primary"
                :loading="savingNickname"
                @click="handleChangeNickname"
              >
                保存昵称
              </el-button>
            </div>
          </div>

          <el-divider />

          <div class="security-section">
            <h3 class="section-title">更换邮箱</h3>
            <el-form
              :model="emailForm"
              label-width="110px"
              class="security-form"
            >
              <el-form-item label="新邮箱">
                <el-input
                  v-model="emailForm.email"
                  placeholder="输入新邮箱地址"
                  clearable
                  style="max-width: 340px"
                />
              </el-form-item>
              <el-form-item label="验证码">
                <div class="code-row">
                  <el-input
                    v-model="emailForm.code"
                    placeholder="输入验证码"
                    style="max-width: 220px"
                  />
                  <el-button
                    :loading="sendingEmailCode"
                    :disabled="emailCounting"
                    @click="sendEmailCode"
                  >
                    {{
                      emailCounting ? `${emailCountdown}s 后重试` : '获取验证码'
                    }}
                  </el-button>
                </div>
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  :loading="savingEmail"
                  @click="handleChangeEmail"
                >
                  确认更换
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog
      v-model="configDialogVisible"
      :title="configEditing ? '编辑配置' : '新增配置'"
      width="520px"
      :close-on-click-modal="false"
    >
      <el-form :model="configForm" label-width="90px">
        <el-form-item label="配置键" required>
          <el-input
            v-model="configForm.configKey"
            placeholder="例如：site.title"
            clearable
          />
        </el-form-item>
        <el-form-item label="配置值">
          <el-input
            v-model="configForm.configValue"
            type="textarea"
            :rows="3"
            placeholder="配置值"
          />
        </el-form-item>
        <el-form-item label="类型">
          <el-select
            v-model="configForm.configType"
            placeholder="选择类型"
            clearable
            style="width: 100%"
          >
            <el-option label="string" value="string" />
            <el-option label="number" value="number" />
            <el-option label="boolean" value="boolean" />
            <el-option label="json" value="json" />
            <el-option label="text" value="text" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="configForm.description"
            type="textarea"
            :rows="2"
            placeholder="配置说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveConfig">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.settings-page {
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

.security-wrap {
  padding: 8px 0;
}

.security-section {
  padding: 8px 0 16px;
}

.section-title {
  margin: 0 0 16px;
  font-size: 15px;
  font-weight: 600;
  color: var(--admin-text);
}

.security-form {
  max-width: 560px;
}

.inline-action {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.code-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
</style>
