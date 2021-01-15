import React, {useState, useEffect} from 'react'
import {format, parseISO, formatDistanceToNow} from 'date-fns'

import {Tooltip} from './Tooltip'

export const DateTime = ({value, forceFull}) => {
  const [prepared, setPrepared] = useState({ fullText: '', agoText: '' })

  useEffect(() => {
    const parsed = parseISO(value)
    const fullText = format(parsed, 'dd.MM.yyyy HH:mm:ss.SSS')
    const agoText = formatDistanceToNow(parsed)
    setPrepared({fullText, agoText})
  }, [value])

  if (forceFull) {
    return <span>{prepared.fullText}</span>
  }

  return <Tooltip tip={prepared.fullText}>
    <span>{prepared.agoText} ago</span>
  </Tooltip>
}