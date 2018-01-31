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

import static javafx.beans.binding.Bindings.createBooleanBinding;
import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;
import static javafx.scene.layout.HBox.setHgrow;
import static land.tower.core.ext.binding.Strings.toUpperCase;

import com.jfoenix.controls.JFXCheckBox;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.util.converter.IntegerStringConverter;
import land.tower.core.ext.font.FontAwesome;
import land.tower.core.model.tournament.ObservableMatch;
import land.tower.core.model.tournament.ObservableTimer;
import land.tower.core.view.component.FaButton;
import land.tower.core.view.component.FaMenu;
import land.tower.core.view.component.FaMenuItem;
import land.tower.data.TournamentStatus;

/**
 * Created on 30/12/2017
 * @author Cédric Longo
 */
public class TournamentRoundTab extends Tab {

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

        _itemList = new FilteredList<>( _model.getRound( ).getMatches( ) );
        _model.filterNotEmptyScoreProperty( )
              .addListener( ( observable, oldValue, newValue ) -> resetFilter( ) );
        resetFilter( );

        final SortedList<ObservableMatch> sortedList = new SortedList<>( _itemList );
        sortedList.comparatorProperty( ).bind( tableView.comparatorProperty( ) );
        tableView.itemsProperty( ).bind( new SimpleListProperty<>( sortedList ) );

        final TableColumn<ObservableMatch, Integer> posCol = new TableColumn<>( );
        posCol.setEditable( false );
        posCol.setMinWidth( 100 );
        posCol.setMaxWidth( 100 );
        posCol.textProperty( ).bind( _model.getI18n( ).get( "match.position" ) );
        posCol.setCellValueFactory( new PropertyValueFactory<>( "position" ) );
        posCol.setCellFactory( param -> new PositionCell( ) );
        tableView.getColumns( ).add( posCol );
        tableView.getSortOrder( ).add( posCol );
        Platform.runLater( tableView::sort );

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
        addScoreColumns( scoreCol );

        final TableColumn<ObservableMatch, String> teamRightCol = new TableColumn<>( );
        teamRightCol.setEditable( false );
        teamRightCol.textProperty( ).bind( _model.getI18n( ).get( "match.team.right" ) );
        teamRightCol.setCellValueFactory(
            param -> {
                final int teamId = param.getValue( ).getRightTeamId( );
                return _model.getTournament( ).getTeam( teamId ).nameProperty( );
            } );
        tableView.getColumns( ).add( teamRightCol );

        tableView.setRowFactory( tv -> {
            TableRow<ObservableMatch> row = new TableRow<>( );
            row.setOnMouseClicked( event -> {
                if ( event.getClickCount( ) == 2 && ( !row.isEmpty( ) ) ) {
                    if ( _model.getTournament( ).getHeader( ).getStatus( ) == TournamentStatus.CLOSED ) {
                        return;
                    }

                    final ObservableMatch match = row.getItem( );
                    final SetScoreDialog setScoreDialog = _model.createSetScoreDialog( );
                    setScoreDialog.getModel( ).positionProperty( ).set( match.getPosition( ) );
                    if ( match.hasScore( ) ) {
                        setScoreDialog.getModel( ).leftScoreProperty( ).set( match.getScoreLeft( ) );
                        setScoreDialog.getModel( ).drawsProperty( ).set( match.getScoreDraw( ) );
                        setScoreDialog.getModel( ).rightScoreProperty( ).set( match.getScoreRight( ) );
                    }
                    setScoreDialog.setOnCloseRequest( e -> Platform.runLater( this::resetFilter ) );
                    setScoreDialog.show( );
                }
            } );
            return row;
        } );

        final Label emptyLabel = new Label( );
        emptyLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.round.no.match" ) );
        tableView.setPlaceholder( emptyLabel );

