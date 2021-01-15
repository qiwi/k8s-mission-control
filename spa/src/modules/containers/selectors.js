import {initContainerState} from "./reducer"

const INIT_STATE = initContainerState

export const selectActiveContainer = (cluster, ns, podName) => (state) => {
    const container = state.containers.logContainer || INIT_STATE
    if(container.cluster === cluster && container.ns === ns && container.pod === podName) {
        return container.activeContainer
    } else {
        return INIT_STATE.logContainer.activeContainer
    }
}

export const selectReadyForLog = (state) => {
    return state.containers.isReadyForLog || INIT_STATE.isReadyForLog
}
