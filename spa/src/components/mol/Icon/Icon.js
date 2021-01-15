import React from 'react'

import {Stub, Icon as PijmaIcon, styled} from '@qiwi/pijma-core'

import {Tooltip} from '../Tooltip'

const sizes = {
  s: 4,
  m: 6,
  l: 8
}

const colors = {
  default: '#666',
  ok: '#4BBD5C',
  warn: '#FFB242',
  error: '#D0021B',
  accent: '#ff8c00',
  black: '#000'
}

const IconWrapper = styled.span`
  width: ${({size, theme}) => (size || 6) * theme.scale}px;
  height: ${({size, theme}) => (size || 6) * theme.scale}px;
  float: left;
  display: flex;
`

const makeTooltip = (children, tooltip) => {
  if (tooltip) {
    return <Tooltip tip={tooltip}>
      {children}
    </Tooltip>
  } else {
    return children
  }
}

export const Icon = ({icon, stub, size, color, tooltip}) => {
  const mSize = sizes[size] || sizes.m
  const mColor = colors[color || icon.color] || colors.default
  const mIcon = icon.icon

  if (stub) {
    return <Stub width={mSize} height={mSize} r={mSize * 2}/>
  }

  return <IconWrapper size={mSize}>
    {makeTooltip(<PijmaIcon name={mIcon} color={mColor} />, tooltip)}
  </IconWrapper>
}

