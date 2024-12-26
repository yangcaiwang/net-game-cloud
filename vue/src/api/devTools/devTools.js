import request from '@/utils/request'

// 查询所有proto文件列表
export function listAllProto(fileVersion) {
  const data ={
    fileVersion
  }
  return request({
    url: '/devTools/proto/listProtoFile',
    method: 'get',
    params: data
  })
}

// 查询版本列表
export function listProtoVersionInfo() {
  return request({
    url: '/devTools/proto/listProtoVersionInfo',
    method: 'get'
  })
}

// 查询单个proto文件的结构数据
export function listProtoFileStruct(query) {
  return request({
    url: '/devTools/proto/listProtocolFileStruct',
    method: 'get',
    params: query
  })
}

// 查询单个proto文件的结构数据
export function getProtoFileAllStruct(query) {
  return request({
    url: '/devTools/proto/getProtoFileAllStruct',
    method: 'get',
    params: query
  })
}

// 查询单个proto文件的结构数据
export function listProtocolFileStructField(query) {
  return request({
    url: '/devTools/proto/listProtocolFileStructField',
    method: 'get',
    params: query
  })
}

export function genProtoFile(fileVersion) {
  const data ={
    fileVersion
  }
  return request({
    url: '/devTools/proto/genProtoFile',
    method: 'get',
    params: data
  })
}

export function listProtoStruct(query) {
  return request({
    url: '/devTools/proto/listProtoStruct',
    method: 'get',
    params: query
  })
}

// 新增proto文件
export function saveProtoFileDesc(data) {
  return request({
    url: '/devTools/proto/saveProtoFileDesc',
    method: 'post',
    data: data
  })
}

// 修改proto文件
export function updateProtoFileDesc(data) {
  return request({
    url: '/devTools/proto/updateProtoFileDesc',
    method: 'put',
    data: data
  })
}

// 新增proto文件内容
export function saveProtoFileStruct(data) {
  return request({
    url: '/devTools/proto/saveProtoFileStruct',
    method: 'post',
    data: data
  })
}

// 修改proto文件内容
export function updateProtoFileStruct(data) {
  return request({
    url: '/devTools/proto/updateProtoFileStruct',
    method: 'put',
    data: data
  })
}

// 删除proto文件内容
export function deleteProtocolFileStruct(data) {
  return request({
    url: '/devTools/proto/deleteProtocolFileStruct',
    method: 'delete',
    data: data
  })
}

// 删除proto文件
export function deleteProtocolFile(data) {
  return request({
    url: '/devTools/proto/deleteProtocolFile',
    method: 'delete',
    data: data
  })
}

// 提交更新操作
export function commitOpt(data) {
  return request({
    url: '/devTools/config/commitOpt',
    method: 'post',
    data: data
  })
}