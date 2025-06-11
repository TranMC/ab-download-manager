package ir.amirab.installer.tasks.macos

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class CreateDmgTask : DefaultTask() {
    @get:InputDirectory
    abstract val inputDir: DirectoryProperty

    @get:OutputDirectory
    abstract val destFolder: DirectoryProperty

    @get:Input
    abstract val appName: Property<String>

    @get:Input
    abstract val iconSize: Property<Int>

    @get:Input
    abstract val windowWidth: Property<Int>

    @get:Input
    abstract val windowHeight: Property<Int>

    @get:Input
    abstract val iconsY: Property<Int>

    @get:Input
    abstract val appOffsetX: Property<Int>

    @get:Input
    abstract val folderOffsetX: Property<Int>

    @get:Input
    abstract val windowX: Property<Int>

    @get:Input
    abstract val windowY: Property<Int>

    @get:Input
    abstract val appFileName: Property<String>

    @get:InputFile
    abstract val backgroundImage: Property<File>

    @get:InputFile
    abstract val volumeIcon: Property<File>

    @get:Input
    abstract val licenseFile: Property<File>

    @get:Input
    abstract val outputFileName: Property<String>

    @get:Internal
    abstract val dmgExecutable: Property<File>

    init {
        dmgExecutable.convention(
            project.provider {
                val process = ProcessBuilder("which", "create-dmg")
                    .redirectErrorStream(true)
                    .start()
                val path = process.inputStream.bufferedReader().readText().trim()
                if (path.isBlank()) {
                    throw GradleException("create-dmg not found in PATH. Please install it or check your PATH.")
                }
                File(path)
            }
        )
    }

    private fun createDmgContext(): Map<String, Any> {
        val outputFileNameWithExt = outputFileName.get() + ".dmg"
        return mapOf(
            "input_dir" to inputDir.get().asFile.absolutePath.asQuoted(),
            "output_file" to destFolder.file(outputFileNameWithExt)
                .get().asFile.absolutePath.asQuoted(),
            "background_image" to backgroundImage.get().absolutePath.asQuoted(),
            "volume_icon" to volumeIcon.get().absolutePath.asQuoted(),
            "icon_file" to appFileName.get().asQuoted(),
            "app_name" to appName.get().asQuoted(),
            "icon_size" to iconSize.get(),
            "window_width" to windowWidth.get(),
            "window_height" to windowHeight.get(),
            "icons_y" to iconsY.get(),
            "app_offset_x" to appOffsetX.get(),
            "folder_offset_x" to folderOffsetX.get(),
            "window_x" to windowX.get(),
            "window_y" to windowY.get(),
            "license_file" to licenseFile.get().absolutePath
        )
    }

    private fun String.asQuoted() = "\"${this}\""

    @TaskAction
    fun createDmg() {
        val appName = extension.appName.get()
        val inputDir = extension.inputDir.get().asFile
        val appFileName = extension.appFileName.get()
        val backgroundImage = extension.backgroundImage.get().asFile
        val outputFileName = extension.outputFileName.get()
        val licenseFile = extension.licenseFile.get().asFile
        val volumeIcon = extension.volumeIcon.get().asFile

        val tempDir = temporaryDir
        val dmgDir = File(tempDir, "dmg")
        dmgDir.mkdirs()

        val appDir = File(dmgDir, appFileName)
        project.copy {
            from(inputDir)
            into(appDir)
        }

        val backgroundDir = File(dmgDir, ".background")
        backgroundDir.mkdirs()
        project.copy {
            from(backgroundImage)
            into(backgroundDir)
        }

        val dmgFile = File(project.buildDir, "$outputFileName.dmg")
        project.exec {
            workingDir = tempDir
            commandLine(
                "hdiutil", "create",
                "-volname", appName,
                "-srcfolder", dmgDir,
                "-ov",
                "-format", "UDZO",
                dmgFile.absolutePath
            )
        }
    }
}