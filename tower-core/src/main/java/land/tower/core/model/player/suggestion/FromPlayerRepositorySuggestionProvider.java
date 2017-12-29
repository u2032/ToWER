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

package land.tower.core.model.player.suggestion;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javafx.util.Pair;
import javax.inject.Inject;
import land.tower.core.ext.util.LevenshteinDistance;
import land.tower.core.model.player.PlayerRepository;
import land.tower.core.view.player.ObservablePlayer;
import land.tower.data.Player;

/**
 * Created on 28/12/2017
 * @author Cédric Longo
 */
public final class FromPlayerRepositorySuggestionProvider implements IPlayerSuggestionProvider {

    @Inject
    FromPlayerRepositorySuggestionProvider( final PlayerRepository playerRepository ) {
        _playerRepository = playerRepository;
    }

    @Override
    public List<Player> getSuggestionsForName( final String name ) {
        return _playerRepository.getPlayersList( )
                                .stream( )
                                .map( p -> {
                                    final String ref =
                                        p.getLastname( )
                                         .substring( 0, Math.min( p.getLastname( ).length( ), name.length( ) ) );
                                    return new Pair<>( p.getPlayer( ), _levenshteinDistance.apply( ref, name ) );
                                } )
                                .filter( p -> p.getValue( ) >= 0 )
                                .sorted( Comparator.comparing( Pair::getValue ) )
                                .limit( 10 )
                                .map( Pair::getKey )
                                .collect( Collectors.toList( ) );
    }

    @Override
    public List<Player> getSuggestionsForNumero( final long numero ) {
        return _playerRepository.getPlayersList( )
                                .stream( )
                                .filter( p -> p.getNumero( ) == numero )
                                .map( ObservablePlayer::getPlayer )
                                .collect( Collectors.toList( ) );
    }

    private final PlayerRepository _playerRepository;
    private final LevenshteinDistance _levenshteinDistance = new LevenshteinDistance( 3 );
}
