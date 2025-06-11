package ir.amirab.installer.tasks.windows

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Handlebars
import ir.amirab.installer.extensiion.WindowsConfig
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.mapProperty
import java.io.ByteArrayInputStream
import java.io.File

abstract class NsisTask : DefaultTask() {

    @get:InputDirectory
    abstract val sourceFolder: DirectoryProperty

    @get:OutputDirectory
    abstract val destFolder: DirectoryProperty

    @get:Input
    abstract val outputFileName: Property<String>

    @get:InputFile
    abstract val nsisTemplate: Property<File>

    @get:Input
    abstract val commonParams: Property<WindowsConfig>

    @get:Input
    val extraParams: MapProperty<String, Any> = project.objects.mapProperty<String, Any>()

    @get:Internal
    abstract val nsisExecutable: Property<File>

    init {
        nsisExecutable.convention(
            project.provider { File("C:\\Program Files (x86)\\NSIS\\makensis.exe") }
        )
    }

    private fun createHandleBarContext(): Context {
        val commonParams = commonParams.get()
        val common = mapOf(
            "app_name" to commonParams.appName!!,
            "app_display_name" to commonParams.appDisplayName!!,
            "app_version" to commonParams.appVersion!!,
            "app_display_version" to commonParams.appDisplayVersion!!,
            "app_data_dir_name" to commonParams.appDataDirName!!,
            "license_file" to commonParams.licenceFile!!,
            "icon_file" to commonParams.iconFile!!,
        )
        val overrides = mapOf(
            "input_dir" to sourceFolder.get().asFile.absolutePath,
            "output_file" to "${destFolder.file(outputFileName).get().asFile.path}.exe",
        )
        return Context.newContext(
            extraParams
                .get()
                .plus(common)
                .plus(overrides)
        )
    }

    @TaskAction
    fun createInstaller() {
        val appName = commonParams.get().appName
        val appDisplayName = commonParams.get().appDisplayName
        val appVersion = commonParams.get().appVersion
        val appDisplayVersion = commonParams.get().appDisplayVersion
        val appDataDirName = commonParams.get().appDataDirName
        val inputDir = sourceFolder.get().asFile
        val outputFileName = outputFileName.get()
        val licenseFile = commonParams.get().licenceFile.asFile
        val iconFile = commonParams.get().iconFile.asFile
        val nsisTemplate = nsisTemplate.get().asFile
        val extraParams = extraParams.get()

        val tempDir = temporaryDir
        val nsisScript = File(tempDir, "installer.nsi")
        
        val scriptContent = nsisTemplate.readText()
            .replace("\${APP_NAME}", appName)
            .replace("\${APP_DISPLAY_NAME}", appDisplayName)
            .replace("\${APP_VERSION}", appVersion)
            .replace("\${APP_DISPLAY_VERSION}", appDisplayVersion)
            .replace("\${APP_DATA_DIR_NAME}", appDataDirName)
            .replace("\${INPUT_DIR}", inputDir.absolutePath)
            .replace("\${OUTPUT_FILE}", outputFileName)
            .replace("\${LICENSE_FILE}", licenseFile.absolutePath)
            .replace("\${ICON_FILE}", iconFile.absolutePath)

        extraParams.forEach { (key, value) ->
            scriptContent.replace("\${$key}", value.toString())
        }

        nsisScript.writeText(scriptContent)

        val outputFile = File(project.buildDir, "$outputFileName.exe")
        project.exec {
            workingDir = tempDir
            commandLine(
                "makensis",
                nsisScript.absolutePath
            )
        }
    }
}
