module.exports = {
  entry: {
    app: './assets/sources/es6/qmobi/app.babel.js'
  },
  output: {
    path:__dirname + '/assets/dist/javascripts',
    filename: 'app.bundle.js'
  },
  module:{
    loaders: [
      {
        test: /\.js$/,
        exclude: /(node_modules|bower_components)/,
        loader: 'babel',
        query: {
          presets: ['es2015', 'react']
        }
      },
      {
        test: /\.styl/,
        exclude: /(node_modules|bower_components)/,
        loader: 'style-loader!css-loader?modules&stylus-loader?paths=node_modules/bootstrap-stylus/stylus/'
      }

    ]
  }
}
