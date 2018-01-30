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

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;
import land.tower.core.ext.effect.Effects;
import land.tower.core.ext.font.FontAwesome;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.view.component.Displayable;

/**
 * Created on 19/12/2017
 * @author Cédric Longo
 */
public final class TournamentView extends BorderPane implements Displayable {

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

        _tournamentList = new ComboBox<>( );
        _tournamentList.itemsProperty( ).bind( _model.getOpenedTournaments( ) );
        _tournamentList.setConverter( new StringConverter<ObservableTournament>( ) {
            @Override
            public String toString( final ObservableTournament object ) {
                return object.getHeader( ).getTitle( );
            }

            @Override
            public ObservableTournament fromString( final String string ) {
                return null;
            }
        } );
        _tournamentList.valueProperty( ).addListener( ( observable, oldValue, newValue ) -> {
            if ( !refreshing && newValue != null ) {
                _model.fireTournamentSelection( newValue );
            }
        } );
        _tournamentList.visibleProperty( ).bind( Bindings.size( _model.getOpenedTournaments( ).getValue( ) )
                                                         .greaterThan( 1 ) );

        final HBox spacing = new HBox( );
        setHgrow( spacing, Priority.ALWAYS );
        getChildren( ).add( spacing );

        final Label title = new Label( );
        title.getStyleClass( ).add( "title" );
        title.getStyleClass( ).add( "large" );
        title.getStyleClass( ).add( "important" );
        title.textProperty( ).bind( _model.getTournament( ).getHeader( ).titleProperty( ) );
        title.setEffect( Effects.dropShadow( ) );

        final HBox header = new HBox( homeButton, _tournamentList, spacing, title );
        header.setPadding( new Insets( 10, 20, 10, 10 ) );
        header.setSpacing( 10 );
        header.setAlignment( Pos.CENTER_LEFT );
        setTop( header );
    }

    public TournamentViewModel getModel( ) {
        return _model;
    }

    @Override
    public synchronized void onDisplay( ) {
        try {
            refreshing = true;
            _tournamentList.setValue( null );
            _tournamentList.itemsProperty( ).unbind( );
            _tournamentList.itemsProperty( ).set( FXCollections.emptyObservableList( ) );
            _tournamentList.itemsProperty( ).bind( _model.getOpenedTournaments( ) );
            _tournamentList.setValue( _model.getTournament( ) );
        } finally {
            refreshing = false;
        }
    }

    private final TournamentViewModel _model;

    private ComboBox<ObservableTournament> _tournamentList;
    private boolean refreshing;
}
