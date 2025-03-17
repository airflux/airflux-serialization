
plugins {
    id("kotlinx-knit")
    id("org.jetbrains.dokka")
}

knit {
    rootDir = project.rootDir // project root dir

    // Custom set of input files to process (default as shown below)
    files = fileTree(project.rootDir) {
        include(
            "**/*.md",
            "**/*.kt"
        )

        exclude(
            "**/build/**",
            "**/.gradle/**"
        )
    }

    defaultLineSeparator = "\n" // line separator used for newly generated files
}

dependencies {
    dokkaPlugin( "org.jetbrains.dokka:versioning-plugin:2.0.0")
}
