import {merge} from 'lodash'
import {createSelector} from 'reselect'

import {selectClustersPageQuery, updateClustersPageQuery} from '../routes'

import {ClustersSettings} from './ClustersSettings'
import {SortMode} from './SortMode'

export const LOAD_PROFILE_COMPLETE = 'profile/LOAD_PROFILE_COMPLETE'

export const CLUSTERS_SET_CLUSTERS_AUTO_RELOAD = 'profile/clusters/SET_CLUSTERS_AUTO_RELOAD'
export const CLUSTERS_SET_CLUSTERS_SORT_MODE = 'profile/clusters/SET_CLUSTERS_SORT_MODE'

const initialState = {
  clusters: new ClustersSettings(),
  deployment: {
    hideImageNames: true
  }
}

export const REDUCER = (state = initialState, action) => {
  switch (action.type) {
    case LOAD_PROFILE_COMPLETE:
      return {
        ...state,
        ...action.payload
      }

    case CLUSTERS_SET_CLUSTERS_AUTO_RELOAD:
      return {
        ...state,
        clusters: state.clusters.withAutoReload(action.payload)
      }

    case CLUSTERS_SET_CLUSTERS_SORT_MODE:
      return {
        ...state,
        clusters: state.clusters.withSortMode(new SortMode(action.payload.fieldName, action.payload.direction))
      }

    default:
      return state
  }
}

export function loadProfile() {
  return async (dispatch) => {
    const settings = merge({}, initialState.settings)
    dispatch({type: LOAD_PROFILE_COMPLETE, payload: settings})
  }
}

const selectClustersSettings = createSelector(
  selectClustersPageQuery,
  state => state.profile.clusters,
  (fromQuery, stored) => fromQuery // TODO Join with stored state
)

export const selectClustersHideNamespaces = createSelector(
  selectClustersSettings,
  settings => settings.hideNamespaces
)

export const selectClustersSortMode = createSelector(
  selectClustersSettings,
  settings => settings.sortMode
)

export const selectClustersAutoReload = createSelector(
  selectClustersSettings,
  settings => settings.autoReload
)

export const selectClustersList = createSelector(
  selectClustersSettings,
  settings => settings.clusters
)

export const selectDeploymentSettings = (state) => state.profile.deployment

export const setClustersSortMode = (field, direction) => (dispatch, getState) => {
  let settings = selectClustersSettings(getState())
  dispatch(updateClustersPageQuery(settings.withSortMode(field, direction)))
}

export const toggleClustersAutoReload = () => (dispatch, getState) => {
  let settings = selectClustersSettings(getState())
  let newAutoReload = settings.autoReload ? 0 : 5
  dispatch(updateClustersPageQuery(settings.withAutoReload(newAutoReload)))
}

export const setClustersList = (clusters) => (dispatch, getState) => {
  let settings = selectClustersSettings(getState())
  dispatch(updateClustersPageQuery(settings.withClusters(clusters)))
}