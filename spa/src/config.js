const BASE_CONFIG = {
  site: {
    baseUrl: window.location.origin
  },
  api: {
    url: window.location.origin
  }
}

export default () => ((serverConfig) => ({
  ...BASE_CONFIG,
  ...serverConfig.site,
  site: {
    ...BASE_CONFIG.site,
    ...serverConfig.site
  },
  api: {
    ...BASE_CONFIG.api,
    ...serverConfig.api
  }
}))(window.__SERVER_CONFIG)