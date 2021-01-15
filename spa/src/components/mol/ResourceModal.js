import React, {useEffect} from 'react'
import {connect} from 'react-redux'

import {Heading, SimpleModal} from '@qiwi/pijma-desktop'
import {Spacer} from '@qiwi/pijma-core'

import LoadingBox from './LoadingBox'

import {closeModal} from '../../modules/routes'

/* Example of usageЖ
const PodModal = createConnectedResourceModal(
  () => 'Pod',
  (props, pod) => <PodPage pod={pod} />
  (state, props) => selectPod(props.cluster, props.ns, props.pod)(state),
  (props) => loadPod(props.cluster, props.ns, props.pod)
)

<PodModal cluster={} ns={} pod={} />
 */

const ResourceModal = ({header, render, resourceKey, resource, load, close }) => {
  useEffect(() => {
    load()
  }, resourceKey)

  return <SimpleModal
    show={true} size={'l'} onHide={() => close()}
    closable backdropClose escapeClose
  >
    <Spacer size="m">
      <Heading size="2">{header}</Heading>

      <LoadingBox loading={resource.loading} error={resource.error}>
        { resource.data && render() }
      </LoadingBox>
    </Spacer>
  </SimpleModal>
}

export const createConnectedResourceModal = (headerFunc, renderFunc, selectFunc, loadFunc) => connect(
  (state, ownProps) => {
    const resource = selectFunc(state, ownProps)
    return {
      resource: selectFunc(state, ownProps),
      header: headerFunc(ownProps),
      render: () => renderFunc(ownProps, resource.data),
      resourceKey: Object.values(ownProps)
    }
  },
  (dispatch, ownProps) => ({
    close: () => dispatch(closeModal()),
    load: () => dispatch(loadFunc(ownProps))
  })
)(ResourceModal)
