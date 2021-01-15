import {getDeploymentsLog} from "../../service/deploymentService"
import {LOG_READY, SWITCH_CONTAINER} from "./reducer"

export function switchContainer(cluster, ns, pod, activeContainer) {
    const payload = {
        cluster: cluster,
        ns: ns,
        pod: pod,
        activeContainer: activeContainer,
    }
    return async (dispatch) => { dispatch({type: SWITCH_CONTAINER, payload: payload}) }
}

export async function loadLog(cluster, ns, deploymentName, podName, containerName, limit, date, isBefore) {
    return getDeploymentsLog(cluster, ns, deploymentName, podName, containerName, limit, date, isBefore)
}

export function setReadyToGetLog(isReady) {
    return (dispatch) => {dispatch({type: LOG_READY, payload: {isReadyForLog: isReady}})}
}