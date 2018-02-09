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

import static org.asynchttpclient.Dsl.asyncHttpClient;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;

import javax.inject.Singleton;
import land.tower.core.ext.service.IService;

/**
 * Created on 09/02/2018
 * @author Cédric Longo
 */
public final class HttpModule extends AbstractModule {

    @Override
    protected void configure( ) {
        Multibinder.newSetBinder( binder( ), IService.class )
                   .addBinding( ).to( HttpService.class );
    }

    @Provides
    @Singleton
    AsyncHttpClientConfig config( ) {
        return new DefaultAsyncHttpClientConfig.Builder( )
                   .setFollowRedirect( true )
                   .setCompressionEnforced( true )
                   .setConnectTimeout( 30000 )
                   .setRequestTimeout( 120000 )
                   .setKeepAlive( true )
                   .build( );
    }

    @Provides
    @Singleton
    AsyncHttpClient client( AsyncHttpClientConfig config ) {
        return asyncHttpClient( config );
    }
}
