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

package land.tower.core.model.tournament;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import land.tower.core.ext.config.ConfigurationModule;
import land.tower.core.ext.thread.ThreadingModule;

/**
 * Created on 17/12/2017
 * @author Cédric Longo
 */
class TournamentModuleTest {

    @Test
    @DisplayName( "TournamentRepository can be injected as singleton" )
    void can_inject_TournamentRepository( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new TournamentModule( ), new ThreadingModule( ),
                                                        new ConfigurationModule( ) );
        // Exercice
        final TournamentRepository instance = injector.getInstance( TournamentRepository.class );
        final TournamentRepository instance2 = injector.getInstance( TournamentRepository.class );
        // Verify
        assertThat( instance ).isNotNull( );
        assertThat( instance ).isSameAs( instance2 );
    }

    @Test
    @DisplayName( "TournamentStorage can be injected as singleton" )
    void can_inject_TournamentStorage( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new TournamentModule( ), new ThreadingModule( ),
                                                        new ConfigurationModule( ) );
        // Exercice
        final ITournamentStorage instance = injector.getInstance( ITournamentStorage.class );
        final ITournamentStorage instance2 = injector.getInstance( ITournamentStorage.class );
        // Verify
        assertThat( instance ).isNotNull( );
        assertThat( instance ).isSameAs( instance2 );
        assertThat( instance ).isInstanceOf( TournamentStorage.class );
    }
}