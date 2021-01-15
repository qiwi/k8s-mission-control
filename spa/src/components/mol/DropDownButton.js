import React from 'react'

import {Box, Pos, styled} from '@qiwi/pijma-core'
import {DropDown} from '@qiwi/pijma-desktop'

import {HeaderButton} from './HeaderButton'
import {Card, CardContent} from './Card'

const StyledItem = styled(Box)`
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  padding: ${({theme}) => `${theme.scale * 3}px ${theme.scale * 5}px`};
  cursor: pointer;
  
  background: ${({theme, active}) => active ? theme.color.gray.light : theme.color.gray.white};
  &:hover {
    background: ${({theme, active}) => active ? theme.color.gray.light : theme.color.gray.lightest};
  }
`

export const DropDownButton = ({items, onItemClick, text, textFunc, itemTextFunc, isSelectedFunc, autoClose = true}) => {
  const [showDropdown, setShowDropdown] = React.useState(false)
  const container = React.useRef()
  const button = React.useRef()

  const getButtonText = textFunc || text || 'Empty'
  const getItemText = itemTextFunc || (item => item)
  const clickItem = (item, index) => {
    if (onItemClick) {
      onItemClick(item, index)
    }
    if (autoClose) {
      setShowDropdown(false)
    }
  }

  return <Pos type="relative" ref={container}>
    <Box ref={button}>
      <HeaderButton
        active={showDropdown}
        onClick={() => setShowDropdown(true)}
        text={getButtonText()}
      />
    </Box>
    <DropDown
      target={button.current} container={container.current}
      show={showDropdown} onHide={() => setShowDropdown(false)}
      rootClose={true} offset={2}
    >
      <Card header="Sort mode" accent={true}>
        <CardContent expand>
          <Box minWidth={45}>
            {Object.values(items).map((item, index) => <StyledItem
              key={index} active={isSelectedFunc(item, index) ? "true" : undefined}
              onClick={() => clickItem(item, index)}
              children={getItemText(item, index)}
            />) }
          </Box>
        </CardContent>
      </Card>
    </DropDown>
  </Pos>
}