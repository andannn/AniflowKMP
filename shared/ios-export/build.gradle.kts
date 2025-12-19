plugins {
    id("kmp.ext")
}

kmpExt {
    withIOS()
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true

            export(project(":shared:domain:api"))
            export(project(":shared:usecase:data-provider:api"))
            export(project(":shared:usecase:paging"))
            export(project(":shared:usecase:operation"))
            export(project(":shared:usecase:sync"))
            export(project(":shared:usecase:util"))
            export(project(":shared:platform"))
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":shared:domain:api"))
                api(project(":shared:usecase:data-provider:api"))
                api(project(":shared:usecase:paging"))
                api(project(":shared:usecase:operation"))
                api(project(":shared:usecase:sync"))
                api(project(":shared:usecase:util"))
                api(project(":shared:platform"))
            }
        }
    }
}
