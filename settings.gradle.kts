pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "DracoFocusAppclone"

// Incluimos el módulo app que está dentro de la carpeta mobile
include(":app")
project(":app").projectDir = file("mobile/app")
