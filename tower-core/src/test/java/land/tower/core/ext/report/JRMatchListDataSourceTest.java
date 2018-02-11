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

package land.tower.core.ext.report;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertyExpression;

import java.util.Comparator;
import land.tower.core.TestTournament;
import land.tower.core.model.tournament.ObservableMatch;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.data.Match;
import land.tower.data.Round;
import land.tower.data.Team;
import land.tower.data.Tournament;

/**
 * Created on 11/02/2018
 * @author Cédric Longo
 */
class JRMatchListDataSourceTest {

    @Test
    @DisplayName( "Field are corectly returned" )
    void fieldsTest( ) throws Exception {
        // Setup
        final ObservableTournament oTournament = new ObservableTournament( initTournament( ) );
        final ObservableRound round = oTournament.getRounds( ).get( 0 );
        final JRMatchListDataSource dataSource =
            new JRMatchListDataSource( oTournament, round.getMatches( ),
                                       Comparator.comparingInt( ObservableMatch::getPosition ) );
        // Exercice
        dataSource.next( );
        // Verify
        assertThat( dataSource.getFieldValue( jrField( "position" ) ) ).isEqualTo( 1 );
        assertThat( dataSource.getFieldValue( jrField( "team.left" ) ) ).isEqualTo( "team3" );
        assertThat( dataSource.getFieldValue( jrField( "team.right" ) ) ).isEqualTo( "team4" );
    }

    private JRField jrField( final String name ) {
        return new JRField( ) {
            @Override
            public String getName( ) {
                return name;
            }

            @Override
            public String getDescription( ) {
                return null;
            }

            @Override
            public void setDescription( final String description ) {

            }

            @Override
            public Class<?> getValueClass( ) {
                return null;
            }

            @Override
            public String getValueClassName( ) {
                return null;
            }

            @Override
            public JRPropertyExpression[] getPropertyExpressions( ) {
                return new JRPropertyExpression[0];
            }

            @Override
            public boolean hasProperties( ) {
                return false;
            }

            @Override
            public JRPropertiesMap getPropertiesMap( ) {
                return null;
            }

            @Override
            public JRPropertiesHolder getParentProperties( ) {
                return null;
            }

            @Override
            public Object clone( ) {
                return null;
            }
        };
    }

    private Tournament initTournament( ) {
        final Tournament tournament = TestTournament.load( "test/new_tournament.twr" );

        final Team team1 = new Team( );
        team1.setName( "team1" );
        team1.setId( 1 );
        team1.getRanking( ).setRank( 2 );

        final Team team2 = new Team( );
        team2.setName( "team2" );
        team2.getRanking( ).setRank( 1 );
        team2.setId( 2 );

        final Team team3 = new Team( );
        team3.setName( "team3" );
        team3.getRanking( ).setRank( 3 );
        team3.setId( 3 );

        final Team team4 = new Team( );
        team4.setName( "team4" );
        team4.getRanking( ).setRank( 3 );
        team4.setId( 4 );

        final Round round = new Round( );

        final Match match1 = new Match( );
        match1.setPosition( 2 );
        match1.setLeftTeamId( 1 );
        match1.setRightTeamId( 2 );

        final Match match2 = new Match( );
        match2.setLeftTeamId( 3 );
        match2.setRightTeamId( 4 );
        match2.setPosition( 1 );

        round.getMatches( ).add( match1 );
        round.getMatches( ).add( match2 );

        tournament.getTeams( ).add( team1 );
        tournament.getTeams( ).add( team2 );
        tournament.getTeams( ).add( team3 );
        tournament.getTeams( ).add( team4 );

        tournament.getRounds( ).add( round );
        return tournament;
    }
}