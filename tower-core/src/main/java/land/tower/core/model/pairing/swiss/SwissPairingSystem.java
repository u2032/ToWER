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

package land.tower.core.model.pairing.swiss;

import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.util.Pair;
import javax.inject.Inject;
import land.tower.core.model.pairing.PairingSystem;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.data.Match;
import land.tower.data.Round;
import land.tower.data.Team;
import land.tower.data.Teams;
import land.tower.data.TimerInfo;
import land.tower.data.Tournament;

/**
 * Created on 30/12/2017
 * @author Cédric Longo
 */
public final class SwissPairingSystem implements PairingSystem {

    @Inject
    public SwissPairingSystem( ) {

    }

    @Override
    public Round createNewRound( final ObservableTournament tournament ) {
        if ( tournament.getRounds( ).isEmpty( ) ) {
            return firstRound( tournament.getTournament( ) );
        }

        return IntStream.rangeClosed( 1, 16 )
                        .unordered( )
                        .parallel( )
                        .mapToObj( i -> makePairing( tournament.getTournament( ) ) )
                        .min( Comparator.comparing( Pair::getKey ) )
                        .map( Pair::getValue )
                        .orElseThrow( IllegalStateException::new );
    }

    @Override
    public Round createFirstRoundFromInitialRanking( ObservableTournament tournament ) {
        return firstRound( tournament.getTournament( ) );
    }

    @Override
    public void roundValidity( final ObservableRound round ) {

    }

    private Pair<Integer, Round> makePairing( final Tournament tournament ) {
        final List<Match> matches = new ArrayList<>( );

        // Compute groups of teams with same score
        final LinkedHashMap<Integer, List<Team>> groups =
            tournament.getTeams( ).stream( )
                      .filter( Team::isActive )
                      .sorted( Comparator.comparing( t -> t.getRanking( ).getPoints( ), Comparator.reverseOrder( ) ) )
                      .collect( Collectors.groupingBy( t -> t.getRanking( ).getPoints( ),
                                                       LinkedHashMap::new,
                                                       Collectors.toList( ) ) );

        // Constructs a map teamId -> GroupNum
        final AtomicInteger groupNum = new AtomicInteger( );
        final Map<Integer, Integer> groupNums = new HashMap<>( );
        groups.forEach( ( points, teams ) -> {
            groupNum.incrementAndGet( );
            teams.forEach( t -> groupNums.put( t.getId( ), groupNum.get( ) ) );
        } );

        // Pairing in same group if possible
        final List<List<Team>> groupList = new ArrayList<>( groups.values( ) );
        while ( !groupList.isEmpty( ) ) {
            final List<Team> group = new ArrayList<>( groupList.remove( 0 ) );
            while ( group.size( ) > 1 ) {
                final Team left = group.remove( _random.nextInt( group.size( ) ) );
                final Team right = group.remove( _random.nextInt( group.size( ) ) );

                final Match match = new Match( );
                match.setLeftTeamId( left.getId( ) );
                match.setRightTeamId( right.getId( ) );
                matches.add( match );
            }

            // The last team on this group is added into next group
            if ( !group.isEmpty( ) && !groupList.isEmpty( ) ) {
                groupList.get( 0 ).add( 0, group.remove( 0 ) );
            }

            // The last team is paired with BYE
            if ( !group.isEmpty( ) && groupList.isEmpty( ) ) {
                final Match match = new Match( );
                match.setLeftTeamId( group.remove( 0 ).getId( ) );
                match.setRightTeamId( Teams.BYE_TEAM.getId( ) );
                matches.add( match );
            }
        }

        // Permutation of already played matches
        final AtomicInteger duplicates = new AtomicInteger( );
        for ( int i = 0; i < matches.size( ); i++ ) {
            final Match match = matches.get( i );
            if ( matchAlreadyExists( tournament, match.getLeftTeamId( ), match.getRightTeamId( ) ) ) {
                for ( int j = 1; j < matches.size( ); j++ ) {
                    final Match bMatch = matches.get( Math.max( 0, i - j ) );
                    if ( matchPermutation( tournament, match, bMatch ) ) {
                        break;
                    }
                    final Match aMatch = matches.get( Math.min( matches.size( ) - 1, i + j ) );
                    if ( matchPermutation( tournament, match, aMatch ) ) {
                        break;
                    }
                    // We fail to permute this duplicated match
                    duplicates.incrementAndGet( );
                }
            }
        }

        // Compute how many group permutation we have
        final AtomicInteger permutes = new AtomicInteger( );
        for ( int i = 0; i < matches.size( ); i++ ) {
            final Match match = matches.get( i );
            final int groupLeft = groupNums.getOrDefault( match.getLeftTeamId( ), groupNum.get( ) );
            final int groupRight = groupNums.getOrDefault( match.getRightTeamId( ), groupNum.get( ) );
            permutes.addAndGet( abs( ( groupNum.get( ) - groupLeft ) * ( groupNum.get( ) - groupLeft )
                                     - ( groupNum.get( ) - groupRight ) * ( groupNum.get( ) - groupRight ) ) );
        }

        // Initialize score for Bye teams and put them on the end
        final List<Match> byeMatches = matches.stream( )
                                              .filter( this::isByeMatch )
                                              .collect( Collectors.toList( ) );
        byeMatches.forEach( match -> {
            if ( match.getLeftTeamId( ) == Teams.BYE_TEAM.getId( ) ) {
                // We put the bye team on the right
                match.setLeftTeamId( match.getRightTeamId( ) );
                match.setRightTeamId( Teams.BYE_TEAM.getId( ) );
            }
            match.setScoreLeft( tournament.getHeader( ).getScoreMax( ) );
            match.setScoreDraw( 0 );
            match.setScoreRight( 0 );
        } );
        matches.removeAll( byeMatches );
        matches.addAll( byeMatches );

        // Set position
        final AtomicInteger position = new AtomicInteger( );
        matches.forEach( m -> m.setPosition( position.incrementAndGet( ) ) );

        final Round round = new Round( );
        round.setNumero( tournament.getRounds( ).size( ) + 1 );
        round.setTimer( new TimerInfo( tournament.getHeader( ).getMatchDuration( ) ) );
        round.getMatches( ).addAll( matches );

        return new Pair<>( computeScore( duplicates.get( ), permutes.get( ) ), round );
    }

