import React from 'react'
import ReactTooltip from 'react-tooltip'
import * as lodash from 'lodash'

export const Tooltip = ({ children, tip }) => {
  // don't render empty tooltip
  if (!tip) return children

  const id = lodash.uniqueId('tooltip-')
  return <span data-tip="" data-for={id}>
    {children}
    <ReactTooltip
      id={id}
      place="right"
      type="dark"
      effect="solid"
      delayHide={100}
      delayUpdate={10}
    >
      {tip}
    </ReactTooltip>
  </span>
}



