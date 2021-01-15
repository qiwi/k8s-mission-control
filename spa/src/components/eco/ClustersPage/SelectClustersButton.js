import React from 'react'

import {Icon, Box} from '@qiwi/pijma-core'
import {Text} from '@qiwi/pijma-desktop'

import {DropDownButton} from '../../mol/DropDownButton'

export const SelectClustersButton = ({ clusters, toggleClusterVisibility }) => {
  const visibleClusters = clusters.filter(c => c.visible).map(c => c.displayName).length
  const clustersText = visibleClusters === clusters.length ? "all" : `${visibleClusters} / ${clusters.length}`
  return <DropDownButton
    autoClose={false}
    items={clusters}
    onItemClick={item => toggleClusterVisibility(item.name)}
    textFunc={() => `Clusters: ${clustersText}`}
    itemTextFunc={item => <>
      <Box mr={1} display="inline"><Icon name={item.visible ? `eye-opened` : `eye-closed`} size={5} /></Box>
      <Text>{item.displayName}</Text>
    </>}
    isSelectedFunc={() => false}
  />
}