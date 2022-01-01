plugins {
    `maven-publish`
    signing
}

configure<SigningExtension> {
    val signingKey: String? = System.getenv("GPG_PRIVATE_KEY")
    val signingKeyPassphrase: String? = System.getenv("GPG_PRIVATE_PASSWORD")
    isRequired = signingKey != null && signingKey != ""
    useInMemoryPgpKeys(signingKey, signingKeyPassphrase)
}
