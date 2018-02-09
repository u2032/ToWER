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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import org.asynchttpclient.AsyncHttpClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import land.tower.core.ext.service.IService;

/**
 * Created on 09/02/2018
 * @author Cédric Longo
 */
public class HttpModuleTest {

    @Test
    @DisplayName( "Can inject AsyncHttpClient" )
    void httpClientInjection( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new HttpModule( ) );
        // Exercice
        final AsyncHttpClient instance1 = injector.getInstance( AsyncHttpClient.class );
        final AsyncHttpClient instance2 = injector.getInstance( AsyncHttpClient.class );
        // Verify
        assertThat( instance1 ).isNotNull( );
        assertThat( instance1 ).isSameAs( instance2 );
    }

    @Test
    @DisplayName( "HttpService is registered" )
    void httpServiceRegistration( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new HttpModule( ) );
        // Exercice
        final Set<IService> instance = injector.getInstance( Key.get( new TypeLiteral<Set<IService>>( ) {
        } ) );
        // Verify
        assertThat( instance ).hasAtLeastOneElementOfType( HttpService.class );
    }
}