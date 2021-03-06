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

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javax.inject.Inject;

/**
 * Created on 12/11/2017
 * @author Cédric Longo
 */
public final class ApplicationScene extends Scene {

    @Inject
    public ApplicationScene( final ApplicationMenuBar menu,
                             final ApplicationStatusBar statusBar,
                             final ApplicationSceneModel model ) {
        super( new BorderPane( ), Color.ALICEBLUE );

        final BorderPane root = (BorderPane) getRoot( );
        root.setPrefSize( 900, 600 );
        root.setTop( menu );
        root.setBottom( statusBar );

        root.setCenter( model.currentPaneProperty( ).getValue( ) );
        model.currentPaneProperty( ).addListener( ( observable, oldValue, newValue ) -> {
            root.setCenter( newValue );
        } );

        model.initSnackBar( root );

        getAccelerators( ).putAll( model.getAccelerators( ) );
    }
}
