import { Component } from 'react'

export class AutoAction extends Component {
  componentDidMount() {
    const {interval, action} = this.props
    this.interval = setInterval(action, Math.max(5, interval) * 1000)
  }

  componentWillUnmount() {
    clearInterval(this.interval)
  }

  render() {
    return null
  }
}