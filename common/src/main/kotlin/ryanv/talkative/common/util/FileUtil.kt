package ryanv.talkative.common.util

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtIo
import ryanv.talkative.common.consts.NBTConstants
import ryanv.talkative.common.data.tree.DialogBranch
import ryanv.talkative.common.data.tree.DialogNode
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

object FileUtil {
    lateinit var DIR_WORLD: Path
    lateinit var DIR_BRANCH: Path

    fun createBranchAtPath(path: String) {
        val file = File(DIR_BRANCH.toFile(), "$path.branch")
        val branch = DialogBranch()
        branch.addNode(DialogNode(nodeId = 0))
        val branchTag = branch.serialize(CompoundTag())
        //ToDo: Do a Exists check to handle overwriting
        file.parentFile.mkdirs()
        NbtIo.writeCompressed(branchTag, file)
    }

    fun getBranchFilePaths(): ArrayList<String> {
        val paths: ArrayList<String> = ArrayList()

        Files.walk(DIR_BRANCH).forEach {
            if(!it.toFile().isDirectory && it.toString().endsWith(".branch")) {
                var s = it.toFile().path
                s = s.removePrefix("$DIR_WORLD\\talkative\\branches\\")
                s = s.removeSuffix(".branch")
                paths.add(s)
            }
        }
        paths.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it })
        return paths
    }

    fun getBranchFromPath(path: String): DialogBranch? {
        return getBranchDataFromPath(path)?.let { DialogBranch.deserialize(it) }
    }

    fun getBranchDataFromPath(path: String): CompoundTag? {
        val file = File(DIR_BRANCH.toFile(), "$path.branch")
        if(file.exists())
            return NbtIo.readCompressed(file)
        return null
    }

    fun saveBranchData(path: String, data: CompoundTag) {
        val file = File(DIR_BRANCH.toFile(), "$path.branch")
        NbtIo.writeCompressed(data, file)
    }
}