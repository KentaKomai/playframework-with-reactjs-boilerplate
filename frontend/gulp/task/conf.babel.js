import gutil from 'gulp-util'

var config = {
  paths : {
    src: 'sources',
    dist: 'dist',
    tmp: '.tmp',
    e2e: 'e2e'
  },
  errorHandler : title => {
    return function(err) {
      gutil.log(gutil.colors.red('[' + title + ']'), err.toString())
      this.emit('end')
    }
  }
}

export default config