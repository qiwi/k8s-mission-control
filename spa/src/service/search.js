import {get} from './common'

export function doSearch(filter) {
  return get(`/api/search?filter=${filter}`)
}