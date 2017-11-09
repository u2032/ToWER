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

package land.tower.core;

import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Created on 09/11/2017
 * @author Cédric Longo
 */
public final class Application extends javafx.application.Application {

    public static void main( final String[] args ) {
        Application.launch( Application.class, args );
    }

    @Override
    public void start( final Stage primaryStage ) throws Exception {
        primaryStage.setMaximized( true );
        primaryStage.setTitle( "♜ ToWER" );

        primaryStage.setScene( new Scene( new GridPane( ) ) );
        primaryStage.show( );
    }
}
