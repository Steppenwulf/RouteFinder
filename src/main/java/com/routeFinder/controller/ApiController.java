package com.routeFinder.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class ApiController {
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView getApiInfo() {
		return new ModelAndView("redirect:/swagger-ui.html");
	}
}
