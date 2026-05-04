package com.avivba.simbanyu.api.features.notification.model

enum class BroadcastTopic(val topic: String) {
    ALL("broadcast-all"),
    ADMIN("broadcast-admin"),
    OPERATOR("broadcast-user"),
    SUBSCRIBER("broadcast-subscriber"),
}