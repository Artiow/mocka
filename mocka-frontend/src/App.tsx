import React from 'react'
import logo from './logo.svg'
import styles from './App.module.css'

export const App: React.FC = () => {
  return (
    <div>
      <header className={styles.header}>
        <img src={logo} className={styles.logo} alt='logo' />
        <p>The RESTful API mocking service</p>
      </header>
    </div>
  )
}
