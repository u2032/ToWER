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
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.ext.i18n.I18nTranslatorEvent;
import land.tower.data.Player;

/**
 * Created on 09/12/2017
 * @author Cédric Longo
 */
final class PlayerManagementViewModel {

    @Inject
    public PlayerManagementViewModel( final EventBus eventBus, final I18nTranslator translator ) {
        eventBus.register( this );
        defineTexts( translator );
    }

    @Subscribe
    public void i18nTranslatorEvent( final I18nTranslatorEvent event ) {
        defineTexts( event.getTranslator( ) );
    }

    private void defineTexts( final I18nTranslator translator ) {
        _i18nPlaceholderProperty.setValue( translator.get( "player.management.no.player" ) );
        _i18nPlayerNumeroProperty.setValue( translator.get( "player.numero" ) );
        _i18nPlayerFirstProperty.setValue( translator.get( "player.firstname" ) );
        _i18nPlayerLastnameProperty.setValue( translator.get( "player.lastname" ) );
        _i18nPlayerBirthdayProperty.setValue( translator.get( "player.birthday" ) );
    }

    public String getI18nPlaceholderProperty( ) {
        return _i18nPlaceholderProperty.get( );
    }

    public SimpleStringProperty i18nPlaceholderPropertyProperty( ) {
        return _i18nPlaceholderProperty;
    }

    public String getI18nPlayerNumeroProperty( ) {
        return _i18nPlayerNumeroProperty.get( );
    }

    public SimpleStringProperty i18nPlayerNumeroPropertyProperty( ) {
        return _i18nPlayerNumeroProperty;
    }

    public String getI18nPlayerLastnameProperty( ) {
        return _i18nPlayerLastnameProperty.get( );
    }

    public SimpleStringProperty i18nPlayerLastnamePropertyProperty( ) {
        return _i18nPlayerLastnameProperty;
    }

    public String getI18nPlayerFirstProperty( ) {
        return _i18nPlayerFirstProperty.get( );
    }

    public SimpleStringProperty i18nPlayerFirstPropertyProperty( ) {
        return _i18nPlayerFirstProperty;
    }

    public String getI18nPlayerBirthdayProperty( ) {
        return _i18nPlayerBirthdayProperty.get( );
    }

    public SimpleStringProperty i18nPlayerBirthdayPropertyProperty( ) {
        return _i18nPlayerBirthdayProperty;
    }

    public ObservableValue<ObservableList<ObservablePlayer>> playerListProperty( ) {
        return new SimpleListProperty<>( FXCollections.observableArrayList(
            new ObservablePlayer( new Player( 123, "John", "DOE",
                                              "1985-08-29" ) ),
            new ObservablePlayer( new Player( 456, "James", "BOND",
                                              "1996-03-25" ) ) ) );
    }

    private final SimpleStringProperty _i18nPlaceholderProperty = new SimpleStringProperty( );
    private final SimpleStringProperty _i18nPlayerNumeroProperty = new SimpleStringProperty( );
    private final SimpleStringProperty _i18nPlayerLastnameProperty = new SimpleStringProperty( );
    private final SimpleStringProperty _i18nPlayerFirstProperty = new SimpleStringProperty( );
    private final SimpleStringProperty _i18nPlayerBirthdayProperty = new SimpleStringProperty( );
}
