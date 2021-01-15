import React from 'react'

import {
  TextFieldControl,
  BasicInput
} from '@qiwi/pijma-core'

export const SearchField = (props) => (
  <TextFieldControl
    onChange={props.onChange}
    onFocus={props.onFocus}
    onBlur={props.onBlur}
    onKeyDown={props.onKeyDown}
    onKeyUp={props.onKeyUp}
    children={(renderProps) => (
      <BasicInput
        type={props.type}
        value={props.value}
        name={props.name}
        autoComplete={props.autoComplete}
        autoFocus={props.autoFocus}
        placeholder={props.placeholder || "Search"}
        disabled={props.disabled}
        pr={props.hint ? 7 : undefined}
        error={!!props.error}
        focused={renderProps.focused}
        maxLength={props.maxLength}
        mask={props.mask}
        pipe={props.pipe}
        onChange={renderProps.onChange}
        onFocus={renderProps.onFocus}
        onBlur={renderProps.onBlur}
        onKeyDown={renderProps.onKeyDown}
        onKeyUp={renderProps.onKeyUp}
      />
    )}
  />
)