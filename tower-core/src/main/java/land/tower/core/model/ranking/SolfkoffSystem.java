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

package land.tower.core.model.ranking;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import land.tower.core.model.tournament.ObservableMatch;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTeam;
import land.tower.core.model.tournament.ObservableTournament;

/**
 * Created on 01/01/2018
 * @author Cédric Longo
 */
public final class SolfkoffSystem {

    /**
     * Sum of opponent scores where a win gives a +1 and a loss a -1
     */
    public static int compute( final ObservableTeam team, final List<ObservableRound> rounds,
                               final ObservableTournament tournament ) {

        final Map<Integer, Integer> scores = Maps.newHashMap( );

        final AtomicInteger points = new AtomicInteger( );
        rounds.forEach( round -> {
            round.getMatchFor( team )
                 .ifPresent( m -> {
                     final ObservableTeam opponent = tournament.getTeam( m.getOpponentId( team ) );
                     if ( !opponent.isByeTeam( ) ) {
                         final int opponentScore = scores.computeIfAbsent( opponent.getId( ),
                                                                           teamId -> computeScore( opponent, rounds ) );
                         points.addAndGet( opponentScore );
                     }
                 } );
        } );
        return points.get( );
    }

    private static int computeScore( final ObservableTeam team, final List<ObservableRound> rounds ) {
        final long winCount = rounds.stream( )
                                    .filter( r -> {
                                        final Optional<ObservableMatch> match = r.getMatchFor( team );
                                        if ( !match.isPresent( ) ) {
                                            return false;
                                        }
                                        if ( match.get( ).getOpponentId( team ) == ObservableTeam.BYE_TEAM.getId( ) ) {
                                            return false;
                                        }
                                        return match.get( ).hasWon( team );
                                    } )
                                    .count( );

        final long lossCount = rounds.stream( )
                                     .filter( r -> {
                                         final Optional<ObservableMatch> match = r.getMatchFor( team );
                                         if ( !match.isPresent( ) ) {
                                             return false;
                                         }
                                         if ( match.get( ).getOpponentId( team ) == ObservableTeam.BYE_TEAM.getId( ) ) {
                                             return false;
                                         }
                                         return match.get( ).hasLost( team );
                                     } )
                                     .count( );

        return (int) Math.max( winCount - lossCount, rounds.size( ) > 8 ? -4 : -3 );
    }

}
