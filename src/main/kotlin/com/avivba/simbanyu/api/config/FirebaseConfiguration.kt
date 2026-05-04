package com.avivba.simbanyu.api.config

import com.avivba.simbanyu.api.config.properties.FirebaseProperties
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
@EnableConfigurationProperties(FirebaseProperties::class)
class FirebaseConfiguration(
    private val firebaseProperties: FirebaseProperties
) {
    @PostConstruct
    fun initialize() {
        try {
            val decoder = Base64.getDecoder()
            val decodedServiceAccount = decoder.decode(firebaseProperties.serviceAccount)
            val googleCredentials = GoogleCredentials.fromStream(decodedServiceAccount.inputStream())

            val firebaseOptions = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .build()

            FirebaseApp.initializeApp(firebaseOptions)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Bean
    fun getFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()
}