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
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import land.tower.data.Team;
import land.tower.data.Teams;

/**
 * Created on 27/12/2017
 * @author Cédric Longo
 */
public final class ObservableTeam {

    public ObservableTeam( final Team team ) {
        _team = team;

        _name = new SimpleStringProperty( team.getName( ) );
        _name.addListener( ( observable, oldValue, newValue ) -> team.setName( newValue ) );
        _name.addListener( ( observable, oldValue, newValue ) -> _dirty.set( true ) );

        _id = new SimpleIntegerProperty( team.getId( ) );
        _id.addListener( ( observable, oldValue, newValue ) -> team.setId( newValue.intValue( ) ) );
        _id.addListener( ( observable, oldValue, newValue ) -> _dirty.set( true ) );

        _active = new SimpleBooleanProperty( team.isActive( ) );
        _active.addListener( ( observable, oldValue, newValue ) -> team.setActive( newValue ) );
        _active.addListener( ( observable, oldValue, newValue ) -> _dirty.set( true ) );

        _ranking = new ObservableRanking( team.getRanking( ) );
        _ranking.dirtyProperty( )
                .addListener( ( observable, oldValue, newValue ) -> _dirty.set( isDirty( ) || newValue ) );
    }

    public Team getTeam( ) {
        return _team;
    }

    public boolean isDirty( ) {
        return _dirty.get( );
    }

    public SimpleBooleanProperty dirtyProperty( ) {
        return _dirty;
    }

    public String getName( ) {
        return _name.get( );
    }

    public SimpleStringProperty nameProperty( ) {
        return _name;
    }

    public int getId( ) {
        return _id.get( );
    }

    public void setId( final int id ) {
        this._id.set( id );
    }

    public boolean isActive( ) {
        return _active.get( );
    }

    public void setName( final String name ) {
        this._name.set( name );
    }

    public SimpleBooleanProperty activeProperty( ) {
        return _active;
    }

    public void setActive( final boolean active ) {
        this._active.set( active );
    }

    public SimpleIntegerProperty idProperty( ) {
        return _id;
    }

    public ObservableRanking getRanking( ) {
        return _ranking;
    }

    public boolean isByeTeam( ) {
        return getId( ) == BYE_TEAM.getId( );
    }

    private final Team _team;
    private final SimpleBooleanProperty _dirty = new SimpleBooleanProperty( false );

    private final SimpleStringProperty _name;
    private final SimpleBooleanProperty _active;
    private final SimpleIntegerProperty _id;
    private final ObservableRanking _ranking;

    public static final ObservableTeam BYE_TEAM = new ObservableTeam( Teams.BYE_TEAM );
}
