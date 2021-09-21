/** @type {Parameters<import('tailwindcss')>[0]} */
const config = {
  mode: 'jit',
  purge: ['./src/**/*.tsx'],
  corePlugins: {
    preflight: false,
  },
}

module.exports = config
