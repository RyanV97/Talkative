package dev.cryptcraft.talkative.common.util

import dev.cryptcraft.talkative.common.data.markers.Marker
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.StringTag
import dev.cryptcraft.talkative.common.data.tree.DialogBranch
import dev.cryptcraft.talkative.common.data.tree.DialogNode
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

object FileUtil {
    var DIR_WORLD: Path? = null
    var DIR_BRANCHES: Path? = null
    var DIR_MARKERS: Path? = null

    //Branches
    fun createBranchAtPath(path: String) {
        DIR_BRANCHES?.let { dir ->
            val file = File(dir.toFile(), "$path.branch")
            val branch = DialogBranch()
            branch.addNode(DialogNode(0))
            val branchTag = branch.serialize()
            //ToDo: Do a Exists check to handle overwriting
            file.parentFile.mkdirs()
            NbtIo.writeCompressed(branchTag, file)
        }
    }

    fun getBranchListCompound(): CompoundTag {
        val tag = CompoundTag()
        val list = ListTag()
        DIR_BRANCHES?.let { dir ->
            getFilesAtPath(dir, ".branch").forEach {
                list.add(StringTag.valueOf(it))
            }
        }

        tag.put("branchList", list)
        return tag
    }

    fun getBranchFromPath(path: String): DialogBranch? {
        DIR_BRANCHES?.let { dir ->
            return readCompoundFromPath(dir, "$path.branch")?.let { DialogBranch.deserialize(it) }
        }
        return null
    }

    fun branchExists(path: String): Boolean {
        DIR_BRANCHES?.let { dir -> return File(dir.toFile(), "$path.branch").exists() }
        return false
    }

    fun saveBranchData(path: String, data: CompoundTag) {
        writeCompoundToPath(DIR_BRANCHES, "$path.branch", data)
    }

    fun deleteBranchAtPath(path: String) {
        deleteFileAtPath(DIR_BRANCHES, "$path.branch")
    }

    //Markers
    fun createMarkerAtPath(path: String) {
        DIR_MARKERS?.let { dir ->
            val file = File(dir.toFile(), "$path.marker")
            val marker = Marker()
            val markerTag = marker.serialize()
            file.parentFile.mkdirs()
            NbtIo.writeCompressed(markerTag, file)
        }
    }

    fun getMarkerFromPath(path: String): Marker? {
        DIR_MARKERS?.let { dir ->
            return readCompoundFromPath(dir, "$path.marker")?.let { Marker.deserialize(it) }
        }
        return null
    }

    fun saveMarkerData(path: String, data: CompoundTag) {
        writeCompoundToPath(DIR_MARKERS, "$path.marker", data)
    }

    fun deleteMarkerAtPath(path: String) {
        deleteFileAtPath(DIR_MARKERS, "$path.branch")
    }

    //General
    private fun readCompoundFromPath(dir: Path?, path: String): CompoundTag? {
        dir?.let { dir ->
            val file = File(dir.toFile(), path)
            if(file.exists()) return NbtIo.readCompressed(file)
        }
        return null
    }

    private fun writeCompoundToPath(dir: Path?, path: String, data: CompoundTag) {
        dir?.let { dir ->
            val file = File(dir.toFile(), path)
            NbtIo.writeCompressed(data, file)
        }
    }

    private fun deleteFileAtPath(dir: Path?, path: String, ) {
        dir?.let { dir ->
            val file = File(dir.toFile(), path)
            file.delete()
        }
    }

    fun getFilesAtPath(dir: Path, suffix: String): ArrayList<String> {
        val paths: ArrayList<String> = ArrayList()

        Files.walk(dir).forEach {
            if(!it.toFile().isDirectory && it.toString().endsWith(suffix)) {
                paths.add(it.toFile().path.removePrefix("$dir\\").removeSuffix(suffix))
            }
        }

        paths.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it })
        return paths
    }
}