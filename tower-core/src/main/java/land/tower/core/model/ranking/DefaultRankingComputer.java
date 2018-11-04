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
import java.util.stream.Collectors;
import land.tower.core.model.tournament.ObservableRanking;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTeam;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.data.TournamentScoringMode;

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
        if ( tournament.getHeader( ).getScoringMode( ) == TournamentScoringMode.BY_WINS ) {
            teams.forEach( team -> team.getRanking( ).setPoints( KashdanSystem.compute( team, rounds ) ) );

        } else if ( tournament.getHeader( ).getScoringMode( ) == TournamentScoringMode.BY_POINTS ) {
            teams.forEach( team -> team.getRanking( ).setPoints( SumOfScoreSystem.compute( team, rounds ) ) );
        }

        if ( tournament.getHeader( ).getDoubleScore( ) ) {
            // Compute secondary points
            teams.forEach( team -> {
                team.getRanking( ).setD1( SumOfScoreBisSystem.compute( team, rounds ) );
                team.getRanking( ).setD2( SolfkoffSystem.compute( team, rounds, tournament ) );
                team.getRanking( ).setD4( TemporalSystem.compute( team, rounds, tournament ) );
            } );

            // D3 needs D2 so it's computed after
            teams.forEach( team -> {
                team.getRanking( ).setD3( OpponentRankingAccumulator.compute( team, rounds, tournament,
                                                                              ObservableRanking::getD2 ) );
            } );

        } else {
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

        }

        setRanks( teams, rounds );
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
