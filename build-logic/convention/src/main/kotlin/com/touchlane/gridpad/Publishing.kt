/*
 * MIT License
 *
 * Copyright (c) 2022 Touchlane LLC tech@touchlane.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.touchlane.gridpad

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.signing.SigningExtension

internal fun Project.configurePublishing(
    extension: PublishingExtension
) {
    val sources = sources()
    val javadoc = javadoc()
    extension.apply {
        val properties = PublishingProperties.load(this@configurePublishing)

        publications {
            create<MavenPublication>(properties.publicationName) {
                from(components["release"])
                groupId = properties.groupId
                artifactId = properties.artifactId
                version = properties.version

                artifact(sources.get())
                artifact(javadoc.get())

                pom {
                    name.set("Touchlane ${project.name}")
                    description.set(properties.pomDescription)
                    url.set(properties.pomUrl)
                    licenses {
                        license {
                            name.set("MIT")
                            url.set("https://choosealicense.com/licenses/mit/")
                        }
                    }
                    developers {
                        developer {
                            id.set("touchlane")
                            name.set("Touchlane LLC")
                            email.set("tech.touchlane@gmail.com")
                        }
                    }
                    scm {
                        connection.set(properties.scmConnection)
                        developerConnection.set(properties.scmDeveloperConnection)
                        url.set(properties.scmUrl)
                    }
                }
            }
        }

        val signingProperties = SigningPropertiesDelegate(rootProject.extra)

        configure<SigningExtension> {
            if (signingProperties.isExists) {
                if (signingProperties.signingKeyId.isNotBlank()) {
                    useInMemoryPgpKeys(
                        signingProperties.signingKeyId,
                        signingProperties.signingKey,
                        signingProperties.signingPassword
                    )
                } else {
                    useInMemoryPgpKeys(
                        signingProperties.signingKey,
                        signingProperties.signingPassword
                    )
                }
                sign(extension.publications[properties.publicationName])
                sign(configurations["archives"])
            }
        }
    }
}

internal fun Project.sources(): TaskProvider<Jar> {
    val sourcesJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")

        if (project.plugins.hasPlugin("com.android.library")) {
            val libExt = checkNotNull(project.extensions.findByType(LibraryExtension::class.java))
            val libMainSourceSet = libExt.sourceSets.getByName("main")

            from(libMainSourceSet.java.srcDirs)
        } else {
            val sourceSetExt =
                checkNotNull(project.extensions.findByType(SourceSetContainer::class.java))
            val mainSourceSet = sourceSetExt.getByName("main")

            from(mainSourceSet.java.srcDirs)
        }
    }
    return sourcesJar
}

internal fun Project.javadoc(): TaskProvider<Jar> {
    val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")

        val dokkaJavadocTask = tasks.getByName("dokkaJavadoc")

        from(dokkaJavadocTask)
        dependsOn(dokkaJavadocTask)
    }
    return javadocJar
}