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

package land.tower.core.model.pairing.robin;

import static land.tower.data.Teams.BYE_TEAM;

import java.util.ArrayList;
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
import land.tower.data.Team;
import land.tower.data.Teams;
import land.tower.data.TimerInfo;

/**
 * Created on 02/01/2018
 * @author Cédric Longo
 */
public final class RoundRobinSystem implements PairingSystem {

    @Inject
    public RoundRobinSystem( ) {
    }

    @Override
    public Round createNewRound( final ObservableTournament tournament ) {
        if ( tournament.getRounds( ).isEmpty( ) ) {
            return firstRound( tournament );
        }

        final ObservableRound lastRound = tournament.getRounds( ).stream( )
                                                    .max( Comparator.comparing( ObservableRound::getNumero ) )
                                                    .orElseThrow( IllegalStateException::new );

        final List<ObservableMatch> lastRoundMatches =
            lastRound.getMatches( )
                     .stream( )
                     .sorted( Comparator.comparing( ObservableMatch::getPosition ) )
                     .collect( Collectors.toList( ) );

        final ArrayList<Match> matches = new ArrayList<>( );

        for ( int i = 1; i < lastRoundMatches.size( ) - 1; i++ ) {
            final ObservableMatch match1 = lastRoundMatches.get( i - 1 );
            final ObservableMatch match2 = lastRoundMatches.get( i + 1 );

            matches.add( createMatch( tournament,
                                      tournament.getTeam( i - 1 == 0 ? match1.getRightTeamId( )
                                                                     : match1.getLeftTeamId( ) ),
                                      tournament.getTeam( match2.getRightTeamId( ) ) ) );
        }

        // Last match for borders
        final ObservableMatch match1 = lastRoundMatches.get( 0 );
        final ObservableMatch match2 = lastRoundMatches.get( 1 );
        matches.add( 0, createMatch( tournament,
                                     tournament.getTeam( match1.getLeftTeamId( ) ),
                                     tournament.getTeam( match2.getRightTeamId( ) ) ) );

        final ObservableMatch match3 = lastRoundMatches.get( lastRoundMatches.size( ) - 2 );
        final ObservableMatch match4 = lastRoundMatches.get( lastRoundMatches.size( ) - 1 );
        matches.add( createMatch( tournament,
                                  tournament.getTeam( lastRoundMatches.size( ) - 2 == 0 ? match3.getRightTeamId( )
                                                                                        : match3.getLeftTeamId( ) ),
                                  tournament.getTeam( match4.getLeftTeamId( ) ) ) );

        // Set position
        final AtomicInteger position = new AtomicInteger( );
        matches.forEach( m -> m.setPosition( position.incrementAndGet( ) ) );

        final Round round = new Round( );
        round.setNumero( tournament.getRounds( ).size( ) + 1 );
        round.setTimer( new TimerInfo( tournament.getHeader( ).getMatchDuration( ) ) );
        round.getMatches( ).addAll( matches );
        round.setFinal( round.getNumero( ) == ( matches.size( ) * 2 ) - 1 );
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
        return firstRound( tournament );
    }

    private Round firstRound( final ObservableTournament tournament ) {
        final Round round = new Round( );
        round.setNumero( 1 );
        round.setTimer( new TimerInfo( tournament.getHeader( ).getMatchDuration( ) ) );

        final List<Team> availableTeams = tournament.getTournament( ).getTeams( ).stream( )
                                                    .filter( Team::isActive )
                                                    .collect( Collectors.toList( ) );

        round.setFinal( availableTeams.size( ) <= 2 );

        final AtomicInteger position = new AtomicInteger( );
        while ( availableTeams.size( ) > 1 ) {
            final Team left = availableTeams.remove( _random.nextInt( availableTeams.size( ) ) );
            final Team right = availableTeams.remove( _random.nextInt( availableTeams.size( ) ) );

            final Match match = new Match( );
            match.setPosition( position.incrementAndGet( ) );
            match.setLeftTeamId( left.getId( ) );
            match.setRightTeamId( right.getId( ) );
            round.getMatches( ).add( match );
        }

        if ( !availableTeams.isEmpty( ) ) {
            final Match match = new Match( );
            match.setPosition( position.incrementAndGet( ) );
            match.setLeftTeamId( availableTeams.remove( 0 ).getId( ) );
            match.setRightTeamId( Teams.BYE_TEAM.getId( ) );
            match.setScoreLeft( tournament.getHeader( ).getScoreMax( ) );
            match.setScoreDraw( 0 );
            match.setScoreRight( 0 );
            round.getMatches( ).add( match );
        }

        return round;
    }

    private final Random _random = new Random( );
}
