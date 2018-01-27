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

package land.tower.core.view.tournament.detail.round;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javax.inject.Inject;
import land.tower.core.ext.font.FontAwesome;
import land.tower.core.model.tournament.ObservableMatch;
import land.tower.core.model.tournament.ObservableTeam;

/**
 * Created on 25/01/2018
 * @author Cédric Longo
 */
final class ManualPairingDialog extends Dialog<Void> {

    @Inject
    ManualPairingDialog( final ManualPairingDialogModel model ) {
        initOwner( model.getOwner( ) );
        setResizable( true );
        getDialogPane( ).setPrefWidth( getOwner( ).widthProperty( ).multiply( 0.9 ).getValue( ) );

        _model = model;
        titleProperty( ).bind( model.getI18n( ).get( "tournament.round.manual.pairing" ) );
        headerTextProperty( ).bind( model.getI18n( ).get( "tournament.round.manual.pairing" ) );

        final ButtonType saveButtonType =
            new ButtonType( model.getI18n( ).get( "action.save" ).get( ).toUpperCase( ), ButtonData.OK_DONE );

        final ButtonType cancelButtonType =
            new ButtonType( model.getI18n( ).get( "action.cancel" ).get( ).toUpperCase( ), ButtonData.CANCEL_CLOSE );
        getDialogPane( ).getButtonTypes( ).addAll( saveButtonType, cancelButtonType );

        final Button saveButton = (Button) getDialogPane( ).lookupButton( saveButtonType );
        saveButton.setDefaultButton( true );
        saveButton.setOnAction( event -> {
            _model.fireSavePairing( );
        } );

        // Build content
        final VBox main = new VBox( );
        main.setSpacing( 5 );
        main.setAlignment( Pos.CENTER );

        final HBox hBox = new HBox( );
        hBox.setAlignment( Pos.CENTER );
        hBox.setSpacing( 20 );
        main.getChildren( ).add( hBox );

        final TableView<ObservableMatch> teamTable = buildMatchTable( );
        teamTable.prefWidthProperty( ).bind( getDialogPane( ).widthProperty( )
                                                             .divide( 2 )
                                                             .subtract( 60 ) );
        hBox.getChildren( ).add( teamTable );

        final VBox buttonContainer = new VBox( );
        buttonContainer.setAlignment( Pos.CENTER );
        buttonContainer.setSpacing( 20 );
        hBox.getChildren( ).add( buttonContainer );

        final Button pairButton = new Button( FontAwesome.PAIR );
        pairButton.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
        pairButton.getStyleClass( ).add( "rich-button" );
        pairButton.setStyle( "-fx-rotate: 180" );
        pairButton.setTooltip( new Tooltip( _model.getI18n( ).get( "manual.pairing.pair" ).get( ) ) );
        pairButton.setOnAction( e -> {
            final ObservableList<ObservableTeam> selected = _teamTable.getSelectionModel( ).getSelectedItems( );
            _model.firePairTeams( selected );
            _matchTable.sort( );
        } );
        buttonContainer.getChildren( ).add( pairButton );

        final Button unpairButton = new Button( FontAwesome.UNPAIR );
        unpairButton.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
        unpairButton.getStyleClass( ).add( "rich-button" );
        unpairButton.setTooltip( new Tooltip( _model.getI18n( ).get( "manual.pairing.unpair" ).get( ) ) );
        unpairButton.setOnAction( e -> {
            final ObservableList<ObservableMatch> selected = _matchTable.getSelectionModel( ).getSelectedItems( );
            _model.fireUnpairMatches( selected );
            _teamTable.sort( );
        } );
        buttonContainer.getChildren( ).add( unpairButton );

        final Button pairByeButton = new Button( FontAwesome.BYE_USER );
        pairByeButton.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
        pairByeButton.getStyleClass( ).add( "rich-button" );
        pairByeButton.setTooltip( new Tooltip( _model.getI18n( ).get( "manual.pairing.pair.bye" ).get( ) ) );
        pairByeButton.setOnAction( e -> {
            final ObservableList<ObservableTeam> selected = _teamTable.getSelectionModel( ).getSelectedItems( );
            _model.firePairByeTeams( selected );
            _matchTable.sort( );
        } );
        buttonContainer.getChildren( ).add( pairByeButton );

        final TableView<ObservableTeam> playerTable = buildPlayerTable( );
        playerTable.prefWidthProperty( ).bind( getDialogPane( ).widthProperty( )
                                                               .divide( 2 )
                                                               .subtract( 60 ) );
        hBox.getChildren( ).add( playerTable );

        if ( !_model.getTournament( ).getCurrentRound( ).equals( _model.getRound( ) ) ) {
            final Label warnLabel = new Label( );
            final Text icon = new Text( FontAwesome.WARNING );
            icon.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
            warnLabel.setGraphic( icon );

            warnLabel.setStyle( "-fx-text-fill: orange;" );
            warnLabel.getStyleClass( ).add( "important" );
            warnLabel.textProperty( )
                     .bind( _model.getI18n( ).get( "tournament.round.scoring.not.current.round.warning" ) );
            warnLabel.setWrapText( true );
            warnLabel.setPrefHeight( 40 );
            main.getChildren( ).add( warnLabel );
        }

        getDialogPane( ).setContent( main );
    }

