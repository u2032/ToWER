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

package land.tower.core.model.pairing.doubleElim;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.inject.Inject;
import land.tower.core.model.pairing.PairingSystem;
import land.tower.core.model.tournament.ObservableMatch;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTeam;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.data.Match;
import land.tower.data.Round;
import land.tower.data.Teams;
import land.tower.data.TimerInfo;

/**
 * Created on 02/01/2018
 * @author Cédric Longo
 */
public final class DoubleEliminationSystem implements PairingSystem {

    @Inject
    public DoubleEliminationSystem( ) {
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

        final List<ObservableMatch> lastRoundMatches =
            lastRound.getMatches( )
                     .stream( )
                     .sorted( Comparator.comparing( ObservableMatch::getPosition ) )
                     .collect( Collectors.toList( ) );

        final boolean isFinal = lastRoundMatches.size( ) == 2;

        // Split round by winner/looser bracket
        final List<ObservableMatch> winnerBracket;
        final List<ObservableMatch> looserBracket;
        if ( lastRound.getNumero( ) == 1 ) {
            winnerBracket = lastRoundMatches;
            looserBracket = Collections.emptyList( );
        } else {
            winnerBracket = lastRoundMatches.subList( 0, lastRoundMatches.size( ) / 2 );
            looserBracket = lastRoundMatches.subList( lastRoundMatches.size( ) / 2, lastRoundMatches.size( ) );
        }

        if ( isFinal ) {
            final ObservableTeam winner1 = tournament.getTeam( getWinningTeam( winnerBracket.get( 0 ) ) );
            winner1.getPairingFlags( ).put( "final", "true" );

            final ObservableTeam winner2 = tournament.getTeam( getWinningTeam( looserBracket.get( 0 ) ) );
            winner2.getPairingFlags( ).put( "final", "true" );

            final Match match = new Match( );
            match.setLeftTeamId( winner1.getId( ) );
            match.setRightTeamId( winner2.getId( ) );
            match.setPosition( 1 );
            setDefaultByeScore( match, tournament );

            final Round round = new Round( );
            round.setNumero( tournament.getRounds( ).size( ) + 1 );
            round.setTimer( new TimerInfo( tournament.getHeader( ).getMatchDuration( ) ) );
            round.getMatches( ).add( match );
            round.setFinal( true );
            return round;
        }

        // Compute pairing for both brackets
        final List<ObservableTeam> looserFromWinnerBracket = new ArrayList<>( );

        final List<Match> winnerNewPairing;

        if ( lastRound.getNumero( ) > 2 && lastRound.getNumero( ) % 2 == 1 ) {
            // Bye for all winners at this round
            winnerNewPairing = new ArrayList<>( );
            for ( int i = 0; i < winnerBracket.size( ); i += 1 ) {
                final ObservableTeam winner = tournament.getTeam( getWinningTeam( winnerBracket.get( i ) ) );
                final ObservableTeam looser = tournament.getTeam( winnerBracket.get( i ).getOpponentId( winner ) );
                looserFromWinnerBracket.add( looser );

                final Match match = new Match( );
                match.setLeftTeamId( winner.isActive( ) ? winner.getTeam( ).getId( ) : Teams.BYE_TEAM.getId( ) );
                match.setRightTeamId( Teams.BYE_TEAM.getId( ) );
                setDefaultByeScore( match, tournament );
                winnerNewPairing.add( match );
            }
        } else {
            winnerNewPairing = directPairingFromLastMatches( tournament, winnerBracket, looserFromWinnerBracket );
        }

        final List<Match> looserNewPairing = new ArrayList<>( );
        if ( lastRound.getNumero( ) == 1 ) {
            // First round for looser bracket: We take loosers of winner bracket in order
            for ( int i = 0; i < looserFromWinnerBracket.size( ); i += 2 ) {
                final ObservableTeam team1 = looserFromWinnerBracket.get( i );
                final ObservableTeam team2;
                if ( looserFromWinnerBracket.size( ) > 1 ) {
                    team2 = looserFromWinnerBracket.get( i + 1 );
                } else {
                    team2 = ObservableTeam.BYE_TEAM;
                }
                final Match match = new Match( );
                match.setLeftTeamId( team1.isActive( ) ? team1.getTeam( ).getId( ) : Teams.BYE_TEAM.getId( ) );
                match.setRightTeamId( team2.isActive( ) ? team2.getTeam( ).getId( ) : Teams.BYE_TEAM.getId( ) );
                setDefaultByeScore( match, tournament );
                looserNewPairing.add( match );
            }

        } else {

            // The looser bracket has already started: We pair loosers of winner bracket with a bye and winner of looser bracket with themself
            final List<ObservableTeam> teams = new ArrayList<>( );
            if ( lastRound.getNumero( ) % 2 == 0 ) {// Criss cross
                teams.addAll( looserFromWinnerBracket.subList( looserFromWinnerBracket.size( ) / 2,
                                                               looserFromWinnerBracket.size( ) ) );
                teams.addAll( looserFromWinnerBracket.subList( 0, looserFromWinnerBracket.size( ) / 2 ) );
            } else {
                teams.addAll( looserFromWinnerBracket );
            }

            teams.removeIf( t -> t.getId( ) == ObservableTeam.BYE_TEAM.getId( ) );

            if ( lastRound.getNumero( ) > 2 && lastRound.getNumero( ) % 2 == 1 ) {
                // We pair new looser with BYE
                int index = 1;
                final int teamSize = teams.size( );
                for ( int k = 0; k < teamSize; k++ ) {
                    teams.add( index, ObservableTeam.BYE_TEAM );
                    index += 2;
                }

                index = 2;
                for ( int i = 0; i < looserBracket.size( ); i += 2 ) {
                    final ObservableTeam winner1 = tournament.getTeam( getWinningTeam( looserBracket.get( i ) ) );
                    final ObservableTeam winner2 = tournament.getTeam( getWinningTeam( looserBracket.get( i + 1 ) ) );
                    teams.addAll( index, ImmutableList.of( winner1, winner2 ) );
                    index += 4;
                }

            } else {
                teams.addAll( looserBracket.stream( )
                                           .map( m -> tournament.getTeam( getWinningTeam( m ) ) )
                                           .collect( Collectors.toList( ) ) );
            }

            for ( int i = 0; i < teams.size( ); i += 2 ) {
                final Match match = new Match( );
                match.setLeftTeamId( teams.get( i ).isActive( ) ? teams.get( i ).getTeam( ).getId( )
                                                                : Teams.BYE_TEAM.getId( ) );
                match.setRightTeamId( teams.get( i + 1 ).isActive( ) ? teams.get( i + 1 ).getTeam( ).getId( )
                                                                     : Teams.BYE_TEAM.getId( ) );
                setDefaultByeScore( match, tournament );
                looserNewPairing.add( match );
            }
        }

        // Add matches in winner bracket to have same match count
        while ( winnerNewPairing.size( ) < looserNewPairing.size( ) ) {
            final Match match = new Match( );
            match.setLeftTeamId( ObservableTeam.BYE_TEAM.getId( ) );
            match.setRightTeamId( ObservableTeam.BYE_TEAM.getId( ) );
            setDefaultByeScore( match, tournament );
            winnerNewPairing.add( match );
        }

        final ArrayList<Match> matches = new ArrayList<>( );
        matches.addAll( winnerNewPairing );
        matches.addAll( looserNewPairing );

        // Set position
        final AtomicInteger position = new AtomicInteger( );
        for ( final Match m : matches ) {
            m.setPosition( position.incrementAndGet( ) );
        }

        final Round round = new Round( );
        round.setNumero( tournament.getRounds( ).size( ) + 1 );
        round.setTimer( new TimerInfo( tournament.getHeader( ).getMatchDuration( ) ) );
        round.getMatches( ).addAll( matches );
        round.setFinal( isFinal );
        return round;
    }

