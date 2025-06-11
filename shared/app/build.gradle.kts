plugins {
    id(MyPlugins.kotlin)
    id(MyPlugins.composeBase)
    id(Plugins.Kotlin.serialization)
}
dependencies {
    api(project(":shared:app-utils"))
    api(libs.markdownRenderer.core)
    
    // Compose dependencies
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.foundation)
    implementation(libs.compose.runtime)
    implementation(libs.compose.animation)
    
    // Debug dependencies
    implementation(libs.compose.ui.tooling)
}