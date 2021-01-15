import React from 'react'
import {connect} from 'react-redux'

import {Card, Flex, FlexItem, Icon} from '@qiwi/pijma-core'
import {Actions, Button, DropDown, Text} from '@qiwi/pijma-desktop'

import {logout, selectUserDisplayName} from '../../../../modules/auth'

const UserDropDown = ({show, onHide, container, target, displayName, logout, goToSettings}) => <DropDown
  show={show}
  onHide={onHide}
  container={container}
  target={target}
>
  <Card s="0 28px 52px 0 rgba(0, 0, 0, 0.16)" bg="#fff" r={0} p={4} pr={8} pl={8}>
    <Flex direction="column">
      <FlexItem align="flex-end" pt={2} pb={6}>
        <Text size="l">
          {displayName}
        </Text>
      </FlexItem>
      <FlexItem>
        <Actions size="minor">
          <Button
            onClick={goToSettings}
            disabled
            icon={<Icon name="settings"/>}
            kind="simple" size="minor" text="Settings"
          />
          <Button
            onClick={logout}
            icon={<Icon name="logout"/>}
            kind="simple" size="minor" text="Log out"
          />
        </Actions>
      </FlexItem>
    </Flex>
  </Card>
</DropDown>

export const ConnectedUserDropDown = connect(
  (state) => ({
    displayName: selectUserDisplayName(state)
  }),
  (dispatch, ownProps) => ({
    logout: () => {
      ownProps.onHide()
      dispatch(logout())
    },
    goToSettings: () => {}
  })
)(UserDropDown)
