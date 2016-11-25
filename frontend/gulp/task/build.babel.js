import gulp from 'gulp'
import webpack from 'webpack-stream'
import webpackConfig     from '../../webpack.config.js'
import webpackConfigProd from '../../webpack.config.prod.js'
import stylus from 'gulp-stylus'
import plumber from 'gulp-plumber'
import del from 'del'

gulp.task('build', () => { gulp.start('build:dev')})
gulp.task('build:dev', () => {
  gulp.start('webpack:dev')
  gulp.start('stylus')
  gulp.start('media')
  //gulp.start('sharder')
})
gulp.task('build:prod', ['clean'], () => {
  gulp.start('webpack:prod')
  gulp.start('stylus')
  gulp.start('media')
  //gulp.start('sharder')
})

gulp.task('clean', cd => {
  del(['./assets/dist/*'], cd)
})

gulp.task('webpack', () => { gulp.start('webpack:dev')})
gulp.task('webpack:dev', () => {
  return gulp.src('')
    .pipe(plumber())
    .pipe(webpack(webpackConfig))
    .pipe(gulp.dest('./assets/dist/javascripts/'))
})
gulp.task('webpack:prod', () => {
  return gulp.src('')
    .pipe(plumber())
    .pipe(webpack(webpackConfigProd))
    .pipe(gulp.dest('./assets/dist/javascripts/'))
})

gulp.task('media', () => {
  return gulp.src('./assets/sources/media/*')
    .pipe(plumber())
    .pipe(gulp.dest('./assets/dist/media/'))
})

gulp.task('stylus', () => {
  return gulp.src('./assets/sources/stylus/*')
    .pipe(plumber())
    .pipe(stylus())
    .pipe(gulp.dest('./assets/dist/css/'))
})

/*
 gulp.task('sharder', () => {
 return gulp.src('./assets/sources/sharder/*')
 .pipe(gulp.dest('./assets/dist/sharder/'))
 })
 */
