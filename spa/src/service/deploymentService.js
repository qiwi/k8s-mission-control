import {get} from './common'

export function getDeployments(clusterName, deploymentCondition) {
    const condition =  deploymentCondition ? null : deploymentCondition
    return get(`/api/clusters/${clusterName}/deployments`, {
        deploymentCondition: condition
    })
}

export function getDeploymentsInNamespace(cluster, ns) {
    return get(`/api/clusters/${cluster}/namespaces/${ns}/deployments`)
}

export function findDeployment(cluster, ns, deployment) {
    return get(`/api/clusters/${cluster}/namespaces/${ns}/deployments/${deployment}`)
}

export function getDeploymentPods(cluster, ns, deployment) {
    return get(`/api/clusters/${cluster}/namespaces/${ns}/deployments/${deployment}/pods`)
}

export function getPod(cluster, ns, pod) {
    return get(`/api/clusters/${cluster}/namespaces/${ns}/pods/${pod}`)
}

export function getDeploymentsLog(cluster, ns, deployment, pod, container, limit, date, isBefore) {
    return get(`/api/clusters/${cluster}/namespaces/${ns}/deployments/${deployment}/logs?pod=${pod}&container=${container}&limit=${limit}&date=${date}&isBefore=${isBefore}`)
}

export function getDeploymentReplicaSets(cluster, ns, deployment) {
    return get(`/api/clusters/${cluster}/namespaces/${ns}/deployments/${deployment}/replicasets`)
}

export function getReplicaSet(cluster, ns, replicaset) {
    return get(`/api/clusters/${cluster}/namespaces/${ns}/replicasets/${replicaset}`)
}

export function getDeploymentServices(cluster, ns, deployment) {
    return get(`/api/clusters/${cluster}/namespaces/${ns}/deployments/${deployment}/services`)
}

export function getDeploymentEndpoints(cluster, ns, deployment) {
    return get(`/api/clusters/${cluster}/namespaces/${ns}/deployments/${deployment}/endpoints`)
}