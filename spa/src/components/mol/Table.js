import React from 'react'
import * as lodash from 'lodash'

import {styled} from '@qiwi/pijma-core'
import {Text} from '@qiwi/pijma-desktop'

const StyledTable = styled.table`
  width: 100%;
`

const StyledRow = styled.tr`
  cursor: pointer;

  &:hover {
    background: ${props => props.theme.color.gray.lightest};
  }
  
  ${props => props.isActive ? { background: props.theme.color.gray.lightest } : {}}
`

const StyledCell = styled.td(({align, nowrap, maxWidth, width, theme}) => {
  const paddingCss = {
    padding: (theme.scale * 2) + 'px',
    '&:first-child': {
      paddingLeft: (theme.scale * 8) + 'px'
    },
    '&:last-child': {
      paddingRight: (theme.scale * 8) + 'px'
    }
  }

  const alignCss = align === 'right' ? {
    direction: 'rtl',
    textAlign: 'left'
  } : {
    textAlign: 'left'
  }

  const nowrapCss = nowrap ? {
    whiteSpace: 'nowrap',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
  } : {}

  const sizeCss = {
    maxWidth: maxWidth ? `${maxWidth}px` : undefined,
    width: width ? `${width}px` : undefined,
  }

  return {
    ...paddingCss, ...alignCss, ...nowrapCss, ...sizeCss,
    verticalAlign: 'middle'
  }
})

const StyledHeader = styled(StyledCell)`
  text-align: left;
`

export const Table = ({showHeader = true, columns, mappers, items = [], isActiveGetter = () => false, onRowClick = () => {}}) => {
  const filteredColumns = lodash.filter(columns, c => !c.hide)

  return <StyledTable>
    { showHeader && <thead>
      <tr>
        {filteredColumns.map((column, colIndex) => <StyledHeader key={colIndex}>
          <Text bold={false} compact size="s" color="support">
            {column.text}
          </Text>
        </StyledHeader>)}
      </tr>
    </thead> }
    <tbody>
      {items.map((item, rowIndex) => <StyledRow key={rowIndex}
                                                isActive={isActiveGetter(item)}
                                                onClick={() => onRowClick(item, rowIndex)}>
        {filteredColumns.map((column, colIndex) => <StyledCell
          {...column}
          key={`${rowIndex}-${colIndex}`}
        >
          {mappers[column.key](item)}
        </StyledCell>)}
      </StyledRow>)}
    </tbody>
  </StyledTable>
}
