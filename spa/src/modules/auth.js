import {createSelector} from 'reselect'

import * as auth from '../service/auth'
import * as security from '../service/security'

const LOGIN = 'auth/LOGIN'
const LOGIN_COMPLETE = 'auth/LOGIN_COMPLETE'
const LOGIN_FAIL = 'auth/LOGIN_FAIL'
const LOGOUT = 'auth/LOGOUT'

const initialState = {
  loading: false,
  error: null,
  user: security.readUserInfo()
}

export default (state = initialState, action) => {
  switch (action.type) {
    case LOGIN:
      return {
        ...state,
        loading: true,
        error: null
      }
    case LOGIN_COMPLETE:
      return {
        ...state,
        loading: false,
        error: false,
        user: action.payload
      }
    case LOGIN_FAIL:
      return {
        ...state,
        loading: false,
        error: action.error,
        user: null
      }
    case LOGOUT:
      return {
        ...state,
        loading: false,
        error: null,
        user: null
      }
    default:
      return state
  }
}

export const selectAuth = (state) => state.auth

export const selectIsAuthenticated = createSelector(
  selectAuth,
  auth => Boolean(auth.user)
)

export const selectIsLoading = createSelector(
  selectAuth,
  auth => Boolean(auth.loading)
)

export const selectError = createSelector(
  selectAuth,
  auth => auth.error
)

export const selectUserDisplayName = createSelector(
  selectAuth,
  auth => auth.user && auth.user.displayName
)

export const login = (userName, password) => async (dispatch) => {
  dispatch({ type: LOGIN })

  try {
    const userInfo = await auth.login(userName, password)

    security.saveAuthData(userInfo, userInfo.csrfToken)

    dispatch({ type: LOGIN_COMPLETE, payload: userInfo })
  } catch (error) {
    dispatch({ type: LOGIN_FAIL, error })
  }
}

export const logout = () => async (dispatch) => {
  try {
    await auth.logout()
  } catch (error) {
    console.log('Can\'t logout', error)
  }

  security.deleteAuthData()
  dispatch({ type: LOGOUT })
}
