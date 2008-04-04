package org.jboss.seam.remoting.gwt;
/*
 * Copyright 2005 JBoss Inc
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



import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.drools.brms.client.rpc.DetailedSerializableException;
import org.drools.brms.client.rpc.SecurityService;
import org.drools.brms.client.rpc.SessionExpiredException;
import org.drools.brms.server.ServiceImplementation;
import org.drools.brms.server.security.SecurityServiceImpl;
import org.drools.brms.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.RulesRepository;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.contexts.Contexts;
/**
 * This class adapts GWT RPC mechanism to Seam actions.
 *
 * @author Michael Neale
 */
public class GWTToSeamAdapter {

    /** A very simple cache of previously looked up methods */
    static final Map METHOD_CACHE = new HashMap();
    private static final Logger log = Logger.getLogger( GWTToSeamAdapter.class );


    /**
     * Call the service.
     * @param serviceIntfName The interface name - this will be the fully qualified name of the remote service interface as
     * understood by GWT. This correlates to a component name in seam.
     * @param methodName The method name of the service being invoked.
     * @param paramTypes The types of parameters - needed for method lookup for polymorphism.
     * @param args The values to be passed to the service method.
     * @return A populated ReturnedObject - the returned object payload may be null, but the type will not be.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public ReturnedObject callWebRemoteMethod(String serviceIntfName,
                         String methodName,
                         Class[] paramTypes,
                         Object[] args) throws InvocationTargetException, IllegalAccessException, SecurityException {

            Object component = getServiceComponent( serviceIntfName );
            Class clz = component.getClass();

            Method method = getMethod( serviceIntfName, methodName,
                            clz,
                            paramTypes );

            try {
                Object result = method.invoke( component, args );
                return new ReturnedObject(method.getReturnType(), result);
            } catch (InvocationTargetException e) {
                //now in this case, we log, and then repack it as some sort of a serializable exception

                log.error("Error invoking a service", e.getCause());
                String exName = e.getCause().getClass().getName();
                if (exName.endsWith( "NotLoggedInException" )) {
                    throw new InvocationTargetException(new SessionExpiredException());
                } else {
                    Throwable cause = e.getCause();
                    StringWriter sw = new StringWriter();
                    PrintWriter w = new PrintWriter(sw);
                    cause.printStackTrace( w );
                    if (cause instanceof DetailedSerializableException) {
                    	throw new InvocationTargetException(cause);
                    } else {
                        throw new InvocationTargetException(new DetailedSerializableException("An error occurred executing the action.", sw.toString()));
                    }
                }
            }

    }

    /**
     * Get the method on the class, including walking up the class hierarchy if needed.
     * Methods have to be marked as "@WebRemote" to be allowed
     * @param methodName
     * @param clz
     * @param paramTypes
     * @return
     */
    private Method getMethod(String serviceName,
                             String methodName,
                                Class clz,
                                Class[] paramTypes)  {
        String key = getKey( serviceName,  methodName, paramTypes );
        if (METHOD_CACHE.containsKey( key )) {
            return (Method) METHOD_CACHE.get( key );
        } else {
            try {
                synchronized ( METHOD_CACHE ) {
                    Method m = findMethod( clz, methodName, paramTypes );
                    if (m == null) throw new NoSuchMethodException();
                    METHOD_CACHE.put( key, m );
                    return m;
                }

            } catch ( NoSuchMethodException e ) {
                  throw new SecurityException("Unable to access a service method called [" + methodName + "] on class [" + clz.getName() + "] without the @WebRemote attribute. " +
                  "This may be a hack attempt, or someone simply neglected to use the @WebRemote attribute to indicate a method as" +
                  " remotely accessible.");
            }
        }
    }

    private String getKey(String serviceName,
                          String methodName,
                          Class[] paramTypes) {
        if (paramTypes == null) {
            return serviceName + "." + methodName;
        } else {
            String pTypes = "";
            for ( int i = 0; i < paramTypes.length; i++ ) {
                pTypes += paramTypes[i].getName();
            }
            return serviceName + "." + methodName + "(" + pTypes + ")";
        }

    }

    /**
     * Recurse up the class hierarchy, looking for a compatible method that is marked as "@WebRemote".
     * If one is not found (or we hit Object.class) then we barf - basically trust nothing from the client
     * other then what we want to allow them to call.
     */
    private Method findMethod(Class clz, String methodName, Class[] paramTypes ) throws NoSuchMethodException {
        if (clz == Object.class) {
            return null;
        } else {
            Method m = clz.getMethod( methodName, paramTypes );
            if (isWebRemoteAnnotated( m )) {
                return m;
            } else {
                return findMethod(clz.getSuperclass(), methodName, paramTypes);
            }
        }
    }

    /**
     * Only allow methods annotated with @WebRemote for security reasons.
     */
    private boolean isWebRemoteAnnotated(Method method) {
        if (method == null) return false;
        return method.getAnnotation( WebRemote.class ) != null;
    }

    /**
     * Return the service component that has been bound to the given name.
     */
    protected Object getServiceComponent(String serviceIntfName) {
        if (Contexts.isApplicationContextActive()) {
            log.debug( "Running in seam mode multi user and authentication enabled" );
            return Component.getInstance( serviceIntfName );
        } else {

            //MN: NOTE THIS IS MY HACKERY TO GET IT WORKING IN GWT HOSTED MODE.
            //THIS IS ALL THAT IS NEEDED.
            log.debug( "WARNING: RUNNING IN NON SEAM MODE SINGLE USER MODE - ONLY FOR TESTING AND DEBUGGING !!!!!" );
            if (serviceIntfName.equals( SecurityService.class.getName() )) {
                return new SecurityServiceImpl();
            }
            ServiceImplementation impl = new ServiceImplementation();

            try {
                impl.repository = new RulesRepository(TestEnvironmentSessionHelper.getSession(false));
                return impl;
            } catch ( Exception e ) {
                throw new IllegalStateException("Unable to launch debug mode...");
            }


        }
    }

    /**
     * This is used for returning results to the GWT service endpoint.
     * The class is needed even if the result is null.
     * a void.class responseType is perfectly acceptable.
     * @author Michael Neale
     */
    static class ReturnedObject {
        public ReturnedObject(Class type,
                              Object result) {
            this.returnType = type;
            this.returnedObject = result;
        }
        public Class returnType;
        public Object returnedObject;
    }



}