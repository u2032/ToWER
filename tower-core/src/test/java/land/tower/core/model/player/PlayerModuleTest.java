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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import land.tower.core.ext.service.IService;
import land.tower.core.ext.thread.ThreadingModule;

/**
 * Created on 15/12/2017
 * @author Cédric Longo
 */
class PlayerModuleTest {

    @Test
    @DisplayName( "PlayerRepository can be injected as singleton" )
    void can_inject_PlayerRepository( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new PlayerModule( ), new ThreadingModule( ) );
        // Exercice
        final PlayerRepository instance = injector.getInstance( PlayerRepository.class );
        final PlayerRepository instance2 = injector.getInstance( PlayerRepository.class );
        // Verify
        assertThat( instance ).isNotNull( );
        assertThat( instance ).isSameAs( instance2 );
    }

    @Test
    @DisplayName( "IPlayerStorage can be injected as singleton" )
    void can_inject_IPlayerStorage( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new PlayerModule( ), new ThreadingModule( ) );
        // Exercice
        final IPlayerStorage instance = injector.getInstance( IPlayerStorage.class );
        final IPlayerStorage instance2 = injector.getInstance( IPlayerStorage.class );
        // Verify
        assertThat( instance ).isNotNull( );
        assertThat( instance ).isInstanceOf( PlayerStorage.class );
        assertThat( instance ).isSameAs( instance2 );
    }

    @Test
    @DisplayName( "PlayerRepository is registered as Service" )
    void PlayerRepository_is_registered( ) throws Exception {
        // Setup
        final Injector injector = Guice.createInjector( new PlayerModule( ), new ThreadingModule( ) );
        // Exercice
        final Set<IService> instance = injector.getInstance( Key.get( new TypeLiteral<Set<IService>>( ) {
        } ) );
        // Verify
        assertThat( instance ).hasAtLeastOneElementOfType( PlayerRepository.class );
    }
}