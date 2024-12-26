import request from '@/utils/request'

// 查询服务器异常记录列表
export function listLog(query) {
  return request({
    url: '/gameGm/exception/list',
    method: 'get',
    params: query
  })
}

// 查询服务器异常记录详细
export function getLog(id) {
  return request({
    url: '/gameGm/exception/' + id,
    method: 'get'
  })
}

// 新增服务器异常记录
export function addLog(data) {
  return request({
    url: '/gameGm/exception',
    method: 'post',
    data: data
  })
}

// 修改服务器异常记录
export function updateLog(data) {
  return request({
    url: '/gameGm/exception',
    method: 'put',
    data: data
  })
}

// 删除服务器异常记录
export function delLog(id) {
  return request({
    url: '/gameGm/exception/' + id,
    method: 'delete'
  })
}

// 清空服务器异常记录
export function cleanLog() {
  return request({
    url: '/gameGm/exception/clean',
    method: 'delete'
  })
}