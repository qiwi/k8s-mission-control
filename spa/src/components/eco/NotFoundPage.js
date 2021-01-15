import React from 'react'

import {Flex, FlexItem} from '@qiwi/pijma-core'
import {Heading, Text, Link} from '@qiwi/pijma-desktop'
import {connect} from 'react-redux'

import {goMain} from '../../modules/routes'

const NotFoundPage = ({goMain}) => (<Flex justify="center">
  <FlexItem>
    <Heading size="1">Мы не нашли страницу,<br/> которую вы ищете</Heading>
    <br/>
    <Text>
      <span>Попробуйте вернуться назад или </span>
      <Link onClick={() => goMain()}>перейти на главную страницу</Link>
      <span>.</span>
    </Text>
  </FlexItem>
</Flex>)

export const ConnectedNotFoundPage = connect(
  () => ({}),
  (dispatch) => ({
    goMain: () => dispatch(goMain())
  })
)(NotFoundPage)