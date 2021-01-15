import React from 'react'

import {Icon} from './Icon'
import * as icons from './icons'

const mapping = {
  'INACTIVE': icons.iconInactive,
  'OK': icons.iconOk,
  'WARN': icons.iconWarn,
  'ERROR': icons.iconError,
  'UNKNOWN': icons.iconUnknown
}

const getIcon = (status) => mapping[status]
  || (status && mapping[status.value || 'UNKNOWN'])
  || mapping['UNKNOWN']

export const StatusIcon = (props) => {
  return <Icon icon={getIcon(props.status)} {...props} />
}