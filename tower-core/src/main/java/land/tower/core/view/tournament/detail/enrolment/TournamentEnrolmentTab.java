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

package land.tower.core.view.tournament.detail.enrolment;

import static javafx.beans.binding.Bindings.createBooleanBinding;
import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

import java.util.Optional;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javax.inject.Inject;
import land.tower.core.ext.font.FontAwesome;
import land.tower.core.model.tournament.ObservableTeam;
import land.tower.core.view.component.FaButton;
import land.tower.data.Team;
import land.tower.data.TournamentStatus;

/**
 * Created on 20/12/2017
 * @author Cédric Longo
 */
public final class TournamentEnrolmentTab extends Tab {

    @Inject
    public TournamentEnrolmentTab( final TournamentEnrolmentTabModel model ) {
        _model = model;
        textProperty( ).bind( _model.getI18n( ).get( "tournament.tab.enrolment" ) );

        final Label icon = new Label( FontAwesome.PLAYERS );
        icon.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
        setGraphic( icon );

        final BorderPane main = new BorderPane( );

        main.setTop( buildActionBox( ) );
        main.setCenter( buildTeamList( ) );

        setContent( main );
    }

    private HBox buildActionBox( ) {
        final HBox hBox = new HBox( );
        hBox.setSpacing( 20 );
        hBox.setPadding( new Insets( 10 ) );
        hBox.setAlignment( Pos.CENTER_RIGHT );

        final FaButton startTournamentButton = new FaButton( FontAwesome.LIGHTNING, "white" );
        startTournamentButton.textProperty( ).bind( _model.getI18n( ).get( "tournament.enrolment.start.tournament" ) );
        startTournamentButton.getStyleClass( ).add( "rich-button" );
        startTournamentButton.getStyleClass( ).add( "action-button" );
        startTournamentButton.setOnAction( event -> _model.fireStartTournament( ) );
        startTournamentButton.disableProperty( )
                             .bind( new SimpleListProperty<>( _model.getTournament( ).getTeams( ) )
                                        .sizeProperty( )
                                        .lessThan( 2 ) );
        startTournamentButton.visibleProperty( )
                             .bind( new SimpleListProperty<>( _model.getTournament( ).getRounds( ) ).emptyProperty( ) );
        hBox.getChildren( ).add( startTournamentButton );

        final FaButton addTeamButton = new FaButton( FontAwesome.PLUS, "white" );
        addTeamButton.textProperty( ).bind( _model.getI18n( ).get( "tournament.enrolment.add.team" ) );
        addTeamButton.getStyleClass( ).add( "rich-button" );
        addTeamButton.getStyleClass( ).add( "action-button" );
        addTeamButton.setOnAction( event -> {
            // Open Add team dialog
            new AddTeamDialog( _model.newAddTeamDialogModel( ) )
                .showAndWait( )
                .ifPresent( _model::fireTeamAdded );
        } );
        hBox.getChildren( ).add( addTeamButton );

        hBox.visibleProperty( )
            .bind( createBooleanBinding(
                ( ) -> _model.getTournament( ).getHeader( ).getStatus( ) != TournamentStatus.CLOSED,
                _model.getTournament( ).getHeader( ).statusProperty( ) ) );

        return hBox;
    }

