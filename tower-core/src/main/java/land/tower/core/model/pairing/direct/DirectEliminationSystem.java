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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
                                                    .max( Comparator.comparing( ObservableRound::getNumero ) )
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
            setDefaultByeScore( match, tournament );
            matches.add( match );

            if ( isFinal ) {
                // Add match for loosing teams for the third place
                final Match m = new Match( );
                m.setPosition( position.incrementAndGet( ) );
                final ObservableTeam loosingT1 = tournament.getTeam( match1.getOpponentId( winningT1 ) );
                final ObservableTeam loosingT2 = tournament.getTeam( match2.getOpponentId( winningT2 ) );

                m.setLeftTeamId( loosingT1.isActive( ) ? loosingT1.getTeam( ).getId( ) : Teams.BYE_TEAM.getId( ) );
                m.setRightTeamId( loosingT2.isActive( ) ? loosingT2.getTeam( ).getId( ) : Teams.BYE_TEAM.getId( ) );
                setDefaultByeScore( m, tournament );
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

    private void setDefaultByeScore( final Match match, final ObservableTournament tournament ) {
        final boolean byeLeft = match.getLeftTeamId( ) == Teams.BYE_TEAM.getId( );
        final boolean byeRight = match.getRightTeamId( ) == Teams.BYE_TEAM.getId( );
        if ( byeLeft || byeRight ) {
            match.setScoreLeft( byeRight ? tournament.getHeader( ).getWinningGameCount( ) : 0 );
            match.setScoreDraw( 0 );
            match.setScoreRight( byeLeft ? tournament.getHeader( ).getWinningGameCount( ) : 0 );
        }
    }

    public Round createFirstRoundFromInitialRanking( ObservableTournament tournament ) {
        final List<ObservableTeam> activeTeams =
            tournament.getTeams( ).stream( )
                      .filter( ObservableTeam::isActive )
                      .sorted( Comparator.comparingInt( t -> t.getRanking( ).getRank( ) ) )
                      .collect( Collectors.toList( ) );

        double p = Math.floor( Math.log( activeTeams.size( ) ) / Math.log( 2 ) );
        final int maxTeamCountLower = (int) Math.pow( 2, p );
        final int maxTeamCountUpper =
            activeTeams.size( ) > maxTeamCountLower ? (int) Math.pow( 2, p + 1 ) : maxTeamCountLower;

        final int initialTeamCount = activeTeams.size( );

        final boolean isFinal = initialTeamCount == 2;
        final ObservableTeam[] teams = Arrays.copyOf( activeTeams.toArray( new ObservableTeam[activeTeams.size( )] ),
                                                      maxTeamCountUpper );

        final Match match0 = new Match( );
        match0.setPosition( 1 );
        match0.setLeftTeamId( teams[0].getId( ) );
        match0.setRightTeamId( teams[1].getId( ) );
        Match[] matches = new Match[] { match0 };

        while ( matches.length < maxTeamCountUpper / 2 ) {
            final Match[] previous = matches;
            matches = new Match[previous.length * 2];
            for ( int i = 0; i < previous.length; i++ ) {
                final Match match1 = new Match( );
                match1.setPosition( i * 2 + 1 );
                match1.setLeftTeamId( previous[i].getLeftTeamId( ) );
                match1.setRightTeamId( opponentTeam( teams, match1.getLeftTeamId( ), matches.length * 2 ) );
                setDefaultByeScore( match1, tournament );
                matches[i * 2] = match1;

                final Match match2 = new Match( );
                match2.setPosition( i * 2 + 2 );
                match2.setLeftTeamId( previous[i].getRightTeamId( ) );
                match2.setRightTeamId( opponentTeam( teams, match2.getLeftTeamId( ), matches.length * 2 ) );
                setDefaultByeScore( match2, tournament );
                matches[i * 2 + 1] = match2;
            }
        }

        if ( isFinal ) {
            for ( final Match m : matches ) {
                final ObservableTeam left = tournament.getTeam( m.getLeftTeamId( ) );
                if ( left.getId( ) != Teams.BYE_TEAM.getId( ) ) {
                    left.getPairingFlags( ).put( "final", String.valueOf( true ) );
                }
                final ObservableTeam right = tournament.getTeam( m.getRightTeamId( ) );
                if ( right.getId( ) != Teams.BYE_TEAM.getId( ) ) {
                    right.getPairingFlags( ).put( "final", String.valueOf( true ) );
                }
            }
        }

        final Round round = new Round( );
        round.setNumero( tournament.getRounds( ).size( ) + 1 );
        round.setStartDate( ZonedDateTime.now( ) );
        round.getMatches( ).addAll( Arrays.asList( matches ) );
        round.setFinal( isFinal );
        return round;
    }

    private int indexOf( ObservableTeam[] array, int teamId ) {
        for ( int i = 0; i < array.length; i++ ) {
            if ( array[i] != null && teamId == array[i].getId( ) ) {
                return i;
            }
        }
        return -1;
    }

    private int opponentTeam( ObservableTeam[] array, int teamId, int max ) {
        int index = indexOf( array, teamId );
        return array[max - 1 - index] == null ? Teams.BYE_TEAM.getId( ) : array[max - 1 - index].getId( );
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
        final List<ObservableTeam> teams = tournament.getTeams( ).stream( )
                                                     .filter( ObservableTeam::isActive )
                                                     .collect( Collectors.toList( ) );

        double p = Math.floor( Math.log( teams.size( ) ) / Math.log( 2 ) );
        final int maxTeamCountLower = (int) Math.pow( 2, p );
        final int maxTeamCountUpper =
            teams.size( ) > maxTeamCountLower ? (int) Math.pow( 2, p + 1 ) : maxTeamCountLower;

        final int initialTeamCount = teams.size( );
        final int byeCount = maxTeamCountUpper - initialTeamCount;

        final boolean isFinal = teams.size( ) == 2;

        final List<Match> matches = new ArrayList<>( );

        final AtomicInteger position = new AtomicInteger( );
        while ( teams.size( ) > 1 && matches.size( ) * 2 < initialTeamCount - byeCount ) {
            final ObservableTeam left = teams.remove( _random.nextInt( teams.size( ) ) );
            if ( isFinal ) {
                left.getPairingFlags( ).put( "final", String.valueOf( true ) );
            }
            final ObservableTeam right = teams.remove( _random.nextInt( teams.size( ) ) );
            if ( isFinal ) {
                right.getPairingFlags( ).put( "final", String.valueOf( true ) );
            }

            final Match match = new Match( );
            match.setPosition( position.incrementAndGet( ) );
            match.setLeftTeamId( left.getId( ) );
            match.setRightTeamId( right.getId( ) );
            matches.add( match );
        }

        while ( !teams.isEmpty( ) ) {
            final ObservableTeam left = teams.remove( _random.nextInt( teams.size( ) ) );
            if ( isFinal ) {
                left.getPairingFlags( ).put( "final", String.valueOf( true ) );
            }

            final Match match = new Match( );
            match.setPosition( position.incrementAndGet( ) );
            match.setLeftTeamId( left.getId( ) );
            match.setRightTeamId( ObservableTeam.BYE_TEAM.getId( ) );
            setDefaultByeScore( match, tournament );
            matches.add( match );
        }

        final Round round = new Round( );
        round.setNumero( tournament.getRounds( ).size( ) + 1 );
        round.setStartDate( ZonedDateTime.now( ) );
        round.getMatches( ).addAll( matches );
        round.setFinal( isFinal );
        return round;
    }

    @Override
    public IRankingComputer getRankingComputer( ) {
        return _rankingComputer;
    }

    private final Random _random = new Random( );
    private final DirectEliminiationRankingComputer _rankingComputer;
}
