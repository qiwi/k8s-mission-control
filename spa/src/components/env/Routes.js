import React, {Component} from 'react'
import {connect} from 'react-redux'
import * as queryString from 'query-string'

import {Route, Switch, Redirect} from 'react-router'

import {ConnectedClustersPage} from '../eco/ClustersPage'
import {ConnectedNotFoundPage} from '../eco/NotFoundPage'
import {ConnectedDeploymentPage} from '../eco/DeploymentPage'
import {ConnectedPodPage} from '../eco/PodPage'
import {ConnectedReplicaSetPage} from '../eco/ReplicaSetPage/ReplicaSetPage'
import {ConnectedLoginModal} from '../eco/LoginModal'
import {ConnectedLogPage} from "../eco/LogPage/LogPage"

class Routes extends Component {
  render() {
    let {location} = this.props
    let modalLocation = getModalLocation(location)

    return <>
      <Switch location={location}>
        <Route exact path="/">
          <Redirect to="/clusters"/>
        </Route>

        <Route exact path="/clusters" component={ConnectedClustersPage}/>

        <Route exact path="/clusters/:cluster/namespaces/:ns/deployments/:deployment"
               render={e => {
                 const {cluster, ns, deployment} = e.match.params
                 return <ConnectedDeploymentPage cluster={cluster} ns={ns} deployment={deployment} />
               }}/>

        <Route exact path="/clusters/:cluster/deployments/:ns/:deployment"
               render={e => {
                 const {cluster, ns, deployment} = e.match.params
                 return <ConnectedDeploymentPage cluster={cluster} ns={ns} deployment={deployment} />
               }}/>

        <Route exact path="/clusters/:cluster/deployments/:ns/:deployment/pods/:pod"
               render={e => {
                 const {cluster, ns, deployment, pod} = e.match.params
                 return <ConnectedPodPage cluster={cluster} ns={ns} deploymentName={deployment} podName={pod} />
               }}/>

        <Route exact path="/clusters/:cluster/deployments/:ns/:deployment/replicasets/:replicaset"
               render={e => {
                 const {cluster, ns, deployment, replicaset} = e.match.params
                 return <ConnectedReplicaSetPage cluster={cluster} ns={ns} deploymentName={deployment} replicaSetName={replicaset} />
               }}/>

        <Route exact path="/clusters/:cluster/deployments/:ns/:deployment/pods/:pod/logs"
               render={e => {
                   const {cluster, ns, deployment, pod} = e.match.params
                   return <ConnectedLogPage cluster={cluster} ns={ns} deployment={deployment} podName={pod} />
               }}/>

         <Route path="*"><ConnectedNotFoundPage /></Route>
      </Switch>

      {modalLocation && <Switch location={modalLocation}>
        <Route exact path="login" render={() => <ConnectedLoginModal />} />
      </Switch>}
    </>
  }
}

function getModalLocation(location) {
  let {modal} = queryString.parse(location.search)
 return modal && {
    ...location,
    pathname: modal
  }
}

const mapStateToProps = (state) => ({
  location: state.router.location
})

const mapDispatchToProps = () => ({
})

export const ConnectedRoutes = connect(
  mapStateToProps,
  mapDispatchToProps
)(Routes)