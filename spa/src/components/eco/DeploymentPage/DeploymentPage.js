import React, {Component} from 'react'
import {connect} from 'react-redux'

import {Spacer, Grid} from '@qiwi/pijma-core'

import {ConnectedDeploymentHeader} from './DeploymentHeader'
import {GeneralCard} from './GeneralInfo'
import {ServiceCard} from './ServiceInfo'
import {ReplicaSetsCard} from './ReplicaSetsInfo'
import {PodsCard} from './PodsCard'
import {MessagesCard} from './MessagesCard'
import {EndpointsCard} from './Endpoints'

import {PageLoader} from '../../mol/Loader'
import {PageError} from '../../mol/Error'

import {loadClusters} from '../../../modules/clusters'
import {loadDeployment, selectDeploymentInfo, selectPods, selectReplicaSets, selectServices, selectEndpoints, selectMessages} from '../../../modules/deploymentDetails'
import {selectDeploymentSettings} from '../../../modules/profile'
import {checkFeature, FEATURE_DEPLOYMENT_ENDPOINTS_MODE} from '../../../features'
import {goToPod, goToPodLog, goToReplicaSet} from '../../../modules/routes'

class Index extends Component {
  componentDidMount() {
    this.props.load()
  }

  render() {
    const {cluster, ns, deployment} = this.props

    return <>
      <ConnectedDeploymentHeader cluster={cluster} ns={ns} deployment={deployment} />

      {this.renderBody()}
    </>
  }

  renderBody() {
    const {details, messages} = this.props
    const classicMode = !checkFeature(FEATURE_DEPLOYMENT_ENDPOINTS_MODE)

    if (details.loading) {
      return <PageLoader />
    } else if (details.error) {
      return <PageError error={details.error} />
    }

    return <Spacer size="m">
      { Boolean(messages.count) && <MessagesCard data={messages} /> }

      <GeneralCard data={details} />

      { classicMode
        ? this.renderClassicView()
        : this.renderEndpointsView()
      }
    </Spacer>
  }

  renderClassicView() {
    const {pods, replicasets, services, settings, goToPod, goToReplicaSet, goToLog} = this.props
    return [
      services.data && services.data.length && services.data.map(service => <ServiceCard service={service}/>),

      <Grid layout={[6]}>
        <ReplicaSetsCard data={replicasets} onReplicaSetClick={rs => goToReplicaSet(rs.metadata.name)} settings={settings} />

        <PodsCard data={pods} onPodClick={pod => goToPod(pod.metadata.name)} onLogClick={pod => goToLog(pod.metadata.name)} />
      </Grid>
    ]
  }

  renderEndpointsView () {
    const {pods, replicasets, endpoints, settings, goToPod, goToReplicaSet, goToLog} = this.props

    return <Grid layout={[6]}>
      <Grid layout={[12]}>
        <EndpointsCard endpoints={endpoints} />

        <PodsCard data={pods} onPodClick={pod => goToPod(pod.metadata.name)} onLogClick={pod => goToLog(pod)} />
      </Grid>

      <ReplicaSetsCard data={replicasets} onReplicaSetClick={rs => goToReplicaSet(rs.metadata.name)} settings={settings} />
    </Grid>
  }
}

const mapStateToProps = (state, ownProps) => ({
  details: selectDeploymentInfo(ownProps.cluster, ownProps.ns, ownProps.deployment)(state),
  pods: selectPods(ownProps.cluster, ownProps.ns, ownProps.deployment)(state),
  replicasets: selectReplicaSets(ownProps.cluster, ownProps.ns, ownProps.deployment)(state),
  services: selectServices(ownProps.cluster, ownProps.ns, ownProps.deployment)(state),
  endpoints: selectEndpoints(ownProps.cluster, ownProps.ns, ownProps.deployment)(state),
  messages: selectMessages(ownProps.cluster, ownProps.ns, ownProps.deployment)(state),
  settings: selectDeploymentSettings(state)
})

const mapDispatchToProps = (dispatch, ownProps) => ({
  load: () => {
    dispatch(loadClusters(false))
    dispatch(loadDeployment(ownProps.cluster, ownProps.ns, ownProps.deployment))
  },
  goToPod: (pod) => dispatch(goToPod(ownProps.cluster, ownProps.ns, ownProps.deployment, pod)),
  goToReplicaSet: (replicaset) => dispatch(goToReplicaSet(ownProps.cluster, ownProps.ns, ownProps.deployment, replicaset)),
  goToLog: (pod) => dispatch(goToPodLog(ownProps.cluster, ownProps.ns, ownProps.deployment, pod))
})

export const ConnectedDeploymentPage = connect(
  mapStateToProps,
  mapDispatchToProps
)(Index)