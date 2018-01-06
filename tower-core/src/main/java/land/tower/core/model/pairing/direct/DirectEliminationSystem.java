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

import static java.util.Comparator.reverseOrder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.inject.Inject;
import land.tower.core.model.pairing.PairingSystem;
import land.tower.core.model.ranking.IRankingComputer;
import land.tower.core.model.tournament.ObservableMatch;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTeam;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.data.Match;
import land.tower.data.Round;
import land.tower.data.Team;
import land.tower.data.Teams;

/**
 * Created on 02/01/2018
 * @author Cédric Longo
 */
public final class DirectEliminationSystem implements PairingSystem {

    @Inject
    public DirectEliminationSystem( final DirectEliminiationRankingComputer rankingComputer ) {
        _rankingComputer = rankingComputer;
    }

    @Override
    public Round createNewRound( final ObservableTournament tournament ) {
        if ( tournament.getRounds( ).isEmpty( ) ) {
            return firstRound( tournament );
        }

        tournament.getTeams( ).forEach( team -> team.getPairingFlags( ).remove( "final" ) );

        final ObservableRound lastRound = tournament.getRounds( ).stream( )
                                                    .sorted( Comparator.comparing( ObservableRound::getNumero,
                                                                                   reverseOrder( ) ) )
                                                    .findFirst( )
                                                    .orElseThrow( IllegalStateException::new );

        // Last round has 2 matches, so this round is the final
        final boolean isFinal = lastRound.getMatches( ).size( ) == 2;

        final List<Match> matches = new ArrayList<>( );
        final AtomicInteger position = new AtomicInteger( );
        for ( int i = 0; i < lastRound.getMatches( ).size( ); i += 2 ) {
            // Pair winning teams
            final ObservableMatch match1 = lastRound.getMatches( ).get( i );
            final ObservableTeam winningT1 = tournament.getTeam( getWinningTeam( match1 ) );
            if ( isFinal ) {
                winningT1.getPairingFlags( ).put( "final", String.valueOf( true ) );
            }

            final ObservableMatch match2 = lastRound.getMatches( ).get( i + 1 );
            final ObservableTeam winningT2 = tournament.getTeam( getWinningTeam( match2 ) );
            if ( isFinal ) {
                winningT2.getPairingFlags( ).put( "final", String.valueOf( true ) );
            }

            final Match match = new Match( );
            match.setPosition( position.incrementAndGet( ) );
            match.setLeftTeamId( winningT1.isActive( ) ? winningT1.getTeam( ).getId( ) : Teams.BYE_TEAM.getId( ) );
            match.setRightTeamId( winningT2.isActive( ) ? winningT2.getTeam( ).getId( ) : Teams.BYE_TEAM.getId( ) );
            matches.add( match );

            if ( isFinal ) {
                // Add match for loosing teams for the third place
                final Match m = new Match( );
                m.setPosition( position.incrementAndGet( ) );
                final ObservableTeam loosingT1 = tournament.getTeam( match1.getOpponentId( winningT1 ) );
                final ObservableTeam loosingT2 = tournament.getTeam( match2.getOpponentId( winningT2 ) );

                m.setLeftTeamId( loosingT1.isActive( ) ? loosingT1.getTeam( ).getId( ) : Teams.BYE_TEAM.getId( ) );
                m.setRightTeamId( loosingT2.isActive( ) ? loosingT2.getTeam( ).getId( ) : Teams.BYE_TEAM.getId( ) );
                matches.add( m );
            }
        }

        final Round round = new Round( );
        round.setNumero( tournament.getRounds( ).size( ) + 1 );
        round.setStartDate( ZonedDateTime.now( ) );
        round.getMatches( ).addAll( matches );
        round.setFinal( isFinal );
        return round;
    }

    public Round createFirstRoundFromInitialRanking( ObservableTournament tournament ) {
        final List<Team> activeTeams = tournament.getTeams( ).stream( )
                                                 .map( ObservableTeam::getTeam )
                                                 .filter( Team::isActive )
                                                 .collect( Collectors.toList( ) );

        double p = Math.floor( Math.log( activeTeams.size( ) ) / Math.log( 2 ) );
        final int maxTeamCount = (int) Math.pow( 2, p );
        final Team[] teams = activeTeams.stream( )
                                        .sorted( Comparator.comparingInt( t -> t.getRanking( ).getRank( ) ) )
                                        .limit( maxTeamCount )
                                        .toArray( Team[]::new );

        final List<Match> matches = new ArrayList<>( );

        final AtomicInteger position = new AtomicInteger( );
        for ( int i = 0; i < teams.length / 2; i++ ) {
            final Team left = teams[i];
            final Team right = teams[teams.length - 1 - i];

            final Match match = new Match( );
            match.setPosition( position.incrementAndGet( ) );
            match.setLeftTeamId( left.getId( ) );
            match.setRightTeamId( right.getId( ) );
            matches.add( match );
        }

        final Round round = new Round( );
        round.setNumero( tournament.getRounds( ).size( ) + 1 );
        round.setStartDate( ZonedDateTime.now( ) );
        round.getMatches( ).addAll( matches );
        return round;
    }

    private int getWinningTeam( final ObservableMatch match ) {
        if ( match.hasWon( match.getLeftTeamId( ) ) ) {
            return match.getLeftTeamId( );
        }
        if ( match.hasWon( match.getRightTeamId( ) ) ) {
            return match.getRightTeamId( );
        }
        // In Direct elimination system, draw should not be authorized... but if it is, we take randomly the winner
        return _random.nextBoolean( ) ? match.getLeftTeamId( ) : match.getRightTeamId( );
    }

    private Round firstRound( final ObservableTournament tournament ) {
        final List<Team> activeTeams = tournament.getTeams( ).stream( )
                                                 .map( ObservableTeam::getTeam )
                                                 .filter( Team::isActive )
                                                 .collect( Collectors.toList( ) );

        double p = Math.floor( Math.log( activeTeams.size( ) ) / Math.log( 2 ) );
        final int maxTeamCount = (int) Math.pow( 2, p );
        final List<Team> teams = activeTeams.stream( )
                                            .limit( maxTeamCount )
                                            .collect( Collectors.toList( ) );

        final List<Match> matches = new ArrayList<>( );

        final AtomicInteger position = new AtomicInteger( );
        while ( teams.size( ) > 1 ) {
            final Team left = teams.remove( _random.nextInt( teams.size( ) ) );
            final Team right = teams.remove( _random.nextInt( teams.size( ) ) );

            final Match match = new Match( );
            match.setPosition( position.incrementAndGet( ) );
            match.setLeftTeamId( left.getId( ) );
            match.setRightTeamId( right.getId( ) );
            matches.add( match );
        }

        final Round round = new Round( );
        round.setNumero( tournament.getRounds( ).size( ) + 1 );
        round.setStartDate( ZonedDateTime.now( ) );
        round.getMatches( ).addAll( matches );
        return round;
    }

    @Override
    public IRankingComputer getRankingComputer( ) {
        return _rankingComputer;
    }

    private final Random _random = new Random( );
    private final DirectEliminiationRankingComputer _rankingComputer;
}
