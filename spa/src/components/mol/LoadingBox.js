import React from 'react'

import {Spinner, Flex, styled} from '@qiwi/pijma-core'

import {PageError} from './Error'

const SpinnerWrapper = styled(Flex)`
  height: 100%;
`

export default function LoadingBox({loading, error, errorHeader, children}) {
  if (loading) {
    return <SpinnerWrapper align='center' justify='center'>
      <Spinner color={'#ff8c00'} width={'25px'} height={'25px'} />
    </SpinnerWrapper>
  }
  if (error) {
    return <PageError error={error} />
  }
  return children || false
}
