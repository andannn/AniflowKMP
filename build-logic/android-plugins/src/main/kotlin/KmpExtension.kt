import com.andanana.melodify.util.libs
import com.android.build.api.dsl.KotlinMultiplatformAndroidCompilation
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import javax.inject.Inject

abstract class KmpExtension
    @Inject
    constructor(
        val project: Project,
    ) {
        private val libs get() = project.libs
        private var alreadyConfigCommonDependency = false

        private var isAndroidConfig: Boolean = false
        private var isDesktopConfig: Boolean = false

        fun withDesktop() {
            isDesktopConfig = true

            project.extensions.configure<KotlinMultiplatformExtension> {
                jvm("desktop")
                configCommonDependencyIfNeeded()

                addJvmTargetIfNeeded()
            }
        }

        fun withAndroid(config: AndroidConfigParam.() -> Unit = {}) {
            val config = AndroidConfigParam().apply(config)

            isAndroidConfig = true

            // AGP config
            project.pluginManager.apply("com.android.kotlin.multiplatform.library")

            project.extensions.configure<KotlinMultiplatformExtension> {
                this.configure<KotlinMultiplatformAndroidLibraryExtension> {
                    compileSdk = 36
                    minSdk = 26

                    if (config.enableHostTest) {
                        withHostTestBuilder {
                            sourceSetTreeName = "test".takeIf { config.includeHostTestToCommonTest }
                        }.configure {
                            isIncludeAndroidResources = true
                        }
                    }

                    if (config.enableDeviceTest) {
                        withDeviceTestBuilder {
                            sourceSetTreeName =
                                "test".takeIf { config.includeDeviceTestToCommonTest }
                        }.configure {
                            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                        }
                    }
                }

                addJvmTargetIfNeeded()

                configCommonDependencyIfNeeded()

                sourceSets.apply {
                    androidMain.dependencies {
                        implementation(libs.findLibrary("koin.android").get())
                    }

                    if (config.enableHostTest) {
                        getByName("androidHostTest").dependencies {}
                    }

                    if (config.enableDeviceTest) {
                        getByName("androidDeviceTest").dependencies {
                            implementation(libs.findLibrary("koin.test.junit4").get())
                            implementation(libs.findLibrary("androidx.test.runner").get())
                            implementation(libs.findLibrary("androidx.test.core.ktx").get())
                            implementation(libs.findLibrary("androidx.test.ext.junit").get())
                        }
                    }
                }
            }
        }

        fun withIOS() {
            project.extensions.configure<KotlinMultiplatformExtension> {
                listOf(
                    iosArm64(),
                    iosSimulatorArm64(),
                )
                configCommonDependencyIfNeeded()
            }
        }

        private fun KotlinMultiplatformExtension.configCommonDependencyIfNeeded() {
            if (!alreadyConfigCommonDependency) {
                configKMPCommonDependency()

                alreadyConfigCommonDependency = true
            }
        }

        private fun KotlinMultiplatformExtension.configKMPCommonDependency() {
            sourceSets.apply {
                commonMain.dependencies {
                    val bom = libs.findLibrary("koin-bom").get()
                    implementation(project.dependencies.platform(bom))
                    implementation(libs.findLibrary("koin.core").get())

                    implementation(libs.findLibrary("kotlinx.collections.immutable").get())
                    implementation(libs.findLibrary("kotlinx.coroutines.core").get())
                    implementation(libs.findLibrary("napier").get())
                }

                commonTest.dependencies {
                    implementation(libs.findLibrary("kotlin.test").get())
                    implementation(libs.findLibrary("kotlinx.coroutines.test").get())
                }
            }
        }

        private fun KotlinMultiplatformExtension.addJvmTargetIfNeeded() {
            if (isDesktopConfig && isAndroidConfig) {
                configJvmTarget()
            }
        }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        private fun KotlinMultiplatformExtension.configJvmTarget() {
            applyDefaultHierarchyTemplate {
                common {
                    group("jvmAndAndroid") {
                        withJvm()
                        // https://github.com/andannn/Melodify/issues/470
                        withCompilations { it is KotlinMultiplatformAndroidCompilation }
                    }
                }
            }
        }
    }

class AndroidConfigParam(
    var enableHostTest: Boolean = true,
    var includeHostTestToCommonTest: Boolean = true,
    var enableDeviceTest: Boolean = false,
    var includeDeviceTestToCommonTest: Boolean = false,
)
