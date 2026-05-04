package com.avivba.simbanyu.api.config.wrapper

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ResponseMessage(val message: String)
