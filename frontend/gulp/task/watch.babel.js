import gulp from 'gulp'

gulp.task('watch', ['build'], () => {
  gulp.watch(['assets/sources/**/*.js'], ['lint', 'webpack'])
  gulp.watch(['assets/sources/**/*.jsx'], ['lint', 'webpack'])
  gulp.watch(['assets/sources/**/*.styl'], ['stylus'])
  gulp.watch(['assets/sources/media/*'], ['media'])
  //gulp.watch(['assets/sources/sharder/*'], ['sharder'])
})