const path = require('path')
const HtmlWebpackPlugin = require('html-webpack-plugin')

const IS_DEV = process.env.NODE_ENV === 'development'

/** @type {import('webpack').Configuration} */
const config = {
  mode: IS_DEV ? 'development' : 'production',
  entry: './src/index.tsx',
  plugins: configurePlugins(),
  output: {
    filename: 'bundle.js',
    path: path.resolve('build'),
    clean: true,
  },
  resolve: {
    extensions: ['.ts', '.tsx', '...'],
  },
  stats: 'minimal',
  devtool: IS_DEV ? 'eval-source-map' : undefined,
  devServer: {
    open: false,
    port: 80,
  },
  module: {
    rules: [
      {
        test: /\.(js|jsx|ts|tsx)$/i,
        include: path.resolve(__dirname, 'src'),
        use: [
          {
            loader: 'babel-loader',
            options: {
              presets: ['@babel/preset-env', '@babel/preset-react', '@babel/preset-typescript'],
            },
          },
        ],
      },
      {
        test: /(?<!\.module)\.css$/,
        include: path.resolve(__dirname, 'src'),
        use: [
          { loader: 'style-loader' },
          {
            loader: 'css-loader',
            options: { modules: false },
          },
        ],
      },
      {
        test: /\.module\.css$/i,
        include: path.resolve(__dirname, 'src'),
        use: [
          { loader: 'style-loader' },
          {
            loader: 'css-loader',
            options: { modules: true },
          },
        ],
      },
      {
        test: /\.(png|svg|jpg|jpeg|gif)$/i,
        type: 'asset/resource',
      },
    ],
  },
  performance: {
    hints: false,
  },
}

function configurePlugins() {
  const plugins = [
    new HtmlWebpackPlugin({
      template: './public/index.html',
      title: 'Mocka',
      templateParameters: {
        description: 'The RESTful API mocking service',
      },
      favicon: './public/favicon.ico',
    }),
  ]

  if (IS_DEV) {
    const ForkTsCheckerWebpackPlugin = require('fork-ts-checker-webpack-plugin')
    plugins.push(
      new ForkTsCheckerWebpackPlugin({
        eslint: {
          files: './src/**/*.{ts,tsx,js,jsx}',
        },
        typescript: {
          diagnosticOptions: {
            semantic: true,
            syntactic: true,
          },
          mode: 'write-references',
        },
      })
    )
  }

  return plugins
}

module.exports = config
