import React from 'react'
import {connect} from 'react-redux'

import {selectClustersAutoReload} from '../../../modules/profile'
import {loadDeployments} from '../../../modules/deployments'
import {AutoAction} from '../../mol/AutoAction'

const AutoReload = ({ autoReload, load }) => {
  if (autoReload) {
     return <AutoAction interval={autoReload} action={load} />
  } else {
    return null
  }
}

export const ConnectedAutoReload = connect(
  state => ({
    autoReload: selectClustersAutoReload(state)
  }),
  dispatch => ({
    load: () => dispatch(loadDeployments())
  })
)(AutoReload)