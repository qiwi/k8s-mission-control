export const makeKey = (cluster, ns, name) => `${ns}/${name}@${cluster}`

export const makeOrder = (indexes) => indexes.reverse().reduce((a, n, i) => a + n * Math.pow(100, i))
