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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import land.tower.core.model.pairing.PairingSystem;
import land.tower.data.Match;
import land.tower.data.Round;
import land.tower.data.Team;
import land.tower.data.Teams;
import land.tower.data.Tournament;

/**
 * Created on 30/12/2017
 * @author Cédric Longo
 */
public final class SwissPairingSystem implements PairingSystem {

    @Override
    public Round createNewRound( final Tournament tournament ) {
        if ( tournament.getRounds( ).isEmpty( ) ) {
            return firstRound( tournament.getTeams( ) );
        }
        throw new UnsupportedOperationException( "Not yet implemented" );
    }

    private Round firstRound( final List<Team> teams ) {
        final Round round = new Round( );
        round.setNumero( 1 );
        round.setStartDate( ZonedDateTime.now( ) );

        final List<Team> availableTeams = new ArrayList<>( teams.stream( )
                                                                .filter( Team::isActive )
                                                                .collect( Collectors.toList( ) ) );

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
            match.setLeftTeamId( availableTeams.get( 0 ).getId( ) );
            match.setRightTeamId( Teams.BYE_TEAM.getId( ) );
            round.getMatches( ).add( match );
        }

        return round;
    }

    private final Random _random = new Random( );
}
