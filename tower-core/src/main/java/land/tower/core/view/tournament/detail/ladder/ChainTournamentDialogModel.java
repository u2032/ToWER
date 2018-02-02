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

package land.tower.core.view.tournament.detail.ladder;

import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;
import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.pairing.PairingRule;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.core.model.tournament.ObservableTeam;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.model.tournament.TournamentRepository;
import land.tower.core.view.event.SceneRequestedEvent;
import land.tower.core.view.tournament.detail.TournamentView;
import land.tower.core.view.tournament.detail.TournamentViewModel;
import land.tower.data.PairingMode;
import land.tower.data.Round;
import land.tower.data.Team;

/**
 * Created on 10/12/2017
 * @author Cédric Longo
 */
public final class ChainTournamentDialogModel {

    public interface Factory {

        ChainTournamentDialogModel forTournament( ObservableTournament tournament );
    }

    @Inject
    public ChainTournamentDialogModel( final I18nTranslator translator,
                                       final Stage owner,
                                       @Assisted final ObservableTournament tournament,
                                       final TournamentRepository tournamentRepository,
                                       final Map<PairingMode, PairingRule> pairingSystems,
                                       final EventBus eventBus,
                                       final TournamentViewModel.Factory tournamentViewFactory ) {
        _i18n = translator;
        _owner = owner;
        _tournament = tournament;
        _tournamentRepository = tournamentRepository;
        _pairingSystems = pairingSystems;
        _eventBus = eventBus;
        _tournamentViewFactory = tournamentViewFactory;

        _teamCount.addListener( ( observable, oldV, newV ) -> checkValidity( ) );
        _pairingMode.addListener( ( observable, oldV, newV ) -> checkValidity( ) );
        _activeTeamsOnly.addListener( ( observable, oldV, newV ) -> checkValidity( ) );

        checkValidity( );
    }

    private void checkValidity( ) {
        if ( _teamCount.get( ) == null ) {
            _isValid.set( false );
            return;
        } else {
            try {
                if ( _teamCount.get( ) < 2 ) {
                    _isValid.set( false );
                    return;
                }
            } catch ( final NumberFormatException e ) {
                _isValid.set( false );
                return;
            }
        }

        if ( _pairingMode.get( ) == null ) {
            _isValid.set( false );
            return;
        }

        _isValid.set( true );
    }

    public boolean isValid( ) {
        return _isValid.get( );
    }

    public SimpleBooleanProperty isValidProperty( ) {
        return _isValid;
    }

    public Long getTeamCount( ) {
        return _teamCount.get( );
    }

    public SimpleObjectProperty<Long> teamCountProperty( ) {
        return _teamCount;
    }

    public boolean getActiveTeamsOnly( ) {
        return _activeTeamsOnly.get( );
    }

    public SimpleBooleanProperty activeTeamsOnlyProperty( ) {
        return _activeTeamsOnly;
    }

    public PairingMode getPairingMode( ) {
        return _pairingMode.get( );
    }

    public SimpleObjectProperty<PairingMode> pairingModeProperty( ) {
        return _pairingMode;
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    public Stage getOwner( ) {
        return _owner;
    }

    public boolean isIsValid( ) {
        return _isValid.get( );
    }

    public boolean isActiveTeamsOnly( ) {
        return _activeTeamsOnly.get( );
    }

    public ObservableTournament getTournament( ) {
        return _tournament;
    }

    public void fireCreateTournament( ) {
        final ObservableTournament newTournament = _tournamentRepository.create( );

        // Copy of tournament information (except title)
        newTournament.getHeader( ).setDate( _tournament.getHeader( ).getDate( ) );
        newTournament.getHeader( ).setMatchDuration( _tournament.getHeader( ).getMatchDuration( ) );
        newTournament.getHeader( ).setTeamSize( _tournament.getHeader( ).getTeamSize( ) );
        newTournament.getHeader( ).setScoreMax( _tournament.getHeader( ).getScoreMax( ) );
        newTournament.getHeader( ).setType( _tournament.getHeader( ).getType( ) );
        newTournament.getHeader( ).setPairingMode( _pairingMode.get( ) );

        newTournament.getHeader( ).getAddress( ).setName( _tournament.getHeader( ).getAddress( ).getName( ) );
        newTournament.getHeader( ).getAddress( ).setLine1( _tournament.getHeader( ).getAddress( ).getLine1( ) );
        newTournament.getHeader( ).getAddress( ).setLine2( _tournament.getHeader( ).getAddress( ).getLine2( ) );
        newTournament.getHeader( ).getAddress( )
                     .setPostalCode( _tournament.getHeader( ).getAddress( ).getPostalCode( ) );
        newTournament.getHeader( ).getAddress( ).setCity( _tournament.getHeader( ).getAddress( ).getCity( ) );
        newTournament.getHeader( ).getAddress( ).setCountry( _tournament.getHeader( ).getAddress( ).getCountry( ) );

        // Registration of teams
        final List<ObservableTeam> teams = _tournament.getTeams( ).stream( )
                                                      .sorted(
                                                          Comparator.comparingInt( t -> t.getRanking( ).getRank( ) ) )
                                                      .filter( t -> !_activeTeamsOnly.get( ) || t.isActive( ) )
                                                      .limit( _teamCount.get( ) )
                                                      .collect( Collectors.toList( ) );
        teams.forEach( ( ObservableTeam team ) -> {
            final Team newTeam = new Team( );
            newTeam.setName( team.getName( ) );
            newTeam.setActive( true );
            newTeam.getRanking( ).setRank( team.getRanking( ).getRank( ) );
            newTeam.getPlayers( ).addAll( team.getTeam( ).getPlayers( ) );
            newTournament.registerTeam( new ObservableTeam( newTeam ) );
        } );

        // Registration of first round
        final PairingRule pairingSystem = _pairingSystems.get( _pairingMode.get( ) );
        final Round firstRound = pairingSystem.getPairingSystem( ).createFirstRoundFromInitialRanking( newTournament );
        newTournament.registerRound( new ObservableRound( firstRound ) );
        pairingSystem.getRankingComputer( ).computeRanking( newTournament );

        // Request display of tournament
        final TournamentViewModel viewModel = _tournamentViewFactory.forTournament( newTournament );
        _eventBus.post( new SceneRequestedEvent( new TournamentView( viewModel ) ) );
    }

    private final SimpleBooleanProperty _isValid = new SimpleBooleanProperty( );

    private final SimpleObjectProperty<Long> _teamCount = new SimpleObjectProperty<>( );
    private final SimpleBooleanProperty _activeTeamsOnly = new SimpleBooleanProperty( );
    private final SimpleObjectProperty<PairingMode> _pairingMode = new SimpleObjectProperty<>( PairingMode.DIRECT_ELIMINATION );

    private final I18nTranslator _i18n;
    private final Stage _owner;
    private final ObservableTournament _tournament;

    private final TournamentRepository _tournamentRepository;
    private final Map<PairingMode, PairingRule> _pairingSystems;
    private final EventBus _eventBus;
    private final TournamentViewModel.Factory _tournamentViewFactory;
}
