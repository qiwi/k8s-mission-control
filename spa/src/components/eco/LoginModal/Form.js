import React from 'react'
import {connect} from 'react-redux'
import {Controller, useForm} from 'react-hook-form'

import {FlexItem} from '@qiwi/pijma-core'
import {Button, PasswordField, Text, TextField} from '@qiwi/pijma-desktop'

import {login, selectError, selectIsLoading} from '../../../modules/auth'

const handleEnter = (submit) => (event) => {
  if (event.key === 'Enter') submit()
}

const LoginForm = ({loading, doLogin, error}) => {
  const {control, errors, handleSubmit} = useForm()
  const submit = handleSubmit(({userName, password}) => doLogin(userName, password))

  return <>
    <FlexItem mt={6}>
      <Controller as={TextField}
                  name="userName" title="User name" defaultValue=""
                  error={errors.userName && "Required"}
                  rules={{required: true}}
                  control={control}
                  onKeyDown={handleEnter(submit)}
      />
    </FlexItem>
    <FlexItem>
      <Controller as={PasswordField} viewed
                  name="password" title="Password" defaultValue=""
                  error={errors.password && "Required"}
                  rules={{required: true}}
                  control={control}
                  onKeyDown={handleEnter(submit)}
      />
    </FlexItem>
    { error && <FlexItem align="center">
      <Text color="failure">{error.userMessage}</Text>
    </FlexItem> }
    <FlexItem mt={6} align="center">
      <Button kind="brand" size="normal" type="button" text="Log in"
              loading={loading}
              onClick={submit}
      />
    </FlexItem>
  </>
}

export const ConnectedLoginForm = connect(
  (state) => ({
    loading: selectIsLoading(state),
    error: selectError(state)
  }),
  (dispatch) => ({
    doLogin: (userName, password) => dispatch(login(userName, password))
  })
)(LoginForm)