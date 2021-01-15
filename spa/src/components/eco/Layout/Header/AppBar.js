import React from 'react'
import {connect} from 'react-redux'

import {Flex, FlexItem, styled} from '@qiwi/pijma-core'
import {HeaderMenu, Link} from '@qiwi/pijma-desktop'

import * as routes from '../../../../modules/routes'

const Title = styled('span')`
  font-size: 26px;
  line-height: 48px;
  color: #000;
  color: #000;
  font-weight: 900;
`

const AppBar = ({isNavActive, goMain, goToClusters}) => <Flex height={1}>
  <FlexItem align="center" shrink={0} mr={11}>
    <Link size="l" onClick={() => goMain()}>
      <Title>Mission Control</Title>
    </Link>
  </FlexItem>
  <FlexItem shrink={0} mr={6}>
    <HeaderMenu
      children={[
        {title: 'Clusters', active: isNavActive('clusters'), onClick: () => goToClusters()}
      ]}
    />
  </FlexItem>
</Flex>

export const ConnectedAppBar = connect(
  (state) => ({
    isNavActive: routes.isNavActive(state)
  }),
  (dispatch) => ({
    goMain: () => dispatch(routes.goMain()),
    goToClusters: () => dispatch(routes.goToClusters())
  })
)(AppBar)
