package com.alipay.jarslink.support;

import com.alipay.jarslink.api.ModuleManager;
import com.alipay.jarslink.support.annotation.ActionReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.springframework.core.BridgeMethodResolver.findBridgedMethod;
import static org.springframework.core.BridgeMethodResolver.isVisibilityBridgeMethodPair;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;

/**
 * 处理使用注解上下文的action注入问题
 *
 * @author joe
 * @version 2018.04.03 16:41
 */
public class ReferenceAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
        implements MergedBeanDefinitionPostProcessor, PriorityOrdered, DisposableBean {
    /**
     * The bean name of {@link ReferenceAnnotationBeanPostProcessor}
     */
    public static final String BEAN_NAME = "referenceAnnotationBeanPostProcessor";

    private final Log logger = LogFactory.getLog(getClass());

    private ModuleManager moduleManager;

    public ReferenceAnnotationBeanPostProcessor(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }


    private final ConcurrentMap<String, InjectionMetadata> injectionMetadataCache =
            new ConcurrentHashMap<String, InjectionMetadata>(256);

    private final ConcurrentMap<String, ActionFactoryBean> referenceBeansCache =
            new ConcurrentHashMap<String, ActionFactoryBean>();

    @Override
    public PropertyValues postProcessPropertyValues(
            PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeanCreationException {

        InjectionMetadata metadata = findReferenceMetadata(beanName, bean.getClass(), pvs);
        try {
            metadata.inject(bean, beanName, pvs);
        } catch (BeanCreationException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of @ActionReference dependencies failed", ex);
        }
        return pvs;
    }


    /**
     * Finds {@link InjectionMetadata.InjectedElement} Metadata from annotated
     * {@link ActionReference @ActionReference} fields
     *
     * @param beanClass The {@link Class} of Bean
     * @return non-null {@link List}
     */
    private List<InjectionMetadata.InjectedElement> findFieldReferenceMetadata(final Class<?> beanClass) {

        final List<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();

        ReflectionUtils.doWithFields(beanClass, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                ActionReference reference = getAnnotation(field, ActionReference.class);

                if (reference != null) {

                    if (Modifier.isStatic(field.getModifiers())) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("@ActionReference annotation is not supported on static fields: " + field);
                        }
                        return;
                    }

                    elements.add(new ReferenceFieldElement(field, reference));
                }
            }
        });

        return elements;

    }

    /**
     * Finds {@link InjectionMetadata.InjectedElement} Metadata from annotated
     * {@link ActionReference @ActionReference} methods
     *
     * @param beanClass The {@link Class} of Bean
     * @return non-null {@link List}
     */
    private List<InjectionMetadata.InjectedElement> findMethodReferenceMetadata(final Class<?> beanClass) {

        final List<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();

        ReflectionUtils.doWithMethods(beanClass, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {


                Method bridgedMethod = findBridgedMethod(method);

                if (!isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                    return;
                }

                ActionReference reference = findAnnotation(bridgedMethod, ActionReference.class);

                if (reference != null && method.equals(ClassUtils.getMostSpecificMethod(method, beanClass))) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("@ActionReference annotation is not supported on static methods: " + method);
                        }
                        return;
                    }
                    if (method.getParameterTypes().length == 0) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("@ActionReference  annotation should only be used on methods with parameters:" +
                                    " " +
                                    method);
                        }
                    }
                    PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, beanClass);
                    elements.add(new ReferenceMethodElement(method, pd, reference));
                }
            }
        });

        return elements;
    }


    /**
     * @param beanClass
     * @return
     */
    private InjectionMetadata buildReferenceMetadata(final Class<?> beanClass) {

        final List<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();

        elements.addAll(findFieldReferenceMetadata(beanClass));

        elements.addAll(findMethodReferenceMetadata(beanClass));

        return new InjectionMetadata(beanClass, elements);

    }

    private InjectionMetadata findReferenceMetadata(String beanName, Class<?> clazz, PropertyValues pvs) {
        // Fall back to class name as cache key, for backwards compatibility with custom callers.
        String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
        // Quick check on the concurrent map first, with minimal locking.
        InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    try {
                        metadata = buildReferenceMetadata(clazz);
                        this.injectionMetadataCache.put(cacheKey, metadata);
                    } catch (NoClassDefFoundError err) {
                        throw new IllegalStateException("Failed to introspect bean class [" + clazz.getName() +
                                "] for reference metadata: could not find class that it depends on", err);
                    }
                }
            }
        }
        return metadata;
    }


    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        if (beanType != null) {
            InjectionMetadata metadata = findReferenceMetadata(beanName, beanType, null);
            metadata.checkConfigMembers(beanDefinition);
        }
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public void destroy() throws Exception {

        for (ActionFactoryBean actionFactoryBean : referenceBeansCache.values()) {
            if (logger.isInfoEnabled()) {
                logger.info(actionFactoryBean + " was destroying!");
            }
            actionFactoryBean.destroy();
        }

        injectionMetadataCache.clear();
        referenceBeansCache.clear();

        if (logger.isInfoEnabled()) {
            logger.info(getClass() + " was destroying!");
        }
    }

    /**
     * {@link ActionReference} {@link Method} {@link InjectionMetadata.InjectedElement}
     */
    private class ReferenceMethodElement extends InjectionMetadata.InjectedElement {

        private final Method method;

        private final ActionReference reference;

        protected ReferenceMethodElement(Method method, PropertyDescriptor pd, ActionReference reference) {
            super(method, pd);
            this.method = method;
            this.reference = reference;
        }

        @Override
        protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
            Object referenceBean = buildReferenceBean(reference);
            ReflectionUtils.makeAccessible(method);
            method.invoke(bean, referenceBean);
        }
    }

    /**
     * {@link ActionReference} {@link Field} {@link InjectionMetadata.InjectedElement}
     */
    private class ReferenceFieldElement extends InjectionMetadata.InjectedElement {

        private final Field field;

        private final ActionReference reference;

        protected ReferenceFieldElement(Field field, ActionReference reference) {
            super(field, null);
            this.field = field;
            this.reference = reference;
        }

        @Override
        protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
            Object referenceBean = buildReferenceBean(reference);
            ReflectionUtils.makeAccessible(field);
            field.set(bean, referenceBean);
        }

    }

    private Object buildReferenceBean(ActionReference reference) throws Exception {
        String key = reference.moduleName() + ":" + reference.version() + "#" + reference.name();
        ActionFactoryBean bean = referenceBeansCache.get(key);
        ActionFactoryBean old = null;
        if (bean == null) {
            bean = new ActionFactoryBean(moduleManager, reference);
            old = referenceBeansCache.putIfAbsent(key, bean);
        }

        return old == null ? bean.getObject() : old.getObject();
    }
}
