package ryanv.talkative.common.util

import java.nio.file.Files
import java.nio.file.Path

class FileUtil {

    companion object {

        lateinit var DIR_WORLD: Path
        lateinit var DIR_BRANCH: Path

        suspend fun getBranchFilePaths(): ArrayList<String> {
            val paths: ArrayList<String> = ArrayList()

            Files.walk(DIR_BRANCH).forEach {
                if(!it.toFile().isDirectory && it.toString().endsWith(".branch")) {
                    var s = it.toFile().path
                    s = s.removePrefix("$DIR_WORLD\\talkative\\branches\\")
                    paths.add(s)
                }
            }
            paths.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it })
            return paths
        }

    }

}