import React from 'react'
import {connect} from 'react-redux'

import {Button, HeaderMenu} from '@qiwi/pijma-desktop'
import {Icon} from '@qiwi/pijma-core'

import {goToLoginModal} from '../../../../modules/routes'
import {selectIsAuthenticated, selectUserDisplayName} from '../../../../modules/auth'
import {checkFeature, FEATURE_SHOW_LOGIN_BUTTON} from '../../../../features'

const ProfileBar = ({authorized, displayName, showUserDropDown, goToLogin}) => {
  if (authorized) {
    return <>
      <HeaderMenu
        children={[
          {title: displayName, active: false, onClick: showUserDropDown}
        ]}
      />
    </>
  } else if (checkFeature(FEATURE_SHOW_LOGIN_BUTTON)) {
    return <>
      <Button
        kind="brand" size="minor" type="button" text="Log in"
        icon={<Icon name="login"/>}
        onClick={goToLogin}
      />
    </>
  } else {
    return false
  }
}

export const ConnectedProfileBar = connect(
  (state) => ({
      authorized: selectIsAuthenticated(state),
      displayName: selectUserDisplayName(state)
  }),
  (dispatch) => ({
    goToLogin: () => dispatch(goToLoginModal())
  })
)(ProfileBar)