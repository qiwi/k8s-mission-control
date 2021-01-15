import React from 'react'

import {Flex, FlexItem} from '@qiwi/pijma-core'

const maxColumns = 4
const gutter = 15

const calcHeight = (children, gutterPercent) => {
  if (React.Children.count(children) <= 4) {
    return `100%`
  } else {
    return `calc(50% + ${gutterPercent}px)`
  }
}

export const Layout = ({children, height}) => {
  const childrenCount = React.Children.count(children)
  const columns = Math.min(maxColumns, childrenCount)
  const oneColumnPercent = 1 / columns
  const gutterPercent = gutter * oneColumnPercent - gutter
  const childWidth = `calc(${100 * oneColumnPercent}% + ${gutterPercent}px)`
  const childHeight = calcHeight(children, gutterPercent)
  const mt = index => index >= columns ? `${gutter}px` : 0
  const ml = index => index % columns !== 0 ? `${gutter}px` : 0

  return <Flex wrap="wrap" height={height}>
    {React.Children.map(children, (child, index) => {
      return <FlexItem key={index} width={childWidth} height={childHeight} mt={mt(index)} ml={ml(index)}>
        {child}
      </FlexItem>
    })}
  </Flex>
}