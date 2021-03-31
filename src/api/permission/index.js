import request from '@/utils/request'

export function listPermission() {
  return request({
    url:'/permission/list',
    method: 'get',
  })
}
