<script setup>
import { ref, reactive, computed, onUnmounted } from 'vue'
import { useUserStore } from '@/stores'
import { sendCode } from '@/api/auth'

const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)
const sending = ref(false)
const isCounting = ref(false)
const countdown = ref(60)

/** @type {ReturnType<typeof setInterval>|null} */
let timer = null

const loginForm = reactive({
  username: '',
  password: '',
  code: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const codeButtonText = computed(() =>
  isCounting.value ? `${countdown.value}s 后重试` : '获取验证码'
)

const startCountdown = () => {
  isCounting.value = true
  countdown.value = 60
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer)
      isCounting.value = false
      countdown.value = 60
    }
  }, 1000)
}

const onSendCode = async () => {
  if (!loginForm.username.trim()) {
    ElMessage.warning('请先输入用户名')
    return
  }
  sending.value = true
  try {
    await sendCode({ username: loginForm.username })
    ElMessage.success('验证码已发送至邮箱')
    startCountdown()
  } catch {
    // 错误由请求拦截器统一处理
  } finally {
    sending.value = false
  }
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.loginAction({ ...loginForm })
  } catch {
    // 错误由请求拦截器统一处理
  } finally {
    loading.value = false
  }
}

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<template>
  <div class="login-page">
    <div class="login-box">
      <div class="login-header">
        <div class="login-brand">
          <span class="iconfont icon-guanliduan brand-icon" />
        </div>
        <h1 class="login-title">管理控制台</h1>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="用户名"
            size="large"
            clearable
          >
            <template #prefix>
              <span class="iconfont icon-user field-icon" />
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="登录密码"
            size="large"
            show-password
          >
            <template #prefix>
              <span class="iconfont icon-lock field-icon" />
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="code">
          <div class="code-row">
            <el-input
              v-model="loginForm.code"
              placeholder="邮箱验证码"
              size="large"
              class="code-input"
            >
              <template #prefix>
                <span class="iconfont icon-shield field-icon" />
              </template>
            </el-input>
            <el-button
              size="large"
              class="code-btn"
              :loading="sending"
              :disabled="isCounting"
              @click.stop="onSendCode"
            >
              {{ codeButtonText }}
            </el-button>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="login-btn"
            size="large"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: var(--admin-bg);
}

.login-box {
  width: 420px;
  background: var(--admin-card);
  border: 1px solid var(--admin-border);
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.28);
  padding: 48px 40px 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.login-brand {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  border-radius: 12px;
  background: #0f1115;
  border: 1px solid #2a2f3a;
  margin: 0 auto 16px;
}

.brand-icon {
  font-size: 26px;
  color: #f5f7fa;
}

.login-title {
  margin: 0 0 6px;
  font-size: 22px;
  font-weight: 700;
  color: var(--admin-text);
  letter-spacing: 1px;
}

.login-form {
  display: flex;
  flex-direction: column;
}

.field-icon {
  font-size: 16px;
  color: var(--admin-text3);
}

.code-row {
  display: flex;
  gap: 10px;
  width: 100%;
}

.code-input {
  flex: 1;
}

.code-btn {
  width: 118px;
  flex-shrink: 0;
  font-size: 13px;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 15px;
  font-weight: 500;
  letter-spacing: 4px;
  margin-top: 4px;
}

:deep(.el-input__wrapper) {
  background: var(--admin-soft-bg);
  box-shadow: 0 0 0 1px var(--admin-border) inset;
}

:deep(.el-input__inner) {
  color: var(--admin-text);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #4f8cff inset !important;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}
</style>
