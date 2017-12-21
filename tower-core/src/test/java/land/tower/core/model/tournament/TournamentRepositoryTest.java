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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.inject.Guice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import javax.inject.Inject;
import land.tower.core.ext.thread.ThreadingModule;
import land.tower.data.Tournament;

/**
 * Created on 17/12/2017
 * @author Cédric Longo
 */
class TournamentRepositoryTest {

    @Inject
    private TournamentRepository _repository;

    @Inject
    private ITournamentStorage _storage;

    @BeforeEach
    void setUp( ) {
        Guice.createInjector( new TestTournamentModule( ), new ThreadingModule( ) ).injectMembers( this );
    }

    @Test
    @DisplayName( "When service is started, it loads players from storage" )
    void startTest( ) throws Exception {
        // Setup
        final Tournament tournament1 = _repository.create( ).getTournament( );
        final Tournament tournament2 = _repository.create( ).getTournament( );
        when( _storage.loadTournaments( ) )
            .thenReturn( Arrays.asList( tournament1, tournament2 ) );
        // Exercice
        _repository.start( );
        // Verify
        verify( _storage ).loadTournaments( );
        assertThat( _repository.getTournamentList( ) ).hasSize( 2 );
    }

    @Test
    @DisplayName( "When a tournament is removed, it doesn't exist anymore and saved in storage" )
    void removeTest( ) throws Exception {
        // Setup
        final Tournament tournament1 = _repository.create( ).getTournament( );
        final Tournament tournament2 = _repository.create( ).getTournament( );
        // Exercice
        _repository.remove( tournament1 );
        // Verify
        assertThat( _repository.getTournamentList( ) ).hasSize( 1 );
        assertThat( _repository.getTournamentList( ).get( 0 ).getTournament( ) ).isEqualTo( tournament2 );
        verify( _storage ).deleteTournament( tournament1 );
    }
}