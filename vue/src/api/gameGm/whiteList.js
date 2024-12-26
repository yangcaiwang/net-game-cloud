import request from '@/utils/request'

// 查询白名单列表
export function listList(query) {
  return request({
    url: '/gm/whitelist/list',
    method: 'get',
    params: query
  })
}

// 查询白名单详细
export function getList(id) {
  return request({
    url: '/gm/whitelist/' + id,
    method: 'get'
  })
}

// 新增白名单
export function addList(data) {
  return request({
    url: '/gm/whitelist',
    method: 'post',
    data: data
  })
}

// 修改白名单
export function updateList(data) {
  return request({
    url: '/gm/whitelist',
    method: 'put',
    data: data
  })
}

// 删除白名单
export function delList(id) {
  return request({
    url: '/gm/whitelist/' + id,
    method: 'delete'
  })
}
