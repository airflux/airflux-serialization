import com.github.jk1.license.filter.LicenseBundleNormalizer
import com.github.jk1.license.render.CsvReportRenderer
import com.github.jk1.license.render.InventoryHtmlReportRenderer
import com.github.jk1.license.render.TextReportRenderer

plugins {
    id("com.github.jk1.dependency-license-report")
}

val licenseConfigHomeFolder = "$projectDir/config/licenses"

licenseReport {
    allowedLicensesFile = File("$licenseConfigHomeFolder/allowed-licenses.json")
    outputDir = "$projectDir/build/licenses"
    configurations = arrayOf("runtimeClasspath")
    renderers = arrayOf(
        CsvReportRenderer("third-party-licenses.csv"),
        TextReportRenderer("third-party-licenses.txt"),
        InventoryHtmlReportRenderer("third-party-licenses.html")
    )
    filters = arrayOf(
        LicenseBundleNormalizer("$licenseConfigHomeFolder/license-normalizer-bundle.json", false)
    )
    excludeBoms = true
}
