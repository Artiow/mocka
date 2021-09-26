import React from 'react'

type HeaderProps = React.PropsWithChildren<unknown>

export const Header: React.FC<HeaderProps> = () => {
  return (
    <header
      className={
        'flex flex-shrink justify-center py-2 bg-gradient-to-t from-gray-200 ' +
        'border-b border-gray-300'
      }
    >
      <h1 className='text-3xl font-thin text-gray-500 select-none'>Mocka</h1>
    </header>
  )
}
