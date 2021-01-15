import React from 'react'

import {Block, Box, Flex, styled} from "@qiwi/pijma-core"
import {Text} from '@qiwi/pijma-desktop'

const ClusterHeader = styled.div`
  border-bottom: 1px solid ${props => props.theme.color.gray.light}
`

export const ClusterCard = ({cluster, totalCount, warnsCount, errorsCount, children}) => {
  return <Block py={2} height="100%">
    <ClusterHeader>
      <Flex justify='space-between' pl={2} pr={2}>
        <Box mb={1}>{cluster.displayName} </Box>
        <Box>
          {Boolean(warnsCount) && <Box display="inline" mr={1}>
            <Text color="warning" size="s">{warnsCount}</Text>
          </Box>}
          {Boolean(errorsCount) && <Box display="inline" mr={1}>
            <Text color="failure" size="s">{errorsCount}</Text>
          </Box>}
          <Text>{totalCount}</Text>
        </Box>
      </Flex>
    </ClusterHeader>
    <Box mb={8} height="calc(100% - 27px)">
      {children}
    </Box>
  </Block>
}