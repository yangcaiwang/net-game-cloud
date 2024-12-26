import request from '@/utils/request'

// 查询充值订单记录列表
export function listRecharge(query) {
  return request({
    url: '/rechargelog/rechargelog/list',
    method: 'get',
    params: query
  })
}

// 查询充值订单记录详细
export function getRechargeSum(query) {
  return request({
    url: '/rechargelog/rechargesum/list',
    method: 'get',
    params: query
  })
}

// 查询充值订单记录详细
export function getRechargeDetailList(query) {
  return request({
    url: '/rechargelog/rechargesum/detail',
    method: 'get',
    params: query
  })
}

// // 新增充值订单记录
// export function add1(data) {
//   return request({
//     url: '/gm/rechargelog',
//     method: 'post',
//     data: data
//   })
// }
