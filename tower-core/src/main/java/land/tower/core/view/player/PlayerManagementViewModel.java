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

package land.tower.core.view.player;

import com.google.common.eventbus.EventBus;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javax.inject.Inject;
import javax.inject.Provider;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.ext.preference.Preferences;
import land.tower.core.model.player.PlayerRepository;
import land.tower.core.view.event.InformationEvent;
import land.tower.core.view.event.SceneRequestedEvent;
import land.tower.core.view.home.HomepageView;
import land.tower.data.Player;

/**
 * Created on 09/12/2017
 * @author Cédric Longo
 */
final class PlayerManagementViewModel {

    @Inject
    public PlayerManagementViewModel( final EventBus eventBus, final I18nTranslator i18n,
                                      final Provider<HomepageView> homepageViewProvider,
                                      final Provider<AddPlayerDialogModel> addPlayerDialogModelProvider,
                                      final PlayerRepository playerRepository,
                                      final Preferences preferences, final Stage owner ) {
        _eventBus = eventBus;
        _homepageViewProvider = homepageViewProvider;
        _addPlayerDialogModelProvider = addPlayerDialogModelProvider;
        _playerRepository = playerRepository;
        _i18n = i18n;
        _preferences = preferences;
        _owner = owner;
        eventBus.register( this );
    }

    ObservableValue<ObservableList<ObservablePlayer>> playerListProperty( ) {
        return new SimpleListProperty<>( _playerRepository.getPlayersList( ) );
    }

    void fireHomeButton( ) {
        _eventBus.post( new SceneRequestedEvent( _homepageViewProvider.get( ) ) );
    }

    public AddPlayerDialogModel newAddPlayerDialogModel( ) {
        return _addPlayerDialogModelProvider.get( );
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    public void firePlayerCreated( final Player player ) {
        _playerRepository.registerPlayer( player );
        _eventBus.post( new InformationEvent( _i18n.get( "player.created" ) ) );
        _preferences.save( "player.nationality", player.getNationality( ).name( ) );
    }

    public void firePlayerDeleted( final Player player ) {
        _playerRepository.removePlayer( player );
        _eventBus.post( new InformationEvent( _i18n.get( "player.deleted" ) ) );
    }

    public Stage getOwner( ) {
        return _owner;
    }

    private final EventBus _eventBus;
    private final Provider<HomepageView> _homepageViewProvider;
    private final Provider<AddPlayerDialogModel> _addPlayerDialogModelProvider;

    private final PlayerRepository _playerRepository;
    private final I18nTranslator _i18n;
    private final Preferences _preferences;
    private final Stage _owner;
}
