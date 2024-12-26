import request from '@/utils/request'

// 查询游戏管理  跑马灯列表
export function listBroadcast(query) {
  return request({
    url: '/gameGm/broadcast/list',
    method: 'get',
    params: query
  })
}

// 查询游戏管理  跑马灯详细
export function getBroadcast(id) {
  return request({
    url: '/gameGm/broadcast/' + id,
    method: 'get'
  })
}

// 新增游戏管理  跑马灯
export function addBroadcast(data) {
  return request({
    url: '/gameGm/broadcast',
    method: 'post',
    data: data
  })
}

// 修改游戏管理  跑马灯
export function updateBroadcast(data) {
  return request({
    url: '/gameGm/broadcast',
    method: 'put',
    data: data
  })
}

// 删除游戏管理  跑马灯
export function delBroadcast(id) {
  return request({
    url: '/gameGm/broadcast/' + id,
    method: 'delete'
  })
}

// 通过邮件
export function pass(ids, status) {
  const data = {
    status
  }
  return request({
    url: '/gameGm/broadcast/pass/' + ids,
    method: 'post',
    data: data
  })
}
