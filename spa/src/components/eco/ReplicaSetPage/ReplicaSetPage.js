import React from 'react'
import {connect} from 'react-redux'

import {PodsTable} from '../../org/PodsTable'
import {SimpleCard} from '../../mol/Card'

import {selectReplicaSet, loadReplicaSet} from '../../../modules/replicasets'
import {goToPod, goToPodLog} from '../../../modules/routes'

import {DeploymentResourceLayout} from '../DeploymentResourceLayout'

import {ReplicaSetGeneralCard} from './ReplicaSetGeneralCard'
import {createProvider} from '../../../utils/provider'

export const ReplicaSetPage = ({cluster, ns, deploymentName, replicaSetName, resource, goToPod, goToLog}) => <DeploymentResourceLayout
  {...{cluster, ns, deploymentName, resourceName: replicaSetName, resource}}
>
  <ReplicaSetGeneralCard replicaSet={resource.data} />

  <SimpleCard header="Pods" expand>
    <PodsTable pods={resource.data ? resource.data.info.pods : {}} onPodClick={pod => goToPod(pod.metadata.name)} onLogClick={pod => goToLog(pod.metadata.name)}/>
  </SimpleCard>
</DeploymentResourceLayout>

const ReplicaSetProvider = createProvider(
  (state, props) => ({
    resource: selectReplicaSet(props.cluster, props.ns, props.replicaSetName)(state)
  }),
  props => loadReplicaSet(props.cluster, props.ns, props.replicaSetName)
)

export const ConnectedReplicaSetPage = connect(
  () => ({}),
  (dispatch, ownProps) => ({
    goToPod: (podName) => dispatch(goToPod(ownProps.cluster, ownProps.ns, ownProps.deploymentName, podName)),
    goToLog: (podName) => dispatch(goToPodLog(ownProps.cluster, ownProps.ns, ownProps.deploymentName, podName))
  })
)(props => <ReplicaSetProvider {...props} component={ReplicaSetPage} />)