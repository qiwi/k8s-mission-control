import React from 'react'

import {Icon, Box} from '@qiwi/pijma-core'
import {Text} from '@qiwi/pijma-desktop'

import {DropDownButton} from '../mol/DropDownButton'

const getNewSortMode = (oldField, oldDirection, field) => {
  if (oldField !== field) {
    return { field, direction: 'asc' }
  } else if (oldDirection === 'asc') {
    return { field, direction: 'desc' }
  } else {
    return { field, direction: 'asc' }
  }
}

export const SortModeButton = ({ fields, selectedField, selectedDirection, onSelect }) => {
  return <DropDownButton
    autoClose={false}
    items={Object.values(fields)}
    onItemClick={item => {
      const {field, direction} = getNewSortMode(selectedField, selectedDirection, item.name)
      onSelect(field, direction)
    }}
    textFunc={() => <>
      <Text>Sorting by {fields[selectedField].displayName}</Text>
      <Box ml={1} display="inline">
        <Icon name={`sort-${selectedDirection}`} size={5} />
      </Box>
    </>}
    itemTextFunc={item => <>
      <Text>{item.displayName}</Text>
      {selectedField === item.name && <Box ml={1} display="inline"><Icon name={`sort-${selectedDirection}`} size={5} /></Box>}
    </>}
    isSelectedFunc={item => item.name === selectedField}
  />
}