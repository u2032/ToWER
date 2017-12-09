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
import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.inject.Inject;
import javax.inject.Provider;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.ext.i18n.I18nTranslatorEvent;
import land.tower.core.view.event.SceneRequestedEvent;
import land.tower.core.view.player.PlayerManagementView;

/**
 * Created on 18/11/2017
 * @author Cédric Longo
 */
final class HomepageViewModel {

    @Inject
    HomepageViewModel( final EventBus eventBus, final I18nTranslator i18n,
                       final Provider<PlayerManagementView> playerManagementViewProvider ) {
        _eventBus = eventBus;
        _playerManagementViewProvider = playerManagementViewProvider;
        _eventBus.register( this );

        defineTexts( i18n );
    }

    private void defineTexts( final I18nTranslator i18n ) {
        _playerManagementTitle.setValue( i18n.get( "homepage.player.management.title" ) );
        _tournamentManagementTitle.setValue( i18n.get( "homepage.tournament.management.title" ) );
    }

    @Subscribe
    public void i18nTranslatorEvent( final I18nTranslatorEvent event ) {
        Platform.runLater( ( ) -> defineTexts( event.getTranslator( ) ) );
    }

    public StringProperty playerManagementTitle( ) {
        return _playerManagementTitle;
    }

    public StringProperty tournamentManagementTitle( ) {
        return _tournamentManagementTitle;
    }

    public void firePlayerViewRequested( ) {
        _eventBus.post( new SceneRequestedEvent( _playerManagementViewProvider.get( ) ) );
    }

    private final EventBus _eventBus;
    private final Provider<PlayerManagementView> _playerManagementViewProvider;

    private final StringProperty _playerManagementTitle = new SimpleStringProperty( );
    private final StringProperty _tournamentManagementTitle = new SimpleStringProperty( );
}
