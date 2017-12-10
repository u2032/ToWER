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

import com.google.common.base.Strings;

import java.time.LocalDate;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javax.inject.Inject;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.player.PlayerNumeroValidator;

/**
 * Created on 10/12/2017
 * @author Cédric Longo
 */
final class AddPlayerDialogModel {

    @Inject
    public AddPlayerDialogModel( final I18nTranslator translator,
                                 final PlayerNumeroValidator playerNumeroValidator ) {
        _playerNumeroValidator = playerNumeroValidator;
        _i18nTitle.setValue( translator.get( "player.add.title" ) );
        _i18nHeader.setValue( translator.get( "player.information" ) );
        _i18nSave.setValue( translator.get( "action.save" ).toUpperCase( ) );
        _i18nCancel.setValue( translator.get( "action.cancel" ).toUpperCase( ) );
        _i18nPlayerNumero.setValue( translator.get( "player.numero" ) );
        _i18nPlayerLastname.setValue( translator.get( "player.lastname" ) );
        _i18nPlayerFirstname.setValue( translator.get( "player.firstname" ) );
        _i18nPlayerBirthday.setValue( translator.get( "player.birthday" ) );

        _playerNumero.addListener( ( observable, oldV, newV ) -> checkValidty( ) );
        _playerLastname.addListener( ( observable, oldV, newV ) -> checkValidty( ) );
        _playerFirstname.addListener( ( observable, oldV, newV ) -> checkValidty( ) );
        _playerBirthday.addListener( ( observable, oldV, newV ) -> checkValidty( ) );

        _playerNumeroValidator.generate( ).ifPresent( n -> _playerNumero.set( String.valueOf( n ) ) );
        checkValidty( );
    }

    private void checkValidty( ) {
        if ( Strings.isNullOrEmpty( _playerNumero.get( ) ) ) {
            _isValid.set( false );
            _playerNumeroValidity.set( true );
            return;
        } else {
            try {
                final long numero = Long.parseLong( _playerNumero.get( ) );
                if ( !_playerNumeroValidator.isValid( numero ) || _playerNumeroValidator.exists( numero ) ) {
                    _playerNumeroValidity.set( false );
                    _isValid.set( false );
                    return;
                }
                _playerNumeroValidity.set( true );

            } catch ( final NumberFormatException e ) {
                _isValid.set( false );
                _playerNumeroValidity.set( false );
                return;
            }
        }

        if ( Strings.isNullOrEmpty( _playerLastname.get( ) ) ) {
            _isValid.set( false );
            return;
        }

        if ( Strings.isNullOrEmpty( _playerFirstname.get( ) ) ) {
            _isValid.set( false );
            return;
        }

        if ( _playerBirthday.get( ) == null ) {
            _isValid.set( false );
            return;
        }
        _isValid.set( true );
    }

    public SimpleStringProperty i18nTitleProperty( ) {
        return _i18nTitle;
    }

    public SimpleStringProperty i18nHeaderProperty( ) {
        return _i18nHeader;
    }

    public String getI18nSave( ) {
        return _i18nSave.get( );
    }

    public SimpleStringProperty i18nSaveProperty( ) {
        return _i18nSave;
    }

    public void setI18nSave( final String i18nSave ) {
        this._i18nSave.set( i18nSave );
    }

    public String getI18nCancel( ) {
        return _i18nCancel.get( );
    }

    public SimpleStringProperty i18nCancelProperty( ) {
        return _i18nCancel;
    }

    public void setI18nCancel( final String i18nCancel ) {
        this._i18nCancel.set( i18nCancel );
    }

    public String getI18nPlayerNumero( ) {
        return _i18nPlayerNumero.get( );
    }

    public SimpleStringProperty i18nPlayerNumeroProperty( ) {
        return _i18nPlayerNumero;
    }

    public String getI18nPlayerLastname( ) {
        return _i18nPlayerLastname.get( );
    }

    public SimpleStringProperty i18nPlayerLastnameProperty( ) {
        return _i18nPlayerLastname;
    }

    public String getI18nPlayerFirstname( ) {
        return _i18nPlayerFirstname.get( );
    }

    public SimpleStringProperty i18nPlayerFirstnameProperty( ) {
        return _i18nPlayerFirstname;
    }

    public String getI18nPlayerBirthday( ) {
        return _i18nPlayerBirthday.get( );
    }

    public SimpleStringProperty i18nPlayerBirthdayProperty( ) {
        return _i18nPlayerBirthday;
    }

    public SimpleBooleanProperty isValidProperty( ) {
        return _isValid;
    }

    public String getPlayerNumero( ) {
        return _playerNumero.get( );
    }

    public SimpleStringProperty playerNumeroProperty( ) {
        return _playerNumero;
    }

    public String getPlayerLastname( ) {
        return _playerLastname.get( );
    }

    public SimpleStringProperty playerLastnameProperty( ) {
        return _playerLastname;
    }

    public String getPlayerFirstname( ) {
        return _playerFirstname.get( );
    }

    public SimpleStringProperty playerFirstnameProperty( ) {
        return _playerFirstname;
    }

    public LocalDate getPlayerBirthday( ) {
        return _playerBirthday.get( );
    }

    public SimpleObjectProperty<LocalDate> playerBirthdayProperty( ) {
        return _playerBirthday;
    }

    public boolean isPlayerNumeroValidity( ) {
        return _playerNumeroValidity.get( );
    }

    public SimpleBooleanProperty playerNumeroValidityProperty( ) {
        return _playerNumeroValidity;
    }

    private final SimpleStringProperty _i18nTitle = new SimpleStringProperty( );
    private final SimpleStringProperty _i18nHeader = new SimpleStringProperty( );
    private final SimpleStringProperty _i18nSave = new SimpleStringProperty( );
    private final SimpleStringProperty _i18nCancel = new SimpleStringProperty( );
    private final SimpleStringProperty _i18nPlayerNumero = new SimpleStringProperty( );
    private final SimpleStringProperty _i18nPlayerLastname = new SimpleStringProperty( );
    private final SimpleStringProperty _i18nPlayerFirstname = new SimpleStringProperty( );
    private final SimpleStringProperty _i18nPlayerBirthday = new SimpleStringProperty( );

    private final SimpleBooleanProperty _isValid = new SimpleBooleanProperty( );

    private final SimpleStringProperty _playerNumero = new SimpleStringProperty( );
    private final SimpleStringProperty _playerLastname = new SimpleStringProperty( );
    private final SimpleStringProperty _playerFirstname = new SimpleStringProperty( );
    private final SimpleObjectProperty<LocalDate> _playerBirthday = new SimpleObjectProperty<>( LocalDate.of( 2000, 1,
                                                                                                              1 ) );
    private final SimpleBooleanProperty _playerNumeroValidity = new SimpleBooleanProperty( );

    private final PlayerNumeroValidator _playerNumeroValidator;
}
