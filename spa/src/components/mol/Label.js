import React from 'react'

import {styled} from '@qiwi/pijma-core'

const StyledLabel = styled('span')`
  font-size: 12px;
  font-weight: 500;
  line-height: 20px;
  text-transform: uppercase;
  letter-spacing: 1.5px;
  
  padding: 4px;
  background-color: #ff8c00;
  color: white;
  border-radius: 6px;
`

export const Label = ({children}) => <StyledLabel>
  {children}
</StyledLabel>