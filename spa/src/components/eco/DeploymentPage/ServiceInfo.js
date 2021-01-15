import React from 'react'

import {Spacer} from '@qiwi/pijma-core'

import {Field, Glossary} from '../../mol/Glossary'
import {Card, CardContent, CardHeader} from '../../mol/Card'

const ServiceInfo = ({service}) => <Spacer>
  <Glossary>
    <Field title="Type" children={service.info.type} />
    <Field title="Cluster IP" children={service.info.clusterIp} />
  </Glossary>
  <Glossary>
    {service.info.ports.map((port, index) => <Field title={`Port '${port.name || port.port}'`} key={index}>
      <span>{port.protocol} </span>
      {port.targetPort && port.targetPort !== port.port ? <span>{port.targetPort} -> </span> : null}
      <span>{port.port}</span>
      <br/>
      {port.nodePort ? <span> {service.info.clusterIp}:{port.nodePort}</span> : null}
    </Field>)}
  </Glossary>
</Spacer>

export const ServiceCard = ({service}) => <Card>
  <CardHeader text="Service"/>
  <CardContent render={() => <ServiceInfo service={service} />} />
</Card>