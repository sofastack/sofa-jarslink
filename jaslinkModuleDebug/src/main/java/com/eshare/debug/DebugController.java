package com.eshare.debug;

import com.alipay.jarslink.api.Module;
import com.alipay.jarslink.api.ModuleConfig;
import com.alipay.jarslink.api.ModuleLoader;
import com.alipay.jarslink.api.ModuleManager;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liangyh on 2018/4/2.
 * Email:10856214@163.com
 */
@RestController
@RequestMapping("/dubug/test")
public class DebugController {

    @Autowired
    private ModuleManager moduleManager;

    @Autowired
    private ModuleLoader moduleLoader;

    //创建订单
    @GetMapping("/helloworld")
    public void helloworld() {
        //查找模块
        Module findModule = moduleManager.find("demo");
        Assert.assertNotNull(findModule);
        //查找和执行Action
        String actionName = "helloworld";
        ModuleConfig moduleConfig = new ModuleConfig();
        moduleConfig.setName("h");
        moduleConfig.setEnabled(true);
        ModuleConfig result = findModule.doAction(actionName, moduleConfig);
        System.out.println("result====>"+result);
        System.out.println("actionName is "+result.getName());
    }
}
