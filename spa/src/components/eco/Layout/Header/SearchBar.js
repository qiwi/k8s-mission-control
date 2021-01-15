import React, {useState} from 'react'
import {connect} from 'react-redux'

import {Icon, iconSearch} from '../../../mol/Icon'

import {goToDeployment} from '../../../../modules/routes'
import {loadDeployment} from '../../../../modules/deploymentDetails'
import {doSearch} from '../../../../service/search'

import {SuggestField} from '../../../mol/SuggestField'

const prepareItem = (item) => {
  if (item.type === 'deployment') {
    return {
      value: item,
      title: item.title,
      description: <span>Cluster: {item.value.clusterName} | Namespace: {item.value.namespace}</span>
    }
  } else {
    return {
      value: item,
      title: '',
      description: ''
    }
  }
}

const SearchBar = ({goToDeployment, target, container}) => {
  const [suggest, setSuggest] = useState('')
  const [timer, setTimer] = useState(undefined)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(undefined)
  const [items, setItems] = useState([])

  const onRequest = (value) => {
    setSuggest(value)
    setError(false)

    if (!value || value.length < 3) {
      setLoading(false)
      setItems([])
      return
    }

    setLoading(true)
    if (timer) {
      clearTimeout(timer)
    }

    const handle = setTimeout(async () => {
      try {
        const items = (await doSearch(value)).map(prepareItem)
        setItems(items)
      } catch (e) {
        setError(true)
        setItems([])
      } finally {
        setTimer(undefined)
        setLoading(false)
      }
    }, 500)

    setTimer(handle)
  }

  const onItemClick = (item) => {
    if (item.type === 'deployment') {
      goToDeployment(item.value)
    }

    setItems(undefined)
    setSuggest('')
  }

  const getEmptyMessage = () => {
    if (error) {
      return {
        text: 'Unknown error has been occurred,',
        link: {
          text: 'try again',
          suggest: suggest,
        }
      }
    }

    if (suggest.length >= 3) {
      return {
        text: 'We couldn\'t find anything'
      }
    }

    return undefined
  }

  return <SuggestField
    type="search"
    hint={<Icon icon={iconSearch} />}
    value={undefined}
    items={items}
    suggest={suggest}
    loading={loading}
    error={error}
    placeholder="Global search"
    target={target}
    container={container}
    onChange={onItemClick}
    onRequest={onRequest}
    empty={getEmptyMessage()}
  />
}

export const ConnectedSearchBar = connect(
  () => ({}),
  (dispatch) => ({
    goToDeployment: ({clusterName, namespace, name}) => {
      dispatch(goToDeployment(clusterName, namespace, name))
      dispatch(loadDeployment(clusterName, namespace, name))
    }
  })
)(SearchBar)