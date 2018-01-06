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

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import javafx.application.Platform;
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
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javax.inject.Inject;
import land.tower.core.ext.binding.Strings;
import land.tower.core.ext.effect.Effects;
import land.tower.core.ext.font.FontAwesome;
import land.tower.core.view.component.FaButton;
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

        final Button addButton = new FaButton( FontAwesome.PLUS, "white" );
        addButton.textProperty( )
                 .bind( Bindings.concat( Strings.toUpperCase( model.getI18n( ).get( "player.add.action" ) ) ) );
        addButton.setOnMouseClicked( e -> {
            new AddPlayerDialog( model.newAddPlayerDialogModel( ) )
                .showAndWait( )
                .ifPresent( _model::firePlayerCreated );
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
        title.textProperty( ).bind( model.getI18n( ).get( "player.management.title" ) );
        title.setEffect( Effects.dropShadow( ) );

        final HBox header = new HBox( homeButton, addButton, spacing, title );
        header.setPadding( new Insets( 10, 20, 10, 10 ) );
        header.setSpacing( 10 );
        header.setAlignment( Pos.CENTER_LEFT );
        setTop( header );

        final TableView<ObservablePlayer> tableView = new TableView<>( );
        tableView.setColumnResizePolicy( CONSTRAINED_RESIZE_POLICY );
        tableView.itemsProperty( ).bind( _model.playerListProperty( ) );

        final TableColumn<ObservablePlayer, Long> numeroCol = new TableColumn<>( );
        numeroCol.textProperty( ).bind( model.getI18n( ).get( "player.numero" ) );
        numeroCol.setCellValueFactory( new PropertyValueFactory<>( "numero" ) );
        tableView.getColumns( ).add( numeroCol );
        tableView.getSortOrder( ).add( numeroCol );
        Platform.runLater( tableView::sort );

        final TableColumn<ObservablePlayer, Void> nationalityCol = new TableColumn<>( );
        nationalityCol.textProperty( ).bind( model.getI18n( ).get( "player.nationality" ) );
        nationalityCol.setCellFactory( param -> new NationalityCell( ) );
        tableView.getColumns( ).add( nationalityCol );

        final TableColumn<ObservablePlayer, String> firstNameCol = new TableColumn<>( "Firstname" );
        firstNameCol.textProperty( ).bind( model.getI18n( ).get( "player.firstname" ) );
        firstNameCol.setCellValueFactory( new PropertyValueFactory<>( "firstname" ) );
        tableView.getColumns( ).add( firstNameCol );

        final TableColumn<ObservablePlayer, String> lastNameCol = new TableColumn<>( "Lastname" );
        lastNameCol.textProperty( ).bind( model.getI18n( ).get( "player.lastname" ) );
        lastNameCol.setCellValueFactory( new PropertyValueFactory<>( "lastname" ) );
        tableView.getColumns( ).add( lastNameCol );

        final TableColumn<ObservablePlayer, String> birthdayCol = new TableColumn<>( "Birthday" );
        birthdayCol.textProperty( ).bind( model.getI18n( ).get( "player.birthday" ) );
        birthdayCol.setCellValueFactory( new PropertyValueFactory<>( "birthday" ) );
        tableView.getColumns( ).add( birthdayCol );

        final TableColumn<ObservablePlayer, Void> actionColumn = new TableColumn<>( );
        actionColumn.setMaxWidth( 50 );
        actionColumn.setMinWidth( 50 );
        actionColumn.setResizable( false );
        actionColumn.setCellFactory( param -> new DeletePlayerCell( ) );
        tableView.getColumns( ).add( actionColumn );

        final Label emptyLabel = new Label( );
        emptyLabel.textProperty( ).bind( model.getI18n( ).get( "player.management.no.player" ) );
        tableView.setPlaceholder( emptyLabel );

        setCenter( tableView );
    }

    private final PlayerManagementViewModel _model;

    private class NationalityCell extends TableCell<ObservablePlayer, Void> {

        @Override
        protected void updateItem( final Void item, final boolean empty ) {
            super.updateItem( item, empty );
            if ( empty ) {
                setGraphic( null );
                return;
            }
            final Player player = getTableView( ).getItems( ).get( getIndex( ) ).getPlayer( );
            final String iconName = "img/country/" + player.getNationality( ).name( ).toLowerCase( ) + ".png";
            try ( final InputStream imStream = getClass( ).getClassLoader( ).getResourceAsStream( iconName ) ) {
                if ( imStream != null ) {
                    final Image icon = new Image( imStream, 20, 20, true, true );
                    final ImageView image = new ImageView( icon );
                    final Tooltip tooltip = new Tooltip( _model.getI18n( )
                                                               .get( "nationality." + player.getNationality( ).name( ) )
                                                               .get( ) );
                    setTooltip( tooltip );
                    setGraphic( image );
                } else {
                    setGraphic( null );
                }
            } catch ( final IOException ignored ) {
            }
        }
    }

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
                alert.headerTextProperty( ).bind( _model.getI18n( ).get( "player.delete.title" ) );
                alert.contentTextProperty( ).bind( _model.getI18n( ).get( "player.delete.message" ) );

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
                    _model.firePlayerDeleted( player );
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
