import React from 'react'

import {styled} from '@qiwi/pijma-core'

const StyledList = styled.ul`
  list-style-type: none
`

export const List = ({children, onSelect}) => (
  <StyledList>
    {React.Children.map(children, child =>
      React.cloneElement(child, { onSelect })
    )}
  </StyledList>
)

