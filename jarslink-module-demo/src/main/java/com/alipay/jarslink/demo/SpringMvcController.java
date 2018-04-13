package com.alipay.jarslink.demo;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 使用说明：需要在spring mvc的配置文件中配置  SpringModuleRequestMappingHandlerMapping
 * @author liangruisen
 */
@Controller
public class SpringMvcController {

	@RequestMapping("springmvc.htm")
	public void index(HttpServletResponse response) throws IOException {
		response.getWriter().print("<html><head><meta charset=\"UTF-8\"></head><body><h2>jarslink spring mvc 测试。</h2></body></html>");
	}
}
