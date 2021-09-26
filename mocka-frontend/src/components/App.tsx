import Editor from '@monaco-editor/react'
import { editor as Monaco } from 'monaco-editor'
import { TailwindFont } from 'mocka/constants/tailwind-tokens'
import { useMutation, useQuery } from 'react-query'
import { BiLoaderAlt } from 'react-icons/bi'
import { apiGetMethodScript, apiUpdateMethodScript } from 'mocka/api/method'
import debounce from 'debounce'
import { useRef } from 'react'
import { Header } from './Header'
import { ScriptsExplorer } from './ScriptsExplorer'

const SCRIPT_ID = 0
const EDITOR_TOP_MAPPING_PX = 5

export const App: React.FC = () => {
  const editorRef = useRef<Monaco.IStandaloneCodeEditor>()

  const { isLoading, data: loadedScriptStr } = useQuery(
    ['method', SCRIPT_ID, 'script'],
    () => apiGetMethodScript(SCRIPT_ID),
    {
      onSuccess: data => {
        if (editorRef.current) {
          editorRef.current.setValue(data)
        }
      },
    }
  )

  const updateScriptMutation = useMutation((scriptStr: string) =>
    apiUpdateMethodScript(SCRIPT_ID, scriptStr)
  )

  const handleEditorMount = (editor: Monaco.IStandaloneCodeEditor) => {
    editorRef.current = editor
    if (loadedScriptStr) {
      editor.setValue(loadedScriptStr)
    }
  }

  const handleEditorChange = debounce(() => {
    if (editorRef.current) {
      updateScriptMutation.mutate(editorRef.current.getValue())
    }
  }, 1000)

  return (
    <div className='flex flex-col h-screen'>
      <Header />
      <div className='flex flex-row flex-grow'>
        <div className='w-1/6 min-w-[200px] border-0 border-r border-gray-300 border-solid'>
          <ScriptsExplorer />
        </div>
        <div className='w-5/6 overflow-hidden'>
          {isLoading ? (
            <div className='h-full flex justify-center items-center'>
              <LoadingIcon />
            </div>
          ) : (
            <div className='h-full'>
              <Editor
                defaultLanguage='javascript'
                options={{
                  fontFamily: TailwindFont.Mono,
                  padding: { top: EDITOR_TOP_MAPPING_PX },
                  minimap: { enabled: false },
                }}
                onMount={handleEditorMount}
                onChange={handleEditorChange}
                loading={<LoadingIcon />}
              />
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

const LoadingIcon = () => <BiLoaderAlt className='text-4xl text-gray-600 animate-spin' />
