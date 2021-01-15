import React from 'react'

import {Text} from '@qiwi/pijma-desktop'

import {Card, CardContent, CardHeader} from '../mol/Card'
import {Table} from '../mol/Table'
import {StatusIcon} from '../mol/Icon'
import {Label} from '../mol/Label'

export const MessagesTable = ({messages, showResourceColumn = false, showStatusHeader = false}) => <Table
  columns={[
    {key: 'value', text: showStatusHeader ? 'Status' : '', width: 40},
    {key: 'resource', text: 'Resource', width: 120, hide: !showResourceColumn},
    {key: 'message', text: 'Message'},
  ]}
  mappers={{
    value: (item) => <StatusIcon status={item.level}/>,
    resource: (item) => item.resourceType,
    message: (item) => <>
      <ClarityLabel clarity={item.clarityLabel} />
      <Text>{(item.times > 1 ? `[${item.times} times] ` : '') + item.userMessage}</Text>
    </>,
  }}
  items={messages}
/>

const ClarityLabel = ({clarity}) => {
  if (clarity === 'HIGHEST' || clarity === 'HIGH') {
    return <>
      <Label>Hint</Label>
      <span> </span>
    </>
  } else {
    return false
  }
}

export const MessagesCard = (props) => {
  return <Card>
    <CardHeader text="Messages" />
    <CardContent expand render={() => <MessagesTable {...props} />} />
  </Card>
}