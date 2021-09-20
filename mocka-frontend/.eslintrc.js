/** @type {import("eslint").Linter.Config} */
const config = {
  env: {
    browser: true,
    es2021: true,
    node: true,
  },
  ignorePatterns: ['build'],
  overrides: [
    {
      files: ['./src/**/*'],
      extends: [
        'eslint:recommended',
        'plugin:react/recommended',
        'plugin:react-hooks/recommended',
        'prettier',
      ],
      plugins: ['react', 'import'],
      rules: {
        'no-console': 'warn',
        'no-empty': 'warn',
        'prefer-const': 'warn',
        'no-unused-vars': 'warn',
        'no-unsafe-optional-chaining': 'warn',
        'require-atomic-updates': 'warn',
        'no-return-await': 'warn',
        'require-await': 'warn',
        'no-useless-catch': 'warn',
        'import/order': 'warn',
        'import/first': 'warn',
      },
    },
    {
      files: ['./src/**/*.{ts,tsx}'],
      extends: [
        'plugin:@typescript-eslint/recommended',
        'plugin:@typescript-eslint/recommended-requiring-type-checking',
      ],
      parser: '@typescript-eslint/parser',
      parserOptions: {
        tsconfigRootDir: __dirname,
        project: ['./tsconfig.json'],
      },
      plugins: ['@typescript-eslint'],
      rules: {
        '@typescript-eslint/ban-types': 'warn',
        '@typescript-eslint/no-unsafe-member-access': 'off',
        '@typescript-eslint/no-unsafe-assignment': 'off',
      },
    },
  ],
  settings: {
    react: {
      version: '17.0',
    },
  },
}

module.exports = config
