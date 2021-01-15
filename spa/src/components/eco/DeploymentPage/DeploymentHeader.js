import React from 'react'
import {connect} from 'react-redux'

import {Flex, FlexItem} from '@qiwi/pijma-core'
import {Button, Caption} from '@qiwi/pijma-desktop'

import {HeaderButton} from '../../mol/HeaderButton'

import {goToDeployment, goToPodLog} from '../../../modules/routes'
import {selectClusters} from '../../../modules/clusters'
import {loadDeployment, selectDeploymentInfo, selectPods} from '../../../modules/deploymentDetails'
import {StatusIcon} from '../../mol/Icon'

const DeploymentHeader = ({details, cluster, ns, deployment, clusters, pods, goToDeployment, goToLog}) => {
  return <Flex
  >
    <Flex
        align="center"
        mb={2}
        pl={8}
        style={{flex: "70%", textAlign: "left"}}>
      <FlexItem mr={2} pb={1}>
        <StatusIcon stub={details.loading} status={details.data ? details.data.status : { value: 'UNKNOWN' }} />
      </FlexItem>

      <FlexItem>
        <Caption color="default">
          {ns} | {deployment}
        </Caption>
      </FlexItem>

      {clusters && clusters.map((c, index) => <FlexItem key={index} pl={2}>
        <HeaderButton active={c.name === cluster} onClick={() => goToDeployment(c.name)} text={c.displayName}/>
      </FlexItem>)}
    </Flex>
    <Flex mb={2} style={{textAlign: "end"}}>
      <FlexItem justify="right" style={{flex: "25%", textAlign: "end"}}>
        <Button kind="simple" size="minor" text="Лог.." onClick={()=>goToLog(pods.data[0].metadata.name)} type="button"/>
      </FlexItem>
    </Flex>
  </Flex>
}

export const ConnectedDeploymentHeader = connect(
  (state, {cluster, ns, deployment}) => ({
    details: selectDeploymentInfo(cluster, ns, deployment)(state),
    clusters: selectClusters(state),
    pods: selectPods(cluster, ns, deployment)(state),
  }),
  (dispatch, ownProps) => ({
    goToDeployment: cluster => {
      dispatch(loadDeployment(cluster, ownProps.ns, ownProps.deployment))
      dispatch(goToDeployment(cluster, ownProps.ns, ownProps.deployment))
    },
    goToLog: (pod) => dispatch(goToPodLog(ownProps.cluster, ownProps.ns, ownProps.deployment, pod))
  })
)(DeploymentHeader)