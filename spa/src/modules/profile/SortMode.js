export class SortMode {
  constructor(fieldName, direction = 'asc') {
    this.field = fieldName
    this.direction = direction === 'asc' ? 'asc' : 'desc'
  }

  static parse(str) {
    let sortParts = str.split('-')
    return new SortMode(sortParts[0], sortParts[1] || 'asc')
  }

  toString() {
    return `${this.field}-${this.direction}`
  }
}