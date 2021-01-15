import React from 'react'

import {Block, Box, Spinner, Flex, FlexItem, styled} from '@qiwi/pijma-core'
import {Heading, Status} from '@qiwi/pijma-desktop'

export const Card = ({children, loading, error, accent}) => <Block active={accent} accent={accent}>
  {React.Children.map(children, (child, index) => {
    if (!child) {
      return child
    }

    const childType = child.type && child.type.displayName
    // hide all CardContent elements if Card is in loading or error state
    if ((loading || error) && childType === CardContent.displayName) {
      return false
    }
    return React.cloneElement(child, {
      last: index === React.Children.count(children) - 1,
    })
  })}
  { loading && <Box p={5} align="center" justify="center">
    <Spinner color="#ff8c00" width="40px" height="40px" />
  </Box> }
  { error && <Box p={3}>
    <Status
      title={error.titleMessage || "Error occurred"}
      content={error.userMessage || error.message}
    />
  </Box> }
</Block>

export const CardContent = ({children, last, expand, render, pt}) => {
  const lr = expand ? 0 : 8
  const b = last ? 4 : 0
  return <Box pr={lr} pl={lr} pt={pt || 3} pb={b}>
    {render ? render() : children}
  </Box>
}
CardContent.displayName = 'CardContent'

export const CardDivider = styled(Box)`
  height: 1px;
  background: ${({theme}) => theme.color.gray.light};
`
export const CardHeader = ({text, children}) => <CardContent pt={6}>
  <Flex align="baseline">
    <FlexItem>
      <Heading size="4">
        {text}
      </Heading>
    </FlexItem>
    <FlexItem ml={2} align="baseline">
      {children}
    </FlexItem>
  </Flex>
</CardContent>

export const SimpleCard = ({children, header, expand = false}) => <Card>
  <CardHeader text={header}/>
  <CardContent expand={expand}>
    {children}
  </CardContent>
</Card>