import request from '@/utils/request'

// 查询GM充值列表
export function listRecharge(query) {
  return request({
    url: '/gameGm/recharge/list',
    method: 'get',
    params: query
  })
}

// 查询GM充值详细
export function getRecharge(id) {
  return request({
    url: '/gameGm/recharge/' + id,
    method: 'get'
  })
}

// 新增GM充值
export function addRecharge(data) {
  return request({
    url: '/gameGm/recharge',
    method: 'post',
    data: data
  })
}

// 修改GM充值
export function updateRecharge(data) {
  return request({
    url: '/gameGm/recharge',
    method: 'put',
    data: data
  })
}

// 删除GM充值
export function delRecharge(id) {
  return request({
    url: '/gameGm/recharge/' + id,
    method: 'delete'
  })
}

// 通过邮件
export function pass(ids, status) {
  const data = {
    status
  }
  return request({
    url: '/gameGm/recharge/pass/' + ids,
    method: 'post',
    data: data
  })
}
