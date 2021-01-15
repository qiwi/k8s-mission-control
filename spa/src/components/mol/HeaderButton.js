import {SectionLink, Text} from '@qiwi/pijma-desktop'
import {Box} from '@qiwi/pijma-core'
import React from 'react'

export const HeaderButton = ({ text, icon, active, onClick, iconPosition = 'left' }) => <SectionLink
  active={active} onClick={onClick} href={null}
>
  {() => <Box p={2}>
    <Text size="s">
      { iconPosition === 'left' && icon && <Box display="inline" mr={1}>
        {icon}
      </Box>}
      {text}
      { iconPosition === 'right' && icon && <Box display="inline" ml={1}>
        {icon}
      </Box>}
    </Text>
  </Box>}
</SectionLink>