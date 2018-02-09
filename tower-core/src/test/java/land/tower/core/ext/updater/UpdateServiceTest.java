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

package land.tower.core.ext.updater;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import land.tower.core.ext.config.Configuration;
import land.tower.core.ext.config.ConfigurationModule;
import land.tower.core.ext.thread.ThreadingModule;

/**
 * Created on 09/02/2018
 * @author Cédric Longo
 */
class UpdateServiceTest {

    @Inject
    private AsyncHttpClient _client;

    @Inject
    private UpdateService _service;

    @Inject
    private Configuration _config;

    @Inject
    private EventBus _eventBus;

    @BeforeEach
    void setUp( ) {
        Guice.createInjector( mockModules( ), new ThreadingModule( ),
                              new ConfigurationModule( ) )
             .injectMembers( this );
    }

    private Module mockModules( ) {
        return new AbstractModule( ) {
            @Override
            protected void configure( ) {
                bind( AsyncHttpClient.class ).toInstance( mock( AsyncHttpClient.class ) );
                bind( EventBus.class ).toInstance( mock( EventBus.class ) );
            }
        };
    }

    @Test
    @DisplayName( "When checking update, a request is done over update url" )
    void checkUpdateTest( ) throws Exception {
        // Setup
        final BoundRequestBuilder mockRequest = mock( BoundRequestBuilder.class );
        when( _client.prepareGet( anyString( ) ) ).thenReturn( mockRequest );
        final ListenableFuture mockFuture = mock( ListenableFuture.class );
        when( mockRequest.execute( ) ).thenReturn( mockFuture );
        final Response mockResponse = mock( Response.class );
        when( mockFuture.toCompletableFuture( ) ).thenReturn( CompletableFuture.completedFuture( mockResponse ) );
        // Exercice
        _service.checkUpdate( );
        // Verify
        verify( _client ).prepareGet( _config.get( "update.url" ) );
        verify( mockRequest ).execute( );
    }

    @Test
    @DisplayName( "When update is available an event is thrown" )
    void eventTest( ) throws Exception {
        // Setup
        final BoundRequestBuilder mockRequest = mock( BoundRequestBuilder.class );
        when( _client.prepareGet( anyString( ) ) ).thenReturn( mockRequest );
        final ListenableFuture mockFuture = mock( ListenableFuture.class );
        when( mockRequest.execute( ) ).thenReturn( mockFuture );
        final Response mockResponse = mock( Response.class );
        when( mockFuture.toCompletableFuture( ) ).thenReturn( CompletableFuture.completedFuture( mockResponse ) );
        when( mockResponse.getResponseBody( any( ) ) )
            .thenReturn( "{ \"version\" : \"40000.0\", \"url\" : \"updateUrl\"}" );
        // Exercice
        _service.checkUpdate( );
        // Verify
        final ArgumentCaptor<UpdateEvent> captor = ArgumentCaptor.forClass( UpdateEvent.class );
        verify( _eventBus ).post( captor.capture( ) );
        assertThat( captor.getValue( ).getVersion( ).getVersion( ) ).isEqualTo( "40000.0" );
        assertThat( captor.getValue( ).getVersion( ).getUrl( ) ).isEqualTo( "updateUrl" );
    }
}