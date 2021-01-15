import React from 'react'

import {Box, Spinner} from '@qiwi/pijma-core'
import {useTheme} from 'emotion-theming'

const sizes = {
  s: 5,
  m: 10,
  l: 20
}

export const Loader = ({ size = 'm' }) => {
  const theme = useTheme()
  const sizePx = `${(sizes[size] || sizes.m) * theme.scale}px`
  return <Spinner color={theme.color.brand} width={sizePx} height={sizePx} />
}

export const PageLoader = () => <Box m={10} css={{ textAlign: 'center' }}>
  <Loader size="l" />
</Box>