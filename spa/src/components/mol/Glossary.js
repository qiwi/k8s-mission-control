import React from 'react'
import * as lodash from 'lodash'

import {Grid, Box} from '@qiwi/pijma-core'
import {Text} from '@qiwi/pijma-desktop'

export const Glossary = ({children, autosize = true, columns = 12}) => <Box as="dl">
  { createGrids({columns, autosize}, children) }
</Box>

export const Field = ({title, children, size = 1}) => <Box as="dl">
  <Box as="dt">
    <Text bold={false} compact size="s" color="support">
      {title}
    </Text>
  </Box>
  <Box mt={1} as="dd">
    <Text>{children}</Text>
  </Box>
</Box>

const createGrids = ({columns, autosize}, children) => {
  return lodash.chain(React.Children.map(children, lodash.identity))
    .reduce((accumulator, child) => {
      const size = child.props.size || 1
      const lastGroup = lodash.last(accumulator)
      if (!lastGroup || lastGroup.sum + size > columns) {
        accumulator.push({sum: size, children: [child]})
      } else {
        lastGroup.sum += size
        lastGroup.children.push(child)
      }
      return accumulator
    }, [])
    .map((group, index) => {
      return <Box key={index} pt={index ? 4 : 0}>
        <Grid layout={determineLayout(columns, autosize, group.children)}>
          {group.children.map(child => React.cloneElement(child))}
        </Grid>
      </Box>
    })
    .value()
}

const determineLayout = (columns, autosize, children) => {
  const grid = 12
  const colSize = grid / columns

  let sizes = children.map(child => child.props.size || 1)

  const sum = lodash.sum(sizes)
  const count = sizes.length

  if (sum > columns) {
    throw Error(`Sum of sizes can't be greater that number of columns (${columns})`)
  }

  if (!count) {
    sizes = [1]
  } else if (sum < columns) {
    if (autosize) {
      const d = Math.floor(columns / count)
      const rem = columns % count
      sizes = lodash.times(count, lodash.constant(d))
      sizes[sizes.length - 1] += rem
    } else {
      sizes = lodash.concat(sizes, lodash.times(columns - sum, lodash.constant(1)))
    }
  }

  return sizes.map(size => size * colSize)
}
