import {createStore, combineReducers, applyMiddleware, compose} from 'redux'
import {connectRouter, routerMiddleware} from 'connected-react-router'

import thunk from 'redux-thunk'
import {createBrowserHistory} from 'history'

import auth from './modules/auth'
import profile, {loadProfile} from './modules/profile'
import clusters from './modules/clusters'
import deployments from './modules/deployments'
import pods from './modules/pods'
import replicasets from './modules/replicasets'
import deploymentDetails from './modules/deploymentDetails'
import containers from './modules/containers/reducer'

export const history = createBrowserHistory()

const initialState = {}
const enhancers = []
const middleware = [
  thunk,
  routerMiddleware(history)
]

if (process.env.NODE_ENV === 'development') {
  const devToolsExtension = window.__REDUX_DEVTOOLS_EXTENSION__

  if (typeof devToolsExtension === 'function') {
    enhancers.push(devToolsExtension())
  }
}

const composedEnhancers = compose(
  applyMiddleware(...middleware),
  ...enhancers
)

const rootReducer = combineReducers({
  router: connectRouter(history),
  auth, profile, clusters, deployments, pods, replicasets, deploymentDetails, containers
})

export const store = createStore(
  rootReducer,
  initialState,
  composedEnhancers
)

store.dispatch(loadProfile())