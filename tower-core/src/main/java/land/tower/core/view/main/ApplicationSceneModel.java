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

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javax.inject.Inject;
import land.tower.core.view.event.CloseRequestEvent;
import land.tower.core.view.event.SceneRequestedEvent;
import land.tower.core.view.home.HomepageView;

/**
 * Created on 04/12/2017
 * @author Cédric Longo
 */
final class ApplicationSceneModel {

    @Inject
    public ApplicationSceneModel( final EventBus eventBus,
                                  final HomepageView homepageView ) {
        eventBus.register( this );
        _current.setValue( homepageView );
    }

    @Subscribe
    void sceneRequested( final SceneRequestedEvent event ) {
        Platform.runLater( ( ) -> _current.setValue( event.getView( ) ) );
    }

    @Subscribe
    void closeRequested( final CloseRequestEvent event ) {
        Platform.runLater( ( ) -> {
            final Window window = _current.getValue( ).getScene( ).getWindow( );
            window.fireEvent( new WindowEvent( window, WindowEvent.WINDOW_CLOSE_REQUEST ) );
        } );
    }

    public Property<Pane> currentPaneProperty( ) {
        return _current;
    }

    private final Property<Pane> _current = new SimpleObjectProperty<>( );

}
