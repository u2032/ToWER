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

package land.tower.core.view.tournament.detail.round;

import static javafx.collections.FXCollections.observableArrayList;

import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.rules.ITournamentRulesProvider;
import land.tower.core.model.rules.PairingRule;
import land.tower.core.model.rules.TournamentRules;
import land.tower.core.model.tournament.ObservableMatch;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTeam;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.view.event.InformationEvent;
import land.tower.data.Match;
import land.tower.data.PairingMode;
import land.tower.data.Teams;

/**
 * Created on 25/01/2018
 * @author Cédric Longo
 */
public final class ManualPairingDialogModel {

    @Inject
    public ManualPairingDialogModel( @Assisted final ObservableTournament tournament,
                                     @Assisted final ObservableRound round,
                                     final I18nTranslator i18n,
                                     final Stage owner, final EventBus eventBus,
                                     final ITournamentRulesProvider tournamentRules,
                                     final Map<PairingMode, PairingRule> pairingRules ) {
        _tournament = tournament;
        _round = round;
        _i18n = i18n;
        _owner = owner;
        _eventBus = eventBus;
        _tournamentRules = tournamentRules;
        _pairingRules = pairingRules;

        _matches = observableArrayList( _round.getMatches( ) );
        _teams = observableArrayList( tournament.getTeams( )
                                                .stream( )
                                                .filter( ObservableTeam::isActive )
                                                .filter( t -> !_round.getMatchFor( t ).isPresent( ) )
                                                .collect( Collectors.toList( ) ) );
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    public synchronized void fireSavePairing( ) {
        if ( !_matches.isEmpty( ) ) {
            final AtomicInteger position = new AtomicInteger( );
            _matches.stream( )
                    .sorted( Comparator.comparing( ObservableMatch::getPosition ) )
                    .forEach( m -> m.setPosition( position.incrementAndGet( ) ) );

            _round.getMatches( ).setAll( _matches );

            _pairingRules.get( _tournament.getHeader( ).getPairingMode( ) )
                         .getPairingSystem( )
                         .roundValidity( _round );

            if ( _round.isEnded( ) ) {
                // If the round is ended, trigger ranking computing
                _tournamentRules.forGame( _tournament.getHeader( ).getGame( ) )
                                .getPairingRules( )
                                .get( _tournament.getHeader( ).getPairingMode( ) )
                                .getRankingComputer( )
                                .computeRanking( _tournament );

            }

            _eventBus.post( new InformationEvent( _i18n.get( "manuel.pairing.done" ) ) );
        }
    }

    public ObservableTournament getTournament( ) {
        return _tournament;
    }

    public ObservableRound getRound( ) {
        return _round;
    }

    public ObservableValue<ObservableList<ObservableMatch>> matchesProperty( ) {
        return new SimpleListProperty<>( _matches );
    }

    public ObservableValue<ObservableList<ObservableTeam>> teamsProperty( ) {
        return new SimpleListProperty<>( _teams );
    }

    public synchronized void firePairTeams( final ObservableList<ObservableTeam> obsTeams ) {
        final List<ObservableTeam> teams = new ArrayList<>( obsTeams );
        teams.removeIf( Objects::isNull );
        while ( teams.size( ) >= 2 ) {
            final ObservableTeam left = teams.remove( _random.nextInt( teams.size( ) ) );
            final ObservableTeam right = teams.remove( _random.nextInt( teams.size( ) ) );
            final Match match = new Match( );
            match.setLeftTeamId( left.getId( ) );
            match.setRightTeamId( right.getId( ) );
            match.setPosition( !_freePositions.isEmpty( ) ? _freePositions.pollFirst( )
                                                          : _matches
                                                                .stream( )
                                                                .mapToInt( ObservableMatch::getPosition )
                                                                .max( )
                                                                .orElse( 0 ) + 1 );
            _matches.add( new ObservableMatch( match ) );
            _teams.remove( left );
            _teams.remove( right );
        }
    }

    public synchronized void fireUnpairMatches( final ObservableList<ObservableMatch> obsMatches ) {
        final List<ObservableMatch> matches = new ArrayList<>( obsMatches );
        matches.removeIf( Objects::isNull );
        _matches.removeAll( matches );
        matches.forEach( m -> {
            _freePositions.add( m.getPosition( ) );
            final ObservableTeam leftTeam = _tournament.getTeam( m.getLeftTeamId( ) );
            if ( leftTeam.getId( ) != Teams.BYE_TEAM.getId( ) ) {
                _teams.add( leftTeam );
            }
            final ObservableTeam rightTeam = _tournament.getTeam( m.getRightTeamId( ) );
            if ( rightTeam.getId( ) != Teams.BYE_TEAM.getId( ) ) {
                _teams.add( rightTeam );
            }
        } );
    }

    public synchronized void firePairByeTeams( final ObservableList<ObservableTeam> obsTeams ) {
        final TournamentRules tournamentRules = _tournamentRules.forGame( _tournament.getHeader( ).getGame( ) );

        final List<ObservableTeam> teams = new ArrayList<>( obsTeams );
        teams.removeIf( Objects::isNull );
        while ( !teams.isEmpty( ) ) {
            final ObservableTeam left = teams.remove( _random.nextInt( teams.size( ) ) );
            final Match match = new Match( );
            match.setLeftTeamId( left.getId( ) );
            match.setRightTeamId( Teams.BYE_TEAM.getId( ) );
            //noinspection ConstantConditions
            match.setPosition( !_freePositions.isEmpty( ) ? _freePositions.pollFirst( )
                                                          : _matches
                                                                .stream( )
                                                                .mapToInt( ObservableMatch::getPosition )
                                                                .max( )
                                                                .orElse( 0 ) + 1 );
            match.setScoreLeft( tournamentRules.getByeScore( ).orElse( _tournament.getHeader( ).getScoreMax( ) ) );
            match.setScoreDraw( 0 );
            match.setScoreRight( 0 );

            if ( _tournament.getHeader( ).getDoubleScore( ) ) {
                match.setScoreLeftBis( tournamentRules.getByeScoreBis( )
                                                      .orElse( _tournament.getHeader( ).getScoreMaxBis( ) ) );
            }

            _matches.add( new ObservableMatch( match ) );
            _teams.remove( left );
        }
    }

    public Stage getOwner( ) {
        return _owner;
    }

    public interface Factory {

        ManualPairingDialogModel forRound( ObservableTournament tournament, ObservableRound round );
    }

    private final ObservableTournament _tournament;
    private final ObservableRound _round;

    private final I18nTranslator _i18n;
    private final Stage _owner;

    private final ObservableList<ObservableMatch> _matches;
    private final ObservableList<ObservableTeam> _teams;

    private final TreeSet<Integer> _freePositions = new TreeSet<>( );
    private final EventBus _eventBus;
    private final ITournamentRulesProvider _tournamentRules;

    private final Map<PairingMode, PairingRule> _pairingRules;

    private static final Random _random = new Random( );
}
