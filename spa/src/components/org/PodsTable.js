import React from 'react'

import {Box, CrossBurger} from "@qiwi/pijma-core";

import {Table} from '../mol/Table'
import {DateTime} from '../mol/DateTime'
import {StatusIcon} from '../mol/Icon'

export const PodsTable = ({pods, onPodClick, onLogClick}) => <Table
  onRowClick={onPodClick}
  columns={[
    {key: 'status', text: '', width: 40},
    {key: 'name', text: 'Name', align: 'right', maxWidth: 200, nowrap: true},
    {key: 'ip', text: 'IP', onClick: e => e.stopPropagation()},
    {key: 'date', text: 'Creation time'},
    {key: 'node', text: 'Node'},
    {key: 'log', text: '', width: 40, onClick: e => e.stopPropagation()}
  ]}
  mappers={{
    name: (item) => item.metadata.name,
    date: (item) => <DateTime value={item.metadata.creationDateTime}/>,
    status: (item) => <StatusIcon status={item.status}/>,
    node: (item) => item.info.node,
    ip: (item) => item.info.podIp,
    log: (item) => <Box onClick={()=> onLogClick(item.metadata.name)}><CrossBurger/></Box>
  }}
  items={pods}
/>