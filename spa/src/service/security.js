const USER_INFO_KEY = 'mission-control-user-info'
const CSRF_TOKEN_KEY = 'mission-control-csrf-token'

export const deleteAuthData = () => {
  localStorage.setItem(USER_INFO_KEY, '')
  localStorage.setItem(CSRF_TOKEN_KEY, '')
}

export const saveAuthData = (userInfo, csrfToken) => {
  localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo))
  localStorage.setItem(CSRF_TOKEN_KEY, csrfToken)
}

export const readUserInfo = () => {
  const str = localStorage.getItem(USER_INFO_KEY)
  if (!str) return null
  return JSON.parse(str)
}

export const readCsrfToken = () => {
  return localStorage.getItem(CSRF_TOKEN_KEY)
}