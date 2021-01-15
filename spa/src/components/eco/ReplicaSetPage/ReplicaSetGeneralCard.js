import React from 'react'

import {Spacer} from '@qiwi/pijma-core'

import {Field, Glossary} from '../../mol/Glossary'
import {Card, CardContent, CardHeader} from '../../mol/Card'
import {DateTime} from '../../mol/DateTime'
import {DockerImageText} from '../../mol/DockerImageText'
import {AnnotationsList} from '../../mol/Annotation'

const GeneralInfo = ({replicaSet}) => <Spacer>
  <Glossary columns={12}>
    <Field size={3} title="Creation time" children={<DateTime value={replicaSet.metadata.creationDateTime} />} />
    <Field size={3} title="Pods" children={`${replicaSet.info.availablePods} / ${replicaSet.info.desiredPods}`} />
    <Field size={6} title="Images" children={<>{replicaSet.info.images.map((image, index) =>
      <DockerImageText key={index} image={image} />
    )}</>} />

    <Field size={6} title="Labels" children={<AnnotationsList items={replicaSet.metadata.labels} />} />
    <Field size={6} title="Annotations" children={<AnnotationsList items={replicaSet.metadata.annotations} hideValues={true} />} />
  </Glossary>
</Spacer>

export const ReplicaSetGeneralCard = ({replicaSet}) => <Card>
  <CardHeader text="General"/>
  <CardContent>
    <GeneralInfo replicaSet={replicaSet} />
  </CardContent>
</Card>