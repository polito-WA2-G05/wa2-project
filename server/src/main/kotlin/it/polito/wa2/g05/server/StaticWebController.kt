package it.polito.wa2.g05.server

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView


@Controller
@RequestMapping("/")
class StaticWebController {
    @RequestMapping("/products/**")
    fun toProductsPages(request: HttpServletRequest?, response: HttpServletResponse?): ModelAndView? {
        return ModelAndView("forward:/")
    }

    @RequestMapping("/login")
    fun toLoginPage(request: HttpServletRequest?, response: HttpServletResponse?): ModelAndView? {
        return ModelAndView("forward:/")
    }

    @RequestMapping("/signup")
    fun toSignupPage(request: HttpServletRequest?, response: HttpServletResponse?): ModelAndView? {
        return ModelAndView("forward:/")
    }

    @RequestMapping("/tickets/**")
    fun toTicketsPages(request: HttpServletRequest?, response: HttpServletResponse?): ModelAndView? {
        return ModelAndView("forward:/")
    }

    @RequestMapping("/me/**")
    fun toPersonalPages(request: HttpServletRequest?, response: HttpServletResponse?): ModelAndView? {
        return ModelAndView("forward:/")
    }

    @RequestMapping("/manager/**")
    fun toManagerPages(request: HttpServletRequest?, response: HttpServletResponse?): ModelAndView? {
        return ModelAndView("forward:/")
    }
}