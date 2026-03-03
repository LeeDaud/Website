import axios from 'axios'
import router from '@/router'

const baseURL = '/api'

const http = axios.create({
  baseURL,
  timeout: 15000
})

const getToken = () => localStorage.getItem('admin_token') || ''

http.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers = config.headers || {}
      config.headers['Authorization'] = token
    }
    return config
  },
  (error) => Promise.reject(error)
)

http.interceptors.response.use(
  (response) => {
    const { data } = response
    if (data?.code === 1) {
      return data
    }
    ElMessage.error(data?.msg || '请求失败')
    return Promise.reject(data)
  },
  (error) => {
    const status = error?.response?.status
    if (status === 401) {
      // 防止并发 401 重复弹窗
      if (!http._isRedirecting401) {
        http._isRedirecting401 = true
        ElMessage.warning('登录状态失效，请重新登录')
        localStorage.removeItem('admin_token')
        router.push('/login')
        setTimeout(() => {
          http._isRedirecting401 = false
        }, 2000)
      }
    } else if (status === 403) {
      ElMessage.error('权限不足，无法执行该操作')
    } else {
      ElMessage.error('网络错误，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default http
export { baseURL }
