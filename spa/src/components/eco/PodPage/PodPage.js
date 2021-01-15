import React from 'react'

import {createProvider} from '../../../utils/provider'

import {selectPod, loadPod} from '../../../modules/pods'

import {DeploymentResourceLayout} from '../DeploymentResourceLayout'

import {PodGeneralCard} from './PodGeneralCard'
import {PodContainersCard} from './PodContainersCard'
import {PodOwnersBlock} from './PodOwnersBlock'

const PodProvider = createProvider(
  (state, props) => ({
    resource: selectPod(props.cluster, props.ns, props.podName)(state)
  }),
  props => loadPod(props.cluster, props.ns, props.podName)
)

export const PodPage = ({cluster, ns, deploymentName, podName, resource}) => <DeploymentResourceLayout
  {...{cluster, ns, deploymentName, resourceName: podName, resource}}
>
  <PodGeneralCard pod={resource.data} />
  <PodOwnersBlock pod={resource.data} />
  <PodContainersCard pod={resource.data} />
</DeploymentResourceLayout>

export const ConnectedPodPage = (props) => <PodProvider {...props} component={PodPage} />