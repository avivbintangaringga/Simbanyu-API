package com.avivba.simbanyu.api.database.repository

import com.avivba.simbanyu.api.database.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, String> {
}