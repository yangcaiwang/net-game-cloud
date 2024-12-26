import request from '@/utils/request'

// 查询gm命令行列表
export function listCommand(query) {
  return request({
    url: '/gameGm/command/list',
    method: 'get',
    params: query
  })
}

// 查询gm命令行详细
export function getCommand(id) {
  return request({
    url: '/gameGm/command/' + id,
    method: 'get'
  })
}

// 新增gm命令行
export function addCommand(data) {
  return request({
    url: '/gameGm/command',
    method: 'post',
    data: data
  })
}

// 修改gm命令行
export function updateCommand(data) {
  return request({
    url: '/gameGm/command',
    method: 'put',
    data: data
  })
}

// 删除gm命令行
export function delCommand(id) {
  return request({
    url: '/gameGm/command/' + id,
    method: 'delete'
  })
}

// 通过邮件
export function pass(ids, status) {
  const data = {
    status
  }
  return request({
    url: '/gameGm/command/pass/' + ids,
    method: 'post',
    data: data
  })
}