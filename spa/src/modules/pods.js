import {AbstractResourceModule, initialResource} from './common/abstractResourceModule'

import {getPod} from '../service/deploymentService'

const pods = new AbstractResourceModule('pods', p => `${p.cluster}-${p.ns}-${p.pod}`, p => getPod(p.cluster, p.ns, p.pod))

export default pods.handle.bind(pods)
export const loadPod = (cluster, ns, pod) => pods.load({cluster, ns, pod})
export const selectPod = (cluster, ns, pod) => state => state.pods.byKeys[`${cluster}-${ns}-${pod}`] || initialResource