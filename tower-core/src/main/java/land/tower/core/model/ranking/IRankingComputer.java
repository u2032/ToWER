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

import static land.tower.core.model.ranking.DefaultRankingComputer.RANKING_COMPARATOR;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTeam;
import land.tower.core.model.tournament.ObservableTournament;

/**
 * Created on 01/01/2018
 * @author Cédric Longo
 */
public interface IRankingComputer {

    void computeRanking( final ObservableTournament tournament );

    default void setRanks( final List<ObservableTeam> teams, final List<ObservableRound> rounds ) {
        // Set Rank
        final AtomicInteger rank = new AtomicInteger( );
        final AtomicReference<ObservableTeam> previous = new AtomicReference<>( );
        teams.stream( )
             .sorted( RANKING_COMPARATOR )
             .forEach( team -> {
                 if ( rounds.isEmpty( ) ) {
                     team.getRanking( ).setRank( 0 );
                     return;
                 }

                 if ( previous.get( ) != null && RANKING_COMPARATOR.compare( previous.get( ), team ) == 0 ) {
                     team.getRanking( ).setRank( rank.get( ) );
                 } else {
                     team.getRanking( ).setRank( rank.incrementAndGet( ) );
                 }
                 previous.set( team );
             } );
    }

}