    private TableView<ObservableTeam> buildPlayerTable( ) {
        _teamTable = new TableView<>( );
        _teamTable.setColumnResizePolicy( CONSTRAINED_RESIZE_POLICY );
        _teamTable.getSelectionModel( ).setSelectionMode( SelectionMode.MULTIPLE );
        _teamTable.itemsProperty( ).bind( _model.teamsProperty( ) );

        final TableColumn<ObservableTeam, String> nameCol = new TableColumn<>( );
        nameCol.textProperty( ).bind( _model.getI18n( ).get( "team.name" ) );
        nameCol.setCellValueFactory( new PropertyValueFactory<>( "name" ) );
        _teamTable.getColumns( ).add( nameCol );

        final TableColumn<ObservableTeam, Integer> rankCol = new TableColumn<>( );
        rankCol.textProperty( ).bind( _model.getI18n( ).get( "ranking.rank" ) );
        rankCol.setCellValueFactory( param -> param.getValue( ).getRanking( ).rankProperty( ) );
        _teamTable.getColumns( ).add( rankCol );

        final TableColumn<ObservableTeam, Integer> pointsCol = new TableColumn<>( );
        pointsCol.textProperty( ).bind( _model.getI18n( ).get( "ranking.points" ) );
        pointsCol.setCellValueFactory( param -> param.getValue( ).getRanking( ).pointsProperty( ) );
        _teamTable.getColumns( ).add( pointsCol );

        final Label emptyLabel = new Label( );
        emptyLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.enrolment.no.team" ) );
        _teamTable.setPlaceholder( emptyLabel );

        return _teamTable;
    }

    private TableView<ObservableMatch> buildMatchTable( ) {
        _matchTable = new TableView<>( );
        _matchTable.setColumnResizePolicy( CONSTRAINED_RESIZE_POLICY );
        _matchTable.getSelectionModel( ).setSelectionMode( SelectionMode.MULTIPLE );
        _matchTable.itemsProperty( ).bind( _model.matchesProperty( ) );

        final TableColumn<ObservableMatch, Integer> posCol = new TableColumn<>( );
        posCol.setEditable( false );
        posCol.setMaxWidth( 100 );
        posCol.textProperty( ).bind( _model.getI18n( ).get( "match.position" ) );
        posCol.setCellValueFactory( new PropertyValueFactory<>( "position" ) );
        _matchTable.getColumns( ).add( posCol );
        _matchTable.getSortOrder( ).add( posCol );
        Platform.runLater( _matchTable::sort );

        final TableColumn<ObservableMatch, String> teamLeftCol = new TableColumn<>( );
        teamLeftCol.setEditable( false );
        teamLeftCol.textProperty( ).bind( _model.getI18n( ).get( "match.team.left" ) );
        teamLeftCol.setCellValueFactory(
            param -> {
                final int teamId = param.getValue( ).getLeftTeamId( );
                return _model.getTournament( ).getTeam( teamId ).nameProperty( );
            } );
        _matchTable.getColumns( ).add( teamLeftCol );

        final TableColumn<ObservableMatch, String> teamRightCol = new TableColumn<>( );
        teamRightCol.setEditable( false );
        teamRightCol.textProperty( ).bind( _model.getI18n( ).get( "match.team.right" ) );
        teamRightCol.setCellValueFactory(
            param -> {
                final int teamId = param.getValue( ).getRightTeamId( );
                return _model.getTournament( ).getTeam( teamId ).nameProperty( );
            } );
        _matchTable.getColumns( ).add( teamRightCol );

        final Label emptyLabel = new Label( );
        emptyLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.round.no.match" ) );
        _matchTable.setPlaceholder( emptyLabel );

        return _matchTable;
    }

    private final ManualPairingDialogModel _model;

    private TableView<ObservableMatch> _matchTable;
    private TableView<ObservableTeam> _teamTable;
}
