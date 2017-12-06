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

package land.tower.core.view.home;

import java.io.InputStream;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javax.inject.Inject;

/**
 * Created on 18/11/2017
 * @author Cédric Longo
 */
public final class HomepageView extends HBox {

    @Inject
    public HomepageView( final HomepageViewModel model ) {
        setAlignment( Pos.CENTER );
        setPadding( new Insets( 50, 50, 50, 50 ) );
        setSpacing( 50 );

        final VBox pblock = generateBlock( model.playerManagementTitle( ), "img/home-players.png" );
        pblock.setOnMouseClicked( event -> model.firePlayerViewRequested());
        getChildren( ).add( pblock );

        final VBox tblock = generateBlock( model.tournamentManagementTitle( ), "img/home-tournaments.png" );
        getChildren( ).add( tblock );
    }

    private VBox generateBlock( final StringProperty text, final String imageName ) {
        final VBox block = new VBox( );
        block.setPrefSize( 200, 450 );
        block.setMinSize( USE_PREF_SIZE, USE_PREF_SIZE );
        block.setMaxSize( USE_PREF_SIZE, USE_PREF_SIZE );
        block.setCursor( Cursor.HAND );

        final InputStream imStream = getClass( ).getClassLoader( ).getResourceAsStream( imageName );
        final Image image = new Image( imStream, 200, 400, true, true );
        final ImageView view = new ImageView( image );
        block.getChildren( ).add( view );

        final Label label = new Label( );
        label.textProperty( ).bind( text );
        label.setWrapText( true );
        label.setAlignment( Pos.CENTER );
        label.setTextAlignment( TextAlignment.CENTER );
        label.setStyle( "-fx-font-weight: bold;" );
        label.setPrefWidth( block.getPrefWidth( ) );
        label.setPadding( new Insets( 5, 0, 0, 0 ) );
        block.getChildren( ).add( label );
        return block;
    }
}
