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

import static java.util.Comparator.comparingInt;

import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.rules.ITournamentRulesProvider;
import land.tower.core.model.rules.PairingRule;
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

        ChainTournamentDialogModel forTournament( ObservableTournament tournament,
                                                  ObservableList<ObservableTeam> selectedTeams );
    }

    @Inject
    public ChainTournamentDialogModel( final I18nTranslator translator,
                                       final Stage owner,
                                       @Assisted final ObservableTournament tournament,
                                       @Assisted final ObservableList<ObservableTeam> selectedTeams,
                                       final TournamentRepository tournamentRepository,
                                       final ITournamentRulesProvider tournamentRules,
                                       final EventBus eventBus,
                                       final TournamentViewModel.Factory tournamentViewFactory ) {
        _i18n = translator;
        _owner = owner;
        _tournament = tournament;
        _selectedTeams = selectedTeams;
        _tournamentRepository = tournamentRepository;
        _tournamentRules = tournamentRules;
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

    public boolean isSelectedTeamsOnly( ) {
        return _selectedTeamsOnly.get( );
    }

    public SimpleBooleanProperty selectedTeamsOnlyProperty( ) {
        return _selectedTeamsOnly;
    }

    public ObservableTournament getTournament( ) {
        return _tournament;
    }

    public ObservableList<ObservableTeam> getSelectedTeams( ) {
        return _selectedTeams;
    }

    public void fireCreateTournament( ) {
        final ObservableTournament newTournament = _tournamentRepository.create( );

        // Copy of tournament information (except title and date)
        newTournament.getHeader( ).setDate( ZonedDateTime.now( _tournament.getHeader( ).getDate( ).getZone( ) )
                                                         .plusHours( 1 )
                                                         .truncatedTo( ChronoUnit.HOURS ) );
        newTournament.getHeader( ).setMatchDuration( _tournament.getHeader( ).getMatchDuration( ) );
        newTournament.getHeader( ).setTeamSize( _tournament.getHeader( ).getTeamSize( ) );
        newTournament.getHeader( ).setScoreMax( _tournament.getHeader( ).getScoreMax( ) );
        newTournament.getHeader( ).setScoreMaxBis( _tournament.getHeader( ).getScoreMaxBis( ) );
        newTournament.getHeader( ).setDoubleScore( _tournament.getHeader( ).getDoubleScore( ) );
        newTournament.getHeader( ).setType( _tournament.getHeader( ).getType( ) );
        newTournament.getHeader( ).setPairingMode( _pairingMode.get( ) );
        newTournament.getHeader( ).setGame( _tournament.getHeader( ).getGame( ) );

        newTournament.getHeader( ).getAddress( ).setName( _tournament.getHeader( ).getAddress( ).getName( ) );
        newTournament.getHeader( ).getAddress( ).setLine1( _tournament.getHeader( ).getAddress( ).getLine1( ) );
        newTournament.getHeader( ).getAddress( ).setLine2( _tournament.getHeader( ).getAddress( ).getLine2( ) );
        newTournament.getHeader( ).getAddress( )
                     .setPostalCode( _tournament.getHeader( ).getAddress( ).getPostalCode( ) );
        newTournament.getHeader( ).getAddress( ).setCity( _tournament.getHeader( ).getAddress( ).getCity( ) );
        newTournament.getHeader( ).getAddress( ).setCountry( _tournament.getHeader( ).getAddress( ).getCountry( ) );

        // Registration of teams
        final ObservableList<ObservableTeam> fromList =
            isSelectedTeamsOnly( ) ? _selectedTeams : _tournament.getTeams( );
        final List<ObservableTeam> teams = fromList.stream( )
                                                   .sorted( comparingInt( t -> t.getRanking( ).getRank( ) ) )
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
        final PairingRule pairingSystem = _tournamentRules.forGame( _tournament.getHeader( ).getGame( ) )
                                                          .getPairingRules( )
                                                          .get( newTournament.getHeader( ).getPairingMode( ) );
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
    private final SimpleBooleanProperty _selectedTeamsOnly = new SimpleBooleanProperty( );
    private final SimpleObjectProperty<PairingMode> _pairingMode =
        new SimpleObjectProperty<>( PairingMode.DIRECT_ELIMINATION );

    private final I18nTranslator _i18n;
    private final Stage _owner;
    private final ObservableTournament _tournament;

    private final ObservableList<ObservableTeam> _selectedTeams;
    private final TournamentRepository _tournamentRepository;
    private final ITournamentRulesProvider _tournamentRules;
    private final EventBus _eventBus;
    private final TournamentViewModel.Factory _tournamentViewFactory;
}
