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

package land.tower.core.view.main;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;
import javax.inject.Inject;
import land.tower.core.view.event.SceneRequestedEvent;
import land.tower.core.view.home.HomepageView;

/**
 * Created on 04/12/2017
 * @author Cédric Longo
 */
final class ApplicationSceneModel {

    @Inject
    public ApplicationSceneModel( final EventBus eventBus, final HomepageView homepageView ) {
        _eventBus = eventBus;
        _eventBus.register( this );

        _homepageView = homepageView;
        _current.setValue( _homepageView );
    }

    @Subscribe
    void sceneRequested( final SceneRequestedEvent event ) {
        switch ( event.getType( ) ) {
            case HOMEPAGE:
                Platform.runLater( ( ) -> _current.setValue( _homepageView ) );
                break;
            case PLAYER_MANAGEMENT:
                Platform.runLater( ( ) -> _current.setValue( null ) );
                break;
        }
    }

    public Property<Pane> currentPaneProperty( ) {
        return _current;
    }

    private final Property<Pane> _current = new SimpleObjectProperty<>( );

    private final EventBus _eventBus;
    private final HomepageView _homepageView;
}
