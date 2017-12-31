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

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
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
import land.tower.core.model.tournament.ObservableMatch;
import land.tower.core.view.component.FaButton;

/**
 * Created on 30/12/2017
 * @author Cédric Longo
 */
public final class TournamentRoundTab extends Tab {

    public TournamentRoundTab( final TournamentRoundTabModel model ) {
        _model = model;
        textProperty( ).bind( Bindings.concat( _model.getI18n( ).get( "tournament.tab.round" ), " #",
                                               _model.getRound( ).numeroProperty( ) ) );

        final Label icon = new Label( FontAwesome.LIGHTNING );
        icon.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
        setGraphic( icon );

        final BorderPane main = new BorderPane( );

        main.setTop( buildActionBox( ) );
        main.setCenter( buildMatchList( ) );

        setContent( main );
    }

    private TableView<ObservableMatch> buildMatchList( ) {
        final TableView<ObservableMatch> tableView = new TableView<>( );
        tableView.setColumnResizePolicy( CONSTRAINED_RESIZE_POLICY );
        tableView.itemsProperty( ).bind( new SimpleListProperty<>( _model.getRound( ).getMatches( ) ) );

        final TableColumn<ObservableMatch, String> posCol = new TableColumn<>( );
        posCol.setEditable( false );
        posCol.setMinWidth( 100 );
        posCol.setMaxWidth( 100 );
        posCol.textProperty( ).bind( _model.getI18n( ).get( "match.position" ) );
        posCol.setCellValueFactory( new PropertyValueFactory<>( "position" ) );
        tableView.getColumns( ).add( posCol );

        final TableColumn<ObservableMatch, String> teamLeftCol = new TableColumn<>( );
        teamLeftCol.setEditable( false );
        teamLeftCol.textProperty( ).bind( _model.getI18n( ).get( "match.team.left" ) );
        teamLeftCol.setCellValueFactory(
            param -> {
                final int teamId = param.getValue( ).getLeftTeamId( );
                return _model.getTournament( ).getTeam( teamId ).nameProperty( );
            } );
        tableView.getColumns( ).add( teamLeftCol );

        final TableColumn<ObservableMatch, String> scoreCol = new TableColumn<>( );
        scoreCol.setEditable( false );
        scoreCol.textProperty( ).bind( _model.getI18n( ).get( "match.score" ) );
        scoreCol.setPrefWidth( tableView.widthProperty( ).divide( 3 ).getValue( ) );
        tableView.getColumns( ).add( scoreCol );

        // CODEREVIEW These columns should be not reorderable but available only since Java 9
        final TableColumn<ObservableMatch, String> winsLeftCol = new TableColumn<>( );
        winsLeftCol.setEditable( false );
        winsLeftCol.setPrefWidth( 100 );
        winsLeftCol.textProperty( ).bind( _model.getI18n( ).get( "match.score.wins" ) );
        winsLeftCol.setCellValueFactory(
            param -> {
                final ObservableMatch match = param.getValue( );
                final SimpleStringProperty value = new SimpleStringProperty( );
                value.bind( Bindings.createStringBinding( ( ) -> {
                    return match.hasScore( ) ? String.valueOf( match.getScoreLeft( ) ) : "";
                }, match.hasScoreProperty( ), match.scoreLeftProperty( ) ) );
                return value;
            } );
        scoreCol.getColumns( ).add( winsLeftCol );

        final TableColumn<ObservableMatch, String> drawCol = new TableColumn<>( );
        drawCol.setEditable( false );
        drawCol.setPrefWidth( 100 );
        drawCol.textProperty( ).bind( _model.getI18n( ).get( "match.score.draw" ) );
        drawCol.setCellValueFactory(
            param -> {
                final ObservableMatch match = param.getValue( );
                final SimpleStringProperty value = new SimpleStringProperty( );
                value.bind( Bindings.createStringBinding( ( ) -> {
                    return match.hasScore( ) ? String.valueOf( match.getScoreDraw( ) ) : "";
                }, match.hasScoreProperty( ), match.scoreDrawProperty( ) ) );
                return value;
            }
        );
        scoreCol.getColumns( ).add( drawCol );

        final TableColumn<ObservableMatch, String> winsRightCol = new TableColumn<>( );
        winsRightCol.setEditable( false );
        winsRightCol.setPrefWidth( 100 );
        winsRightCol.textProperty( ).bind( _model.getI18n( ).get( "match.score.wins" ) );
        winsRightCol.setCellValueFactory( param -> {
            final ObservableMatch match = param.getValue( );
            final SimpleStringProperty value = new SimpleStringProperty( );
            value.bind( Bindings.createStringBinding( ( ) -> {
                return match.hasScore( ) ? String.valueOf( match.getScoreRight( ) ) : "";
            }, match.hasScoreProperty( ), match.scoreRightProperty( ) ) );
            return value;
        } );
        scoreCol.getColumns( ).add( winsRightCol );

        final TableColumn<ObservableMatch, String> teamRightCol = new TableColumn<>( );
        teamRightCol.setEditable( false );
        teamRightCol.textProperty( ).bind( _model.getI18n( ).get( "match.team.right" ) );
        teamRightCol.setCellValueFactory(
            param -> {
                final int teamId = param.getValue( ).getRightTeamId( );
                return _model.getTournament( ).getTeam( teamId ).nameProperty( );
            } );
        tableView.getColumns( ).add( teamRightCol );

        return tableView;
    }

    private HBox buildActionBox( ) {
        final HBox hBox = new HBox( );
        hBox.setSpacing( 20 );
        hBox.setPadding( new Insets( 10 ) );
        hBox.setAlignment( Pos.CENTER_RIGHT );

        final FaButton setScoreButton = new FaButton( FontAwesome.PLUS, "white" );
        setScoreButton.textProperty( ).bind( _model.getI18n( ).get( "tournament.round.scoring" ) );
        setScoreButton.getStyleClass( ).add( "rich-button" );
        setScoreButton.getStyleClass( ).add( "action-button" );
        hBox.getChildren( ).add( setScoreButton );

        return hBox;
    }

    private final TournamentRoundTabModel _model;
}
