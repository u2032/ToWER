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

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import land.tower.data.Player;

/**
 * Created on 09/12/2017
 * @author Cédric Longo
 */
public final class ObservablePlayer {

    public ObservablePlayer( final Player player ) {
        _player = player;

        _numero = new ReadOnlyStringWrapper( player.getNumero( ) + "" );
        _firstname = new ReadOnlyStringWrapper( player.getFirstname( ) );
        _lastname = new ReadOnlyStringWrapper( player.getLastname( ) );
        _birthday = new ReadOnlyStringWrapper( player.getBirthday( ) );
    }

    public String getNumero( ) {
        return _numero.get( );
    }

    public SimpleStringProperty numeroProperty( ) {
        return _numero;
    }

    public String getFirstname( ) {
        return _firstname.get( );
    }

    public SimpleStringProperty firstnameProperty( ) {
        return _firstname;
    }

    public String getLastname( ) {
        return _lastname.get( );
    }

    public SimpleStringProperty lastnameProperty( ) {
        return _lastname;
    }

    public String getBirthday( ) {
        return _birthday.get( );
    }

    public SimpleStringProperty birthdayProperty( ) {
        return _birthday;
    }

    private final Player _player;

    private final SimpleStringProperty _numero;
    private final SimpleStringProperty _firstname;
    private final SimpleStringProperty _lastname;
    private final SimpleStringProperty _birthday;
}