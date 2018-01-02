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

package land.tower.core.model.pairing.direct;

import static land.tower.core.model.ranking.DefaultRankingComputer.RANKING_COMPARATOR;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import land.tower.core.model.ranking.IRankingComputer;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTeam;
import land.tower.core.model.tournament.ObservableTournament;

/**
 * Created on 02/01/2018
 * @author Cédric Longo
 */
final class DirectEliminiationRankingComputer implements IRankingComputer {

    @Override
    public void computeRanking( final ObservableTournament tournament ) {

        final List<ObservableTeam> teams = tournament.getTeams( );
        final List<ObservableRound> rounds = tournament.getRounds( )
                                                       .stream( )
                                                       .sorted( Comparator.comparing( ObservableRound::getNumero ) )
                                                       .collect( Collectors.toList( ) );

        teams.forEach( team -> {
            team.getRanking( ).setPoints( 0 );
            team.getRanking( ).setD1( 0 );
            team.getRanking( ).setD2( 0 );
            team.getRanking( ).setD3( 0 );
            team.getRanking( ).setD4( 0 );
        } );

        for ( int i = 0; i < rounds.size( ); i++ ) {
            final ObservableRound round = rounds.get( i );
            final ObservableRound nextRound = i + 1 < rounds.size( ) ? rounds.get( i + 1 ) : null;
            if ( !round.isEnded( ) ) {
                continue;
            }

            round.getMatches( )
                 .forEach( match -> {
                     final ObservableTeam leftTeam = tournament.getTeam( match.getLeftTeamId( ) );
                     if ( round.getNumero( ) == 1 ) {
                         leftTeam.getRanking( ).setPoints( leftTeam.getRanking( ).getPoints( ) + 1 );
                     }
                     if ( match.hasWon( leftTeam ) ) {
                         leftTeam.getRanking( ).setPoints( leftTeam.getRanking( ).getPoints( ) + 1 );
                     }

                     final ObservableTeam rightTeam = tournament.getTeam( match.getRightTeamId( ) );
                     if ( round.getNumero( ) == 1 ) {
                         rightTeam.getRanking( ).setPoints( rightTeam.getRanking( ).getPoints( ) + 1 );
                     }
                     if ( match.hasWon( rightTeam ) ) {
                         rightTeam.getRanking( ).setPoints( rightTeam.getRanking( ).getPoints( ) + 1 );
                     }

                     if ( match.isDraw( ) && nextRound != null ) {
                         if ( nextRound.getMatchFor( leftTeam ).isPresent( ) ) {
                             leftTeam.getRanking( ).setPoints( leftTeam.getRanking( ).getPoints( ) + 1 );
                         } else if ( nextRound.getMatchFor( rightTeam ).isPresent( ) ) {
                             rightTeam.getRanking( ).setPoints( rightTeam.getRanking( ).getPoints( ) + 1 );
                         }
                     }

                     if ( round.getRound( ).isFinal( ) ) {
                         // For the final, we add one point for finalists
                         if ( leftTeam.getPairingFlags( ).get( "final" ) != null ) {
                             leftTeam.getRanking( ).setPoints( leftTeam.getRanking( ).getPoints( ) + 1 );
                         }
                         if ( rightTeam.getPairingFlags( ).get( "final" ) != null ) {
                             rightTeam.getRanking( ).setPoints( rightTeam.getRanking( ).getPoints( ) + 1 );
                         }
                     }
                 } );
        }

        // Set Rank
        final AtomicInteger rank = new AtomicInteger( );
        final AtomicReference<ObservableTeam> previous = new AtomicReference<>( );
        teams.stream( )
             .sorted( RANKING_COMPARATOR )
             .forEach( team -> {
                 if ( previous.get( ) != null && RANKING_COMPARATOR.compare( previous.get( ), team ) == 0 ) {
                     team.getRanking( ).setRank( rank.get( ) );
                 } else {
                     team.getRanking( ).setRank( rank.incrementAndGet( ) );
                 }
                 previous.set( team );
             } );
    }

}
