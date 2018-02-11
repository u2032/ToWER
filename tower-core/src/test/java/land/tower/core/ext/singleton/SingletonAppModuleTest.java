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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import land.tower.core.ext.config.ConfigurationModule;
import land.tower.core.ext.event.EventModule;
import land.tower.core.ext.service.IService;
import land.tower.core.ext.thread.ThreadingModule;

/**
 * Created on 11/02/2018
 * @author Cédric Longo
 */
class SingletonAppModuleTest {

    @Test
    @DisplayName( "Can inject SingleApp Service" )
    void singletonAppServiceInjection( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new SingletonAppModule( ), new EventModule( ),
                                                        new ConfigurationModule( ), new ThreadingModule( ) );
        // Exercice
        final SingletonAppService instance = injector.getInstance( SingletonAppService.class );
        // Verify
        assertThat( instance ).isNotNull( );
    }

    @Test
    @DisplayName( "SingleApp Service is registered" )
    void singletonAppServiceRegistration( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new SingletonAppModule( ), new EventModule( ),
                                                        new ConfigurationModule( ), new ThreadingModule( ) );
        // Exercice
        final Set<IService> instance = injector.getInstance( Key.get( new TypeLiteral<Set<IService>>( ) {
        } ) );
        // Verify
        assertThat( instance ).hasAtLeastOneElementOfType( SingletonAppService.class );
    }
}