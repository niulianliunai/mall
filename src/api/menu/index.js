import request from '@/utils/request'

export function listMenu(params) {
  return request({
    url: '/menu/list',
    method: 'get',
    params
  })
}
export function insert(data) {
  return request({
    url: '/menu/insert',
    method: 'post',
    data
  })
}
export function update(data) {
  return request({
    url: '/menu/update',
    method: 'post',
    data
  })
}
