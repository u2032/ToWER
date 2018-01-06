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

package land.tower.core.model.tournament;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import land.tower.data.Address;

/**
 * Created on 21/12/2017
 * @author Cédric Longo
 */
public final class ObservableAddress {

    public ObservableAddress( final Address address ) {
        _address = address;

        _name = new SimpleStringProperty( address.getName( ) );
        _name.addListener( ( obs, oldValue, newValue ) -> address.setName( newValue ) );
        _name.addListener( ( obs, oldValue, newValue ) -> _dirty.set( true ) );

        _line1 = new SimpleStringProperty( address.getLine1( ) );
        _line1.addListener( ( obs, oldValue, newValue ) -> address.setLine1( newValue ) );
        _line1.addListener( ( obs, oldValue, newValue ) -> _dirty.set( true ) );

        _line2 = new SimpleStringProperty( address.getLine2( ) );
        _line2.addListener( ( obs, oldValue, newValue ) -> address.setLine2( newValue ) );
        _line2.addListener( ( obs, oldValue, newValue ) -> _dirty.set( true ) );

        _postalCode = new SimpleStringProperty( address.getPostalCode( ) );
        _postalCode.addListener( ( obs, oldValue, newValue ) -> address.setPostalCode( newValue ) );
        _postalCode.addListener( ( obs, oldValue, newValue ) -> _dirty.set( true ) );

        _city = new SimpleStringProperty( address.getCity( ) );
        _city.addListener( ( obs, oldValue, newValue ) -> address.setCity( newValue ) );
        _city.addListener( ( obs, oldValue, newValue ) -> _dirty.set( true ) );

        _country = new SimpleStringProperty( address.getCountry( ) );
        _country.addListener( ( obs, oldValue, newValue ) -> address.setCountry( newValue ) );
        _country.addListener( ( obs, oldValue, newValue ) -> _dirty.set( true ) );
    }

    public Address getAddress( ) {
        return _address;
    }

    public String getName( ) {
        return _name.get( );
    }

    public SimpleStringProperty nameProperty( ) {
        return _name;
    }

    public String getLine1( ) {
        return _line1.get( );
    }

    public SimpleStringProperty line1Property( ) {
        return _line1;
    }

    public String getLine2( ) {
        return _line2.get( );
    }

    public SimpleStringProperty line2Property( ) {
        return _line2;
    }

    public String getPostalCode( ) {
        return _postalCode.get( );
    }

    public SimpleStringProperty postalCodeProperty( ) {
        return _postalCode;
    }

    public String getCity( ) {
        return _city.get( );
    }

    public SimpleStringProperty cityProperty( ) {
        return _city;
    }

    public String getCountry( ) {
        return _country.get( );
    }

    public SimpleStringProperty countryProperty( ) {
        return _country;
    }

    public void setName( final String name ) {
        this._name.set( name );
    }

    public void setLine1( final String line1 ) {
        this._line1.set( line1 );
    }

    public void setLine2( final String line2 ) {
        this._line2.set( line2 );
    }

    public void setPostalCode( final String postalCode ) {
        this._postalCode.set( postalCode );
    }

    public void setCity( final String city ) {
        this._city.set( city );
    }

    public void setCountry( final String country ) {
        this._country.set( country );
    }

    public boolean isDirty( ) {
        return _dirty.get( );
    }

    public SimpleBooleanProperty dirtyProperty( ) {
        return _dirty;
    }

    public void markAsClean( ) {
        _dirty.set( false );
    }

    private final SimpleBooleanProperty _dirty = new SimpleBooleanProperty( );
    private final Address _address;

    private final SimpleStringProperty _name;
    private final SimpleStringProperty _line1;
    private final SimpleStringProperty _line2;
    private final SimpleStringProperty _postalCode;
    private final SimpleStringProperty _city;
    private final SimpleStringProperty _country;
}
