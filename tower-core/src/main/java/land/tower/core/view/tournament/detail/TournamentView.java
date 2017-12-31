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

package land.tower.core.view.tournament.detail;

import static javafx.scene.layout.HBox.setHgrow;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import land.tower.core.ext.effect.Effects;
import land.tower.core.ext.font.FontAwesome;

/**
 * Created on 19/12/2017
 * @author Cédric Longo
 */
public final class TournamentView extends BorderPane {

    public TournamentView( final TournamentViewModel model ) {
        _model = model;
        buildHeader( );

        final TabPane tabPane = new TabPane( );
        tabPane.setTabClosingPolicy( TabClosingPolicy.UNAVAILABLE );

        tabPane.getTabs( ).setAll( _model.tabListProperty( ).get( ) );
        _model.tabListProperty( ).addListener( ( observable, oldValue, newValue ) -> {
            tabPane.getTabs( ).clear( );
            tabPane.getTabs( ).setAll( newValue );
        } );

        tabPane.getSelectionModel( ).select( _model.getSelectedTab( ) );
        _model.selectedTabProperty( ).addListener( ( observable, oldValue, newValue ) -> {
            tabPane.getSelectionModel( ).select( newValue );
        } );

        setCenter( tabPane );
    }

    private void buildHeader( ) {
        final Button homeButton = new Button( FontAwesome.HOME );
        homeButton.setOnMouseClicked( e -> _model.fireHomeButton( ) );
        homeButton.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
        homeButton.getStyleClass( ).add( "rich-button" );

        final HBox spacing = new HBox( );
        setHgrow( spacing, Priority.ALWAYS );
        getChildren( ).add( spacing );

        final Label title = new Label( );
        title.getStyleClass( ).add( "title" );
        title.getStyleClass( ).add( "large" );
        title.getStyleClass( ).add( "important" );
        title.textProperty( ).bind( _model.getTournament( ).getHeader( ).titleProperty( ) );
        title.setEffect( Effects.dropShadow( ) );

        final HBox header = new HBox( homeButton, spacing, title );
        header.setPadding( new Insets( 10, 20, 10, 10 ) );
        header.setSpacing( 10 );
        header.setAlignment( Pos.CENTER_LEFT );
        setTop( header );
    }

    public TournamentViewModel getModel( ) {
        return _model;
    }

    private final TournamentViewModel _model;

}
