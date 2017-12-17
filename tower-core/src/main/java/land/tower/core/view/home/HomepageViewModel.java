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

package land.tower.core.view.home;

import com.google.common.eventbus.EventBus;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Provider;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.ext.thread.ApplicationThread;
import land.tower.core.view.event.InformationEvent;
import land.tower.core.view.event.SceneRequestedEvent;
import land.tower.core.view.player.PlayerManagementView;
import land.tower.core.view.tournament.management.TournamentManagementView;

/**
 * Created on 18/11/2017
 * @author Cédric Longo
 */
final class HomepageViewModel {

    @Inject
    HomepageViewModel( final EventBus eventBus, final I18nTranslator i18n,
                       @ApplicationThread final ScheduledExecutorService scheduler,
                       final Provider<PlayerManagementView> playerManagementViewProvider,
                       final Provider<TournamentManagementView> tournamentManagementViewProvider ) {
        _eventBus = eventBus;
        _i18n = i18n;
        _playerManagementViewProvider = playerManagementViewProvider;
        _tournamentManagementViewProvider = tournamentManagementViewProvider;
        _eventBus.register( this );

        scheduler.schedule( ( ) -> {
            eventBus.post( new InformationEvent( i18n.get( "information.welcome" ) ) );
        }, 1, TimeUnit.SECONDS );
    }

    public void firePlayerViewRequested( ) {
        _eventBus.post( new SceneRequestedEvent( _playerManagementViewProvider.get( ) ) );
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    public void fireTournamentViewRequested( ) {
        _eventBus.post( new SceneRequestedEvent( _tournamentManagementViewProvider.get( ) ) );
    }

    private final EventBus _eventBus;
    private final I18nTranslator _i18n;
    private final Provider<PlayerManagementView> _playerManagementViewProvider;
    private final Provider<TournamentManagementView> _tournamentManagementViewProvider;
}
