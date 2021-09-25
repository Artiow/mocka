import Editor from '@monaco-editor/react'
import { TailwindFont } from 'mocka/constants/tailwind-tokens'
import { useQuery } from 'react-query'
import { QueryKey } from 'mocka/constants/query-keys'
import { apiGetScriptSample } from 'api'
import { BiLoaderAlt } from 'react-icons/bi'
import { Header } from './Header'
import { ScriptsExplorer } from './ScriptsExplorer'

export const App: React.FC = () => {
  const { isLoading, data } = useQuery(QueryKey.ScriptSample, apiGetScriptSample)

  return (
    <div className='flex flex-col h-screen'>
      <Header />
      <div className='flex flex-row flex-grow'>
        <div className='w-1/6 min-w-[200px] border-0 border-r border-gray-300 border-solid'>
          <ScriptsExplorer />
        </div>
        <div className='w-5/6 pt-2 overflow-hidden'>
          {isLoading ? (
            <div className='w-min animate-spin'>
              <BiLoaderAlt className='text-4xl text-gray-600' />
            </div>
          ) : (
            <Editor
              className='font-sans'
              defaultLanguage='javascript'
              defaultValue={data}
              options={{
                fontFamily: TailwindFont.Mono,
              }}
            />
          )}
        </div>
      </div>
    </div>
  )
}
