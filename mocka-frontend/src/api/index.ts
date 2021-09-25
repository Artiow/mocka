import axios from 'axios'
import { config } from 'mocka/config'
import urlJoin from 'url-join'

export function apiGetScriptSample(): Promise<string> {
  return axios.get<string>(urlJoin(config.apiUrl, 'script', 'sample')).then(res => res.data)
}

export function apiGetEndpointTest(): Promise<string> {
  return axios.get<string>(urlJoin(config.apiUrl, 'endpoint', 'test')).then(res => res.data)
}
