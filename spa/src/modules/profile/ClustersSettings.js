import {SortMode} from './SortMode'

export class ClustersSettings {
  constructor(hideNamespaces = true, autoReload = 0, sortMode = new SortMode('name'), clusters = []) {
    this.hideNamespaces = hideNamespaces
    this.autoReload = autoReload
    this.sortMode = sortMode
    this.clusters = clusters
  }

  static parseQueryString(query) {
    let hideNamespaces = true
    let autoReload = query.autoReload ? parseInt(query.autoReload) : 0
    let sortMode = query.sort ? SortMode.parse(query.sort) : new SortMode('name')
    let clusters = query.clusters ? query.clusters.split(',') : []

    return new ClustersSettings(hideNamespaces, autoReload, sortMode, clusters)
  }

  toQueryObject() {
    return {
      autoReload: this.autoReload,
      sort: this.sortMode.toString(),
      clusters: this.clusters.length ? this.clusters.join(',') : null
    }
  }

  withHideNamespaces(hide) {
    return new ClustersSettings(hide, this.autoReload, this.sortMode, this.clusters)
  }

  withAutoReload(autoReload) {
    return new ClustersSettings(this.hideNamespaces, autoReload, this.sortMode, this.clusters)
  }

  withSortMode(fieldName, direction) {
    return new ClustersSettings(this.hideNamespaces, this.autoReload, new SortMode(fieldName, direction), this.clusters)
  }

  withClusters(clusters) {
    return new ClustersSettings(this.hideNamespaces, this.autoReload, this.sortMode, clusters)
  }
}