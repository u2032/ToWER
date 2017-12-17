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

import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.synchronizedObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javax.inject.Inject;
import land.tower.core.ext.logger.Loggers;
import land.tower.core.ext.service.IService;
import land.tower.core.view.player.ObservablePlayer;
import land.tower.data.Player;

/**
 * Created on 06/12/2017
 * @author Cédric Longo
 */
public final class PlayerRepository implements IService {

    @Inject
    public PlayerRepository( final IPlayerStorage storage ) {
        _storage = storage;
    }

    public void registerPlayer( Player player ) {
        _players.add( new ObservablePlayer( player ) );
        saveInStorage( );
    }

    public void removePlayer( Player player ) {
        _players.removeIf( p -> p.getNumero( ) == player.getNumero( ) );
        saveInStorage( );
    }

    public ObservableList<ObservablePlayer> getPlayersList( ) {
        return _players;
    }

    @Override
    public void start( ) {
        _storage.loadPlayers( )
                .stream( )
                .map( ObservablePlayer::new )
                .forEach( _players::add );
        _logger.info( "{} players loaded from storage", _players.size( ) );
    }

    @Override
    public void stop( ) {

    }

    public Optional<ObservablePlayer> getPlayer( final long numero ) {
        return _players.stream( )
                       .filter( p -> p.getNumero( ) == numero )
                       .findAny( );
    }

    private void saveInStorage( ) {
        final List<Player> players = _players.stream( )
                                             .map( ObservablePlayer::getPlayer )
                                             .collect( Collectors.toList( ) );
        _storage.savePlayers( players );
    }

    private final ObservableList<ObservablePlayer> _players = synchronizedObservableList( observableArrayList( ) );
    private final IPlayerStorage _storage;

    private Logger _logger = LoggerFactory.getLogger( Loggers.MAIN );
}
