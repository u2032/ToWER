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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import land.tower.core.model.tournament.ObservableRanking;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTeam;
import land.tower.core.model.tournament.ObservableTournament;

/**
 * Created on 02/01/2018
 * @author Cédric Longo
 */
public final class OpponentRankingAccumulator {

    public static int compute( final ObservableTeam team, final List<ObservableRound> rounds,
                        final ObservableTournament tournament,
                        final Function<ObservableRanking, Integer> extractor ) {

        final AtomicInteger points = new AtomicInteger( );
        rounds.forEach( round -> {
            round.getMatchFor( team )
                 .ifPresent( m -> {
                     final ObservableTeam opponent = tournament.getTeam( m.getOpponentId( team ) );
                     if ( !opponent.isByeTeam( ) ) {
                         points.addAndGet( extractor.apply( opponent.getRanking( ) ) );
                     }
                 } );
        } );
        return points.get( );
    }

}
