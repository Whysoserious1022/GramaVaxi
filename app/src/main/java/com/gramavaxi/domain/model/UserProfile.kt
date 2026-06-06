package com.gramavaxi.domain.model

/**
 * Represents a Grama-Vaxi user profile stored in Firestore under users/{uid}.
 */
data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val village: String = "",
    val district: String = "",
    val language: String = "en",           // "en" or "kn"
    val photoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
) {
    /** First name for greeting display */
    val firstName: String get() = name.trim().split(" ").firstOrNull() ?: "Farmer"

    companion object {
        fun fromMap(uid: String, map: Map<String, Any?>): UserProfile = UserProfile(
            uid         = uid,
            name        = map["name"] as? String ?: "",
            email       = map["email"] as? String ?: "",
            phone       = map["phone"] as? String ?: "",
            village     = map["village"] as? String ?: "",
            district    = map["district"] as? String ?: "",
            language    = map["language"] as? String ?: "en",
            photoUrl    = map["photoUrl"] as? String ?: "",
            createdAt   = (map["createdAt"] as? Long) ?: System.currentTimeMillis(),
            lastLoginAt = (map["lastLoginAt"] as? Long) ?: System.currentTimeMillis()
        )
    }

    fun toMap(): Map<String, Any> = mapOf(
        "name"        to name,
        "email"       to email,
        "phone"       to phone,
        "village"     to village,
        "district"    to district,
        "language"    to language,
        "photoUrl"    to photoUrl,
        "createdAt"   to createdAt,
        "lastLoginAt" to lastLoginAt
    )
}
