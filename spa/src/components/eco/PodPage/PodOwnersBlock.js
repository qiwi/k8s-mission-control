import React from 'react'

import {Spacer} from '@qiwi/pijma-core'

import {SimpleCard} from '../../mol/Card'
import {Field, Glossary} from '../../mol/Glossary'
import {StatusIcon} from '../../mol/Icon'
import {DateTime} from '../../mol/DateTime'

const PodOwnerInfo = ({owner}) => <Spacer>
  <Glossary columns={12}>
    <Field size={2} title="Status" children={<StatusIcon status={owner.status} />} />
    <Field size={4} title="Name" children={owner.metadata.name} />
    <Field size={3} title="Creation time" children={<DateTime value={owner.metadata.creationDateTime} />} />
    <Field size={3} title="Pods" children={`${owner.availablePods} / ${owner.desiredPods}`} />
  </Glossary>
</Spacer>

const PodOwnerCard = ({pod, owner}) => <SimpleCard header={owner.kind}>
  <PodOwnerInfo pod={pod} owner={owner} />
</SimpleCard>

export const PodOwnersBlock = ({pod}) => {
  return pod.info.owners.map((owner, index) => <PodOwnerCard key={index} pod={pod} owner={owner} />)
}