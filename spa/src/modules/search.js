import { push } from 'connected-react-router'

const initialState = {
  value: null
}

export const SET_SEARCH_TEXT = 'search/SET_SEARCH_TEXT'

export default (state = initialState, action) => {
  if (action.type === SET_SEARCH_TEXT) {
    return { ...state, value: action.value }
  } else {
    return state
  }
}

export function selectSearch(state) {
  return {
    ...state.search,
    value: state.router.location.query.search || '',
  }
}

export function goSearch(value) {
  return (dispatch) => {
    dispatch({ type: SET_SEARCH_TEXT, value })
    dispatch(push({ search: value ? `?search=${value}` : '' }))
  }
}