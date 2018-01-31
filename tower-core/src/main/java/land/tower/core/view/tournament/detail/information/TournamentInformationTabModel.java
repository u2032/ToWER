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
import com.google.inject.assistedinject.Assisted;

import javax.inject.Inject;
import land.tower.core.ext.config.Configuration;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.ext.preference.Preferences;
import land.tower.core.model.rules.ITournamentRulesProvider;
import land.tower.core.model.rules.TournamentRules;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.view.event.InformationEvent;

/**
 * Created on 20/12/2017
 * @author Cédric Longo
 */
public final class TournamentInformationTabModel {

    public ObservableTournament getTournament( ) {
        return _tournament;
    }

    public Configuration getConfiguration( ) {
        return _configuration;
    }

    public TournamentRules getTournamentRules( final String game ) {
        return _tournamentRulesProvider.forGame( game );
    }

    public interface Factory {

        TournamentInformationTabModel forTournament( final ObservableTournament tournament );

    }

    @Inject
    TournamentInformationTabModel( final @Assisted ObservableTournament tournament, final I18nTranslator i18n,
                                   final Preferences preferences, final EventBus eventBus,
                                   final Configuration configuration,
                                   final ITournamentRulesProvider tournamentRulesProvider ) {
        _i18n = i18n;
        _tournament = tournament;
        _preferences = preferences;
        _eventBus = eventBus;
        _configuration = configuration;
        _tournamentRulesProvider = tournamentRulesProvider;
    }

    public void fireSaveAsPreference( ) {
        _preferences.saveJson( "tournament.info", _tournament.getHeader( ).getHeader( ) );
        _eventBus.post( new InformationEvent( _i18n.get( "information.saved" ) ) );
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
}
