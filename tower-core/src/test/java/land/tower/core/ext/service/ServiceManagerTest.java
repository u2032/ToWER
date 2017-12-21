/*
 *  Copyright 2017 Cedric Longo.
 *  This file is part of ToWER program <https://github.com/u2032/ToWER>
 *
 *  ToWER is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  ToWER is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 *  of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with ToWER.
 *  If not, see <http://www.gnu.org/licenses/>
 */

package land.tower.core.ext.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import javax.inject.Inject;

/**
 * Created on 15/12/2017
 * @author Cédric Longo
 */
class ServiceManagerTest {

    @Inject
    private ServiceManager _serviceManager;

    @BeforeEach
    void setUp( ) {
        Guice.createInjector( new ServiceModule( ), mockModules( ) )
             .injectMembers( this );

        when( _mockService1.getPriority( ) ).thenReturn( ServicePriority.LOW );
        when( _mockService2.getPriority( ) ).thenReturn( ServicePriority.NORMAL );
        when( _mockService3.getPriority( ) ).thenReturn( ServicePriority.HIGH );
    }

    private Module mockModules( ) {
        return new AbstractModule( ) {
            @Override
            protected void configure( ) {
                Multibinder.newSetBinder( binder( ), IService.class )
                           .addBinding( ).toInstance( _mockService1 );

                Multibinder.newSetBinder( binder( ), IService.class )
                           .addBinding( ).toInstance( _mockService2 );

                Multibinder.newSetBinder( binder( ), IService.class )
                           .addBinding( ).toInstance( _mockService3 );
            }
        };
    }

    @Test
    @DisplayName( "Starting all services calls start entry point of each service according to priority" )
    void startServiceTest( ) throws Exception {
        // Setup
        // Exercice
        _serviceManager.startAll( );
        // Verify
        final InOrder inOrder = Mockito.inOrder( _mockService1, _mockService2, _mockService3 );
        inOrder.verify( _mockService3 ).start( );
        inOrder.verify( _mockService2 ).start( );
        inOrder.verify( _mockService1 ).start( );
    }

    @Test
    @DisplayName( "Stopping all services calls stop entry point of each service according to priority" )
    void stopServiceTest( ) throws Exception {
        // Setup
        // Exercice
        _serviceManager.stopAll( );
        // Verify
        final InOrder inOrder = Mockito.inOrder( _mockService1, _mockService2, _mockService3 );
        inOrder.verify( _mockService1 ).stop( );
        inOrder.verify( _mockService2 ).stop( );
        inOrder.verify( _mockService3 ).stop( );
    }

    private IService _mockService1 = mock( IService.class );
    private IService _mockService2 = mock( IService.class );
    private IService _mockService3 = mock( IService.class );
}