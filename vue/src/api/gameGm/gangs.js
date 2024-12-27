import request from '@/utils/request'
import {parseStrEmpty} from "@/utils/format";

// 查询帮派列表
export function listGangs(query) {
  return request({
    url: '/gameGm/gangs/list',
    method: 'get',
    params: query
  })
}

// 查询用户详细
export function getGangs(gangsId) {
  return request({
    url: '/gameGm/gangs/' + parseStrEmpty(gangsId),
    method: 'get'
  })
}

// 查询用户详细
export function getGangsMember(gangsId, query) {
  return request({
    url: '/gameGm/gangs/playerlist/' + parseStrEmpty(gangsId),
    method: 'get',
    params: query
  })
}

// 查询用户详细
export function modifyGangsNotice(gangsId, query) {
  return request({
    url: '/gameGm/gangs/notice/' + parseStrEmpty(gangsId),
    method: 'post',
    params: query
  })
}
