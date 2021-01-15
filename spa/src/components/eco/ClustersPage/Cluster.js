import React from 'react'
import {connect} from 'react-redux'

import {goToDeployment} from '../../../modules/routes'
import {selectDeploymentsByCluster} from '../../../modules/deployments'
import {selectClustersHideNamespaces} from '../../../modules/profile'

import {DeploymentsList} from './DeploymentsList'
import {ClusterCard} from './ClusterCard'

const Cluster = ({ search, cluster, deployments, goToDeployment, hideNamespaces }) => {
  const filteredItems = applySearch(deployments.items, search)
  const totalCount = filteredItems && filteredItems.length
  const warnsCount = filteredItems && filteredItems.filter(i => i.status.value === 'WARN').length
  const errorsCount = filteredItems && filteredItems.filter(i => i.status.value === 'ERROR').length
  return <ClusterCard cluster={cluster} totalCount={totalCount} warnsCount={warnsCount} errorsCount={errorsCount} items={filteredItems}>
    <DeploymentsList
      onSelect={goToDeployment}
      items={filteredItems}
      error={deployments.error}
      loading={deployments.loading}
      hideNamespaces={hideNamespaces}
    />
  </ClusterCard>
}

function applySearch(items, search) {
  return (items && items.length && search)
    ? items.filter(d => d.metadata.name.indexOf(search) !== -1)
    : items
}

export const ConnectedCluster = connect(
  (state, ownProps) => ({
    deployments: selectDeploymentsByCluster(ownProps.cluster.name)(state),
    hideNamespaces: selectClustersHideNamespaces(state)
  }),
  (dispatch, ownProps) => ({
    goToDeployment: (deployment) => dispatch(goToDeployment(
      ownProps.cluster.name, deployment.metadata.namespace, deployment.metadata.name
    ))
  })
)(Cluster)
