import React from 'react'

import {Card, CardContent, CardHeader} from '../../mol/Card'

import {PodsTable} from '../../org/PodsTable'

export const PodsCard = ({data, onPodClick, onLogClick}) => <Card loading={data.loading} error={data.error}>
  <CardHeader text="Pods"/>
  <CardContent expand render={() => <PodsTable pods={data.data} onPodClick={onPodClick} onLogClick={onLogClick}/>} />
</Card>