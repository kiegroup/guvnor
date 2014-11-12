package org.guvnor.asset.management.backend.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.deltaspike.core.api.provider.BeanManagerProvider;
import org.kie.internal.executor.api.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CDIUtils {

	private static final Logger logger = LoggerFactory.getLogger(CDIUtils.class);
	private static final String[] BEAN_MANAGER_NAMES = {"java:comp/BeanManager", "java:comp/env/BeanManager", System.getProperty("org.kie.cdi.bm")};
	
	public static BeanManager lookUpBeanManager(CommandContext ctx) {
		BeanManager beanManager = null;

		// This is probably not needed anymore. The BeanManagerProvider should cover this.
		// But at this point too risky to remove
		for (String jndiName : BEAN_MANAGER_NAMES) {
			if (jndiName == null) {
				continue;
			}
			try {
				beanManager = InitialContext.doLookup(jndiName);
				logger.debug("Found bean manager under {} jndi name", jndiName);
				break;
			} catch (NamingException e) {
				logger.debug("No bean manager under {} jndi name", jndiName);
			}
		}

		if (beanManager == null) {
			beanManager = BeanManagerProvider.getInstance().getBeanManager();
		}

		if (beanManager == null) {
			beanManager = (BeanManager) ctx.getData("BeanManager");
		}
		return beanManager;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T createBean(Class<T> beanType, BeanManager beanManager, Annotation... bindings) throws Exception {

        Set<Bean<?>> beans = beanManager.getBeans( beanType, bindings );
   
        if (beans != null && !beans.isEmpty()) {
	        Bean<T> bean = (Bean<T>) beans.iterator().next();

	        return (T) beanManager.getReference(bean, beanType, beanManager.createCreationalContext(bean));
        }
        
        throw new IllegalArgumentException("Unable to to find bean of type " + beanType);
    }

    @SuppressWarnings("unchecked")
    public static <T> T createBean(Type beanType, BeanManager beanManager, Annotation... bindings) throws Exception {

        Set<Bean<?>> beans = beanManager.getBeans( beanType, bindings );

        if (beans != null && !beans.isEmpty()) {
            Bean<T> bean = (Bean<T>) beans.iterator().next();

            return (T) beanManager.getReference(bean, beanType, beanManager.createCreationalContext(bean));
        }

        throw new IllegalArgumentException("Unable to to find bean of type " + beanType);
    }
}