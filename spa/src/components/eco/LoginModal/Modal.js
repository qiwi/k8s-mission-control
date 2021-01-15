import React from 'react'
import {connect} from 'react-redux'

import {Flex, FlexItem, Icon} from '@qiwi/pijma-core'
import {SimpleModal, Text,} from '@qiwi/pijma-desktop'

import {closeModal} from '../../../modules/routes'
import {selectIsAuthenticated} from '../../../modules/auth'

import {ConnectedLoginForm} from './Form'

const Modal = ({isAuthenticated, close}) => <SimpleModal
  show={true} size={'s'} onHide={() => close()}
  closable backdropClose escapeClose
>
  <Flex direction="column">
    <FlexItem align="center">
      <Text size="l">Log in</Text>
    </FlexItem>

    { !isAuthenticated && <ConnectedLoginForm /> }

    { isAuthenticated && <WaitAndClose close={close} /> }
  </Flex>
</SimpleModal>

const WaitAndClose = ({close}) => {
  // Close modal after 0.5s
  React.useEffect(() => {
    const timeoutId = setTimeout(() => close(), 500)
    return () => clearTimeout(timeoutId)
  }, [close])

  return <FlexItem align="center" mt={12} mb={12}>
    <Icon name="success" size={20} color="#4BBD5C" />
  </FlexItem>
}

export const ConnectedLoginModal = connect(
  (state) => ({
    isAuthenticated: selectIsAuthenticated(state)
  }),
  (dispatch) => ({
    close: () => dispatch(closeModal())
  })
)(Modal)

