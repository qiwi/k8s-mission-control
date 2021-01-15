import {AbstractResourceModule, initialResource} from './common/abstractResourceModule'

import {getReplicaSet} from '../service/deploymentService'

const replicasets = new AbstractResourceModule(
  'replicasets',
  p => `${p.cluster}-${p.ns}-${p.replicaset}`,
  p => getReplicaSet(p.cluster, p.ns, p.replicaset)
)

export default replicasets.handle.bind(replicasets)
export const loadReplicaSet = (cluster, ns, replicaset) => replicasets.load({cluster, ns, replicaset})
export const selectReplicaSet = (cluster, ns, replicaset) => state => state.replicasets.byKeys[`${cluster}-${ns}-${replicaset}`] || initialResource