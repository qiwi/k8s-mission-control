import React from 'react'
import {connect} from 'react-redux'

import {Flex, FlexItem, Icon} from '@qiwi/pijma-core'

import {SearchField} from '../../mol/SearchField'
import {HeaderButton} from '../../mol/HeaderButton'
import {SortModeButton} from '../../org/SortModeButton'

import {SORT_FIELDS_BY_NAME} from '../../../modules/deployments'
import {selectClustersWithVisibility, toggleClusterVisibility} from '../../../modules/clusters'
import {
  setClustersSortMode,
  selectClustersSortMode,
  toggleClustersAutoReload,
  selectClustersAutoReload
} from '../../../modules/profile'

import {SelectClustersButton} from './SelectClustersButton'

const ClustersHeader = ({ clusters, toggleClusterVisibility,
                          sortField, sortDirection, setSortMode,
                          autoReload, toggleAutoReload,
                          search, onSearchChange
}) => <Flex align="center" justify='space-between' mb={3}>
  <FlexItem>
    <Flex align="center">
      <FlexItem mr={2}>
        <SelectClustersButton clusters={clusters || []} toggleClusterVisibility={toggleClusterVisibility} />
      </FlexItem>

      <FlexItem mr={2}>
        <SortModeButton
          fields={SORT_FIELDS_BY_NAME}
          selectedField={sortField}
          selectedDirection={sortDirection}
          onSelect={(field, direction) => setSortMode({ field, direction })}
        />
      </FlexItem>

      <FlexItem mr={2}>
        <HeaderButton active={autoReload !== 0} onClick={() => toggleAutoReload()}
                      text="Auto reload" icon={<Icon name="clock" size={5} />}
                      iconPosition="left"
        />
      </FlexItem>
    </Flex>
  </FlexItem>
  <FlexItem width={90}>
    <SearchField value={search} placeholder="Filter" onChange={(value) => onSearchChange(value)} />
  </FlexItem>
</Flex>

export const ConnectedClustersHeader = connect(
  state => ({
    clusters: selectClustersWithVisibility(state),
    sortField: selectClustersSortMode(state).field,
    sortDirection: selectClustersSortMode(state).direction,
    autoReload: selectClustersAutoReload(state)
  }),
  dispatch => ({
    toggleClusterVisibility: (cluster) => dispatch(toggleClusterVisibility(cluster)),
    setSortMode: (mode) => dispatch(setClustersSortMode(mode.field, mode.direction)),
    toggleAutoReload: () => dispatch(toggleClustersAutoReload())
  })
)(ClustersHeader)