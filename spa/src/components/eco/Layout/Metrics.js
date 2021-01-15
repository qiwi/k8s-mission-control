import React from 'react'
import {YMInitializer} from 'react-yandex-metrika';

import loadConfig from '../../../config'

const config = loadConfig()

export const Metrics = () => {
  const metrics = []
  const yandex = config.site.metrics.yandexMetrika

  if (yandex && yandex.enable) {
    metrics.push(<YMInitializer key="yandex" accounts={[Number.parseInt(yandex.account)]}
                   options={{clickmap: true, trackLinks: true, accurateTrackBounce: true, webvisor: true}}
                   version="2" />)
  }

  return metrics
}