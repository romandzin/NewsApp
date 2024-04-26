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

rootProject.name = "NewsApp"
include(":app")
include(":feature-saved")
include(":feature-source")
include(":feature-headlines")
include(":core-module")
include(":dat:data-api")
include(":dat:data_impl")
