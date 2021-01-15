import React, {useEffect} from 'react'
import {connect} from 'react-redux'

export const createProvider = (selector, loader) => {
  const InnerComponent = (props) => {
    const {load} = props
    const Component = props.component

    useEffect(() => {
      load()
    }, [load])

    return <Component {...props} />
  }

  return connect(
    (state, ownProps) => ({
      ...selector(state, ownProps)
    }),
    (dispatch, ownProps) => ({
      load: () => dispatch(loader(ownProps))
    })
  )(InnerComponent)
}
