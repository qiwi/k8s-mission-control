import React from "react"
import {connect} from "react-redux"

import {Flex, FlexItem} from '@qiwi/pijma-core'

import {selectActiveContainer, selectReadyForLog} from "../../../modules/containers/selectors"
import {setReadyToGetLog} from "../../../modules/containers/actions"

import {ConnectedPodResourceSidePanel} from "../LogResourceLayout/SidePanel"
import {LogPanelWrapper} from "./LogPanelWrapper";

const LogPage = ({cluster, ns, deployment, podName, container}) => {

    return <Flex height="100%">
        <FlexItem width="calc(25% + -15px)">
            <ConnectedPodResourceSidePanel cluster={cluster} ns={ns} deploymentName={deployment} podName={podName}/>
        </FlexItem>

        <LogPanelWrapper cluster={cluster} ns={ns} deployment={deployment} podName={podName} container={container}/>
    </Flex>
}

const mapStateToProps = (state, props) => ({
    container: selectActiveContainer(props.cluster, props.ns, props.podName)(state),
    isReady: selectReadyForLog(state)
})

const mapDispatchToProps = (dispatch) => ({
    setReady: (isReady) => dispatch(setReadyToGetLog(isReady)),
})

export const ConnectedLogPage = connect(mapStateToProps, mapDispatchToProps)((props) => <LogPage {...props} />)