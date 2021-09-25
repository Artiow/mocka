import Editor from '@monaco-editor/react'
import { TailwindFont } from '../constants/tailwind-tokens'
import { ScriptsExplorer } from './ScriptsExplorer'
import { Header } from './Header'

export const App: React.FC = () => {
  return (
    <div className='flex flex-col h-screen'>
      <Header />
      <div className='flex flex-row flex-grow'>
        <div className='w-1/6 min-w-[200px] border-0 border-r border-gray-300 border-solid'>
          <ScriptsExplorer />
        </div>
        <div className='w-5/6 pt-2 overflow-hidden'>
          <Editor
            className='font-sans'
            defaultLanguage='javascript'
            defaultValue={`// Your script goes here... \n\n`}
            options={{
              fontFamily: TailwindFont.Mono,
            }}
          />
        </div>
      </div>
    </div>
  )
}
