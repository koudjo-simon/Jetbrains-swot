package swot

import java.io.File
import java.nio.file.Paths

/**
 * @author Paul, modified version of https://github.com/JetBrains/swot to retrieve list of domains, blacklist, and institution name list
 */

object CompilationState {
    val blacklist = File("lib/domains/blacklist.txt").readLines().toHashSet()
    val domains = File("lib/domains/tlds.txt").readLines().toHashSet()
}

fun main(args: Array<String>) {

    val path = Paths.get("").toAbsolutePath().toString()
    println("Working Directory = $path")

    val root = File("lib/domains")
    root.walkTopDown().forEach {
        if (it.isFile) {
            val parts = it.toRelativeString(root).replace('\\', '/').removeSuffix(".txt").split('/').toList()
            if (!checkSet(CompilationState.blacklist, parts) && !checkSet(CompilationState.domains, parts)) {
                CompilationState.domains.add(parts.reversed().joinToString("."))
            }
        }
    }

    val blacklist = CompilationState.blacklist.map { "-$it" }.sorted().joinToString("\n")
    File("out/artifacts").mkdirs()
    File("out/artifacts/blacklist.txt").writeText(blacklist)
    val validDomains = CompilationState.domains.sorted().joinToString("\n")
    File("out/artifacts/valid-domains.txt").writeText(validDomains)

    /* write institution names as pipe separated file*/
    val institutions = CompilationState.domains.sorted().map { findSchoolNames( "admin@$it").toList().joinToString("|") + "\n" }
    val schoolFile = File("out/artifacts/institution-names.txt")
    schoolFile.delete()
    schoolFile.createNewFile()
    institutions.sorted().forEach { schoolFile.appendText("$it") }

    /* write full contents out as pipe separated file*/
    val domainsWithInstitutions = CompilationState.domains.sorted().map {  "$it | " + findSchoolNames( "admin@$it").toList().joinToString("|") + "\n" }
    val masterFile = File("out/artifacts/institutions.txt")
    masterFile.delete()
    masterFile.createNewFile()
    domainsWithInstitutions.forEach { masterFile.appendText("$it") }

}