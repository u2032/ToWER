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

package land.tower.core.model.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.inject.Guice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import javax.inject.Inject;
import land.tower.data.Player;
import land.tower.data.PlayerNationality;

/**
 * Created on 16/12/2017
 * @author Cédric Longo
 */
class PlayerRepositoryTest {

    @Inject
    private PlayerRepository _repository;

    @Inject
    private IPlayerStorage _storage;

    @BeforeEach
    void setUp( ) {
        Guice.createInjector( new TestPlayerModule( ) ).injectMembers( this );
    }

    @Test
    @DisplayName( "When service is started, it loads players from storage" )
    void startTest( ) throws Exception {
        // Setup
        when( _storage.loadPlayers( ) )
            .thenReturn( Arrays.asList(
                new Player( 1, "John", "Doe", "1985-08-29", PlayerNationality.FR ),
                new Player( 2, "Manu", "Macaron", "1977-12-16", PlayerNationality.FR )
            ) );
        // Exercice
        _repository.start( );
        // Verify
        verify( _storage ).loadPlayers( );
        assertThat( _repository.getPlayersList( ) ).hasSize( 2 );
    }

    @Test
    @DisplayName( "When a player is registered, it persists into repository and saved into storage" )
    void registerTest( ) throws Exception {
        // Setup
        final Player player = new Player( 1, "John", "Doe", "1985-08-29", PlayerNationality.FR );
        // Exercice
        _repository.registerPlayer( player );
        // Verify
        assertThat( _repository.getPlayer( 1 ).get( ).getPlayer( ) ).isEqualTo( player );
        verify( _storage ).savePlayers( any( ) );
    }

    @Test
    @DisplayName( "When a player is removes, it not anymore into repository and removed from storage" )
    void removeTest( ) throws Exception {
        // Setup
        final Player player = new Player( 1, "John", "Doe", "1985-08-29", PlayerNationality.FR );
        _repository.registerPlayer( player );
        Mockito.clearInvocations( _storage );
        // Exercice
        _repository.removePlayer( player );
        // Verify
        assertThat( _repository.getPlayer( 1 ).isPresent( ) ).isFalse( );
        verify( _storage ).savePlayers( any( ) );
    }
}