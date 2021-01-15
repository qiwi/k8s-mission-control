import { createSelector } from 'reselect'
import * as lodash from 'lodash'

import { loadDeployments } from './deployments'
import { getClusters } from '../service/clusterService'
import { selectClustersList, setClustersList } from './profile'

export const LOAD_CLUSTERS = 'clusters/LOAD_CLUSTERS'
export const LOAD_CLUSTERS_COMPLETE = 'clusters/LOAD_CLUSTERS_COMPLETE'
export const LOAD_CLUSTERS_FAIL = 'clusters/LOAD_CLUSTERS_FAIL'

const initialState = {
  loading: false,
  error: false,
  items: []
}

export default (state = initialState, action) => {
  switch (action.type) {
    case LOAD_CLUSTERS:
      return {
        ...state,
        loading: true,
        error: false
      }
    case LOAD_CLUSTERS_COMPLETE:
      return {
        ...state,
        loading: false,
        error: false,
        items: action.clusters
      }
    case LOAD_CLUSTERS_FAIL:
      return {
        ...state,
        loading: false,
        error: action.error
      }
    default:
      return state
  }
}

export const selectClusters = state => state.clusters.items

export const selectClustersWithVisibility = createSelector(
  [selectClusters, selectClustersList],
  (clusters, list) => {
    if (!list || !list.length) {
      return clusters.map(c => ({...c, visible: true}))
    }

    return lodash(clusters).map(c => {
      let index = list.indexOf(c.name)
      return {...c, visible: index !== -1, order: index}
    }).value()
  }
)

export const selectVisibleClusters = createSelector(
  selectClustersWithVisibility,
  clusters => lodash(clusters).filter('visible').orderBy('order').value()
)

export const toggleClusterVisibility = cluster => (dispatch, getState) => {
  const clusters = selectClustersWithVisibility(getState())
  const newList = lodash(clusters)
    .filter(c => c.name === cluster ? !c.visible : c.visible)
    .map('name')
    .value()
  dispatch(setClustersList(newList))
}

export function loadClusters(deployments = true) {
  return async (dispatch) => {
    dispatch({ type: LOAD_CLUSTERS })

    try {
      const clusters = await getClusters()
      dispatch({ type: LOAD_CLUSTERS_COMPLETE, clusters: clusters })

      if (deployments) {
        dispatch(loadDeployments())
      }
    } catch (error) {
      dispatch({ type: LOAD_CLUSTERS_FAIL, error })
    }
  }
}
