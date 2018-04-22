package com.eshare.debug;

import com.alipay.jarslink.api.Module;
import com.alipay.jarslink.api.ModuleConfig;
import com.alipay.jarslink.api.ModuleLoader;
import com.alipay.jarslink.api.ModuleManager;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 调试控制器
 * <pre>
 *     用于源码调试入口
 * </pre>
 * Created by liangyh on 2018/4/2.
 * Email:10856214@163.com
 */
@RestController
@RequestMapping("/dubug")
public class DebugController {

    @Autowired
    private ModuleManager moduleManager;

    @Autowired
    private ModuleLoader moduleLoader;

    /**
     * HeLloworld程序
     */
    @GetMapping("/helloworld")
    public void helloworld() {
        //查找模块
        Module findModule = moduleManager.find("demo");
        Assert.assertNotNull(findModule);
        //查找和执行Action
        String actionName = "helloworld";
        ModuleConfig moduleConfig = new ModuleConfig();
        moduleConfig.setName("helloworld");
        moduleConfig.setEnabled(true);
        moduleConfig.setVersion("1.0.0.20180403");
        ModuleConfig result = findModule.doAction(actionName, moduleConfig);
        System.out.println(String.format("Module Name :%s,Enabled :%s,Version :%s", result.getName(), result.getEnabled(), result.getVersion()));
    }

    /**
     * 根据模块名称和动作名称请求
     * <pre>
     *     真实场景可以用于对各模块方法调用
     * </pre>
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/module/{moduleName}/{actionName}/process.json", method = {RequestMethod.GET, RequestMethod.POST})
    public Object process(HttpServletRequest request, HttpServletResponse response) {

        Map<String, String> pathVariables = resolvePathVariables(request);

        String moduleName = pathVariables.get("moduleName");
        String actionName = pathVariables.get("actionName");
        //查找模块
        Module findModule = moduleManager.find(moduleName);
        String actionRequest = "启动工作流";
        return findModule.doAction(actionName, actionRequest);
    }

    /**
     * 解析地址参数
     *
     * @param request
     * @return
     */
    private Map<String, String> resolvePathVariables(HttpServletRequest request) {
        return (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    }
}
