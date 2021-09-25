/* CSS */
declare module '*.css'

// CSS modules
declare module '*.module.css' {
  const classes: { [key: string]: string }
  export default classes
}

// Images
declare module '*.svg' {
  const svg: string
  export default svg
}
