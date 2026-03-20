import request from '@/utils/request'

export const getResumeStatus = () => request.get('/cv/resume/status')
