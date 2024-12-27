import request from '@/utils/request'
import {parseStrEmpty} from "@/utils/format";

// 查询服务器列表
export function listServer(query) {
  return request({
    url: '/gameGm/server/list',
    method: 'get',
    params: query
  })
}

// 查询服务器列表
export function listServerAll() {
  return request({
    url: '/gameGm/server/all',
    method: 'get',
  })
}

// 查询服务器详细
export function getServer(sid) {
  return request({
    url: '/gameGm/server/' + parseStrEmpty(sid),
    method: 'get'
  })
}

// 新增服务器
export function addServer(data) {
  return request({
    url: '/gameGm/server',
    method: 'post',
    data: data
  })
}

// 修改服务器
export function updateServer(data) {
  return request({
    url: '/gameGm/server',
    method: 'put',
    data: data
  })
}

// 删除服务器
export function delServer(sid) {
  return request({
    url: '/gameGm/server/' + sid,
    method: 'delete'
  })
}

// 踢下线玩家
export function kitoutAll(serverId, data) {
  return request({
    url: '/gameGm/server/kitout/' + serverId,
    method: 'post',
    data: data
  })
}

// 服务器开启状态修改
export function changeServerStatus(data) {
  return request({
    url: '/gameGm/server/changeAllServerStatus',
    method: 'put',
    data: data
  })
}

// 服务器开启状态修改
export function changeServerShowOutStatus(serverKeyId, showOut) {
  const data = {
    serverKeyId,
    showOut
  }
  return request({
    url: '/gameGm/server/changeServerStatus',
    method: 'put',
    data: data
  })
}

// 服务器开启状态修改
export function batchChangeServerStatus(serverKeyId, showOut) {
  const data = {
    serverKeyId,
    showOut
  }
  return request({
    url: '/gameGm/server/changeServerStatus',
    method: 'put',
    data: data
  })
}

// 服务器定时开服修改
export function changeServerTimeOpen(serverKeyId, timeOpen) {
  const data = {
    serverKeyId,
    timeOpen
  }
  return request({
    url: '/gameGm/server/changeServerTimeOpen',
    method: 'put',
    data: data
  })
}

// 服务器注册状态修改
export function changeServerRegister(serverKeyId, registerSwitch) {
  const data = {
    serverKeyId,
    registerSwitch
  }
  return request({
    url: '/gameGm/server/changeServerRegister',
    method: 'put',
    data: data
  })
}

// 启动服务器
export function startServer(serverIds, data) {
  return request({
    url: '/gameGm/server/start/' + serverIds,
    method: 'post',
    data: data
  })
}

// 启动服务器
export function stopServer(serverIds, data) {
  return request({
    url: '/gameGm/server/stop/' + serverIds,
    method: 'post',
    data: data
  })
}

// 更改服务配置
export function changeServerConfig(serverIds) {
  return request({
    url: '/gameGm/server/changeServerConfig/' + serverIds,
    method: 'post'
  })
}

// 部署服务
export function deployServer(serverIds) {
  return request({
    url: '/gameGm/server/deployServer/' + serverIds,
    method: 'post'
  })
}

// 查询合服服务器列表
export function listMergeServer() {
  return request({
    url: '/gameGm/server/mergelist',
    method: 'get',
  })
}

// 查询合服服务器列表
export function doMergeServer(mainServerId, subServers) {
  return request({
    url: '/gameGm/server/merge/' + mainServerId + "/" + subServers,
    method: 'post',
  })
}
