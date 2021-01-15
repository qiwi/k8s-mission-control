import React from 'react'
import {styled} from '@qiwi/pijma-core'

const StyledItem = styled.li`
  padding: 6px 6px;
  cursor: pointer;

  &:hover {
    background: ${props => props.theme.color.gray.lightest};
  }
`

export const Item = ({children, value, onSelect}) => (
  <StyledItem onClick={() => onSelect && onSelect(value)}>
    {children}
  </StyledItem>
)