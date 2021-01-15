import React, {Component} from 'react'
import {connect} from 'react-redux'

import LoadingBox from '../../mol/LoadingBox'

import {loadClusters, selectVisibleClusters} from '../../../modules/clusters'
import {pushSearchQuery, selectSearchQuery} from '../../../modules/routes'
import {selectClustersAutoReload} from '../../../modules/profile'

import {ConnectedClustersHeader} from './ClustersHeader'
import {Layout} from './Layout'
import {ConnectedCluster} from './Cluster'
import {ConnectedAutoReload} from './AutoReload'

class ClustersPage extends Component {
  constructor(props) {
    super(props)

    this.state = {
      search: props.search
    }
  }

  componentDidMount() {
    this.props.load()
  }

  render() {
    const {search} = this.state
    const {loading, error, clusters} = this.props

    return <>
      <ConnectedAutoReload/>

      <ConnectedClustersHeader
        search={search}
        onSearchChange={value => this.search(value)}
        onShowSettings={() => this.showSettingsModal()}
      />

      <LoadingBox loading={loading} error={error}>
        <Layout height="calc(100% + -48px)"
                children={clusters.map(cluster => <ConnectedCluster key={`cluster-${cluster.name}`} cluster={cluster} search={search} />)
        } />
      </LoadingBox>
    </>
  }

  search(value) {
    this.setState(state => ({ ...state, search: value }))
    this.props.pushSearchQuery(value)
  }

  showSettingsModal() {
    this.setState(state => ({ ...state, settingsModal: true }))
  }

  hideSettingsModal() {
    this.setState(state => ({ ...state, settingsModal: false }))
  }
}

const mapStateToProps = (state) => ({
  clusters: selectVisibleClusters(state),
  loading: state.clusters.loading,
  error: state.clusters.error,
  search: selectSearchQuery(state),
  autoReload: selectClustersAutoReload(state)
})

const mapDispatchToProps = (dispatch) => ({
  load: (search) => dispatch(loadClusters(search)),
  pushSearchQuery: (value) => dispatch(pushSearchQuery(value))
})

export const ConnectedClustersPage = connect(
  mapStateToProps,
  mapDispatchToProps
)(ClustersPage)
