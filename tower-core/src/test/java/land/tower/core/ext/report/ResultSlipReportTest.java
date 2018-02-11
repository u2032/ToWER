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
class ResultSlipReportTest {

    @Inject
    private ResultSlipReport.Factory _factory;

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
        final Tournament tournament = initTournament( );
        final ObservableRound round = new ObservableRound( tournament.getRounds( ).get( 0 ) );
        final ResultSlipReport report = _factory.create( new ObservableTournament( tournament ), round );
        // Exercice
        final Map<String, Object> parameters = report.getParameters( );
        // Verify
        assertThat( parameters ).containsKey( "page.title" );
        assertThat( parameters ).containsKey( "tournament.title" );
        assertThat( parameters ).containsKey( "match.position" );
        assertThat( parameters ).containsKey( "match.team.left" );
        assertThat( parameters ).containsKey( "match.team.right" );
        assertThat( parameters ).containsKey( "draws" );
        assertThat( parameters ).containsKey( "victory" );
        assertThat( parameters ).containsKey( "round" );
        assertThat( parameters ).containsKey( "round.numero" );
        assertThat( parameters ).containsKey( "page" );
    }

    @Test
    @DisplayName( "Data source has expected match sorted for guillotine" )
    void dataSourceTest( ) throws Exception {
        // Setup
        final Tournament tournament = initTournament( );

        final ObservableTournament oTournament = new ObservableTournament( tournament );
        final ObservableRound round = new ObservableRound( tournament.getRounds( ).get( 0 ) );
        final ResultSlipReport ladderReport = _factory.create( oTournament, round );
        // Exercice
        final JRMatchListDataSource dataSource = ladderReport.getJRDataSource( );
        // Verify
        assertThat( dataSource.getMatches( ).size( ) ).isEqualTo( 6 );
        assertThat( dataSource.getMatches( ).get( 0 ).getPosition( ) ).isEqualTo( 1 );
        assertThat( dataSource.getMatches( ).get( 1 ).getPosition( ) ).isEqualTo( 2 );
        assertThat( dataSource.getMatches( ).get( 2 ) ).isNull( );
        assertThat( dataSource.getMatches( ).get( 3 ) ).isNull( );
        assertThat( dataSource.getMatches( ).get( 4 ) ).isNull( );
        assertThat( dataSource.getMatches( ).get( 5 ) ).isNull( );
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