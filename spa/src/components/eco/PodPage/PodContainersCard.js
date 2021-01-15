import React from 'react'

import {SimpleCard} from '../../mol/Card'
import {Table} from '../../mol/Table'
import {StatusIcon} from '../../mol/Icon'
import {DockerImageText} from '../../mol/DockerImageText'

export const ContainersTable = ({containers}) => <Table
  columns={[
    {key: 'status', text: '', width: 40},
    {key: 'name', text: 'Name', align: 'right', maxWidth: 200, nowrap: true},
    {key: 'image', text: 'Image', align: 'right', maxWidth: 200, nowrap: true},
    {key: 'ports', text: 'Ports' },
    {key: 'restarts', text: 'Restarts' }
  ]}
  mappers={{
    status: (container) => <StatusIcon status={container.status} />,
    name: (container) => container.name,
    image: (container) => <DockerImageText image={container.image} />,
    ports: (container) => container.ports.map(p => p.port).join(', '),
    restarts: (container) => container.restartsCount
  }}
  items={containers}
/>

export const PodContainersCard = ({pod}) => <SimpleCard header="Containers" expand>
  <ContainersTable containers={pod.info.containers} />
</SimpleCard>