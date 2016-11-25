import gulp from "gulp"

import './gulp/task/build.babel'
import './gulp/task/watch.babel'
import './gulp/task/test.babel'

gulp.task("default",[], () => {
	console.log("hello gulp")
})


