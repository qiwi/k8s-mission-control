import React from "react"
import InfiniteScroll from "react-infinite-scroller"
import {Text} from "@qiwi/pijma-desktop"
import {Flex, FlexItem, Box, styled} from '@qiwi/pijma-core'

import {MonospacedText} from '../../mol/Text'
import {DateTime} from '../../mol/DateTime'
import {Loader} from '../../mol/Loader'

export const LogsText = styled(MonospacedText)`
  font-size: 14px;
  line-height: 16px;
`

const StyledRow = styled(Box)`
  cursor: pointer;

  &:hover {
    background: ${props => props.theme.color.gray.lightest};
  }
`

const LogRow = ({item}) => {
    return <StyledRow>
        <Flex px={2} py={1}>
            <FlexItem width="60px">
                <LogsText>{item.data.level || 'level'}</LogsText>
            </FlexItem>

            <FlexItem width="calc(100% - 60px)">
                <Flex direction="column">
                    <FlexItem>
                        <LogsText>
                            <DateTime value={item.timestamp} forceFull/>
                        </LogsText>
                    </FlexItem>
                    <FlexItem>
                        <Text display="block" size="m" align="left" clamp={2} compact={true}>
                            <LogsText>{item.data.message}</LogsText>
                        </Text>
                    </FlexItem>
                </Flex>
            </FlexItem>
        </Flex>
    </StyledRow>
}

const LogsLoader = () => <Flex justify="center" width="100%">
    <FlexItem>
        <Loader size="l"/>
    </FlexItem>
</Flex>

export const LogPanel = ({items, loader, isContainerSelected}) => {
    return (
        <Box height="100%" css={{overflowY: "scroll"}}>
            <InfiniteScroll
                useWindow={false}
                pageStart={0}
                loadMore={loader}
                hasMore={isContainerSelected}
                loader={<LogsLoader key={0} />}
            >
                {items.map((item, index) => <LogRow key={index} item={item} />)}
            </InfiniteScroll>
        </Box>
    )
}