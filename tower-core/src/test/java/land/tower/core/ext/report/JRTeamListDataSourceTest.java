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

import land.tower.core.TestTournament;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.data.Team;
import land.tower.data.Tournament;

/**
 * Created on 11/02/2018
 * @author Cédric Longo
 */
class JRTeamListDataSourceTest {

    @Test
    @DisplayName( "Field return expected values" )
    void fieldTest( ) throws Exception {
        // Setup
        final Tournament tournament = TestTournament.load( "test/new_tournament.twr" );
        final Team team1 = new Team( );
        team1.setId( 1 );
        team1.setName( "team1" );
        team1.getRanking( ).setRank( 2 );
        team1.getRanking( ).setD1( 5 );
        team1.getRanking( ).setD2( 6 );
        team1.getRanking( ).setD3( 7 );
        team1.getRanking( ).setD4( 8 );
        team1.getRanking( ).setPoints( 1000 );
        tournament.getTeams( ).add( team1 );
        final JRTeamListDataSource dataSource =
            new JRTeamListDataSource( new ObservableTournament( tournament ).getTeams( ) );
        // Exercice
        // Verify
        dataSource.next( );
        assertThat( dataSource.getFieldValue( jrField( "name" ) ) ).isEqualTo( "team1" );
        assertThat( dataSource.getFieldValue( jrField( "rank" ) ) ).isEqualTo( 2 );
        assertThat( dataSource.getFieldValue( jrField( "d1" ) ) ).isEqualTo( 5 );
        assertThat( dataSource.getFieldValue( jrField( "d2" ) ) ).isEqualTo( 6 );
        assertThat( dataSource.getFieldValue( jrField( "d3" ) ) ).isEqualTo( 7 );
        assertThat( dataSource.getFieldValue( jrField( "d4" ) ) ).isEqualTo( 8 );
        assertThat( dataSource.getFieldValue( jrField( "points" ) ) ).isEqualTo( 1000 );
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

}