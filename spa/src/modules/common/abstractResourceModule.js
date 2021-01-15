const initialState = {
  byKeys: { }
}

export const initialResource = {
  loading: true,
  error: false,
  data: undefined
}

export class AbstractResourceModule {
  constructor(resourceName, keyGetterFunc, loaderFunc) {
    this.keyGetterFunc = keyGetterFunc
    this.loaderFunc = loaderFunc

    this._load_action = `resources/${resourceName}/LOAD`
    this._complete_load_action = `resources/${resourceName}/COMPLETE`
    this._fail_load_action = `resources/${resourceName}/FAIL`
  }

  handle(state = initialState, action) {
    switch (action.type) {
      case this._load_action:
        return {
          ...state,
          byKeys: {
            ...state.byKeys,
            [action.payload.key]: initialResource
          }
        }
      case this._complete_load_action:
        return {
          ...state,
          byKeys: {
            ...state.byKeys,
            [action.payload.key]: {
              loading: false,
              error: false,
              data: action.payload.data
            }
          }
        }
      case this._fail_load_action:
        return {
          ...state,
          byKeys: {
            ...state.byKeys,
            [action.payload.key]: {
              loading: false,
              error: action.payload.error,
              data: undefined
            }
          }
        }
      default:
        return state
    }
  }

  load(params) {
    const key = this.keyGetterFunc(params)
    return async (dispatch) => {
      dispatch({ type: this._load_action, payload: { key } })
      try {
        const data = await this.loaderFunc(params)
        dispatch({ type: this._complete_load_action, payload: { key, data } })
      } catch (error) {
        dispatch({ type: this._fail_load_action, payload: { key, error } })
      }
    }
  }
}
