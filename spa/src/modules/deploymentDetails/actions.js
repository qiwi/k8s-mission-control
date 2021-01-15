import * as service from '../../service/deploymentService'

import {LOAD_DEPLOYMENT_DETAILS, LOAD_DETAILS_COMPLETE, LOAD_DETAILS_FAIL} from './reducer'
import {makeKey} from './utils'

const resources = [
  { resourceType: 'info', loader: service.findDeployment },
  { resourceType: 'pods', loader: service.getDeploymentPods },
  { resourceType: 'replicaSets', loader: service.getDeploymentReplicaSets },
  { resourceType: 'services', loader: service.getDeploymentServices },
  { resourceType: 'endpoints', loader: service.getDeploymentEndpoints }
]

export function loadDeployment(cluster, ns, name) {
  const deploymentKey = makeKey(cluster, ns, name)
  return (dispatch) => {
    dispatch({ type: LOAD_DEPLOYMENT_DETAILS, payload: { deploymentKey } })

    resources.forEach(({ resourceType, loader }) => {
      loader(cluster, ns, name).then(data => {
        dispatch({ type: LOAD_DETAILS_COMPLETE, payload: { resourceType, deploymentKey, data } })
      }).catch(error => {
        dispatch({ type: LOAD_DETAILS_FAIL, payload: { resourceType, deploymentKey, error } })
      })
    })
  }
}