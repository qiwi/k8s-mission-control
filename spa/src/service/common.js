import loadConfig from '../config'
import {store} from '../store'
import {logout} from '../modules/auth'
import {readCsrfToken} from './security'

const config = loadConfig()

export async function request(path, options) {
  const mergedOptions = {
    allowAutoLogout: true,
    allowUnauthorizedRetry: options.method === 'GET',

    ...options,
    headers: {
      ...options.headers,
      'X-CSRF-TOKEN': readCsrfToken()
    }
  }

  const response = await fetch(config.api.url + path, mergedOptions)

  try {
    return await handleResponse(response)
  } catch (e) {
    if (e instanceof ApiError && e.isUnauthorized()) {
      if (mergedOptions.allowAutoLogout) {
        console.log('We has received 401 error, logout...')
        store.dispatch(logout())
      }

      if (mergedOptions.allowUnauthorizedRetry) {
        return request(path, {
          ...mergedOptions,
          allowAutoLogout: false,
          allowUnauthorizedRetry: false
        })
      }
    }

    throw e
  }
}

export function get(path) {
  return request(path, {method: 'GET'})
}

export async function post(path, data) {
  return request(path, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json;charset=utf-8'
    },
    body: JSON.stringify(data)
  })
}

export async function handleResponse(response) {
  if (response.ok) {
    return await response.json()
  } else {
    let error = await response.json()
    if (response.status === 401) {
      throw new ApiError(API_UNAUTHORIZED_ERROR, error.errorCode, error.userMessage)
    } else if (response.status === 404) {
      throw new ApiError(API_ERROR_NOT_FOUND, error.errorCode, error.userMessage)
    } else {
      throw new ApiError(API_ERROR_SERVER_ERROR, error.errorCode, error.userMessage)
    }
  }
}

export const API_ERROR_NOT_FOUND = 'not_found'
export const API_ERROR_SERVER_ERROR = 'server_error'
export const API_UNAUTHORIZED_ERROR = 'unauthorized'

const titles = {
  'default': 'Error occurred',
  [API_ERROR_NOT_FOUND]: 'Resource not found',
  [API_ERROR_SERVER_ERROR]: 'Error occurred',
  [API_UNAUTHORIZED_ERROR]: 'Access denied'
}

const messages = {
  'auth.unauthorized': 'Wrong authentication data',
  'default': 'Error occurred, try again later',
  'internal.error': 'Internal error',
  'http.argument.type.mismatch': 'Bad request: wrong data type',
  'http.code.conversion.failed': 'HTTP code conversion failed',
  'http.media.type.not.acceptable': 'Wrong media type',
  'http.media.type.not.supported': 'Unsupported media type',
  'http.message.conversion.failed': 'Bad request',
  'http.method.not.supported': 'HTTP method not supported',
  'http.missing.request.parameter': 'Missing request parameter(s)',
  'http.url.not.found': 'Resource not found',
  'validation.error': 'Validation error',

  'mission-control.resource.not-found': 'Resource not found',
  'mission-control.clusters.not-found': 'Cluster not found',
  'mission-control.clusters.unavailable': 'Cluster API unavailable',
  'mission-control.auth.invalid-credentials': 'Incorrect user name or password'
}

export class ApiError extends Error {
  constructor(type, code, userMessage) {
    super(`Error occurred ${code}: ${userMessage}`)

    this.type = type
    this.code = code
    this.titleMessage = titles[type] || titles['default']
    this.userMessage = userMessage || messages[code] || messages['default']
  }

  static notFound(message) {
    return new ApiError(API_ERROR_NOT_FOUND, 'site.page.not.found', message)
  }

  isUnauthorized() {
    return this.type === API_UNAUTHORIZED_ERROR
  }
}