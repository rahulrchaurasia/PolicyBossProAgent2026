package com.policyboss.policybosspro.feature.register.util

/**
 * Helper to safely split a Full Name into FirstName and LastName as expected by the backend API.
 * Preserves middle names in LastName.
 */
object NameSplitter {

    fun splitFullName(fullName: String): Pair<String, String> {
        val trimmed = fullName.trim()
        if (trimmed.isEmpty()) return Pair("", "")

        val parts = trimmed.split("\\s+".toRegex()).filter { it.isNotBlank() }
        return when {
            parts.isEmpty() -> Pair("", "")
            parts.size == 1 -> Pair(parts[0], ".") // Fallback dot for single name
            else -> {
                val firstName = parts.first()
                val lastName = parts.drop(1).joinToString(" ")
                Pair(firstName, lastName)
            }
        }
    }
}