    private TableView<ObservableTeam> buildTeamList( ) {
        final TableView<ObservableTeam> tableView = new TableView<>( );
        tableView.setEditable( true );
        tableView.setColumnResizePolicy( CONSTRAINED_RESIZE_POLICY );
        tableView.itemsProperty( ).bind( new SimpleListProperty<>( _model.getTournament( ).getTeams( ) ) );

        final TableColumn<ObservableTeam, Integer> idCol = new TableColumn<>( );
        idCol.setEditable( false );
        idCol.setMaxWidth( 50 );
        idCol.setMinWidth( 50 );
        idCol.setResizable( false );
        idCol.textProperty( ).bind( _model.getI18n( ).get( "team.id" ) );
        idCol.setCellValueFactory( new PropertyValueFactory<>( "id" ) );
        tableView.getColumns( ).add( idCol );
        tableView.getSortOrder( ).add( idCol );
        Platform.runLater( tableView::sort );

        final TableColumn<ObservableTeam, Boolean> activeCol = new TableColumn<>( );
        activeCol.setMaxWidth( 50 );
        activeCol.setMinWidth( 50 );
        activeCol.setResizable( false );
        activeCol.textProperty( ).bind( _model.getI18n( ).get( "team.active" ) );
        activeCol.setCellValueFactory( new PropertyValueFactory<>( "active" ) );
        activeCol.setCellFactory( CheckBoxTableCell.forTableColumn( activeCol ) );
        activeCol.editableProperty( )
                 .bind( createBooleanBinding(
                     ( ) -> _model.getTournament( ).getHeader( ).getStatus( ) != TournamentStatus.CLOSED,
                     _model.getTournament( ).getHeader( ).statusProperty( ) ) );
        tableView.getColumns( ).add( activeCol );

        final TableColumn<ObservableTeam, String> nameCol = new TableColumn<>( );
        nameCol.textProperty( ).bind( _model.getI18n( ).get( "team.name" ) );
        nameCol.setCellValueFactory( new PropertyValueFactory<>( "name" ) );
        tableView.getColumns( ).add( nameCol );

        final TableColumn<ObservableTeam, String> playersCol = new TableColumn<>( );
        playersCol.textProperty( ).bind( _model.getI18n( ).get( "team.players" ) );
        playersCol.setCellValueFactory(
            param -> new ReadOnlyStringWrapper( param.getValue( ).getTeam( ).getPlayers( ).stream( )
                                                     .map( p -> String.format( "%s %s",
                                                                               p.getLastname( ).toUpperCase( ),
                                                                               p.getFirstname( ) ) )
                                                     .collect( Collectors.joining( ", " ) ) ) );
        tableView.getColumns( ).add( playersCol );

        final TableColumn<ObservableTeam, Void> actionColumn = new TableColumn<>( );
        actionColumn.setEditable( false );
        actionColumn.setMaxWidth( 50 );
        actionColumn.setMinWidth( 50 );
        actionColumn.setResizable( false );
        actionColumn.setCellFactory( param -> new DeleteTeamCell( ) );
        tableView.getColumns( ).add( actionColumn );

        final Label emptyLabel = new Label( );
        emptyLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.enrolment.no.team" ) );
        tableView.setPlaceholder( emptyLabel );

        return tableView;
    }

    private class DeleteTeamCell extends TableCell<ObservableTeam, Void> {

        final Button _button = new Button( FontAwesome.DELETE );

        DeleteTeamCell( ) {
            _button.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
            _button.getStyleClass( ).add( "rich-button" );
            _button.getStyleClass( ).add( "dangerous-button" );
            _button.setCursor( Cursor.HAND );
            _button.disableProperty( ).bind( createBooleanBinding( ( ) -> {
                final TournamentStatus status = _model.getTournament( ).getHeader( ).getStatus( );
                switch ( status ) {
                    case NOT_CONFIGURED:
                    case PLANNED:
                    case ENROLMENT:
                        return false;
                    default:
                        return true;
                }
            }, _model.getTournament( ).getHeader( ).statusProperty( ) ) );

            _button.setOnAction( t -> {
                final Team team = getTableView( ).getItems( ).get( getIndex( ) ).getTeam( );

                final Alert alert = new Alert( AlertType.WARNING );
                alert.headerTextProperty( ).bind( _model.getI18n( ).get( "team.delete.title" ) );
                alert.contentTextProperty( ).bind( _model.getI18n( ).get( "team.delete.message" ) );

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
                    _model.fireTeamDeleted( team );
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

    private final TournamentEnrolmentTabModel _model;
}
