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

package land.tower.core.view.tournament.management;

import static javafx.beans.binding.Bindings.concat;
import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;
import static javafx.scene.layout.HBox.setHgrow;
import static land.tower.core.ext.binding.Strings.toUpperCase;

import java.util.Optional;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javax.inject.Inject;
import land.tower.core.ext.effect.Effects;
import land.tower.core.ext.font.FontAwesome;
import land.tower.core.model.tournament.ObservableTournament;
import land.tower.core.view.component.FaButton;
import land.tower.data.Tournament;

/**
 * Created on 17/12/2017
 * @author Cédric Longo
 */
public final class TournamentManagementView extends BorderPane {

    @Inject
    public TournamentManagementView( final TournamentManagementViewModel model ) {
        _model = model;

        final Button homeButton = new Button( FontAwesome.HOME );
        homeButton.setOnMouseClicked( e -> model.fireHomeButton( ) );
        homeButton.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
        homeButton.getStyleClass( ).add( "rich-button" );

        final Button addButton = new FaButton( FontAwesome.PLUS, "white" );
        addButton.textProperty( ).bind( concat( toUpperCase( model.getI18n( ).get( "tournament.add.action" ) ) ) );
        addButton.setOnMouseClicked( e -> {
            _model.fireTournamentCreation( );
        } );
        addButton.getStyleClass( ).add( "rich-button" );
        addButton.getStyleClass( ).add( "action-button" );

        final HBox spacing = new HBox( );
        setHgrow( spacing, Priority.ALWAYS );
        getChildren( ).add( spacing );

        final Label title = new Label( );
        title.getStyleClass( ).add( "title" );
        title.getStyleClass( ).add( "large" );
        title.getStyleClass( ).add( "important" );
        title.textProperty( ).bind( model.getI18n( ).get( "tournament.management.title" ) );
        title.setEffect( Effects.dropShadow( ) );

        final HBox header = new HBox( homeButton, addButton, spacing, title );
        header.setPadding( new Insets( 10, 20, 10, 10 ) );
        header.setSpacing( 10 );
        header.setAlignment( Pos.CENTER_LEFT );
        setTop( header );

        final TableView<ObservableTournament> tableView = new TableView<>( );
        tableView.setColumnResizePolicy( CONSTRAINED_RESIZE_POLICY );

        final TableColumn<ObservableTournament, String> dateCol = new TableColumn<>( );
        dateCol.textProperty( ).bind( model.getI18n( ).get( "tournament.date" ) );
        dateCol.setCellValueFactory( param -> {
            return Bindings.createObjectBinding( ( ) -> param.getValue( ).getHeader( ).getDate( ).toLocalDateTime( )
                                                             .toString( ),
                                                 param.getValue( ).getHeader( ).dateProperty( ) );
        } );
        tableView.getColumns( ).add( dateCol );

        final TableColumn<ObservableTournament, String> nameCol = new TableColumn<>( );
        nameCol.textProperty( ).bind( model.getI18n( ).get( "tournament.title" ) );
        nameCol.setCellValueFactory( param -> param.getValue( ).getHeader( ).titleProperty( ) );
        tableView.getColumns( ).add( nameCol );

        final TableColumn<ObservableTournament, String> statusCol = new TableColumn<>( );
        statusCol.textProperty( ).bind( model.getI18n( ).get( "tournament.status" ) );
        statusCol.setCellValueFactory(
            param -> Bindings.createStringBinding(
                ( ) -> _model.getI18n( ).get( "tournament.status." + param.getValue( ).getHeader( ).getStatus( ) )
                             .get( ).toUpperCase( ),
                param.getValue( ).getHeader( ).statusProperty( ) ) );
        tableView.getColumns( ).add( statusCol );

        final TableColumn<ObservableTournament, Void> actionColumn = new TableColumn<>( );
        actionColumn.setMaxWidth( 50 );
        actionColumn.setMinWidth( 50 );
        actionColumn.setResizable( false );
        actionColumn.setCellFactory( param -> new DeleteTournamentCell( ) );
        tableView.getColumns( ).add( actionColumn );

        tableView.setRowFactory( tv -> {
            final TableRow<ObservableTournament> row = new TableRow<>( );
            row.indexProperty( ).addListener( ( observable, oldValue, newValue ) -> {
                row.setCursor( Cursor.DEFAULT );
                if ( newValue.intValue( ) >= 0 && newValue.intValue( ) < row.getTableView( ).getItems( ).size( ) ) {
                    final ObservableTournament item = row.getTableView( ).getItems( ).get( newValue.intValue( ) );
                    row.setCursor( item == null ? Cursor.DEFAULT : Cursor.HAND );
                }
            } );
            row.setOnMouseClicked( event -> {
                if ( !row.isEmpty( ) ) {
                    _model.fireTournamentDisplay( row.getItem( ) );
                }
            } );
            return row;
        } );

        final Label emptyLabel = new Label( );
        emptyLabel.textProperty( ).bind( model.getI18n( ).get( "tournament.management.no.tournament" ) );
        tableView.setPlaceholder( emptyLabel );

        tableView.itemsProperty( ).bind( _model.tournamentListProperty( ) );

        setCenter( tableView );
    }

    private final TournamentManagementViewModel _model;

    private class DeleteTournamentCell extends TableCell<ObservableTournament, Void> {

        final Button _button = new Button( FontAwesome.DELETE );

        DeleteTournamentCell( ) {
            _button.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
            _button.getStyleClass( ).add( "rich-button" );
            _button.getStyleClass( ).add( "dangerous-button" );
            _button.setCursor( Cursor.HAND );
            _button.setOnAction( t -> {
                final Tournament tournament = getTableView( ).getItems( ).get( getIndex( ) ).getTournament( );

                final Alert alert = new Alert( AlertType.WARNING );
                alert.headerTextProperty( ).bind( _model.getI18n( ).get( "tournament.delete.title" ) );
                alert.contentTextProperty( ).bind( _model.getI18n( ).get( "tournament.delete.message" ) );

                final ButtonType deleteButtonType =
                    new ButtonType( _model.getI18n( ).get( "action.delete" ).get( ).toUpperCase( ),
                                    ButtonData.APPLY );
                final ButtonType cancelButtonType =
                    new ButtonType( _model.getI18n( ).get( "action.cancel" ).get( ).toUpperCase( ),
                                    ButtonData.CANCEL_CLOSE );
                alert.getDialogPane( ).getButtonTypes( ).setAll( deleteButtonType, cancelButtonType );

                final Button cancelButton = (Button) alert.getDialogPane( ).lookupButton( cancelButtonType );
                cancelButton.setDefaultButton( true );

                final Button deleteButton = (Button) alert.getDialogPane( ).lookupButton( deleteButtonType );
                deleteButton.setDefaultButton( false );

                final Optional<ButtonType> clicked = alert.showAndWait( );
                if ( clicked.isPresent( ) && clicked.get( ) == deleteButtonType ) {
                    _model.fireTournamentDeleted( tournament );
                }
            } );
        }

        @Override
        protected void updateItem( Void t, boolean empty ) {
            super.updateItem( t, empty );
            if ( !empty ) {
                setGraphic( _button );
            } else {
                setGraphic( null );
            }
        }
    }

}
