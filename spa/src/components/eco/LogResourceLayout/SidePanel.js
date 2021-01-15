import React, {useEffect} from "react"
import {connect} from "react-redux"
import {Flex, FlexItem, Spacer} from "@qiwi/pijma-core"

import {createProvider} from "../../../utils/provider"
import {loadDeployment, selectPods} from "../../../modules/deploymentDetails"
import {goToDeployment, goToPodLog} from "../../../modules/routes"
import {loadPod, selectPod} from "../../../modules/pods"
import {switchContainer} from "../../../modules/containers/actions"
import {selectActiveContainer} from "../../../modules/containers/selectors"

import {HeaderButton} from "../../mol/HeaderButton"
import {Icon, iconGoBack} from "../../mol/Icon"
import {Card, CardContent, CardHeader} from "../../mol/Card"
import {Table} from "../../mol/Table"

const ItemPanel = ({selectedItem, items, onClick}) => <Table
    showHeader={false}
    isActiveGetter={(item) => selectedItem === item}
    onRowClick={item => onClick(item)}
    columns={[
        {key: 'name', text: 'Name', align: 'left', width: 30}
    ]}
    mappers={{
        name: (item) => item
    }}
    items={items}
/>

function mapPods(pods) {
    return pods.map(pod => pod.metadata.name)
}

function mapContainers(pod) {
    return pod.info.containers.map(container => container.name)
}

const PodResourceSidePanel = ({cluster, ns, deploymentName, podName, pod, pods, activeContainer, onPodClick, goBackToDeployment, choiceContainer}) => {

    useEffect(() => {
        if (!pod.loading && pod.data) {
            let container = activeContainer || pod.data.info.containers[0].name
            choiceContainer(container)
        }
    }, [pod, activeContainer, choiceContainer])

    return <Spacer size="m">
        <Flex align="stretch" width="100%" mb={2}>
            <FlexItem width={1}>
                <HeaderButton icon={<Icon icon={iconGoBack}/>}
                              onClick={goBackToDeployment}
                              text={deploymentName.toUpperCase()}
                />
            </FlexItem>
        </Flex>
        <Card loading={pods.loading} error={pods.error}>
            <CardHeader text="Pods"/>
            <CardContent expand
                         render={() =>
                             <ItemPanel
                                 selectedItem={podName}
                                 items={mapPods(pods.data)}
                                 onClick={onPodClick}
                             />}
            />
        </Card>
        <Card loading={pod.loading} error={pod.error}>
            <CardHeader text="Containers"/>
            <CardContent expand render={() => <ItemPanel
                selectedItem={activeContainer}
                items={mapContainers(pod.data)}
                onClick={choiceContainer}/>}/>
        </Card>
    </Spacer>
}

const PodResourceProvider = createProvider(
    (state, props) => ({
        pods: selectPods(props.cluster, props.ns, props.deploymentName)(state),
        activeContainer: selectActiveContainer(props.cluster, props.ns, props.podName)(state),
        pod: selectPod(props.cluster, props.ns, props.podName)(state)
    }),
    props => (dispatch) => {
        dispatch(loadDeployment(props.cluster, props.ns, props.deploymentName))
        dispatch(loadPod(props.cluster, props.ns, props.podName))
    }
)

export const ConnectedPodResourceSidePanel = connect(
    () => ({}),
    (dispatch, ownProps) => ({
        goBackToDeployment: () => dispatch(goToDeployment(ownProps.cluster, ownProps.ns, ownProps.deploymentName)),
        onPodClick: (podName) => dispatch(goToPodLog(ownProps.cluster, ownProps.ns, ownProps.deploymentName, podName)),
        choiceContainer: (container) => dispatch(switchContainer(ownProps.cluster, ownProps.ns, ownProps.podName, container))
    })
)(props => <PodResourceProvider {...props} component={PodResourceSidePanel}/>)