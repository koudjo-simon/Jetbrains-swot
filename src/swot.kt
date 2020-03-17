package swot

import java.io.File
import java.nio.file.Paths


fun findSchoolNames(emailOrDomain: String): List<String> {
    return findSchoolNames(domainParts(emailOrDomain))
}

private object Resources {

    fun readList(resource: String) : Set<String>? {
        var txtFile = File(resource)
        if(txtFile.exists()) {
            return txtFile.bufferedReader()?.lineSequence()?.toHashSet()
        }
        return null
    }
}

private fun findSchoolNames(parts: List<String>): List<String> {
    val path = Paths.get("").toAbsolutePath().toString()
    val resourcePath = StringBuilder("$path/lib/domains")
    for (part in parts) {
        resourcePath.append('/').append(part)
    }
    val school = Resources.readList("${resourcePath}.txt")
    if (school != null) {
        return school.toList()
    }

    return arrayListOf()
}

private fun domainParts(emailOrDomain: String): List<String> {
    return emailOrDomain.trim().toLowerCase().substringAfter('@').substringAfter("://").substringBefore(':').split('.').reversed()
}

internal fun checkSet(set: Set<String>, parts: List<String>): Boolean {
    val subj = StringBuilder()
    for (part in parts) {
        subj.insert(0, part)
        if (set.contains(subj.toString())) return true
        subj.insert(0 ,'.')
    }
    return false
}