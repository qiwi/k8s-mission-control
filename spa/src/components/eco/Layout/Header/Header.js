import React from 'react'
import {connect} from 'react-redux'
import {Flex, FlexItem, Pos} from '@qiwi/pijma-core'
import {Header} from '@qiwi/pijma-desktop'

import {ConnectedAppBar} from './AppBar'
import {ConnectedProfileBar} from './ProfileBar'
import {ConnectedUserDropDown} from './UserDropDown'
import {ConnectedSearchBar} from './SearchBar'

const SiteHeader = () => {
  const [showDropdown, setShowDropdown] = React.useState(false)
  const container = React.useRef()
  const userDropDownTarget = React.useRef()
  const searchBarTarget = React.useRef()

  return <Pos type="relative" ref={container}>
    <Header>
      <Flex width={1} height={1} justify="space-between" ref={searchBarTarget}>
        <FlexItem align="stretch" shrink={0} ml={6}>
          <Flex height={1}>
            <FlexItem align="stretch">
              <ConnectedAppBar/>
            </FlexItem>
            <FlexItem align="center" shrink={0}>
              <ConnectedSearchBar container={container} target={searchBarTarget}/>
            </FlexItem>
          </Flex>
        </FlexItem>
        <FlexItem align="center" shrink={0} mr={6}>
          <Pos type="absolute" top={0} right={0} ref={userDropDownTarget} />
          <ConnectedProfileBar showUserDropDown={() => setShowDropdown(true)} />
        </FlexItem>
      </Flex>
    </Header>

    <ConnectedUserDropDown
      show={showDropdown}
      onHide={() => setShowDropdown(false)}
      container={container.current}
      target={userDropDownTarget.current}
    />
  </Pos>
}

export const ConnectedHeader = connect(
  (state) => ({
  }),
  (dispatch) => ({
  })
)(SiteHeader)
