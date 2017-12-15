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

package land.tower.core.ext.thread;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import land.tower.core.ext.service.IService;

/**
 * Created on 15/12/2017
 * @author Cédric Longo
 */
class ThreadingModuleTest {

    @Test
    @DisplayName( "ExecutorService can be injected as singleton" )
    void can_inject_ExecutorService_as_singleton( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new ThreadingModule( ) );
        // Exercice
        final ExecutorService instance =
            injector.getInstance( Key.get( ExecutorService.class, ApplicationThread.class ) );
        final ExecutorService instance2 =
            injector.getInstance( Key.get( ExecutorService.class, ApplicationThread.class ) );
        // Verify
        assertThat( instance ).isNotNull( );
        assertThat( instance ).isSameAs( instance2 );
    }

    @Test
    @DisplayName( "ScheduledExecutorService can be injected as singleton" )
    void can_inject_ScheduledExecutorService_as_singleton( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new ThreadingModule( ) );
        // Exercice
        final ExecutorService instance =
            injector.getInstance( Key.get( ScheduledExecutorService.class, ApplicationThread.class ) );
        final ExecutorService instance2 =
            injector.getInstance( Key.get( ScheduledExecutorService.class, ApplicationThread.class ) );
        // Verify
        assertThat( instance ).isNotNull( );
        assertThat( instance ).isSameAs( instance2 );
    }

    @Test
    @DisplayName( "ThreadingService is registered as Service" )
    void ThreadingService_is_registered( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new ThreadingModule( ) );
        // Exercice
        final Set<IService> instance = injector.getInstance( Key.get( new TypeLiteral<Set<IService>>( ) {
        } ) );
        // Verify
        assertThat( instance ).hasAtLeastOneElementOfType( ThreadingService.class );
    }
}