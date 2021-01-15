import {post, request} from './common'

export async function login(userName, password) {
  return await post(`/api/sessions`, {userName, password})
}

export async function logout() {
  return await request(`/api/sessions`, {method: 'DELETE', allowAutoLogout: false})
}
