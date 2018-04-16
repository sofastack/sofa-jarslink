package com.alipay.jarslink.springmvc.demo;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpringMvcController {

	@RequestMapping("springmvc.json")
	public void springmvc(HttpServletResponse response) throws IOException {
		response.getWriter().print("{\"name\"=\"springmvc\"}");
	}
}
