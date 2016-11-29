import gulp from 'gulp'
import eslint from 'gulp-eslint'

gulp.task('test', ['lint'], () => {
  gulp.start('karma')
})

gulp.task('lint', () => {
  return gulp.src('./assets/sources/es6/**/*.js')
    .pipe(eslint({useEslintrc: true}))
    .pipe(eslint.format())
    .pipe(eslint.failAfterError())
})

gulp.task('karma', () => {
  console.log('TODO:')
})
