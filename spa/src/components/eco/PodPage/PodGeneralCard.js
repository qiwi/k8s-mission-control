import React from 'react'

import {SimpleCard} from '../../mol/Card'
import {Field, Glossary} from '../../mol/Glossary'
import {DateTime} from '../../mol/DateTime'
import {AnnotationsList} from '../../mol/Annotation'

const GeneralInfo = ({pod}) => <Glossary columns={12}>
  <Field size={4} title="Creation time" children={<DateTime value={pod.metadata.creationDateTime} />} />
  <Field size={4} title="IP" children={pod.info.podIp || '-'} />
  <Field size={4} title="Node" children={pod.info.node} />

  <Field size={6} title="Labels" children={<AnnotationsList items={pod.metadata.labels} />} />
  <Field size={6} title="Annotations" children={<AnnotationsList items={pod.metadata.annotations} hideValues={true} />} />
</Glossary>

export const PodGeneralCard = ({pod}) => <SimpleCard header="General">
  <GeneralInfo pod={pod} />
</SimpleCard>