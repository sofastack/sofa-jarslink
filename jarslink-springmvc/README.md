# 使用方法
- 在spring mvc的配置文件中定义spring mvc 的jarslink配置
``` xml
	<!-- 模块加载引擎 -->
    <bean name="moduleLoader" class="com.alipay.jarslink.api.impl.ModuleLoaderImpl"></bean>

    <!-- 模块管理器 -->
    <bean name="moduleManager" class="com.alipay.jarslink.api.impl.ModuleManagerImpl">
  		<property name="moduleListenerDispatcher" ref="listenerDispatcher"/>
    </bean>
    
    <!-- 模块服务 -->
    <bean name="moduleService" class="com.alipay.jarslink.api.impl.ModuleServiceImpl">
        <property name="moduleLoader" ref="moduleLoader"></property>
        <property name="moduleManager" ref="moduleManager"></property>
    </bean>
    
    <!-- 模块监听调度器 -->
    <bean id="listenerDispatcher" class="com.alipay.jarslink.api.ModuleListenerDispatcher"/>
    
    <!-- spring mvc RequestMapping 注解jarslink支持  -->
    <bean id="moduleHandlerMapping" class="com.alipay.jarslink.support.SpringModuleRequestMappingHandlerMapping">
    	<property name="moduleListenerDispatcher" ref="listenerDispatcher"/>
    </bean>
```
- 其中 moduleLoader、moduleManager、listenerDispatcher 可以定义在主spring配置文件里面而非spring mvc的配置文件中，
- 但是moduleHandlerMapping必需定义在spring mvc的配置文件中。
- 配置好之后即可按照jarslink的加载注册模块的方法进行加载注册有RequestMapping注解的模块进行使用。
- [jarslink的加载注册模块的方法见](https://github.com/alibaba/taitan/wiki/如何使用)
