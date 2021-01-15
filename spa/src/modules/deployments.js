import * as lodash from 'lodash'
import { createSelector } from 'reselect'

import { getDeployments } from '../service/deploymentService'
import { selectClusters } from './clusters'
import { selectClustersSortMode } from './profile'

const initialState = {
  byClusters: { }
}

const initialDeployment = {
  loading: true,
  error: false,
  items: []
}

export const SORT_FIELDS_BY_NAME = {
  name: {
    name: 'name',
    accessor: v => v.metadata.name,
    displayName: 'Name'
  },
  date: {
    name: 'date',
    accessor: v => v.metadata.creationDateTime,
    displayName: 'Status Time'
  },
  status: {
    name: 'status',
    accessor: v => v.status.value,
    displayName: 'Status'
  }
}

export const SORT_FIELDS = Object.values(SORT_FIELDS_BY_NAME)

const DEFAULT_SORT_FIELD = SORT_FIELDS_BY_NAME.name

export const LOAD_DEPLOYMENTS = 'clusters/LOAD_DEPLOYMENTS'
export const LOAD_DEPLOYMENTS_COMPLETE = 'clusters/LOAD_DEPLOYMENTS_COMPLETE'
export const LOAD_DEPLOYMENTS_FAIL = 'clusters/LOAD_DEPLOYMENTS_FAIL'

function updateDeployments(state, cluster, func) {
  return {
    byClusters: {
      ...state.byClusters,
      [cluster]: func(state.byClusters[cluster] || {})
    }
  }
}

export default (state = initialState, action) => {
  switch (action.type) {
    case LOAD_DEPLOYMENTS:
      return updateDeployments(state, action.cluster.name, s => ({
        ...s,
        loading: true,
        error: false
      }))
    case LOAD_DEPLOYMENTS_COMPLETE:
      return updateDeployments(state, action.cluster.name, s => ({
        ...s,
        loading: false,
        error: false,
        items: action.items
      }))
    case LOAD_DEPLOYMENTS_FAIL:
      return updateDeployments(state, action.cluster.name, s => ({
        ...s,
        loading: false,
        error: action.error
      }))
    default:
      return state
  }
}

export const selectDeploymentsByCluster = (clusterName) => createSelector(
  state => state.deployments.byClusters[clusterName] || initialDeployment,
  selectClustersSortMode,
  (deployments, { field, direction }) => sortDeployments(deployments, field, direction)
)

const sortDeployments = (deployments, fieldName, direction) => {
  const items = deployments.items
  if (!items) {
    return deployments
  }

  const field = SORT_FIELDS_BY_NAME[fieldName]
  if (!field) {
    throw new Error(`Can't sort by unknown field ${fieldName}`)
  }

  const sortedItems = lodash.orderBy(items, [field.accessor, DEFAULT_SORT_FIELD.accessor], [direction, 'asc'])

  return {
    ...deployments,
    items: sortedItems
  }
}

export function loadDeployments() {
  return async (dispatch, getState) => {
    const clusters = selectClusters(getState())
    clusters.forEach(cluster => dispatch(loadDeploymentsForCluster(cluster)))
  }
}

export function loadDeploymentsForCluster(cluster) {
  return async (dispatch) => {
    dispatch({ type: LOAD_DEPLOYMENTS, cluster })
    try {
      const items = await getDeployments(cluster.name)
      dispatch({ type: LOAD_DEPLOYMENTS_COMPLETE, cluster, items })
    } catch (error) {
      dispatch({ type: LOAD_DEPLOYMENTS_FAIL, cluster, error })
    }
  }
}
