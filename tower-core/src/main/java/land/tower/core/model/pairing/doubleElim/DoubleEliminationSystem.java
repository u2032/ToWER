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

import static land.tower.data.Teams.BYE_TEAM;

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

        final boolean isFinal = lastRoundMatches.size( ) == 1
                                && ( lastRoundMatches.get( 0 ).getLeftTeamId( ) == Teams.BYE_TEAM.getId( )
                                     || lastRoundMatches.get( 0 ).getRightTeamId( ) == Teams.BYE_TEAM.getId( )

                                     || ( computeDefeatCount( tournament,
                                                              tournament.getTeam( lastRoundMatches.get( 0 )
                                                                                                  .getLeftTeamId( ) ) )
                                          + computeDefeatCount( tournament,
                                                                tournament.getTeam( lastRoundMatches.get( 0 )
                                                                                                    .getRightTeamId( ) ) ) >= 2 ) );

        final boolean isPreFinal = lastRoundMatches.size( ) == 1
                                   || ( tournament.getRounds( ).size( ) > 2
                                        && tournament.getRounds( ).stream( )
                                                     .sorted( Comparator.comparing( ObservableRound::getNumero ) )
                                                     .skip( tournament.getRounds( ).size( ) - 2 )
                                                     .limit( 1 )
                                                     .findFirst( )
                                                     .orElseThrow( IllegalStateException::new )
                                                     .getMatches( ).size( ) <= 2 );

        if ( isFinal ) {
            final int leftDefeatsCount = computeDefeatCount( tournament,
                                                             tournament.getTeam( lastRoundMatches.get( 0 )
                                                                                                 .getLeftTeamId( ) ) );
            final int rightDefeatsCount = computeDefeatCount( tournament,
                                                              tournament.getTeam( lastRoundMatches.get( 0 )
                                                                                                  .getRightTeamId( ) ) );
            final Match match;
            if ( leftDefeatsCount >= 2 ) {
                match = new Match( );
                match.setLeftTeamId( lastRoundMatches.get( 0 ).getRightTeamId( ) );
                tournament.getTeam( lastRoundMatches.get( 0 ).getRightTeamId( ) )
                          .getPairingFlags( ).put( "final", "true" );
                match.setRightTeamId( Teams.BYE_TEAM.getId( ) );

            } else if ( rightDefeatsCount >= 2 ) {
                match = new Match( );
                match.setLeftTeamId( lastRoundMatches.get( 0 ).getLeftTeamId( ) );
                tournament.getTeam( lastRoundMatches.get( 0 ).getLeftTeamId( ) )
                          .getPairingFlags( ).put( "final", "true" );
                match.setRightTeamId( Teams.BYE_TEAM.getId( ) );

            } else {
                // 1 & 1
                match = new Match( );
                match.setLeftTeamId( lastRoundMatches.get( 0 ).getLeftTeamId( ) );
                tournament.getTeam( lastRoundMatches.get( 0 ).getLeftTeamId( ) )
                          .getPairingFlags( ).put( "final", "true" );

                match.setRightTeamId( lastRoundMatches.get( 0 ).getRightTeamId( ) );
                tournament.getTeam( lastRoundMatches.get( 0 ).getRightTeamId( ) )
                          .getPairingFlags( ).put( "final", "true" );
            }

            match.setPosition( 1 );
            setDefaultByeScore( match, tournament );

            final Round round = new Round( );
            round.setNumero( tournament.getRounds( ).size( ) + 1 );
            round.setTimer( new TimerInfo( tournament.getHeader( ).getMatchDuration( ) ) );
            round.getMatches( ).add( match );
            round.setFinal( true );
            return round;
        }

        if ( isPreFinal ) {
            final Match match;

            if ( lastRoundMatches.size( ) == 1 ) {
                match = new Match( );
                match.setLeftTeamId( lastRoundMatches.get( 0 ).getLeftTeamId( ) );
                match.setRightTeamId( lastRoundMatches.get( 0 ).getRightTeamId( ) );
                match.setPosition( 1 );
                setDefaultByeScore( match, tournament );

            } else {
                // Pairing of winners from winner and looser bracket
                final ObservableTeam winner1 = tournament.getTeam( getWinningTeam( lastRoundMatches.get( 0 ) ) );
                final ObservableTeam winner2 = tournament.getTeam( getWinningTeam( lastRoundMatches.get( 1 ) ) );

                match = new Match( );
                match.setLeftTeamId( winner1.getId( ) );
                match.setRightTeamId( winner2.getId( ) );
                match.setPosition( 1 );
                setDefaultByeScore( match, tournament );
            }

            final Round round = new Round( );
            round.setNumero( tournament.getRounds( ).size( ) + 1 );
            round.setTimer( new TimerInfo( tournament.getHeader( ).getMatchDuration( ) ) );
            round.getMatches( ).add( match );
            round.setFinal( false );
            return round;
        }

        final List<Match> matches = new ArrayList<>( );

        if ( lastRound.getNumero( ) == 1 ) { // Pairing of round #2
            // Pair winners with bye and init looser bracket
            final ArrayList<Match> winnerBracketMatches = new ArrayList<>( );
            final ArrayList<Match> looserBracketMatches = new ArrayList<>( );
            for ( int i = 0; i < lastRoundMatches.size( ); i += 2 ) {
                final ObservableTeam winner1 = tournament.getTeam( getWinningTeam( lastRoundMatches.get( i ) ) );
                final ObservableTeam looser1 = tournament.getTeam( lastRoundMatches.get( i ).getOpponentId( winner1 ) );

                final ObservableTeam winner2 = tournament.getTeam( getWinningTeam( lastRoundMatches.get( i + 1 ) ) );
                final ObservableTeam looser2 = tournament.getTeam( lastRoundMatches.get( i + 1 )
                                                                                   .getOpponentId( winner2 ) );

                winnerBracketMatches.add( createMatch( tournament, winner1, winner2 ) );
                looserBracketMatches.add( createMatch( tournament, looser1, looser2 ) );
            }

            matches.addAll( winnerBracketMatches );
            matches.addAll( looserBracketMatches );

        } else if ( lastRound.getNumero( ) == 2 ) { // Pairing of round #3
            final List<ObservableMatch> winnerBracket = lastRoundMatches.subList( 0, lastRoundMatches.size( ) / 2 );
            final List<ObservableMatch> looserBracket = lastRoundMatches.subList( lastRoundMatches.size( ) / 2,
                                                                                  lastRoundMatches.size( ) );

            final List<ObservableTeam> loosersFromWinnerBracket = new ArrayList<>( );

            if ( winnerBracket.size( ) == 1 ) {
                winnerBracket.forEach( m -> {
                    final ObservableTeam winner = tournament.getTeam( getWinningTeam( m ) );
                    matches.add( createMatch( tournament, winner, ObservableTeam.BYE_TEAM ) );

                    final ObservableTeam looser = tournament.getTeam( m.getOpponentId( winner ) );
                    loosersFromWinnerBracket.add( looser );
                } );

            } else {
                for ( int i = 0; i < winnerBracket.size( ); i += 2 ) {
                    final ObservableTeam winner1 = tournament.getTeam( getWinningTeam( winnerBracket.get( i ) ) );
                    final ObservableTeam looser1 = tournament
                                                       .getTeam( winnerBracket.get( i ).getOpponentId( winner1 ) );

                    final ObservableTeam winner2 = tournament.getTeam( getWinningTeam( winnerBracket.get( i + 1 ) ) );
                    final ObservableTeam looser2 = tournament.getTeam( winnerBracket.get( i + 1 )
                                                                                    .getOpponentId( winner2 ) );

                    matches.add( createMatch( tournament, winner1, ObservableTeam.BYE_TEAM ) );
                    matches.add( createMatch( tournament, winner2, ObservableTeam.BYE_TEAM ) );
                    loosersFromWinnerBracket.add( looser1 );
                    loosersFromWinnerBracket.add( looser2 );
                }
            }

            // Criss cross
            Collections.reverse( loosersFromWinnerBracket );

            for ( int i = 0; i < loosersFromWinnerBracket.size( ); i++ ) {
                final ObservableTeam winner = tournament.getTeam( getWinningTeam( looserBracket.get( i ) ) );
                matches.add( createMatch( tournament, loosersFromWinnerBracket.get( i ), winner ) );
            }

        } else {
            final List<ObservableMatch> winnerBracket = lastRoundMatches.subList( 0, lastRoundMatches.size( ) / 2 );
            final List<ObservableMatch> looserBracket = lastRoundMatches.subList( lastRoundMatches.size( ) / 2,
                                                                                  lastRoundMatches.size( ) );

            if ( winnerBracket.size( ) == 1 || lastRound.getNumero( ) % 2 == 0 ) {
                final ArrayList<ObservableTeam> loosersFromWinnerBracket = new ArrayList<>( );

                winnerBracket.forEach( m -> {
                    final ObservableTeam winner = tournament.getTeam( getWinningTeam( m ) );
                    matches.add( createMatch( tournament, winner, ObservableTeam.BYE_TEAM ) );

                    final ObservableTeam looser = tournament.getTeam( m.getOpponentId( winner ) );
                    loosersFromWinnerBracket.add( looser );
                } );

                // Criss cross
                if ( lastRound.getNumero( ) % 8 == 2 ) {
                    Collections.reverse( loosersFromWinnerBracket );

                } else if ( lastRound.getNumero( ) % 8 == 4 ) {
                    final List<ObservableTeam> tmp = new ArrayList<>( loosersFromWinnerBracket );
                    loosersFromWinnerBracket.clear( );

                    final List<ObservableTeam> part = new ArrayList<>( tmp.subList( 0, tmp.size( ) / 2 ) );
                    Collections.reverse( part );
                    loosersFromWinnerBracket.addAll( part );
                    part.clear( );

                    part.addAll( new ArrayList<>( tmp.subList( tmp.size( ) / 2, tmp.size( ) ) ) );
                    Collections.reverse( part );
                    loosersFromWinnerBracket.addAll( part );

                } else if ( lastRound.getNumero( ) % 8 == 6 ) {
                    final List<ObservableTeam> tmp = new ArrayList<>( loosersFromWinnerBracket );
                    loosersFromWinnerBracket.clear( );
                    loosersFromWinnerBracket.addAll( tmp.subList( tmp.size( ) / 2, tmp.size( ) ) );
                    loosersFromWinnerBracket.addAll( tmp.subList( 0, tmp.size( ) / 2 ) );
                }

                for ( int i = 0; i < loosersFromWinnerBracket.size( ); i++ ) {
                    final ObservableTeam winner = tournament.getTeam( getWinningTeam( looserBracket.get( i ) ) );
                    matches.add( createMatch( tournament, loosersFromWinnerBracket.get( i ), winner ) );
                }

            } else {
                // Pair winners and pair loosers
                for ( int i = 0; i < winnerBracket.size( ); i += 2 ) {
                    final ObservableTeam winner1 = tournament.getTeam( getWinningTeam( winnerBracket.get( i ) ) );
                    final ObservableTeam winner2 = tournament.getTeam( getWinningTeam( winnerBracket.get( i + 1 ) ) );
                    matches.add( createMatch( tournament, winner1, winner2 ) );
                }

                for ( int i = 0; i < looserBracket.size( ); i += 2 ) {
                    final ObservableTeam winner1 = tournament.getTeam( getWinningTeam( looserBracket.get( i ) ) );
                    final ObservableTeam winner2 = tournament.getTeam( getWinningTeam( looserBracket.get( i + 1 ) ) );
                    matches.add( createMatch( tournament, winner1, winner2 ) );
                }
            }
        }

        // Set position
        final AtomicInteger position = new AtomicInteger( );
        matches.forEach( m -> m.setPosition( position.incrementAndGet( ) ) );

        final Round round = new Round( );
        round.setNumero( tournament.getRounds( ).size( ) + 1 );
        round.setTimer( new TimerInfo( tournament.getHeader( ).getMatchDuration( ) ) );
        round.getMatches( ).addAll( matches );
        round.setFinal( false );
        return round;
    }

    private Match createMatch( final ObservableTournament tournament, final ObservableTeam left,
                               final ObservableTeam right ) {
        final Match match = new Match( );
        match.setLeftTeamId( tournament.getTeam( left.getId( ) ).isActive( ) ? left.getId( ) : BYE_TEAM.getId( ) );
        match.setRightTeamId( tournament.getTeam( right.getId( ) ).isActive( ) ? right.getId( ) : BYE_TEAM.getId( ) );
        setDefaultByeScore( match, tournament );
        return match;
    }

    private void setDefaultByeScore( final Match match, final ObservableTournament tournament ) {
        final boolean byeLeft = match.getLeftTeamId( ) == BYE_TEAM.getId( );
        final boolean byeRight = match.getRightTeamId( ) == BYE_TEAM.getId( );
        if ( byeLeft || byeRight ) {
            match.setScoreLeft( byeRight ? tournament.getHeader( ).getScoreMax( ) : 0 );
            match.setScoreDraw( 0 );
            match.setScoreRight( byeLeft ? tournament.getHeader( ).getScoreMax( ) : 0 );
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

        final Round round = new Round( );
        round.setNumero( tournament.getRounds( ).size( ) + 1 );
        round.setTimer( new TimerInfo( tournament.getHeader( ).getMatchDuration( ) ) );
        round.getMatches( ).addAll( Arrays.asList( matches ) );
        round.setFinal( false );
        return round;
    }

    @Override
    public void roundValidity( final ObservableRound round ) {
        // Complete with bye matches
        final AtomicInteger position = new AtomicInteger( round.getMatches( ).stream( )
                                                               .mapToInt( ObservableMatch::getPosition )
                                                               .max( )
                                                               .orElse( 1 ) );

        int teamCount = round.getMatches( ).size( ) * 2;
        double p = Math.floor( Math.log( teamCount ) / Math.log( 2 ) );
        final int maxTeamCountLower = (int) Math.pow( 2, p );
        final int maxTeamCountUpper =
            teamCount > maxTeamCountLower ? (int) Math.pow( 2, p + 1 ) : maxTeamCountLower;
        final int matchCount = maxTeamCountUpper / 2;
        while ( round.getMatches( ).size( ) < matchCount ) {
            final Match match = new Match( );
            match.setLeftTeamId( Teams.BYE_TEAM.getId( ) );
            match.setRightTeamId( Teams.BYE_TEAM.getId( ) );
            match.setScoreLeft( 1 );
            match.setScoreRight( 1 );
            match.setPosition( position.incrementAndGet( ) );
            round.getMatches( ).add( new ObservableMatch( match ) );
        }

        round.setFinal( false );
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
        return array[max - 1 - index] == null ? BYE_TEAM.getId( ) : array[max - 1 - index].getId( );
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

        final List<Match> matches = new ArrayList<>( );

        final AtomicInteger position = new AtomicInteger( );
        while ( teams.size( ) > 1 && matches.size( ) * 2 < initialTeamCount - byeCount ) {
            final ObservableTeam left = teams.remove( _random.nextInt( teams.size( ) ) );
            final ObservableTeam right = teams.remove( _random.nextInt( teams.size( ) ) );

            final Match match = new Match( );
            match.setPosition( position.incrementAndGet( ) );
            match.setLeftTeamId( left.getId( ) );
            match.setRightTeamId( right.getId( ) );
            matches.add( match );
        }

        while ( !teams.isEmpty( ) ) {
            final ObservableTeam left = teams.remove( _random.nextInt( teams.size( ) ) );

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
        round.setFinal( false );
        return round;
    }

    private int computeDefeatCount( ObservableTournament tournament, ObservableTeam team ) {
        final List<ObservableRound> rounds = tournament.getRounds( )
                                                       .stream( )
                                                       .sorted( Comparator.comparing( ObservableRound::getNumero ) )
                                                       .collect( Collectors.toList( ) );
        final AtomicInteger count = new AtomicInteger( );

        for ( int i = 0; i < rounds.size( ); i++ ) {
            final ObservableRound round = rounds.get( i );
            final ObservableRound nextRound = i + 1 < rounds.size( ) ? rounds.get( i + 1 ) : null;
            if ( !round.isEnded( ) ) {
                continue;
            }

            round.getMatchFor( team )
                 .ifPresent( match -> {
                     if ( match.hasLost( team ) ) {
                         count.incrementAndGet( );
                     }

                     if ( match.isDraw( ) && nextRound != null ) {
                         if ( !nextRound.getMatchFor( team ).isPresent( ) ) {
                             count.incrementAndGet( );
                         }
                     }
                 } );
        }
        return count.get( );
    }

    private final Random _random = new Random( );
}
