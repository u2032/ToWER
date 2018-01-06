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

package land.tower.core.view.tournament.detail.ladder;

import static javafx.beans.binding.Bindings.createBooleanBinding;
import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import land.tower.core.ext.font.FontAwesome;
import land.tower.core.model.tournament.ObservableTeam;
import land.tower.core.view.component.FaButton;
import land.tower.data.TournamentStatus;

/**
 * Created on 01/01/2018
 * @author Cédric Longo
 */
public final class TournamentLadderView extends Tab {

    public TournamentLadderView( final TournamentLadderViewModel model ) {
        _model = model;
        textProperty( ).bind( _model.getI18n( ).get( "tournament.tab.ladder" ) );

        final Label icon = new Label( FontAwesome.LADDER );
        icon.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
        setGraphic( icon );

        final BorderPane main = new BorderPane( );

        main.setTop( buildActionBox( ) );
        main.setCenter( buildTeamList( ) );

        setContent( main );
    }

    private TableView<ObservableTeam> buildTeamList( ) {
        final TableView<ObservableTeam> tableView = new TableView<>( );
        tableView.setColumnResizePolicy( CONSTRAINED_RESIZE_POLICY );
        tableView.itemsProperty( ).bind( new SimpleListProperty<>( _model.getTournament( ).getTeams( ) ) );

        final TableColumn<ObservableTeam, Integer> rankCol = new TableColumn<>( );
        rankCol.setMaxWidth( 50 );
        rankCol.setMinWidth( 50 );
        rankCol.setResizable( false );
        rankCol.textProperty( ).bind( _model.getI18n( ).get( "ranking.rank" ) );
        rankCol.setCellValueFactory( param -> param.getValue( ).getRanking( ).rankProperty( ) );
        tableView.getColumns( ).add( rankCol );
        tableView.getSortOrder( ).add( rankCol );
        setOnSelectionChanged( t -> {
            if ( isSelected( ) ) {
                tableView.sort( );
            }
        } );

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

        final TableColumn<ObservableTeam, Integer> pointsCol = new TableColumn<>( );
        pointsCol.textProperty( ).bind( _model.getI18n( ).get( "ranking.points" ) );
        pointsCol.setCellValueFactory( param -> param.getValue( ).getRanking( ).pointsProperty( ) );
        tableView.getColumns( ).add( pointsCol );

        final TableColumn<ObservableTeam, Integer> dCol = new TableColumn<>( );
        dCol.textProperty( ).bind( _model.getI18n( ).get( "ranking.deciding" ) );
        tableView.getColumns( ).add( dCol );

        final TableColumn<ObservableTeam, Integer> d1Col = new TableColumn<>( );
        d1Col.setMaxWidth( 50 );
        d1Col.setMinWidth( 50 );
        d1Col.setText( "D1" );
        d1Col.setCellValueFactory( param -> param.getValue( ).getRanking( ).d1Property( ) );
        dCol.getColumns( ).add( d1Col );

        final TableColumn<ObservableTeam, Integer> d2Col = new TableColumn<>( );
        d2Col.setMaxWidth( 50 );
        d2Col.setMinWidth( 50 );
        d2Col.setText( "D2" );
        d2Col.setCellValueFactory( param -> param.getValue( ).getRanking( ).d2Property( ) );
        dCol.getColumns( ).add( d2Col );

        final TableColumn<ObservableTeam, Integer> d3Col = new TableColumn<>( );
        d3Col.setMaxWidth( 50 );
        d3Col.setMinWidth( 50 );
        d3Col.setText( "D3" );
        d3Col.setCellValueFactory( param -> param.getValue( ).getRanking( ).d3Property( ) );
        dCol.getColumns( ).add( d3Col );

        final TableColumn<ObservableTeam, Integer> d4Col = new TableColumn<>( );
        d4Col.setMaxWidth( 50 );
        d4Col.setMinWidth( 50 );
        d4Col.setText( "D4" );
        d4Col.setCellValueFactory( param -> param.getValue( ).getRanking( ).d4Property( ) );
        dCol.getColumns( ).add( d4Col );

        final Label emptyLabel = new Label( );
        emptyLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.ranking.no.team" ) );
        tableView.setPlaceholder( emptyLabel );

        return tableView;
    }

    private HBox buildActionBox( ) {
        final HBox hBox = new HBox( );
        hBox.setSpacing( 20 );
        hBox.setPadding( new Insets( 10 ) );
        hBox.setAlignment( Pos.CENTER_RIGHT );

        final FaButton chainButton = new FaButton( FontAwesome.CONTINUE, "white" );
        chainButton.textProperty( ).bind( _model.getI18n( ).get( "tournament.round.chain" ) );
        chainButton.getStyleClass( ).add( "rich-button" );
        chainButton.getStyleClass( ).add( "action-button" );
        chainButton.setOnAction( event -> {
            _model.fireChainTournamentDialog( );
        } );
        chainButton.visibleProperty( ).bind( _model.getTournament( ).getHeader( ).statusProperty( )
                                                   .isEqualTo( TournamentStatus.CLOSED ) );
        hBox.getChildren( ).add( chainButton );

        final FaButton closeButton = new FaButton( FontAwesome.LOCK, "white" );
        closeButton.textProperty( ).bind( _model.getI18n( ).get( "tournament.round.close" ) );
        closeButton.getStyleClass( ).add( "rich-button" );
        closeButton.getStyleClass( ).add( "dangerous-button" );
        closeButton.setOnAction( event -> {
            new CloseTournamentDialog( _model.createCloseTournamentViewModel( ) ).show( );
        } );
        closeButton.visibleProperty( )
                   .bind( createBooleanBinding(
                       ( ) -> _model.getTournament( ).getHeader( ).getStatus( ) != TournamentStatus.CLOSED,
                       _model.getTournament( ).getHeader( ).statusProperty( ) ) );

        final Runnable updateDisableCloseButton = ( ) -> {
            closeButton.setDisable(
                _model.getTournament( ).getCurrentRound( ) == null
                || !_model.getTournament( ).getCurrentRound( ).isEnded( )
                || _model.getTournament( ).getHeader( ).getStatus( ) == TournamentStatus.CLOSED );
        };
        updateDisableCloseButton.run( );

        _model.getTournament( ).getHeader( ).statusProperty( )
              .addListener( ( observable, oldValue, newValue ) -> updateDisableCloseButton.run( ) );

        _model.getTournament( ).currentRoundProperty( )
              .addListener( ( observable, oldValue, newValue ) -> {
                  updateDisableCloseButton.run( );
                  if ( newValue != null ) {
                      newValue.endedProperty( )
                              .addListener( ( observable1, oldValue1, newValue1 ) -> updateDisableCloseButton.run( ) );
                  }
              } );
        hBox.getChildren( ).add( closeButton );

        return hBox;
    }

    private final TournamentLadderViewModel _model;

}
