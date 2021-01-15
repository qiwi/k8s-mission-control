import React from 'react'

import {LinkControl, Lnk, Pos} from '@qiwi/pijma-core'

import {Icon} from './Icon'

const PosLink = Pos.withComponent(Lnk)

export const IconLink = (props) => <LinkControl
  href={props.href}
  target={props.target}
  download={props.download}
  rel={props.rel}
  onClick={props.onClick}
  onFocus={props.onFocus}
  onBlur={props.onBlur}
  children={renderProps => {
    return <PosLink
      as="div"
      width={6}
      height={6}
      cursor="pointer"
      tabIndex={props.tabIndex}
      href={props.href}
      target={props.href ? props.target : undefined}
      download={props.href ? props.download : undefined}
      rel={props.href ? props.rel : undefined}
      title={props.href ? props.title : undefined}
      onClick={renderProps.onClick}
      onFocus={renderProps.onFocus}
      onBlur={renderProps.onBlur}
      onMouseEnter={renderProps.onMouseEnter}
      onMouseLeave={renderProps.onMouseLeave}
      onMouseUp={renderProps.onMouseUp}
      onMouseDown={renderProps.onMouseDown}
    >
      <Icon icon={props.icon} color={renderProps.hover ? 'accent' : 'black'} />
    </PosLink>
  }}
/>