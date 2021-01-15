import React from 'react'

import {Spacer} from '@qiwi/pijma-core'

import {Field, Glossary} from '../../mol/Glossary'
import {Card, CardContent, CardHeader} from '../../mol/Card'
import {AnnotationsList} from '../../mol/Annotation'

const GeneralInfo = ({deployment: {metadata, info}, columns}) => <Spacer>
  <Glossary columns={columns}>
    <Field size={4} title="Images" children={info.images.map(i => i.fullName).join(', ')} />
    <Field size={4} title="Labels" children={<AnnotationsList items={metadata.labels} />} />
    <Field size={4} title="Annotations" children={<AnnotationsList items={metadata.annotations} hideValues={true} />} />
  </Glossary>
</Spacer>

export const GeneralCard = ({data, columns}) => <Card loading={data.loading} error={data.error}>
  <CardHeader text="General"/>
  <CardContent render={() => <GeneralInfo columns={columns} deployment={data.data} />} />
</Card>