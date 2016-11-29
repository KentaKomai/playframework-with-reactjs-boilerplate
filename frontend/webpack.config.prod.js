const webpack = require('webpack')
module.exports = {
  entry: {
    app: './assets/sources/es6/app.babel.js'
  },
  output: {
    path:__dirname + '/assets/dist/javascripts',
    filename: 'app.bundle.js'
  },
  module:{
    loaders: [
      {
        test: /\.babel.js$/,
        exclude: /(node_modules|bower_components)/,
        loader: 'babel',
        query: {
          presets: ['es2015', 'react']
        }
      }
    ]
  },
  //↓↓↓↓追加
  plugins: [
    new webpack.optimize.UglifyJsPlugin(),
    new webpack.DefinePlugin({
      'process.env': {
        NODE_ENV: JSON.stringify('production')
      }
    })
  ]
}
