import React from 'react'

import {Status} from '@qiwi/pijma-desktop'
import {Box} from '@qiwi/pijma-core'

export const Error = ({error}) => <Status
  title={error.titleMessage || "Error occurred"}
  content={error.userMessage || error.message}
/>

export const PageError = (props) => <Box m={10}>
  <Error {...props} />
</Box>
