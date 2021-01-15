import * as router from 'connected-react-router'
import * as queryString from 'query-string'
import { createSelector } from 'reselect'

import { ClustersSettings } from './profile'

export const isNavActive = state => type => {
  let path = state.router.location.pathname
  if (type === 'clusters') {
    return path === '/clusters'
  } else {
    return false
  }
}

export const selectClustersPageQuery = createSelector(
  state => state.router.location,
  location => {
    let {pathname, search} = location
    if (pathname !== '/clusters') return undefined
    return ClustersSettings.parseQueryString(queryString.parse(search))
  }
)

export const selectIsOnLoginModal = createSelector(
  state => state.router.location,
  location => {
    let {modal} = queryString.parse(location.search)
    return modal === 'login'
  }
)

export const updateClustersPageQuery = (newState) => (dispatch, getState) => {
  let {pathname} = getState().router.location
  if (pathname !== '/clusters') return
  dispatch(router.push({ search: '?' + queryString.stringify(newState.toQueryObject(), {strict: false}) }))
}

export const selectSearchQuery = state => state.router.location.query.search

export const pushSearchQuery = value => router.push({ search: value ? `?search=${value}` : '' })

export const goToClusters = () => router.push('/clusters')

export const goToDeployment = (cluster, ns, deployment) =>
  router.push(`/clusters/${cluster}/deployments/${ns}/${deployment}`)

export const goToPod = (cluster, ns, deployment, pod) =>
  router.push(`/clusters/${cluster}/deployments/${ns}/${deployment}/pods/${pod}`)

export const goToPodLog = (cluster, ns, deployment, pod) =>
  router.push(`/clusters/${cluster}/deployments/${ns}/${deployment}/pods/${pod}/logs`)

export const goToReplicaSet = (cluster, ns, deployment, replicaset) =>
  router.push(`/clusters/${cluster}/deployments/${ns}/${deployment}/replicasets/${replicaset}`)

export function goToLoginModal() {
  return (dispatch, getState) => {
    let location = getState().router.location
    dispatch(router.push({
      pathname: location.pathname,
      search: `?modal=login`
    }))
  }
}

export function closeModal() {
  return (dispatch, getState) => {
    let location = getState().router.location

    let qs = queryString.parse(location.search)
    delete qs.modal

    dispatch(router.push({
      pathname: location.pathname,
      search: '?' + queryString.stringify(qs)
    }))
  }
}

export const goBack = () => router.goBack()

export const goMain = goToClusters