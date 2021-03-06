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

import java.util.OptionalLong;
import javax.inject.Inject;
import land.tower.core.view.player.ObservablePlayer;

/**
 * Created on 10/12/2017
 * @author Cédric Longo
 */
final class PlayerNumeroValidator implements IPlayerNumeroValidator {

    @Inject
    public PlayerNumeroValidator( final PlayerRepository playerRepository ) {
        _playerRepository = playerRepository;
    }

    @Override
    public boolean isValid( final long numero ) {
        return numero > 0L;
    }

    @Override
    public OptionalLong generate( ) {
        final OptionalLong max = _playerRepository.getPlayersList( )
                                                  .stream( )
                                                  .mapToLong( ObservablePlayer::getNumero )
                                                  .map( l -> l + 1 )
                                                  .max( );
        return max.isPresent( ) ? max : OptionalLong.of( 1 );
    }

    private final PlayerRepository _playerRepository;

}
