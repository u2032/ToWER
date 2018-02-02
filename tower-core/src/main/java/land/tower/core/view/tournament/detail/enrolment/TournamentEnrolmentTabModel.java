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

package land.tower.core.view.tournament.detail.enrolment;

import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.stage.Stage;
import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.pairing.PairingRule;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTeam;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.view.event.InformationEvent;
import land.tower.core.view.event.TournamentUpdatedEvent;
import land.tower.data.PairingMode;
import land.tower.data.Round;
import land.tower.data.Team;

/**
 * Created on 20/12/2017
 * @author Cédric Longo
 */
public final class TournamentEnrolmentTabModel {

    public Stage getOwner( ) {
        return _owner;
    }

    public interface Factory {

        TournamentEnrolmentTabModel forTournament( final ObservableTournament tournament );

    }

    public ObservableTournament getTournament( ) {
        return _tournament;
    }

    public void fireTeamDeleted( final Team team ) {
        _tournament.getTeams( ).removeIf( t -> team.getId( ) == t.getId( ) );

        final AtomicInteger idGen = new AtomicInteger( 0 );
        _tournament.getTeams( )
                   .sorted( Comparator.comparing( ObservableTeam::getId ) )
                   .forEach( t -> t.setId( idGen.incrementAndGet( ) ) );
    }

    public AddTeamDialogModel newAddTeamDialogModel( ) {
        return _addTeamDialogProvider.forTournament( _tournament );
    }

    public void fireStartTournament( ) {
        _eventBus.post( new InformationEvent( _i18n.get( "round.generation.started" ) ) );
        final PairingRule pairing = _pairingSystems.get( _tournament.getHeader( ).getPairingMode( ) );
        final Round newRound = pairing.getPairingSystem( ).createNewRound( _tournament );
        _tournament.registerRound( new ObservableRound( newRound ) );
        _eventBus.post( new TournamentUpdatedEvent( _tournament ) );
        _eventBus.post( new InformationEvent( _i18n.get( "round.generation.finished", newRound.getNumero( ) ) ) );
    }

    public void fireTeamAdded( final Team team ) {
        _tournament.registerTeam( new ObservableTeam( team ) );
        _eventBus.post( new TournamentUpdatedEvent( _tournament ) );
        // Recompute ranking
        final PairingRule pairing = _pairingSystems.get( _tournament.getHeader( ).getPairingMode( ) );
        pairing.getRankingComputer( ).computeRanking( _tournament );
    }

    @Inject
    TournamentEnrolmentTabModel( final @Assisted ObservableTournament tournament, final I18nTranslator i18n,
                                 final AddTeamDialogModel.Factory addTeamDialogProvider,
                                 final Map<PairingMode, PairingRule> pairingSystems,
                                 final EventBus eventBus, final Stage owner ) {
        _i18n = i18n;
        _tournament = tournament;
        _addTeamDialogProvider = addTeamDialogProvider;
        _pairingSystems = pairingSystems;
        _eventBus = eventBus;
        _owner = owner;

        tournament.getTeams( )
                  .forEach( t -> t.activeProperty( ).addListener( ( observable, oldValue, newValue ) -> {
                      if ( !newValue ) {
                          _eventBus.post( new InformationEvent( _i18n.get( "tournament.team.dropped" ) ) );
                      }
                  } ) );
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    private final I18nTranslator _i18n;
    private final ObservableTournament _tournament;
    private final AddTeamDialogModel.Factory _addTeamDialogProvider;
    private final Map<PairingMode, PairingRule> _pairingSystems;
    private final EventBus _eventBus;
    private final Stage _owner;
}
