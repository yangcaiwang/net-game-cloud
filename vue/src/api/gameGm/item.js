import request from '@/utils/request'

// 查询游戏管理  添加道具列表
export function listItem(query) {
  return request({
    url: '/gameGm/item/list',
    method: 'get',
    params: query
  })
}

// 查询游戏管理  添加道具详细
export function getItem(id) {
  return request({
    url: '/gameGm/item/' + id,
    method: 'get'
  })
}

// 新增游戏管理  添加道具
export function addItem(data) {
  return request({
    url: '/gameGm/item',
    method: 'post',
    data: data
  })
}

// 修改游戏管理  添加道具
export function updateItem(data) {
  return request({
    url: '/gameGm/item',
    method: 'put',
    data: data
  })
}

// 删除游戏管理  添加道具
export function delItem(id) {
  return request({
    url: '/gameGm/item/' + id,
    method: 'delete'
  })
}

// 通过邮件
export function pass(ids, status) {
  const data = {
    status
  }
  return request({
    url: '/gameGm/item/pass/' + ids,
    method: 'post',
    data: data
  })
}
