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

import com.google.inject.Guice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import javax.inject.Inject;
import land.tower.core.TestTournament;
import land.tower.core.ext.config.ConfigurationModule;
import land.tower.core.ext.i18n.I18nModule;
import land.tower.core.ext.thread.ThreadingModule;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.data.Team;
import land.tower.data.Tournament;

/**
 * Created on 11/02/2018
 * @author Cédric Longo
 */
class LadderReportTest {

    @Inject
    private LadderReport.Factory _factory;

    @BeforeEach
    void setUp( ) {
        Guice.createInjector( new ReportModule( ), new ConfigurationModule( ),
                              new ThreadingModule( ), new I18nModule( ) )
             .injectMembers( this );
    }

    @Test
    @DisplayName( "Parameters have expected keys" )
    void paramatersTest( ) throws Exception {
        // Setup
        final Tournament tournament = TestTournament.load( "test/new_tournament.twr" );
        final LadderReport ladderReport = _factory.create( new ObservableTournament( tournament ) );
        // Exercice
        final Map<String, Object> parameters = ladderReport.getParameters( );
        // Verify
        assertThat( parameters ).containsKey( "page.title" );
        assertThat( parameters ).containsKey( "tournament.title" );
        assertThat( parameters ).containsKey( "team.rank" );
        assertThat( parameters ).containsKey( "team.name" );
        assertThat( parameters ).containsKey( "team.points" );
        assertThat( parameters ).containsKey( "page" );
    }

    @Test
    @DisplayName( "Data source has expected teams sorted by rank" )
    void dataSourceTest( ) throws Exception {
        // Setup
        final Tournament tournament = TestTournament.load( "test/new_tournament.twr" );
        final Team team1 = new Team( );
        team1.setId( 1 );
        team1.getRanking( ).setRank( 2 );

        final Team team2 = new Team( );
        team2.getRanking( ).setRank( 1 );
        team2.setId( 2 );

        tournament.getTeams( ).add( team1 );
        tournament.getTeams( ).add( team2 );
        final ObservableTournament oTournament = new ObservableTournament( tournament );
        final LadderReport ladderReport = _factory.create( oTournament );
        // Exercice
        final JRTeamListDataSource dataSource = ladderReport.getJRDataSource( );
        // Verify
        assertThat( dataSource.getTeams( ) ).contains( oTournament.getTeam( 2 ),
                                                       oTournament.getTeam( 1 ) );
    }
}