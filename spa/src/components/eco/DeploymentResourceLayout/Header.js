import React from 'react'

import {Flex, FlexItem} from '@qiwi/pijma-core'
import {Caption} from '@qiwi/pijma-desktop'

import {StatusIcon} from '../../mol/Icon'

export const ResourceStatusHeader = ({resource, name}) => <Flex
  align="center"
  mb={2} mt={2}
>
  <FlexItem mr={2} pb={1}>
    <StatusIcon stub={resource.loading} status={resource.data ? resource.data.status : { value: 'UNKNOWN' }} />
  </FlexItem>

  <FlexItem>
    <Caption color="default">{name}</Caption>
  </FlexItem>
</Flex>