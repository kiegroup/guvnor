/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.util;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.seam.security.Identity;
import org.jboss.seam.solder.beanManager.BeanManagerLocator;

/**
 * Don't use this class. You're hurting CDI (Weld and Seam) if you do.
 * TODO seam3upgrade
 * <p/>
 * Based on
 * https://github.com/seam/faces/blob/develop/impl/src/main/java/org/jboss/seam/faces/util/BeanManagerUtils.java
 */
@Deprecated
public class BeanManagerUtils {


    @Deprecated
    public static <T> T getContextualInstance(final Class<T> type) {
        return getContextualInstance(new BeanManagerLocator().getBeanManager(), type);
    }

    /**
    * Get a single CDI managed instance of a specific class. Return only the first result if multiple beans are available.
    * <p/>
    * <b>NOTE:</b> Using this method should be avoided at all costs.
    *
    * @param manager The bean manager with which to perform the lookup.
    * @param type The class for which to return an instance.
    * @return The managed instance, or null if none could be provided.
    */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static <T> T getContextualInstance(final BeanManager manager, final Class<T> type) {
        T result = null;
        Bean<T> bean = (Bean<T>) manager.resolve(manager.getBeans(type));
        if (bean != null) {
            CreationalContext<T> context = manager.createCreationalContext(bean);
            if (context != null) {
                result = (T) manager.getReference(bean, type, context);
            }
        }
        return result;
    }

    /**
     * HACK for JSP page that doesn't seem to support java 1.5 syntax
     * @return getContextualInstance(Identity.class)
     */
    public static Identity getIdentityInstance() {
        return getContextualInstance(Identity.class);
    }

    @Deprecated
    public static Object getInstance(String beanName) {
        return new BeanManagerLocator().getBeanManager().getBeans(beanName);
    }

}