    private int computeScore( final int duplicates, final int permutes ) {
        return duplicates * 1000 + permutes;
    }

    private boolean matchPermutation( final Tournament tournament, final Match match, final Match match2 ) {
        if ( isByeMatch( match ) && isByeMatch( match2 ) ) {
            return false;
        }

        if ( !matchAlreadyExists( tournament, match.getLeftTeamId( ), match2.getRightTeamId( ) )
             && !matchAlreadyExists( tournament, match.getRightTeamId( ), match2.getLeftTeamId( ) ) ) {

            // Permutation is possible
            final int matchLeftTeamId = match.getLeftTeamId( );
            final int matchRightTeamId = match.getRightTeamId( );
            final int match2LeftTeamId = match2.getLeftTeamId( );
            final int match2RightTeamId = match2.getRightTeamId( );
            match.setLeftTeamId( matchLeftTeamId );
            match.setRightTeamId( match2RightTeamId );
            match2.setLeftTeamId( match2LeftTeamId );
            match2.setRightTeamId( matchRightTeamId );
            return true;
        }
        return false;
    }

    private boolean isByeMatch( final Match match ) {
        return match.getLeftTeamId( ) == Teams.BYE_TEAM.getId( ) || match.getRightTeamId( ) == Teams.BYE_TEAM.getId( );
    }

    private Round firstRound( final Tournament tournament ) {
        final Round round = new Round( );
        round.setNumero( 1 );
        round.setTimer( new TimerInfo( tournament.getHeader( ).getMatchDuration( ) ) );

        final List<Team> availableTeams = tournament.getTeams( ).stream( )
                                                    .filter( Team::isActive )
                                                    .collect( Collectors.toList( ) );

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

    private boolean matchAlreadyExists( final Tournament tournament, final int leftId, final int rightId ) {
        return tournament.getRounds( )
                         .stream( )
                         .flatMap( t -> t.getMatches( ).stream( ) )
                         .anyMatch(
                             m -> ( m.getLeftTeamId( ) == leftId && m.getRightTeamId( ) == rightId )
                                  || ( m.getLeftTeamId( ) == rightId && m.getRightTeamId( ) == leftId ) );
    }

    private final Random _random = new Random( );
}
