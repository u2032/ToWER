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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * Created on 15/12/2017
 * @author Cédric Longo
 */
class ServiceModuleTest {

    @Test
    @DisplayName( "ServiceManager can be injected as singleton" )
    void can_inject_ServiceManager( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new ServiceModule( ) );
        // Exercice
        final ServiceManager instance = injector.getInstance( ServiceManager.class );
        final ServiceManager instance2 = injector.getInstance( ServiceManager.class );
        // Verify
        assertThat( instance ).isNotNull( );
        assertThat( instance ).isSameAs( instance2 );
    }

    @Test
    @DisplayName( "Services are registered in set" )
    void can_inject_set_of_service( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new ServiceModule( ) );
        // Exercice
        final Set<IService> instance = injector.getInstance( Key.get( new TypeLiteral<Set<IService>>( ) {
        } ) );
        // Verify
        assertThat( instance ).isNotNull( );
    }

}