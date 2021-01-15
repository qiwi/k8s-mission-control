import React from 'react'

import {Text} from '@qiwi/pijma-desktop'

import {Table} from '../../mol/Table'
import {DateTime} from '../../mol/DateTime'
import {DockerImageText} from '../../mol/DockerImageText'
import {StatusIcon} from '../../mol/Icon'
import {Card, CardContent, CardHeader} from '../../mol/Card'

const ReplicaSetsInfo = ({replicasets, onReplicaSetClick, hideImageNames = false}) => <Table
  onRowClick={onReplicaSetClick}
  columns={[
    {key: 'status', text: '', width: 40},
    {key: 'name', text: 'Name', align: 'right', maxWidth: 200, nowrap: true},
    {key: 'date', text: 'Creation time'},
    {key: 'pods', text: 'Pods'},
    {key: 'images', text: 'Images', align: 'right', maxWidth: 200, nowrap: true},
  ]}
  mappers={{
    status: (item) => <StatusIcon status={item.status}/>,
    name: (item) => item.metadata.name,
    date: (item) => <DateTime value={item.metadata.creationDateTime}/>,
    pods: (item) => <Text>{item.info.desiredPods} / {item.info.availablePods}</Text>,
    images: (item) => (<>{item.info.images.map((image, index) =>
      <DockerImageText key={index} image={image} hideImageName={hideImageNames} />
      )}</>)
  }}
  items={replicasets}
/>

export const ReplicaSetsCard = ({data, onReplicaSetClick, settings}) => <Card loading={data.loading} error={data.error}>
  <CardHeader text="Replica Sets / Releases"/>
  <CardContent expand render={() => <ReplicaSetsInfo replicasets={data.data} onReplicaSetClick={onReplicaSetClick} hideImageNames={settings.hideImageNames}/>} />
</Card>