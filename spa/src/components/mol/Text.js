import {styled} from '@qiwi/pijma-core'

export const MonospacedText = styled('span')`
  font-family: monospace;
  word-break: break-all;
`
export const DecoratedText = styled('span')`
  border-bottom: 1px dotted #999;
  text-decoration: none; 
  cursor: pointer;
  white-space: nowrap;
`