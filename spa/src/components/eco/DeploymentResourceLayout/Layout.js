import React from 'react'

import {Spacer, Grid} from '@qiwi/pijma-core'

import {MessagesCard} from '../../org/Messages'
import {ResourceStatusHeader} from './Header'
import {ConnectedDeploymentResourceSidePanel} from './SidePanel'

import LoadingBox from '../../mol/LoadingBox'

export const DeploymentResourceLayout = ({cluster, ns, deploymentName, resourceName, resource, children}) => {
  return <Grid layout={[3, 9]}>
    <ConnectedDeploymentResourceSidePanel
      cluster={cluster} ns={ns} deploymentName={deploymentName}
      podName={resourceName} replicaSetName={resourceName}
    />

    <Spacer size="m">
      <ResourceStatusHeader name={resourceName} resource={resource} />

      <LoadingBox loading={resource.loading} error={resource.error} />

      { resource.data && resource.data.status.messages.length > 0 &&
      <MessagesCard messages={resource.data.status.messages} showStatusHeader={true} />
      }

      { resource.data && children }
    </Spacer>
  </Grid>
}