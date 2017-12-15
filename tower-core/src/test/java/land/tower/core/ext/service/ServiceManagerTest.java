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
import static org.mockito.Mockito.verify;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
    }

    private Module mockModules( ) {
        return new AbstractModule( ) {
            @Override
            protected void configure( ) {
                Multibinder.newSetBinder( binder( ), IService.class )
                           .addBinding( ).toInstance( _mockService );
            }
        };
    }

    @Test
    @DisplayName( "Starting all services calls start entry point of each service" )
    void startServiceTest( ) throws Exception {
        // Setup
        // Exercice
        _serviceManager.startAll( );
        // Verify
        verify( _mockService ).start( );
    }

    @Test
    @DisplayName( "Stopping all services calls stop entry point of each service" )
    void stopServiceTest( ) throws Exception {
        // Setup
        // Exercice
        _serviceManager.stopAll( );
        // Verify
        verify( _mockService ).stop( );
    }

    private IService _mockService = mock( IService.class );
}