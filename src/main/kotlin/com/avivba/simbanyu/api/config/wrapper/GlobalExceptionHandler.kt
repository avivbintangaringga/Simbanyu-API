package com.avivba.simbanyu.api.config.wrapper

import com.avivba.simbanyu.api.base.ApiResponse
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.validation.FieldError
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.servlet.resource.NoResourceFoundException

@Suppress("PROPERTY_HIDES_JAVA_FIELD")
@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.warn("Validation failed", ex)

        val errors = ex.bindingResult.allErrors.map { error ->
            when (error) {
                is FieldError -> "${error.field}: ${error.defaultMessage ?: "Invalid value"}"
                else -> error.defaultMessage ?: "Invalid request value"
            }
        }

        return buildErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Validation failed",
            errors = errors
        )
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.warn("Malformed request body", ex)

        return buildErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Malformed request body",
            errors = listOf(ex.mostSpecificCause.message ?: "Request body is missing or invalid")
        )
    }

    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.warn("Missing request parameter", ex)

        return buildErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Missing request parameter",
            errors = listOf("Parameter '${ex.parameterName}' is required")
        )
    }

    override fun handleHttpRequestMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.warn("HTTP method is not supported", ex)

        val supportedMethods = ex.supportedHttpMethods
            ?.joinToString(", ") { it.name() }
            ?: "No supported methods"

        return buildErrorResponse(
            status = HttpStatus.METHOD_NOT_ALLOWED,
            message = "HTTP method is not supported",
            errors = listOf("${ex.method} is not supported. Supported methods: $supportedMethods")
        )
    }

    override fun handleNoResourceFoundException(
        ex: NoResourceFoundException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.warn("Static resource not found", ex)

        return buildErrorResponse(
            status = HttpStatus.NOT_FOUND,
            message = "Resource not found",
            errors = listOf(ex.message ?: "The requested resource was not found")
        )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<ApiResponse.Error<Nothing>> {
        logger.warn("Constraint violation", ex)

        val errors = ex.constraintViolations.map { violation ->
            "${violation.propertyPath}: ${violation.message}"
        }

        return buildTypedErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Validation failed",
            errors = errors
        )
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatch(
        ex: MethodArgumentTypeMismatchException
    ): ResponseEntity<ApiResponse.Error<Nothing>> {
        logger.warn("Invalid parameter type", ex)

        val requiredType = ex.requiredType?.simpleName ?: "required type"

        return buildTypedErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Invalid parameter type",
            errors = listOf("Parameter '${ex.name}' must be a valid $requiredType")
        )
    }

    @ExceptionHandler(
        EntityNotFoundException::class,
        NoSuchElementException::class
    )
    fun handleNotFound(ex: Exception): ResponseEntity<ApiResponse.Error<Nothing>> {
        logger.warn("Resource not found", ex)

        return buildTypedErrorResponse(
            status = HttpStatus.NOT_FOUND,
            message = "Resource not found",
            errors = listOf(ex.message ?: "The requested resource was not found")
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ApiResponse.Error<Nothing>> {
        logger.warn("Invalid request", ex)

        return buildTypedErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Invalid request",
            errors = listOf(ex.message ?: "Request contains invalid values")
        )
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolation(
        ex: DataIntegrityViolationException
    ): ResponseEntity<ApiResponse.Error<Nothing>> {
        logger.error("Data integrity violation", ex)

        return buildTypedErrorResponse(
            status = HttpStatus.CONFLICT,
            message = "Data integrity violation",
            errors = listOf(ex.mostSpecificCause.message ?: "Request conflicts with existing data")
        )
    }

    @ExceptionHandler(ExpiredJwtException::class)
    fun handleExpiredJwt(ex: ExpiredJwtException): ResponseEntity<ApiResponse.Error<Nothing>> {
        logger.warn("JWT token expired", ex)

        return buildTypedErrorResponse(
            status = HttpStatus.UNAUTHORIZED,
            message = "Authentication failed",
            errors = listOf("JWT token has expired")
        )
    }

    @ExceptionHandler(JwtException::class)
    fun handleJwtException(ex: JwtException): ResponseEntity<ApiResponse.Error<Nothing>> {
        logger.warn("Invalid JWT token", ex)

        return buildTypedErrorResponse(
            status = HttpStatus.UNAUTHORIZED,
            message = "Authentication failed",
            errors = listOf(ex.message ?: "Invalid JWT token")
        )
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthentication(ex: AuthenticationException): ResponseEntity<ApiResponse.Error<Nothing>> {
        logger.warn("Authentication failed", ex)

        return buildTypedErrorResponse(
            status = HttpStatus.UNAUTHORIZED,
            message = "Authentication failed",
            errors = listOf(ex.message ?: "Invalid or missing authentication credentials")
        )
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<ApiResponse.Error<Nothing>> {
        logger.warn("Access denied", ex)

        return buildTypedErrorResponse(
            status = HttpStatus.FORBIDDEN,
            message = "Access denied",
            errors = listOf(ex.message ?: "You do not have permission to access this resource")
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ApiResponse.Error<Nothing>> {
        logger.error("Internal server error", ex)

        return buildTypedErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            message = "Internal server error",
            errors = listOf(ex.message ?: "An unexpected error occurred")
        )
    }

    private fun buildErrorResponse(
        status: HttpStatus,
        message: String,
        errors: List<String> = emptyList()
    ): ResponseEntity<Any> =
        ResponseEntity
            .status(status)
            .body(ApiResponse.error<Nothing>(message = message, errors = errors))

    private fun buildTypedErrorResponse(
        status: HttpStatus,
        message: String,
        errors: List<String> = emptyList()
    ): ResponseEntity<ApiResponse.Error<Nothing>> =
        ResponseEntity
            .status(status)
            .body(ApiResponse.error(message = message, errors = errors))
}