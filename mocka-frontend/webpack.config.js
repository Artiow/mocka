const path = require('path')
const DotenvPlugin = require('dotenv-webpack')
const TsConfigPathsPlugin = require('tsconfig-paths-webpack-plugin')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const CleanTerminalPlugin = require('clean-terminal-webpack-plugin')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')

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
    plugins: [new TsConfigPathsPlugin()],
  },
  stats: IS_DEV ? 'minimal' : undefined,
  devtool: IS_DEV ? 'eval-source-map' : undefined,
  devServer: {
    open: false,
    port: 80,
    client: {
      overlay: { errors: true, warnings: false },
    },
  },
  module: {
    rules: [
      {
        test: /\.(js|jsx|ts|tsx)$/i,
        include: path.resolve('src'),
        use: [
          {
            loader: 'babel-loader',
            options: {
              plugins: [['@babel/plugin-transform-runtime']],
              presets: [
                '@babel/preset-env',
                '@babel/preset-typescript',
                ['@babel/preset-react', { runtime: 'automatic' }],
              ],
            },
          },
        ],
      },
      {
        test: /\.css$/,
        use: [
          { loader: MiniCssExtractPlugin.loader },
          {
            loader: 'css-loader',
            options: {
              modules: {
                auto: true,
                localIdentName: IS_DEV ? '[name]-[local]-[hash:base64:8]' : '[hash:base64]',
              },
            },
          },
          {
            loader: 'postcss-loader',
            options: {
              postcssOptions: {
                plugins: ['tailwindcss', 'autoprefixer'],
              },
            },
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
    new DotenvPlugin({ path: '.env', safe: '.env.example' }),
    new MiniCssExtractPlugin(),
    new CleanTerminalPlugin(),
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
