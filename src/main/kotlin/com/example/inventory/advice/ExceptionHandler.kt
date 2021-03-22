package com.example.inventory.advice

import org.springframework.web.bind.annotation.ControllerAdvice
import org.zalando.problem.spring.web.advice.ProblemHandling

@ControllerAdvice
class ExceptionHandler : ProblemHandling {

    override fun isCausalChainsEnabled(): Boolean {
        return true
    }
}
