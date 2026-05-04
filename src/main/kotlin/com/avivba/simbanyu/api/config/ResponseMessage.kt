package com.avivba.simbanyu.api.config

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ResponseMessage(val message: String)
