import React from 'react'

const scriptNames: string[] = []

type ScriptsExplorerProps = React.PropsWithChildren<unknown>

export const ScriptsExplorer: React.FC<ScriptsExplorerProps> = () => {
  return (
    <section className='flex flex-col h-full bg-gray-50'>
      <h2
        className={
          'flex-shrink px-3 py-1 m-0 border-b border-gray-300 ' +
          'text-sm text-gray-700 uppercase select-none ' +
          'bg-gradient-to-t from-gray-200 to-gray-100'
        }
      >
        Scripts
      </h2>
      {scriptNames.length > 0 ? null : (
        <div className='flex flex-col h-full justify-center'>
          <p className='text-lg text-gray-400 font text-center'>No scripts</p>
        </div>
      )}
    </section>
  )
}
