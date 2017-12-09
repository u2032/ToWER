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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import land.tower.core.ext.service.IService;
import land.tower.data.Player;

/**
 * Created on 06/12/2017
 * @author Cédric Longo
 */
final class PlayerRepository implements IService {

    public void registerPlayer( Player player ) {
        _players.add( player );
    }

    public void removePlayer( Player player ) {
        _players.removeIf( p -> p.getNumero( ) == player.getNumero( ) );
    }

    public List<Player> getPlayersList( ) {
        return _players;
    }

    @Override
    public void start( ) {
        // TODO Load from disk
        final Path storage = Paths.get( "./data/players.json" );
    }

    @Override
    public void stop( ) {

    }

    private final List<Player> _players = Collections.synchronizedList( new ArrayList<>( ) );
}
