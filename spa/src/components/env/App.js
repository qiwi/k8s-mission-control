import React, {Component} from 'react'

import {Provider as StoreProvider} from 'react-redux'
import {ConnectedRouter} from 'connected-react-router'
import {themes, ThemeProvider, CacheProvider, cache} from '@qiwi/pijma-core'

import {store, history} from '../../store'

import {Content, Body, Metrics, ConnectedHeader} from '../eco/Layout'
import {ConnectedRoutes} from './Routes'

class App extends Component {
  render() {
    return <>
      <Metrics />
      <CacheProvider value={cache}>
        <ThemeProvider theme={themes.orange}>
          <StoreProvider store={store}>
            <ConnectedRouter history={history}>
              <Body>
                <ConnectedHeader/>
                <Content>
                  <ConnectedRoutes/>
                </Content>
              </Body>
            </ConnectedRouter>
          </StoreProvider>
        </ThemeProvider>
      </CacheProvider>
    </>
  }
}

export default App
