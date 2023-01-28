package com.harbor.calendly.exception

import com.google.gson.Gson
import mu.KLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@Component
@ControllerAdvice
class ErrorHandler : ResponseEntityExceptionHandler() {
    companion object : KLogging()

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.error("MethodArgumentNotValidException observed : ${ex.message}", ex)
        val errors = ex.bindingResult.allErrors
            .map { error -> error.defaultMessage!! }
            .sorted()
        return newResponseEntity(HttpStatus.BAD_REQUEST, errors.joinToString(", ") { it })
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(
        ex: NotFoundException,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.debug("Exception occurred: ${ex.message} on request: $request")
        return newResponseEntity(HttpStatus.NOT_FOUND, ex.message)
    }

    @ExceptionHandler(InactiveAccountException::class)
    fun handleInactiveAccountException(
        ex: InactiveAccountException,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.debug("Exception occurred: ${ex.message} on request: $request")
        return newResponseEntity(HttpStatus.FORBIDDEN, ex.message)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.debug("Exception occurred: ${ex.message} on request: $request")
        return newResponseEntity(HttpStatus.BAD_REQUEST, ex.message)
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeExceptions(
        ex: RuntimeException,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.debug("Exception occurred: ${ex.message} on request: $request", ex)
        return newResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.message)
    }

    private fun newResponseEntity(
        status: HttpStatus,
        msg: String?
    ): ResponseEntity<Any> = ResponseEntity
        .status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Gson().toJson(msg))
}
