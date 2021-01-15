import * as lodash from 'lodash'
import {createSelector} from 'reselect'

import {getClarityLabel, isHighOrAbove} from '../../model/clarity'

import {INITIAL_ITEM} from './reducer'
import {makeKey, makeOrder} from './utils'

const createDetailSelector = resourceType => (cluster, ns, name) => (state) => {
  const deploymentKey = makeKey(cluster, ns, name)
  return ((state.deploymentDetails.byDeployments[deploymentKey] || {})[resourceType]) || INITIAL_ITEM
}

export const selectDeploymentInfo = createDetailSelector('info')

export const selectPods = createDetailSelector('pods')

export const selectReplicaSets = (cluster, ns, name) => createSelector(
  createDetailSelector('replicaSets')(cluster, ns, name),
  (replicaSets) => {
    return {
      ...replicaSets,
      data: lodash.sortBy([...(replicaSets.data || [])], v => v.metadata.creationDateTime).reverse()
    }
  }
)

export const selectServices = createDetailSelector('services')

export const selectEndpoints = (cluster, ns, name) => createSelector(
  createDetailSelector('endpoints')(cluster, ns, name),
  (endpoints) => {
    if (!endpoints.data) return endpoints

    return {
      ...endpoints,
      data: lodash.map(endpoints.data, endpoint => {
        return {
          ...endpoint,
          nodePort: lodash.chain(endpoint.addresses).filter(address => address.name === 'Node port').head().value(),
          ingress: lodash.chain(endpoint.addresses).filter(address => address.name === 'Ingress URL').head().value()
        }
      })
    }
  }
)

export const selectMessages = (cluster, ns, name) => createSelector(
  selectDeploymentInfo(cluster, ns, name),
  selectServices(cluster, ns, name),
  selectReplicaSets(cluster, ns, name),
  selectPods(cluster, ns, name),
  (deployment, services, replicaSets, pods) => {
    const deploymentMessages =  deployment.data ? deployment.data.status.messages.map((msg, msgIndex) =>
      ({...msg, resourceType: 'Deployment', resource: deployment.data.metadata.name, order: [1, 0, msgIndex]})
    ) : []

    const servicesMessages = services.data ? services.data.flatMap((service, serviceIndex) => {
      return service.status.messages.map((msg, msgIndex) =>
        ({...msg, resourceType: 'Service', resource: service.metadata.name, order: [2, serviceIndex, msgIndex]})
      )
    }) : []

    const replicaSetsMessages = replicaSets.data ? replicaSets.data.flatMap((rs, rsIndex) => {
      return rs.status.messages.map((msg, msgIndex) =>
        ({...msg, resourceType: 'ReplicaSet', resource: rs.metadata.name, order: [3, rsIndex, msgIndex]})
      )
    }) : []

    const podsMessages = pods.data ? pods.data.flatMap((pod, podIndex) => {
      return pod.status.messages.map((msg, msgIndex) =>
        ({...msg, resourceType: 'Pod', resource: pod.metadata.name, order: [4, podIndex, msgIndex]})
      )
    }) : []

    const allMessages = lodash.chain([
      ...deploymentMessages,
      ...servicesMessages,
      ...replicaSetsMessages,
      ...podsMessages
    ])
      .sortBy(msg => msg.clarity * -1, msg => makeOrder(msg.order))
      .map(msg => ({...msg, clarityLabel: getClarityLabel(msg.clarity)}))
      .value()

    const maxErrorClarityIsHigh = isHighOrAbove(lodash.chain(allMessages)
      .filter(msg => msg.level === 'ERROR')
      .map(msg => msg.clarity)
      .max().value() || 0)

    const collapsedMessages = lodash.chain(allMessages)
      .groupBy('key')
      .values()
      .map(arr => ({...arr[0], times: arr.length}))
      .filter(msg => msg.level !== 'ERROR' || !maxErrorClarityIsHigh || isHighOrAbove(msg.clarity))
      .value()

    return {
      allMessages,
      collapsedMessages,
      count: allMessages.length,
      problemsCount: allMessages.filter(msg => msg.level === 'ERROR' || msg.level === 'WARN').length
    }
  }
)