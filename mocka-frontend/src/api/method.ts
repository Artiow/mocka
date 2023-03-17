import axios from 'axios'
import { config } from 'mocka/config'
import urlJoin from 'url-join'

export async function apiGetMethodScript(methodId: number): Promise<string> {
  const url = urlJoin(config.apiUrl, 'method', methodId.toString(), 'script')
  const res = await axios.get<string>(url)
  return res.data
}

export async function apiUpdateMethodScript(methodId: number, scriptStr: string): Promise<void> {
  const url = urlJoin(config.apiUrl, 'method', methodId.toString(), 'script')
  await axios.put(url, JSON.stringify(scriptStr), {
    headers: { 'Content-Type': 'application/javascript' },
  })
}
