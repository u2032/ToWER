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

package land.tower.core.view.tournament.detail.information;

import com.google.common.eventbus.EventBus;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import javax.inject.Inject;
import land.tower.core.ext.config.Configuration;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.ext.preference.Preferences;
import land.tower.core.model.rules.ITournamentRulesProvider;
import land.tower.core.model.rules.TournamentRules;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.view.event.InformationEvent;
import land.tower.core.view.player.ObservablePlayer;

/**
 * Created on 20/12/2017
 * @author Cédric Longo
 */
public final class TournamentInformationTabModel {

    public interface Factory {

        TournamentInformationTabModel forTournament( final ObservableTournament tournament );

    }

    @Inject
    TournamentInformationTabModel( final @Assisted ObservableTournament tournament, final I18nTranslator i18n,
                                   final Preferences preferences, final EventBus eventBus,
                                   final Configuration configuration,
                                   final ITournamentRulesProvider tournamentRulesProvider,
                                   final Provider<SelectPlayerDialog> selectPlayerDialogProvider ) {
        _i18n = i18n;
        _tournament = tournament;
        _preferences = preferences;
        _eventBus = eventBus;
        _configuration = configuration;
        _tournamentRulesProvider = tournamentRulesProvider;
        _selectPlayerDialogProvider = selectPlayerDialogProvider;
    }

    public void fireSaveAsPreference( ) {
        _preferences.saveJson( "tournament.info", _tournament.getHeader( ).getHeader( ) );
        _eventBus.post( new InformationEvent( _i18n.get( "information.saved" ) ) );
    }

    public ObservableTournament getTournament( ) {
        return _tournament;
    }

    public Configuration getConfiguration( ) {
        return _configuration;
    }

    public TournamentRules getTournamentRules( final String game ) {
        return _tournamentRulesProvider.forGame( game );
    }

    public void fireClearMainJudge( ) {
        _tournament.getHeader( ).setMainJudge( null );
    }

    public void fireSelectMainJudge( ) {
        _selectPlayerDialogProvider.get( ).showAndWait( )
                                   .ifPresent( player -> _tournament.getHeader( ).setMainJudge( player ) );
    }

    public void fireSelectSecondaryJudge( ) {
        _selectPlayerDialogProvider.get( ).showAndWait( )
                                   .ifPresent( player -> {
                                       final boolean exists =
                                           _tournament.getHeader( ).getJudges( )
                                                      .stream( )
                                                      .anyMatch( p -> p.getNumero( ) == player.getNumero( ) );
                                       if ( !exists ) {
                                           _tournament.getHeader( ).getJudges( ).add( player );
                                       }
                                   } );
    }

    public void fireDeleteSecondaryJudge( final ObservablePlayer player ) {
        _tournament.getHeader( ).getJudges( )
                   .removeIf( p -> p.getNumero( ) == player.getNumero( ) );
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    private final I18nTranslator _i18n;
    private final ObservableTournament _tournament;
    private final Preferences _preferences;
    private final EventBus _eventBus;
    private final Configuration _configuration;
    private final ITournamentRulesProvider _tournamentRulesProvider;

    private final Provider<SelectPlayerDialog> _selectPlayerDialogProvider;
}
