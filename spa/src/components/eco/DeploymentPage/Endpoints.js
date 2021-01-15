import React from 'react'

import {Link} from '@qiwi/pijma-desktop'

import {Table} from '../../mol/Table'
import {Card, CardContent, CardHeader} from '../../mol/Card'
import {StatusIcon} from '../../mol/Icon'

const EndpointsTable = ({data}) => <Table
  columns={[
    {key: 'name', text: 'Name', width: 140},
    {key: 'port', text: 'Port', width: 140},
    {key: 'nodePort', text: 'Node port', width: 140},
    {key: 'ingress', text: 'Ingress'},
  ]}
  mappers={{
    status: () => <StatusIcon stub status={{ value: 'OK' }} />,
    name: (item) => item.target.name,
    port: (item) => `${item.target.protocol} ${item.target.port}`,
    nodePort: (item) => item.nodePort ? <Address address={item.nodePort.address} /> : <span>-</span>,
    ingress: (item) => item.ingress ? <Address address={item.ingress.address} /> : <span>-</span>,
  }}
  items={data}
/>

const Address = ({address: {type, url, hostName, port}}) => {
  if (type === 'URL') {
    return <Link compact size="s" href={url} target="_blank">{url}</Link>
  } else if (type === 'HOST') {
    return <span>{hostName}:{port}</span>
  } else if (type === 'NODE_PORT') {
    return <span>{port}</span>
  } else {
    return <span>Can't display</span>
  }
}
export const EndpointsCard = ({endpoints}) => <Card loading={endpoints.loading} error={endpoints.error}>
  <CardHeader text="Endpoints" />
  <CardContent expand render={() => <EndpointsTable data={endpoints.data || []} />} />
</Card>