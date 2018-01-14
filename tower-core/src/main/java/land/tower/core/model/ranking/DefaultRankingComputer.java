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

import static java.util.Comparator.reverseOrder;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import land.tower.core.model.tournament.ObservableRanking;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTeam;
import land.tower.core.model.tournament.ObservableTournament;

/**
 * Created on 01/01/2018
 * @author Cédric Longo
 */
public final class DefaultRankingComputer implements IRankingComputer {

    public DefaultRankingComputer( ) {
    }

    @Override
    public void computeRanking( final ObservableTournament tournament ) {
        final List<ObservableTeam> teams = tournament.getTeams( );
        final List<ObservableRound> rounds = tournament.getRounds( )
                                                       .stream( )
                                                       .filter( ObservableRound::isEnded )
                                                       .sorted( Comparator.comparing( ObservableRound::getNumero ) )
                                                       .collect( Collectors.toList( ) );

        // Compute main points
        teams.forEach( team -> team.getRanking( ).setPoints( KashdanSystem.compute( team, rounds ) ) );

        // Compute secondary points
        teams.forEach( team -> {
            team.getRanking( ).setD1( SolfkoffSystem.compute( team, rounds, tournament ) );
            team.getRanking( ).setD3( TemporalSystem.compute( team, rounds, tournament ) );
            team.getRanking( ).setD4( CoonsSystem.compute( team, rounds, tournament ) );
        } );

        // D2 needs D1 so it's computed after
        teams.forEach( team -> {
            team.getRanking( ).setD2( OpponentRankingAccumulator.compute( team, rounds, tournament,
                                                                          ObservableRanking::getD1 ) );
        } );

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

    public static final Comparator<ObservableTeam> RANKING_COMPARATOR;

    static {
        Comparator<ObservableTeam> rankingComparator =
            Comparator.comparing( t -> t.getRanking( ).getPoints( ), reverseOrder( ) );
        rankingComparator = rankingComparator.thenComparing( t -> t.getRanking( ).getD1( ), reverseOrder( ) );
        rankingComparator = rankingComparator.thenComparing( t -> t.getRanking( ).getD2( ), reverseOrder( ) );
        rankingComparator = rankingComparator.thenComparing( t -> t.getRanking( ).getD3( ), reverseOrder( ) );
        rankingComparator = rankingComparator.thenComparing( t -> t.getRanking( ).getD4( ), reverseOrder( ) );
        RANKING_COMPARATOR = rankingComparator;
    }
}
