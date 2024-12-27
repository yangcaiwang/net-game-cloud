import request from '@/utils/request'
import {parseStrEmpty} from "@/utils/format";

// 查询服务器列表
export function listMail(query) {
  return request({
    url: '/gameGm/mail/list',
    method: 'get',
    params: query
  })
}

// 查询服务器列表
export function listMailAll() {
  return request({
    url: '/gameGm/mail/all',
    method: 'get',
  })
}

// 查询服务器详细
export function getMail(sid) {
  return request({
    url: '/gameGm/mail/' + parseStrEmpty(sid),
    method: 'get'
  })
}

// 新增服务器
export function addMail(data) {
  return request({
    url: '/gameGm/mail',
    method: 'post',
    data: data
  })
}

// 修改服务器
export function updateMail(data) {
  return request({
    url: '/gameGm/mail',
    method: 'put',
    data: data
  })
}

// 删除服务器
export function delMail(sid) {
  return request({
    url: '/gameGm/mail/' + sid,
    method: 'delete'
  })
}

// 通过邮件
export function passMail(ids, mailStatus) {
  const data = {
    mailStatus
  }
  return request({
    url: '/gameGm/mail/pass/' + ids,
    method: 'post',
    data: data
  })
}

