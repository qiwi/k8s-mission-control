import loadConfig from './config'
import * as queryString from 'query-string'

export const FEATURE_SHOW_LOGIN_BUTTON = {
  name: 'show_login_button',
  defaultValue: true
}

export const FEATURE_DEPLOYMENT_ENDPOINTS_MODE = {
  name: 'show_deployment_endpoints',
  defaultValue: true
}

export const checkFeature = (feature) => {
  const qsFeatures = (queryString.parse(window.location.search).features || '').split(',')

  const config = loadConfig()
  const value = config.site.features[feature.name]

  if (typeof value === 'undefined') {
    return feature.defaultValue
  }

  if (typeof value === 'string' && value === 'user') {
    return qsFeatures.indexOf(feature.name) > -1
  }

  if (typeof value === 'string' && value === 'false') {
    return false
  }

  return value
}