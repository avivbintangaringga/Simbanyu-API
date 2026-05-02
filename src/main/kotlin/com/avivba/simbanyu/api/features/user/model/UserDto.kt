package com.avivba.simbanyu.api.features.user.model

data class UserDto(
    val id: String,
    val username: String,
    val name: String,
    val role: String,
    val phoneNumber: String,
    val photoImageURL: String? = null,
    val createdAt: String,
    val updatedAt: String,
) {
    companion object {
        fun fromEntity(user: User) = UserDto(
            id = user.id.toString(),
            username = user.username,
            name = user.name,
            role = user.role.name,
            phoneNumber = user.phoneNumber,
            photoImageURL = user.photoImageURL,
            createdAt = user.createdAt.toString(),
            updatedAt = user.updatedAt.toString(),
        )
    }
}