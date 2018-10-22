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
import com.jfoenix.controls.JFXSnackbar;

import java.util.Map;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javax.inject.Inject;
import land.tower.core.ext.singleton.SingletonAppEvent;
import land.tower.core.view.component.Displayable;
import land.tower.core.view.event.CloseRequestEvent;
import land.tower.core.view.event.InformationEvent;
import land.tower.core.view.event.SceneRequestedEvent;
import land.tower.core.view.home.HomepageView;

/**
 * Created on 04/12/2017
 * @author Cédric Longo
 */
final class ApplicationSceneModel {

    @Inject
    public ApplicationSceneModel( final EventBus eventBus,
                                  final HomepageView homepageView,
                                  final Map<KeyCombination, Runnable> accelerators ) {
        _accelerators = accelerators;
        eventBus.register( this );
        _current.setValue( homepageView );
    }

    @Subscribe
    void sceneRequested( final SceneRequestedEvent event ) {
        Platform.runLater( ( ) -> {
            _current.setValue( event.getView( ) );
            if ( event.getView( ) instanceof Displayable ) {
                ( (Displayable) event.getView( ) ).onDisplay( );
            }
        } );
    }

    @Subscribe
    void closeRequested( final CloseRequestEvent event ) {
        Platform.runLater( ( ) -> {
            final Window window = _current.getValue( ).getScene( ).getWindow( );
            window.fireEvent( new WindowEvent( window, WindowEvent.WINDOW_CLOSE_REQUEST ) );
        } );
    }

    @Subscribe
    void singleappEvent( final SingletonAppEvent event ) {
        Platform.runLater( ( ) -> {
            final Window window = _current.getValue( ).getScene( ).getWindow( );
            if ( window instanceof Stage ) {
                final Stage stage = (Stage) window;
                stage.toFront( );
            }
            window.requestFocus( );
        } );
    }

    @Subscribe
    public void notificationEvent( final InformationEvent event ) {
        if ( _snackbar == null ) {
            return;
        }
        Platform.runLater( ( ) -> {
            _snackbar.show( event.getText( ), 2500 );
        } );
    }

    public JFXSnackbar getSnackbar( ) {
        return _snackbar;
    }

    public Property<Pane> currentPaneProperty( ) {
        return _current;
    }

    public JFXSnackbar initSnackBar( final BorderPane root ) {
        _snackbar = new JFXSnackbar( root );
        return _snackbar;
    }

    Map<KeyCombination, Runnable> getAccelerators( ) {
        return _accelerators;
    }

    private final Property<Pane> _current = new SimpleObjectProperty<>( );
    private final Map<KeyCombination, Runnable> _accelerators;
    private JFXSnackbar _snackbar;
}
