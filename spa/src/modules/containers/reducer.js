export const initContainerState = {
    logContainer: {
        cluster: '',
        ns: '',
        pod: '',
        activeContainer: undefined,
    },
    isReadyForLog: false
}

export const SWITCH_CONTAINER = 'pod/SWITCH_CONTAINER'
export const LOG_READY = 'pod/LOG_READY'

export default (state = initContainerState, action) => {
    switch (action.type) {
        case SWITCH_CONTAINER:
            const {cluster, ns, pod, activeContainer} = (action.payload || {})
            return {
                ...state,
                logContainer: {
                    cluster: cluster,
                    ns: ns,
                    pod: pod,
                    activeContainer: activeContainer
                }
            }
        case LOG_READY:
            return {
                ...state,
                isReadyForLog: action.payload.isReadyForLog
            }
        default:
            return state
    }
}

