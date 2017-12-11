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

package land.tower.core.view.player;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;
import static javafx.scene.layout.HBox.setHgrow;

import java.util.Optional;
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
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javax.inject.Inject;
import land.tower.core.ext.effect.Effects;
import land.tower.core.ext.font.FontAwesome;
import land.tower.data.Player;

/**
 * Created on 09/12/2017
 * @author Cédric Longo
 */
public final class PlayerManagementView extends BorderPane {

    @Inject
    public PlayerManagementView( final PlayerManagementViewModel model ) {
        _model = model;

        final Button homeButton = new Button( FontAwesome.HOME );
        homeButton.setOnMouseClicked( e -> model.fireHomeButton( ) );
        homeButton.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
        homeButton.getStyleClass( ).add( "rich-button" );

        final Button addButton = new Button( );
        addButton.textProperty( ).bind( model.i18nAddPlayerActionProperty( ) );
        addButton.setOnMouseClicked( e -> {
            new AddPlayerDialog( model.newAddPlayerDialogModel( ) )
                .showAndWait( )
                .ifPresent( _model::firePlayerCreated );
        } );
        addButton.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
        addButton.getStyleClass( ).add( "rich-button" );
        addButton.getStyleClass( ).add( "action-button" );

        final HBox spacing = new HBox( );
        setHgrow( spacing, Priority.ALWAYS );
        getChildren( ).add( spacing );

        final Label title = new Label( );
        title.getStyleClass( ).add( "title" );
        title.getStyleClass( ).add( "large" );
        title.getStyleClass( ).add( "important" );
        title.textProperty( ).bind( model.i18nPlayerManagementTitleProperty( ) );
        title.setEffect( Effects.dropShadow( ) );

        final HBox header = new HBox( homeButton, addButton, spacing, title );
        header.setPadding( new Insets( 10, 20, 10, 10 ) );
        header.setSpacing( 10 );
        header.setAlignment( Pos.CENTER_LEFT );
        setTop( header );

        final TableView<ObservablePlayer> tableView = new TableView<>( );
        tableView.setColumnResizePolicy( CONSTRAINED_RESIZE_POLICY );

        final TableColumn<ObservablePlayer, String> numeroCol = new TableColumn<>( );
        numeroCol.textProperty( ).bind( model.i18nPlayerNumeroProperty( ) );
        numeroCol.setCellValueFactory( new PropertyValueFactory<>( "numero" ) );
        tableView.getColumns( ).add( numeroCol );

        final TableColumn<ObservablePlayer, String> firstNameCol = new TableColumn<>( "Firstname" );
        firstNameCol.textProperty( ).bind( model.i18nPlayerFirstProperty( ) );
        firstNameCol.setCellValueFactory( new PropertyValueFactory<>( "firstname" ) );
        tableView.getColumns( ).add( firstNameCol );

        final TableColumn<ObservablePlayer, String> lastNameCol = new TableColumn<>( "Lastname" );
        lastNameCol.textProperty( ).bind( model.i18nPlayerLastnameProperty( ) );
        lastNameCol.setCellValueFactory( new PropertyValueFactory<>( "lastname" ) );
        tableView.getColumns( ).add( lastNameCol );

        final TableColumn<ObservablePlayer, String> birthdayCol = new TableColumn<>( "Birthday" );
        birthdayCol.textProperty( ).bind( model.i18nPlayerBirthdayProperty( ) );
        birthdayCol.setCellValueFactory( new PropertyValueFactory<>( "birthday" ) );
        tableView.getColumns( ).add( birthdayCol );

        final TableColumn<ObservablePlayer, Void> actionColumn = new TableColumn<>( );
        actionColumn.setMaxWidth( 50 );
        actionColumn.setMinWidth( 50 );
        actionColumn.setResizable( false );
        actionColumn.setCellFactory( param -> new DeletePlayerCell( ) );
        tableView.getColumns( ).add( actionColumn );

        final Label emptyLabel = new Label( );
        emptyLabel.textProperty( ).bind( model.i18nPlaceholderProperty( ) );
        tableView.setPlaceholder( emptyLabel );

        tableView.itemsProperty( ).bind( _model.playerListProperty( ) );

        setCenter( tableView );
    }

    private final PlayerManagementViewModel _model;

    private class DeletePlayerCell extends TableCell<ObservablePlayer, Void> {

        final Button _button = new Button( FontAwesome.DELETE );

        DeletePlayerCell( ) {
            _button.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
            _button.getStyleClass( ).add( "rich-button" );
            _button.getStyleClass( ).add( "dangerous-button" );
            _button.setCursor( Cursor.HAND );
            _button.setOnAction( t -> {
                final Player player = getTableView( ).getItems( ).get( getIndex( ) ).getPlayer( );

                final Alert alert = new Alert( AlertType.WARNING );
                alert.headerTextProperty( ).bind( _model.i18nDeletePlayerTitleProperty( ) );
                alert.contentTextProperty( ).bind( _model.i18nDeletePlayerMessageProperty( ) );

                final ButtonType deleteButtonType = new ButtonType( _model.getI18nDeleteAction( ),
                                                                    ButtonData.APPLY );
                final ButtonType cancelButtonType = new ButtonType( _model.getI18nCancelAction( ),
                                                                    ButtonData.CANCEL_CLOSE );
                alert.getDialogPane( ).getButtonTypes( ).setAll( deleteButtonType, cancelButtonType );

                final Button cancelButton = (Button) alert.getDialogPane( ).lookupButton( cancelButtonType );
                cancelButton.setDefaultButton( true );

                final Button deleteButton = (Button) alert.getDialogPane( ).lookupButton( deleteButtonType );
                deleteButton.setDefaultButton( false );

                final Optional<ButtonType> clicked = alert.showAndWait( );
                if ( clicked.isPresent( ) && clicked.get( ) == deleteButtonType ) {
                    _model.firePlayerDeleted( player );
                }
            } );
        }

        @Override
        protected void updateItem( Void t, boolean empty ) {
            super.updateItem( t, empty );
            if ( !empty ) {
                setGraphic( _button );
            }
        }
    }
}
