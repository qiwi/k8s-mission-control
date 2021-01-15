import React from 'react'
import {connect} from 'react-redux'

import {Flex, FlexItem, Spacer} from '@qiwi/pijma-core'

import {createProvider} from '../../../utils/provider'

import {Card, CardHeader, CardContent} from '../../mol/Card'
import {StatusIcon, Icon, iconGoBack} from '../../mol/Icon'
import {HeaderButton} from '../../mol/HeaderButton'
import {Table} from '../../mol/Table'

import {selectPods, selectReplicaSets, loadDeployment} from '../../../modules/deploymentDetails'
import {goToPod, goToReplicaSet, goToDeployment} from '../../../modules/routes'

const StatusNameList = ({activeName, items, onClick}) => <Table
  showHeader={false}
  onRowClick={item => onClick(item.metadata.name)}
  isActiveGetter={item => item.metadata.name === activeName}
  columns={[
    {key: 'status', text: '', width: 40},
    {key: 'name', text: 'Name', align: 'right', maxWidth: 200, nowrap: true}
  ]}
  mappers={{
    status: (item) => <StatusIcon status={item.status}/>,
    name: (item) => item.metadata.name
  }}
  items={items}
/>

const DeploymentResourceSidePanel = ({ deploymentName,
                                       podName = undefined, replicaSetName = undefined,
                                       pods, replicasets,
                                       goToPod, goToReplicaSet, goBackToDeployment}) => {
  return <Spacer size="m">
    <Flex align="stretch" width="100%" mb={2}>
      <FlexItem width={1}>
        <HeaderButton icon={<Icon icon={iconGoBack} />}
                      onClick={() => goBackToDeployment()}
                      text={deploymentName.toUpperCase()}
        />
      </FlexItem>
    </Flex>

    <Card loading={replicasets.loading} error={pods.error}>
      <CardHeader text="Replica Sets" />
      <CardContent expand render={() => <StatusNameList activeName={replicaSetName} items={replicasets.data} onClick={goToReplicaSet} />} />
    </Card>

    <Card loading={pods.loading} error={pods.error}>
      <CardHeader text="Pods" />
      <CardContent expand render={() => <StatusNameList activeName={podName} items={pods.data} onClick={goToPod} />} />
    </Card>
  </Spacer>
}

const PodsReplicaSetsProvider = createProvider(
  (state, props) => ({
    pods: selectPods(props.cluster, props.ns, props.deploymentName)(state),
    replicasets: selectReplicaSets(props.cluster, props.ns, props.deploymentName)(state)
  }),
  props => loadDeployment(props.cluster, props.ns, props.deploymentName),
)

export const ConnectedDeploymentResourceSidePanel = connect(
  () => ({}),
  (dispatch, ownProps) => ({
    goToPod: (podName) => dispatch(goToPod(ownProps.cluster, ownProps.ns, ownProps.deploymentName, podName)),
    goToReplicaSet: (replicaSetName) => dispatch(goToReplicaSet(ownProps.cluster, ownProps.ns, ownProps.deploymentName, replicaSetName)),
    goBackToDeployment: () => dispatch(goToDeployment(ownProps.cluster, ownProps.ns, ownProps.deploymentName))
  })
)(props => <PodsReplicaSetsProvider {...props} component={DeploymentResourceSidePanel} />)