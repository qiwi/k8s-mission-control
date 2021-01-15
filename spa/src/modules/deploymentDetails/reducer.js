export const LOAD_DEPLOYMENT_DETAILS = 'deployment/LOAD_DEPLOYMENT'
export const LOAD_DETAILS_COMPLETE = 'deployment/LOAD_DEPLOYMENT_COMPLETE'
export const LOAD_DETAILS_FAIL = 'deployment/LOAD_DEPLOYMENT_FAIL'

export const INITIAL_ITEM = {
  loading: true,
  error: false,
  data: null,
  items: []
}

const initialState = {
  byDeployments: {}
}

const update = (resourceType, deploymentKey, newState) => (state) => ({
  ...state,
  byDeployments: {
    ...state.byDeployments,
    [deploymentKey]: {
      ...state.byDeployments[deploymentKey],
      [resourceType]: newState
    }
  }
})

export const reducer = (state = initialState, action) => {
  const {deploymentKey, resourceType, data, error} = (action.payload || {})

  switch (action.type) {
    case LOAD_DEPLOYMENT_DETAILS:
      return state
    case LOAD_DETAILS_COMPLETE:
      return update(resourceType, deploymentKey, {loading: false, error: null, data})(state)
    case LOAD_DETAILS_FAIL:
      return update(resourceType, deploymentKey, {loading: false, error})(state)
    default:
      return state
  }
}

