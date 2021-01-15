import * as lodash from 'lodash'

// see ResourceStatusMessageClarity.kt
const CLARITY = [
  { value: 0, name: undefined },
  { value: 70, name: 'HIGH' },
  { value: 100, name: 'HIGHEST' }
]

export const isHighOrAbove = clarity => clarity >= 70

export const getClarityLabel = clarity => lodash.chain(CLARITY)
  .filter(c => c.value <= clarity)
  .maxBy('value')
  .value()
  .name