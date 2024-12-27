import request from '@/utils/request'
import {parseStrEmpty} from "@/utils/format";

// 查询平台列表
export function listPlatform(query) {
  return request({
    url: '/gameGm/platform/list',
    method: 'get',
    params: query
  })
}

// 查询平台列表
export function listPlatformAll() {
  return request({
    url: '/gameGm/platform/all',
    method: 'get',
  })
}

// 查询平台详细
export function getPlatform(pid) {
  return request({
    url: '/gameGm/platform/' + parseStrEmpty(pid),
    method: 'get'
  })
}

// 新增平台
export function addPlatform(data) {
  return request({
    url: '/gameGm/platform',
    method: 'post',
    data: data
  })
}

// 修改用户
export function updatePlatform(data) {
  return request({
    url: '/gameGm/platform',
    method: 'put',
    data: data
  })
}

// 删除平台
export function delPlatform(pid) {
  return request({
    url: '/gameGm/platform/' + pid,
    method: 'delete'
  })
}

// 用户状态修改
export function changeWhiteListStatus(platformId, whiteListStatus) {
  const data = {
    platformId,
    whiteListStatus
  }
  return request({
    url: '/gameGm/platform/changeWhiteListStatus',
    method: 'put',
    data: data
  })
}

// 自动关闭服务器注册修改
export function changeAutoRegisterSwitch(platformId, autoRegisterSwitch) {
  const data = {
    platformId,
    autoRegisterSwitch
  }
  return request({
    url: '/gameGm/platform/changeAutoRegisterSwitch',
    method: 'put',
    data: data
  })
}
