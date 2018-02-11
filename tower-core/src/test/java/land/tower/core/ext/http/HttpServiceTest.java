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

package land.tower.core.ext.http;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;

import org.asynchttpclient.AsyncHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

/**
 * Created on 11/02/2018
 * @author Cédric Longo
 */
class HttpServiceTest {

    @Inject
    private AsyncHttpClient _client;

    @Inject
    private HttpService _service;

    @BeforeEach
    void setUp( ) {
        Guice.createInjector( mockModule( ) )
             .injectMembers( this );
    }

    private Module mockModule( ) {
        return new AbstractModule( ) {
            @Override
            protected void configure( ) {
                bind( AsyncHttpClient.class ).toInstance( mock( AsyncHttpClient.class ) );
            }
        };
    }

    @Test
    @DisplayName( "When the service is stopped, the http client is closed" )
    void stopTest( ) throws Exception {
        // Setup
        // Exercice
        _service.stop( );
        // Verify
        verify( _client ).close( );
    }
}