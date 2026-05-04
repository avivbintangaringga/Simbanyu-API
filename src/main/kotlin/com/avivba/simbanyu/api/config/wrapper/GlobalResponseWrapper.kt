package com.avivba.simbanyu.api.config.wrapper

import com.avivba.simbanyu.api.base.ApiResponse
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@RestControllerAdvice
class GlobalResponseWrapper : ResponseBodyAdvice<Any> {
    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out HttpMessageConverter<*>>
    ): Boolean {
        return !(returnType.hasMethodAnnotation(IgnoreResponseWrapper::class.java) ||
                returnType.declaringClass.isAnnotationPresent(IgnoreResponseWrapper::class.java))
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        val annotation = returnType.getMethodAnnotation(ResponseMessage::class.java)
        val message = annotation?.message ?: "Action completed successfully."

        return ApiResponse.success(
            message = message,
            data = body
        )
    }
}