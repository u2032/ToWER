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

import com.google.common.base.Strings;
import com.google.inject.assistedinject.Assisted;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.player.IPlayerNumeroValidator;
import land.tower.core.model.player.PlayerRepository;
import land.tower.core.model.player.suggestion.IPlayerSuggestionProvider;
import land.tower.core.model.rules.ITournamentRulesProvider;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.data.Player;
import land.tower.data.Team;

/**
 * Created on 10/12/2017
 * @author Cédric Longo
 */
public final class AddTeamDialogModel {

    public SimpleStringProperty teamNameProperty( ) {
        return _teamName;
    }

    public SimpleStringProperty defaultTeamNameProperty( ) {
        return _defaultTeamName;
    }

    public IPlayerNumeroValidator getPlayerNumeroValidator( ) {
        return _playerNumeroValidator;
    }

    public interface Factory {

        AddTeamDialogModel forTournament( final ObservableTournament tournament );

    }

    @Inject
    public AddTeamDialogModel( final I18nTranslator translator, @Assisted final ObservableTournament tournament,
                               final Stage owner,
                               final IPlayerSuggestionProvider playerSuggestionProvider,
                               final ITournamentRulesProvider rulesProvider,
                               final PlayerRepository playerRepository,
                               final IPlayerNumeroValidator playerNumeroValidator ) {
        _i18n = translator;
        _tournament = tournament;
        _owner = owner;
        _playerSuggestionProvider = playerSuggestionProvider;

        _players = new Player[tournament.getHeader( ).getTeamSize( )];
        _rulesProvider = rulesProvider;
        _playerRepository = playerRepository;
        _playerNumeroValidator = playerNumeroValidator;
        _defaultTeamName.set( _i18n.get( "team.name.prompt" ).get( ) );
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    public ObservableTournament getTournament( ) {
        return _tournament;
    }

    public List<Player> getPlayerSuggestionsForName( final String userText ) {
        return _playerSuggestionProvider.getSuggestionsForName( userText );
    }

    public List<Player> getPlayerSuggestionsForNumero( final String userText ) {
        try {
            return _playerSuggestionProvider.getSuggestionsForNumero( Long.parseLong( userText.trim( ) ) );
        } catch ( final NumberFormatException ignored ) {
            return Collections.emptyList( );
        }
    }

    public Stage getOwner( ) {
        return _owner;
    }

    public SimpleStringProperty errorInformationProperty( ) {
        return _errorInformation;
    }

    public SimpleIntegerProperty selectedPlayerCountProperty( ) {
        return _selectedPlayerCount;
    }

    public void firePlayerAdded( final Player player, final int index ) {
        if ( !_playerRepository.getPlayer( player.getNumero( ) ).isPresent( ) ) {
            _playerRepository.registerPlayer( player );
        }
        _players[index] = player;
        updateInfo( );
    }

    public void firePlayerRemoved( final int index ) {
        _players[index] = null;
        updateInfo( );
    }

    private void updateInfo( ) {
        _selectedPlayerCount.set( (int) Stream.of( _players ).filter( Objects::nonNull ).distinct( ).count( ) );

        if ( _players[0] != null ) {
            _defaultTeamName.set( String.format( "%s %s",
                                                 _players[0].getLastname( ).toUpperCase( ),
                                                 _players[0].getFirstname( ) ) );
        } else {
            _defaultTeamName.set( getI18n( ).get( "team.name.prompt" ).getValue( ) );
        }

        for ( Player p : _players ) {
            if ( p == null ) {
                continue;
            }
            final long count = Stream.of( _players )
                                     .filter( Objects::nonNull )
                                     .filter( pL -> pL.getNumero( ) == p.getNumero( ) )
                                     .count( );
            if ( count > 1 ) {
                _errorInformation.set( getI18n( ).get( "team.add.error.player.duplication",
                                                       String.format( "%s – %s %s", p.getNumero( ),
                                                                      p.getLastname( ).toUpperCase( ),
                                                                      p.getFirstname( ) ) ) );
                return;
            }

            if ( _tournament.getTeams( ).stream( ).anyMatch( t -> t.getTeam( ).hasPlayer( p ) ) ) {
                _errorInformation.set( getI18n( ).get( "team.add.error.player.already.enrolled",
                                                       String.format( "%s – %s %s", p.getNumero( ),
                                                                      p.getLastname( ).toUpperCase( ),
                                                                      p.getFirstname( ) ) ) );
                return;
            }
        }

        _errorInformation.set( null );
    }

    final Team createTeam( ) {
        final Team team = new Team( );
        final String teamName = _teamName.get( ) == null ? null : _teamName.get( ).trim( );
        if ( Strings.isNullOrEmpty( teamName ) ) {
            team.setName( _defaultTeamName.get( ) );
        } else {
            team.setName( teamName );
        }
        team.setPlayers( Arrays.asList( _players ) );
        team.getExtraInfo( ).putAll( _extraInfo );
        return team;
    }

    public Map<String, String[]> getTeamExtraInfo( ) {
        return _rulesProvider.forGame( _tournament.getHeader( ).getGame( ) )
                             .getTeamExtraInfo( );
    }

    public void registerExtraInfo( final String key, final String newValue ) {
        if ( newValue == null ) {
            _extraInfo.remove( key );
        } else {
            _extraInfo.put( key, newValue );
        }
    }

    public String getExtraInfo( String key ) {
        return _extraInfo.get( key );
    }

    private final I18nTranslator _i18n;
    private final ObservableTournament _tournament;
    private final Stage _owner;

    private final Player[] _players;

    private final IPlayerSuggestionProvider _playerSuggestionProvider;
    private final SimpleStringProperty _errorInformation = new SimpleStringProperty( );
    private final SimpleIntegerProperty _selectedPlayerCount = new SimpleIntegerProperty( );
    private final SimpleStringProperty _teamName = new SimpleStringProperty( );
    private final SimpleStringProperty _defaultTeamName = new SimpleStringProperty( );
    private final Map<String, String> _extraInfo = new HashMap<>( 0 );

    private final ITournamentRulesProvider _rulesProvider;
    private final PlayerRepository _playerRepository;
    private final IPlayerNumeroValidator _playerNumeroValidator;
}
