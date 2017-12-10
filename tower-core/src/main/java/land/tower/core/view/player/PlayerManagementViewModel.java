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
import com.google.common.eventbus.Subscribe;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.inject.Inject;
import javax.inject.Provider;
import land.tower.core.ext.font.FontAwesome;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.ext.i18n.I18nTranslatorEvent;
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
    public PlayerManagementViewModel( final EventBus eventBus, final I18nTranslator translator,
                                      final Provider<HomepageView> homepageViewProvider,
                                      final Provider<AddPlayerDialogModel> addPlayerDialogModelProvider,
                                      final PlayerRepository playerRepository ) {
        _eventBus = eventBus;
        _homepageViewProvider = homepageViewProvider;
        _addPlayerDialogModelProvider = addPlayerDialogModelProvider;
        _playerRepository = playerRepository;
        _translator = translator;
        defineTexts( translator );
        eventBus.register( this );
    }

    @Subscribe
    public void i18nTranslatorEvent( final I18nTranslatorEvent event ) {
        defineTexts( event.getTranslator( ) );
    }

    private void defineTexts( final I18nTranslator translator ) {
        _i18nPlaceholder.setValue( translator.get( "player.management.no.player" ) );
        _i18nPlayerNumero.setValue( translator.get( "player.numero" ) );
        _i18nPlayerFirst.setValue( translator.get( "player.firstname" ) );
        _i18nPlayerLastname.setValue( translator.get( "player.lastname" ) );
        _i18nPlayerBirthday.setValue( translator.get( "player.birthday" ) );
        _i18nPlayerManagementTitle.setValue( translator.get( "player.management.title" ) );
        _i18nAddPlayerAction.setValue( FontAwesome.PLUS + " " + translator.get( "player.add.action" ).toUpperCase( ) );
    }

    SimpleStringProperty i18nPlaceholderProperty( ) {
        return _i18nPlaceholder;
    }

    SimpleStringProperty i18nPlayerNumeroProperty( ) {
        return _i18nPlayerNumero;
    }

    SimpleStringProperty i18nPlayerLastnameProperty( ) {
        return _i18nPlayerLastname;
    }

    SimpleStringProperty i18nPlayerFirstProperty( ) {
        return _i18nPlayerFirst;
    }

    SimpleStringProperty i18nPlayerBirthdayProperty( ) {
        return _i18nPlayerBirthday;
    }

    ObservableValue<ObservableList<ObservablePlayer>> playerListProperty( ) {
        return new SimpleListProperty<>( FXCollections.observableArrayList(
            new ObservablePlayer( new Player( 123, "John", "DOE",
                                              "1985-08-29" ) ),
            new ObservablePlayer( new Player( 456, "James", "BOND",
                                              "1996-03-25" ) ) ) );
    }

    void fireHomeButton( ) {
        _eventBus.post( new SceneRequestedEvent( _homepageViewProvider.get( ) ) );
    }

    SimpleStringProperty i18nPlayerManagementTitleProperty( ) {
        return _i18nPlayerManagementTitle;
    }

    SimpleStringProperty i18nAddPlayerActionProperty( ) {
        return _i18nAddPlayerAction;
    }

    public AddPlayerDialogModel newAddPlayerDialogModel( ) {
        return _addPlayerDialogModelProvider.get( );
    }

    public void firePlayerCreated( final Player player ) {
        _playerRepository.registerPlayer( player );
        _eventBus.post( new InformationEvent( _translator.get( "player.created" ) ) );
    }

    private final SimpleStringProperty _i18nPlaceholder = new SimpleStringProperty( );
    private final SimpleStringProperty _i18nPlayerNumero = new SimpleStringProperty( );
    private final SimpleStringProperty _i18nPlayerLastname = new SimpleStringProperty( );
    private final SimpleStringProperty _i18nPlayerFirst = new SimpleStringProperty( );
    private final SimpleStringProperty _i18nPlayerBirthday = new SimpleStringProperty( );
    private final SimpleStringProperty _i18nPlayerManagementTitle = new SimpleStringProperty( );
    private final SimpleStringProperty _i18nAddPlayerAction = new SimpleStringProperty( );

    private final EventBus _eventBus;
    private final Provider<HomepageView> _homepageViewProvider;
    private final Provider<AddPlayerDialogModel> _addPlayerDialogModelProvider;

    private final PlayerRepository _playerRepository;
    private final I18nTranslator _translator;
}
