import React from 'react'

import {Box, styled} from '@qiwi/pijma-core'
import {Status, Text} from '@qiwi/pijma-desktop'

import {FixedSizeList as List} from 'react-window'
import AutoSizer from 'react-virtualized-auto-sizer'

import {PageLoader} from '../../mol/Loader'
import {StatusIcon} from '../../mol/Icon'

const StyledItem = styled(Box)`
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  padding: 6px 6px;
  cursor: pointer;

  &:hover {
    background: ${props => props.theme.color.gray.lightest};
  }
`

const ErrorMessage = ({error}) => <Box m={3}>
  <Status
    title={"Cannot load deployments"}
    content={error.message}
  />
</Box>

const NotFoundMessage = () => <Box m={3}>
  <Status
    title={"No deployments found"}
    content={"Try to change search request"}
  />
</Box>

const Deployment = ({ deployment, hideNamespaces, onSelect }) => <StyledItem onClick={() => onSelect && onSelect(deployment)}>
  <Box display="inline" mr={1} mt={-0.5}>
    <StatusIcon size="s" status={deployment.status} />
  </Box>
  { !hideNamespaces &&
  <Box display="inline" mr={1}>
    <Text size="s" color="support">{deployment.metadata.namespace}</Text>
  </Box>
  }
  <Box display="inline" mr={1}>
    <Text>{deployment.metadata.name}</Text>
  </Box>
  <Box display="inline">
    <Text size="s" color="support">{deployment.info.images.map(i => i.version).join(', ')}</Text>
  </Box>
</StyledItem>

export const DeploymentsList = ({loading, error, items, onSelect}) => {
  if (loading) {
    return <PageLoader />
  }

  if (error) {
    return <ErrorMessage error={error} />
  }

  if (!items || !items.length) {
    return <NotFoundMessage />
  }

  return <AutoSizer>
    {(autoSizerProps) => <List {...autoSizerProps} itemCount={items.length} itemSize={33}>
      {({index, style}) => <div style={style}>
        <Deployment deployment={items[index]} onSelect={onSelect} />
      </div>}
    </List>}
  </AutoSizer>
}
