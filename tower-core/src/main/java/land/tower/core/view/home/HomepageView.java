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
import land.tower.core.ext.config.Configuration;
import land.tower.core.ext.effect.Effects;

/**
 * Created on 18/11/2017
 * @author Cédric Longo
 */
public final class HomepageView extends HBox {

    @Inject
    public HomepageView( final HomepageViewModel model, final Configuration configuration ) {
        setAlignment( Pos.CENTER );
        setPadding( new Insets( 50, 50, 50, 50 ) );
        setSpacing( 70 );

        final VBox pblock = generateBlock( model.getI18n( ).get( "player.management.title" ),
                                           configuration.getHomePlayerButton( ) );
        pblock.setOnMouseClicked( event -> model.firePlayerViewRequested( ) );
        getChildren( ).add( pblock );

        final VBox tblock = generateBlock( model.getI18n( ).get( "tournament.management.title" ),
                                           configuration.getHomeTournamentButton( ) );
        tblock.setOnMouseClicked( event -> model.fireTournamentViewRequested( ) );
        getChildren( ).add( tblock );
    }

    private VBox generateBlock( final StringProperty text, final Image image ) {
        final VBox block = new VBox( );
        block.setSpacing( 15 );
        block.setPrefSize( 200, 450 );
        block.setMinSize( USE_PREF_SIZE, USE_PREF_SIZE );
        block.setMaxSize( USE_PREF_SIZE, USE_PREF_SIZE );
        block.setCursor( Cursor.HAND );

        final ImageView view = new ImageView( image );
        view.setEffect( Effects.dropShadow( ) );
        block.getChildren( ).add( view );

        final Label label = new Label( );
        label.textProperty( ).bind( text );
        label.setWrapText( true );
        label.setAlignment( Pos.CENTER );
        label.setTextAlignment( TextAlignment.CENTER );
        label.getStyleClass( ).add( "important" );
        label.setPrefWidth( block.getPrefWidth( ) );
        label.setPadding( new Insets( 5, 0, 0, 0 ) );
        block.getChildren( ).add( label );
        return block;
    }
}
