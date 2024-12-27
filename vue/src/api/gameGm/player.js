import request from '@/utils/request'
import {parseStrEmpty} from "@/utils/format";

// 查询玩家列表
export function listPlayer(query) {
  return request({
    url: '/gameGm/player/list',
    method: 'get',
    params: query
  })
}

// 查询玩家列表
export function listPlayerGeneral(playerId, query) {
  return request({
    url: '/gameGm/player/genlist/' + parseStrEmpty(playerId),
    method: 'get',
    params: query
  })
}

// 查询玩家列表
export function listPlayerItem(playerId, query) {
  return request({
    url: '/gameGm/player/item/' + parseStrEmpty(playerId),
    method: 'get',
    params: query
  })
}

// 查询玩家列表
export function listPlayerItemLog(playerId, query) {
  return request({
    url: '/gameGm/player/itemlog/' + parseStrEmpty(playerId),
    method: 'get',
    params: query
  })
}

// 查询玩家战力日志列表
export function listPlayerPowerLog(playerId, query) {
  return request({
    url: '/gameGm/player/powerlog/' + parseStrEmpty(playerId),
    method: 'get',
    params: query
  })
}

// 查询玩家登陆日志列表
export function listPlayerLoginLog(playerId, query) {
  return request({
    url: '/gameGm/player/loginlog/' + parseStrEmpty(playerId),
    method: 'get',
    params: query
  })
}

// 查询玩家将领日志列表
export function listPlayerGeneralLog(playerId, query) {
  return request({
    url: '/gameGm/player/generallog/' + parseStrEmpty(playerId),
    method: 'get',
    params: query
  })
}

// 查询玩家详细
export function getPlayer(playerId, serverId) {
  return request({
    url: '/gameGm/player/' + parseStrEmpty(playerId) + '/' + parseStrEmpty(serverId),
    method: 'get'
  })
}

// 删除玩家
export function delPlayer(roleIds, serverId) {
  const data = {
    serverId : serverId
  };
  return request({
    url: '/gameGm/player/del/' + roleIds,
    method: 'delete',
    params: data
  })
}

// 踢出玩家
export function kitout(roleIds, serverId) {
  const data = {
    serverId : serverId
  };
  return request({
    url: '/gameGm/player/kitout/' + roleIds,
    method: 'post',
    params: data
  })
}

export function mailInvalid(mails, serverId) {
  const data = {
    serverId : serverId
  };
  return request({
    url: '/gameGm/player/mailInvalid/' + mails,
    method: 'post',
    params: data
  })
}

// 查询玩家个人信息
export function getUserProfile() {
  return request({
    url: '/gameGm/player/profile',
    method: 'get'
  })
}

// 查询玩家详细
export function getPlayerBase(playerId, serverId) {
  return request({
    url: '/gameGm/player/base/' + parseStrEmpty(playerId) + '/' + parseStrEmpty(serverId),
    method: 'get'
  })
}

// 查询玩家详细
export function getPlayerMail(playerId, query) {
  return request({
    url: '/gameGm/player/mail/' + parseStrEmpty(playerId),
    method: 'get',
    params: query
  })
}

