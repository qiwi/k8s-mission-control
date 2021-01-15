import React from 'react'
import {connect} from 'react-redux'

import {Route} from 'react-router'

import {goToLoginModal, selectIsOnLoginModal} from '../../modules/routes'
import {selectIsAuthenticated} from '../../modules/auth'

// A wrapper for <Route> that redirects to the login
// screen if you're not yet authenticated.
const PrivateRoute = ({ isOnLoginModal, isAuthenticated, goToLogin, children, ...rest }) => <Route
  {...rest}
  render={() => {
    if (!isAuthenticated) {
      if (!isOnLoginModal) {
        goToLogin()
      }
      return false
    }
    return children
  }
  }
/>

export const ConnectedPrivateRoute = connect(
  (state) => ({
    isOnLoginModal: selectIsOnLoginModal(state),
    isAuthenticated: selectIsAuthenticated(state)
  }),
  (dispatch) => ({
    goToLogin: () => dispatch(goToLoginModal())
  })
)(PrivateRoute)
