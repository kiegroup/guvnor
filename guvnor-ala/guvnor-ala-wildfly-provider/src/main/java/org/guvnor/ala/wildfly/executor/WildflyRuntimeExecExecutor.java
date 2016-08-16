
package org.guvnor.ala.wildfly.executor;

import java.io.File;
import java.util.Optional;

import javax.inject.Inject;

import org.guvnor.ala.config.Config;
import org.guvnor.ala.config.RuntimeConfig;

import org.guvnor.ala.exceptions.ProvisioningException;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.runtime.RuntimeBuilder;
import org.guvnor.ala.runtime.RuntimeDestroyer;
import org.guvnor.ala.runtime.RuntimeId;
import org.guvnor.ala.wildfly.access.WildflyAccessInterface;
import org.guvnor.ala.wildfly.config.WildflyRuntimeConfiguration;
import org.guvnor.ala.wildfly.config.WildflyRuntimeExecConfig;
import org.guvnor.ala.wildfly.model.WildflyProvider;
import org.guvnor.ala.wildfly.model.WildflyRuntime;
import org.guvnor.ala.wildfly.model.WildflyRuntimeEndpoint;
import org.guvnor.ala.wildfly.model.WildflyRuntimeInfo;
import org.guvnor.ala.wildfly.model.WildflyRuntimeState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WildflyRuntimeExecExecutor<T extends WildflyRuntimeConfiguration> implements RuntimeBuilder<T, WildflyRuntime>,
        RuntimeDestroyer,
        FunctionConfigExecutor<T, WildflyRuntime> {

    private final RuntimeRegistry runtimeRegistry;
    private final WildflyAccessInterface wildfly;
    protected static final Logger LOG = LoggerFactory.getLogger( WildflyRuntimeExecExecutor.class );

    @Inject
    public WildflyRuntimeExecExecutor( final RuntimeRegistry runtimeRegistry,
            final WildflyAccessInterface docker ) {
        this.runtimeRegistry = runtimeRegistry;
        this.wildfly = docker;
    }

    @Override
    public Optional<WildflyRuntime> apply( final WildflyRuntimeConfiguration config ) {
        final Optional<WildflyRuntime> runtime = create( config );
        if ( runtime.isPresent() ) {
            runtimeRegistry.registerRuntime( runtime.get() );
        }
        return runtime;
    }

    private Optional<WildflyRuntime> create( final WildflyRuntimeConfiguration runtimeConfig ) throws ProvisioningException {

        String warPath = runtimeConfig.getWarPath();
        final Optional<WildflyProvider> _wildflyProvider = runtimeRegistry.getProvider( runtimeConfig.getProviderId(), WildflyProvider.class );

        WildflyProvider wildflyProvider = _wildflyProvider.get();
        File file = new File( warPath );
        int result = wildfly.getWildflyClient( wildflyProvider ).deploy( file );

        if ( result != 200 ) {
            throw new ProvisioningException( "Deployment to Wildfly Failed with error code: " + result );
        }

        final String id = file.getName();

        return Optional.of( new WildflyRuntime( id, runtimeConfig, wildflyProvider, 
                new WildflyRuntimeEndpoint(), new WildflyRuntimeInfo(), new WildflyRuntimeState() ) );
    }

    @Override
    public Class<? extends Config> executeFor() {
        return WildflyRuntimeExecConfig.class;
    }

    @Override
    public String outputId() {
        return "wildfly-runtime";
    }

    @Override
    public boolean supports( final RuntimeConfig config ) {
        return config instanceof WildflyRuntimeConfiguration;
    }

    @Override
    public boolean supports( final RuntimeId runtimeId ) {
        return runtimeId instanceof WildflyRuntime
                || runtimeRegistry.getRuntimeById( runtimeId.getId() ) instanceof WildflyRuntime;
    }

    @Override
    public void destroy( final RuntimeId runtimeId ) {
        final Optional<WildflyProvider> _wildflyProvider = runtimeRegistry.getProvider( runtimeId.getProviderId(), WildflyProvider.class );
        WildflyProvider wildflyProvider = _wildflyProvider.get();
        int result = wildfly.getWildflyClient( wildflyProvider ).undeploy( runtimeId.getId() );
        if ( result != 200 ) {
            throw new ProvisioningException( "UnDeployment to Wildfly Failed with error code: " + result );
        }
        runtimeRegistry.unregisterRuntime( runtimeId );

    }
}