        return tableView;
    }

    protected void addScoreColumns( final TableColumn<ObservableMatch, String> scoreCol ) {
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
    }

    private void resetFilter( ) {
        if ( _itemList == null ) {
            return;
        }
        _itemList.setPredicate( m -> true );
        if ( _model.filterNotEmptyScoreProperty( ).get( ) ) {
            _itemList.setPredicate( m -> !m.hasScore( ) );
        }
    }

    private HBox buildActionBox( ) {
        final HBox hBox = new HBox( );
        hBox.setAlignment( Pos.CENTER_LEFT );
        hBox.setSpacing( 15 );
        hBox.setPadding( new Insets( 10 ) );

        final CheckBox checkBox = new JFXCheckBox( );
        checkBox.setStyle( "-fx-font-size: smaller" );
        checkBox.textProperty( ).bind( _model.getI18n( ).get( "tournament.round.filter.not.empty.score" ) );
        checkBox.selectedProperty( ).bindBidirectional( _model.filterNotEmptyScoreProperty( ) );
        final VBox checkboxWrapper = new VBox( checkBox );
        checkboxWrapper.setAlignment( Pos.BOTTOM_LEFT );
        checkboxWrapper.setPadding( new Insets( 5 ) );
        hBox.getChildren( ).add( checkboxWrapper );

        final HBox spacing = new HBox( );
        setHgrow( spacing, Priority.ALWAYS );
        hBox.getChildren( ).add( spacing );

        final HBox clockBox = new HBox( );
        clockBox.setSpacing( 8 );
        clockBox.setAlignment( Pos.CENTER );
        hBox.getChildren( ).add( clockBox );

        final TextField timerEditionField = new TextField( );
        timerEditionField.setAlignment( Pos.CENTER );
        timerEditionField.setTextFormatter(
            new TextFormatter<>( new IntegerStringConverter( ),
                                 null,
                                 c -> {
                                     final boolean matches = Pattern.matches( "\\d*", c.getControlNewText( ) );
                                     if ( matches && !c.getControlNewText( ).isEmpty( ) ) {
                                         final int count = Integer.parseInt( c.getControlNewText( ) );
                                         if ( count > 999 ) {
                                             return null;
                                         }
                                     }
                                     return matches ? c : null;
                                 } ) );

        final Text timer = new Text( );
        timer.getStyleClass( ).add( "clock" );
        timer.setBoundsType( TextBoundsType.VISUAL );
        timer.textProperty( ).bind( _model.getRound( ).getTimer( ).textProperty( ) );
        timer.styleProperty( ).bind( Bindings.createStringBinding( ( ) -> {
            return "-fx-fill: " + ( _model.getRound( ).getTimer( ).isOvertime( ) ? "darkred" : "darkgreen" );
        }, _model.getRound( ).getTimer( ).overtimeProperty( ) ) );

        final VBox clockButtonBox = new VBox( );
        clockButtonBox.setSpacing( 2 );
        clockButtonBox.setAlignment( Pos.CENTER );

        final Button playButton = new Button( FontAwesome.PLAY );
        playButton.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
        playButton.setStyle( "-fx-text-fill: green; -fx-font-size: small" );
        playButton.setOnAction( e -> _model.getRound( ).getTimer( ).start( ) );
        playButton.visibleProperty( ).bind( _model.getRound( ).endedProperty( ).not( ) );
        final Button setButton = new Button( FontAwesome.RESET_CLOCK );
        setButton.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
        setButton.setStyle( "-fx-text-fill: #424242; -fx-font-size: small" );
        setButton.visibleProperty( ).bind( _model.getRound( ).endedProperty( ).not( ) );

        clockButtonBox.getChildren( ).addAll( playButton, setButton );
        clockBox.getChildren( ).addAll( timer, clockButtonBox );
        setButton.setOnAction( e -> {
            final ObservableTimer roundTimer = _model.getRound( ).getTimer( );
            if ( roundTimer.getEndTime( ) == null ) {
                timerEditionField.setText( String.valueOf( roundTimer.getInitialDuration( ) ) );
                clockBox.getChildren( ).setAll( timerEditionField );
            } else {
                final Duration d = Duration.between( ZonedDateTime.now( ), roundTimer.getEndTime( ) );
                timerEditionField.setText( String.valueOf( d.isNegative( ) ? 0 : d.toMinutes( ) ) );
                clockBox.getChildren( ).setAll( timerEditionField );
                timerEditionField.requestFocus( );
            }
        } );
        timerEditionField.setOnKeyReleased( e -> {
            if ( e.getCode( ) == KeyCode.ENTER ) {
                final ObservableTimer roundTimer = _model.getRound( ).getTimer( );
                roundTimer.initialDurationProperty( ).set( Integer.parseInt( timerEditionField.getText( ) ) );
                roundTimer.reset( );
                clockBox.getChildren( ).setAll( timer, clockButtonBox );
            } else if ( e.getCode( ) == KeyCode.ESCAPE ) {
                clockBox.getChildren( ).setAll( timer, clockButtonBox );
            }
        } );

        final HBox spacing2 = new HBox( );
        setHgrow( spacing2, Priority.ALWAYS );
        hBox.getChildren( ).add( spacing2 );

        final MenuButton advancedButton = buildToolsMenuButton( );
        hBox.getChildren( ).add( advancedButton );

        final FaButton setScoreButton = new FaButton( FontAwesome.PLUS, "white" );
        setScoreButton.textProperty( ).bind( toUpperCase( _model.getI18n( ).get( "tournament.round.scoring" ) ) );
        setScoreButton.getStyleClass( ).add( "rich-button" );
        setScoreButton.getStyleClass( ).add( "action-button" );
        setScoreButton.setOnAction( event -> {
            final SetScoreDialog setScoreDialog = _model.createSetScoreDialog( );
            setScoreDialog.setOnCloseRequest( e -> Platform.runLater( this::resetFilter ) );
            setScoreDialog.show( );
        } );
        hBox.getChildren( ).add( setScoreButton );

        final FaButton startRoundButton = new FaButton( FontAwesome.LIGHTNING, "white" );
        startRoundButton.textProperty( ).bind( toUpperCase( _model.getI18n( ).get( "tournament.round.start.round" ) ) );
        startRoundButton.getStyleClass( ).add( "rich-button" );
        startRoundButton.getStyleClass( ).add( "action-button" );
        startRoundButton.setOnAction( event -> _model.fireStartNewRound( ) );
        if ( _model.getRound( ).getRound( ).isFinal( ) ) {
            startRoundButton.setDisable( true );
        } else {
            startRoundButton.disableProperty( )
                            .bind( _model.getRound( ).endedProperty( ).not( )
                                         .or( _model.getTournament( ).currentRoundProperty( )
                                                    .isNotEqualTo( _model.getRound( ) ) ) );
        }
        hBox.getChildren( ).addAll( startRoundButton );

        hBox.visibleProperty( )
            .bind( createBooleanBinding(
                ( ) -> _model.getTournament( ).getHeader( ).getStatus( ) != TournamentStatus.CLOSED,
                _model.getTournament( ).getHeader( ).statusProperty( ) ) );

        return hBox;
    }

    private MenuButton buildToolsMenuButton( ) {
        final MenuButton advancedButton = new MenuButton( FontAwesome.TOOLS );
        advancedButton.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );

        final Menu print = new FaMenu( FontAwesome.PRINTER, "black" );
        print.textProperty( ).bind( _model.getI18n( ).get( "tournament.pairing.print" ) );
        advancedButton.getItems( ).add( print );

        final MenuItem printByPosition = new MenuItem( );
        printByPosition.textProperty( ).bind( _model.getI18n( ).get( "tournament.pairing.print.byPosition" ) );
        printByPosition.setOnAction( e -> _model.firePrintLadderByPosition( ) );
        print.getItems( ).add( printByPosition );

        final MenuItem printByName = new MenuItem( );
        printByName.textProperty( ).bind( _model.getI18n( ).get( "tournament.pairing.print.byName" ) );
        printByName.setOnAction( e -> _model.firePrintLadderByName( ) );
        print.getItems( ).add( printByName );

        final MenuItem printSlips = new FaMenuItem( FontAwesome.PRINTER, "black" );
        printSlips.textProperty( ).bind( _model.getI18n( ).get( "tournament.slips.print" ) );
        printSlips.setOnAction( e -> _model.firePrintResultSlips( ) );
        advancedButton.getItems( ).add( printSlips );

        final MenuItem manualPairing = new FaMenuItem( FontAwesome.HAND, "black" );
        manualPairing.textProperty( ).bind( _model.getI18n( ).get( "tournament.round.manual.pairing" ) );
        manualPairing.setOnAction( e -> _model.fireManualPairing( ) );
        advancedButton.getItems( ).add( manualPairing );

        return advancedButton;
    }

    public TournamentRoundTabModel getModel( ) {
        return _model;
    }

    private class PositionCell extends TableCell<ObservableMatch, Integer> {

        @Override
        protected void updateItem( final Integer item, final boolean empty ) {
            super.updateItem( item, empty );
            if ( empty ) {
                setGraphic( null );
                return;
            }
            final ObservableMatch match = getTableView( ).getItems( ).get( getIndex( ) );
            if ( _model.getRound( ).getRound( ).isFinal( ) ) {
                if ( _model.getTournament( ).getTeam( match.getLeftTeamId( ) )
                           .getPairingFlags( ).containsKey( "final" ) ) {

                    final HBox hbox = new HBox( );
                    hbox.setAlignment( Pos.CENTER );
                    hbox.setSpacing( 10 );

                    final Label icon = new Label( FontAwesome.LADDER );
                    icon.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
                    hbox.getChildren( ).add( icon );

                    setGraphic( hbox );
                    return;
                }
            }
            setGraphic( new Label( String.valueOf( item ) ) );
        }
    }

    private final TournamentRoundTabModel _model;
    private FilteredList<ObservableMatch> _itemList;
}
