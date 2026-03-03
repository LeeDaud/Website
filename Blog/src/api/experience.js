import request from '@/utils/request'

/** 获取项目经历（type=2） */
export const getProjectExperiences = () =>
  request.get('/cv/experience', {
    params: { type: 2 }
  })
