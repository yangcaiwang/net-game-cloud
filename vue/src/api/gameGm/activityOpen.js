import request from '@/utils/request'

// 查询运营活动开启时间列表
export function listOpen(query) {
  return request({
    url: '/activity/open/list',
    method: 'get',
    params: query
  })
}

// 查询运营活动开启时间详细
export function getOpen(id) {
  return request({
    url: '/activity/open/' + id,
    method: 'get'
  })
}

// 新增运营活动开启时间
export function addOpen(data) {
  return request({
    url: '/activity/open',
    method: 'post',
    data: data
  })
}

// 修改运营活动开启时间
export function updateOpen(data) {
  return request({
    url: '/activity/open',
    method: 'put',
    data: data
  })
}

// 删除运营活动开启时间
export function delOpen(id) {
  return request({
    url: '/activity/open/' + id,
    method: 'delete'
  })
}

// 通过邮件
export function pass(ids, status) {
  const data = {
    status
  }
  return request({
    url: '/activity/open/pass/' + ids,
    method: 'post',
    data: data
  })
}

// 活动状态修改
export function changeStatus(id, status) {
  const data = {
    id,
    status
  }
  return request({
    url: '/activity/open/changeStatus',
    method: 'put',
    data: data
  })
}
