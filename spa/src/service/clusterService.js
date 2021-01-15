import {get} from './common'

export function getClusters() {
    return get('/api/clusters')
}
