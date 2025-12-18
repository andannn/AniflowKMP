import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jlleitschuh.gradle.ktlint.KtlintExtension

/**
 * Configures Ktlint and Spotless for Android modules using shared helpers.
 */
class AndroidLintConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) =
        with(project) {
            pluginManager.withPlugin("org.jlleitschuh.gradle.ktlint") {
                extensions.configure<KtlintExtension> {
                    configureKtLint()
                }
            }

            pluginManager.withPlugin("com.diffplug.spotless") {
                extensions.configure<SpotlessExtension> {
                    configureSpotless(project)
                }
            }
        }
}
