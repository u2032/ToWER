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
import land.tower.core.ext.config.Configuration;
import land.tower.core.ext.i18n.I18nTranslator;
import land.tower.core.model.player.PlayerNumeroValidator;
import land.tower.data.PlayerNationality;

/**
 * Created on 10/12/2017
 * @author Cédric Longo
 */
final class AddPlayerDialogModel {

    @Inject
    public AddPlayerDialogModel( final I18nTranslator translator,
                                 final PlayerNumeroValidator playerNumeroValidator,
                                 final Configuration config ) {
        _playerNumeroValidator = playerNumeroValidator;
        _i18n = translator;
        _config = config;

        _playerNumero.addListener( ( observable, oldV, newV ) -> checkValidty( ) );
        _playerLastname.addListener( ( observable, oldV, newV ) -> checkValidty( ) );
        _playerFirstname.addListener( ( observable, oldV, newV ) -> checkValidty( ) );
        _playerBirthday.addListener( ( observable, oldV, newV ) -> checkValidty( ) );
        _playerNumeroValidity.addListener( ( observable, oldValue, newValue ) -> checkValidty( ) );

        _playerNumeroValidator.generate( ).ifPresent( _playerNumero::set );
        checkValidty( );
    }

    private void checkValidty( ) {
        if ( _playerNumero.get( ) == null ) {
            _isValid.set( false );
            _playerNumeroValidity.set( true );
            return;
        } else {
            try {
                final long numero = _playerNumero.get( );
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

        if ( _nationality.get( ) == null ) {
            _isValid.set( false );
            return;
        }

        if ( _playerBirthday.get( ) == null ) {
            _isValid.set( false );
            return;
        }
        _isValid.set( true );
    }

    public SimpleBooleanProperty isValidProperty( ) {
        return _isValid;
    }

    public Long getPlayerNumero( ) {
        return _playerNumero.get( );
    }

    public SimpleObjectProperty<Long> playerNumeroProperty( ) {
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

    public PlayerNationality getNationality( ) {
        return _nationality.get( );
    }

    public SimpleObjectProperty<PlayerNationality> nationalityProperty( ) {
        return _nationality;
    }

    public I18nTranslator getI18n( ) {
        return _i18n;
    }

    public Configuration getConfig( ) {
        return _config;
    }

    private final SimpleBooleanProperty _isValid = new SimpleBooleanProperty( );

    private final SimpleObjectProperty<Long> _playerNumero = new SimpleObjectProperty<Long>( );
    private final SimpleStringProperty _playerLastname = new SimpleStringProperty( );
    private final SimpleStringProperty _playerFirstname = new SimpleStringProperty( );
    private final SimpleObjectProperty<LocalDate> _playerBirthday = new SimpleObjectProperty<>( LocalDate.of( 2000, 1,
                                                                                                              1 ) );
    private final SimpleObjectProperty<PlayerNationality> _nationality = new SimpleObjectProperty<>( );
    private final SimpleBooleanProperty _playerNumeroValidity = new SimpleBooleanProperty( );

    private final PlayerNumeroValidator _playerNumeroValidator;
    private final I18nTranslator _i18n;
    private final Configuration _config;
}
