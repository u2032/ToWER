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

package land.tower.core.ext.singleton;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

/**
 * Created on 11/02/2018
 * @author Cédric Longo
 */
class SingletonAppServiceTest {

    @Inject
    private SingletonAppClient _client;

    @Inject
    private SingletonAppServer _server;

    @Inject
    private SingletonAppService _service;

    @BeforeEach
    void setUp( ) {
        final Injector injector = Guice.createInjector( mockModules( ) );
        injector.injectMembers( this );
    }

    private Module mockModules( ) {
        return new AbstractModule( ) {
            @Override
            protected void configure( ) {
                bind( SingletonAppClient.class ).toInstance( mock( SingletonAppClient.class ) );
                bind( SingletonAppServer.class ).toInstance( mock( SingletonAppServer.class ) );
            }
        };
    }

    @Test
    @DisplayName( "When service is started, client sends message" )
    void startTest( ) throws Exception {
        // Setup
        // Exercice
        _service.start( );
        // Verify
        verify( _client ).sendMessage( );
    }

    @Test
    @DisplayName( "When service is stopped, server is stopped" )
    void stopTest( ) throws Exception {
        // Setup
        // Exercice
        _service.stop( );
        // Verify
        verify( _server ).stop( );
    }
}