    private List<Match> directPairingFromLastMatches( final ObservableTournament tournament,
                                                      final List<ObservableMatch> lastRoundMatches,
                                                      final List<ObservableTeam> looserCollector ) {

        final List<Match> matches = new ArrayList<>( );
        for ( int i = 0; i < lastRoundMatches.size( ); i += 2 ) {
            // Pair winning teams
            final ObservableMatch match1 = lastRoundMatches.get( i );
            final ObservableTeam winningT1 = tournament.getTeam( getWinningTeam( match1 ) );
            final ObservableTeam looserT1 = tournament.getTeam( match1.getOpponentId( winningT1 ) );
            looserCollector.add( looserT1 );

            final ObservableTeam winningT2;
            if ( lastRoundMatches.size( ) > 1 ) {
                final ObservableMatch match2 = lastRoundMatches.get( i + 1 );
                winningT2 = tournament.getTeam( getWinningTeam( match2 ) );
                final ObservableTeam looserT2 = tournament.getTeam( match2.getOpponentId( winningT2 ) );
                looserCollector.add( looserT2 );
            } else {
                winningT2 = ObservableTeam.BYE_TEAM;
            }

            final Match match = new Match( );
            match.setLeftTeamId( winningT1.isActive( ) ? winningT1.getTeam( ).getId( ) : Teams.BYE_TEAM.getId( ) );
            match.setRightTeamId( winningT2.isActive( ) ? winningT2.getTeam( ).getId( ) : Teams.BYE_TEAM.getId( ) );
            setDefaultByeScore( match, tournament );
            matches.add( match );
        }

        return matches;
    }

    private void setDefaultByeScore( final Match match, final ObservableTournament tournament ) {
        final boolean byeLeft = match.getLeftTeamId( ) == Teams.BYE_TEAM.getId( );
        final boolean byeRight = match.getRightTeamId( ) == Teams.BYE_TEAM.getId( );
        if ( byeLeft || byeRight ) {
            match.setScoreLeft( byeRight ? tournament.getHeader( ).getScoreMax( ) : 0 );
            match.setScoreDraw( 0 );
            match.setScoreRight( byeLeft ? tournament.getHeader( ).getScoreMax( ) : 0 );
        } else {
            // TODO REMOVE THAT
            match.setScoreLeft( 1 );
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
        round.setTimer( new TimerInfo( tournament.getHeader( ).getMatchDuration( ) ) );
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
        round.setTimer( new TimerInfo( tournament.getHeader( ).getMatchDuration( ) ) );
        round.getMatches( ).addAll( matches );
        round.setFinal( isFinal );
        return round;
    }

    private final Random _random = new Random( );
}
