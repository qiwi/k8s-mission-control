import React from 'react'

import {Text} from '@qiwi/pijma-desktop'

import {Tooltip} from './Tooltip'

export const DockerImageText = ({image, hideImageName}) => {
  return <Tooltip tip={image.fullName}>
    <Text>
      {hideImageName ? image.version : image.fullName}
    </Text>
  </Tooltip>
}