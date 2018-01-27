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

package land.tower.core.view.tournament.radiator;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import land.tower.core.ext.font.FontAwesome;
import land.tower.core.model.tournament.ObservableRound;
import land.tower.data.TournamentStatus;

/**
 * Created on 27/01/2018
 * @author Cédric Longo
 */
public final class RadiatorDialog extends Stage {

    public RadiatorDialog( final Window owner, final RadiatorDialogModel model ) {
        initModality( Modality.NONE );
        //initStyle( StageStyle.UNDECORATED );
        setWidth( owner.getWidth( ) * 0.9 );
        setHeight( owner.getHeight( ) * 0.9 );

        _model = model;

        titleProperty( ).bind( model.getI18n( ).get( "tournament.radiator.information" ) );
        model.getConfig( ).setIcons( this );

        final BorderPane root = new BorderPane( );
        root.setStyle( "-fx-background-color: #eeeeee" );
        root.setPadding( new Insets( 20, 0, 0, 0 ) );

        final Scene scene = new Scene( root );
        scene.getStylesheets( ).add( model.getConfig( ).getApplicationStyle( ) );

        _vbox = new VBox( );
        _vbox.setAlignment( Pos.CENTER );
        _vbox.setSpacing( 40 );

        _header = new HBox( );
        _tournamentTitle = new Text( );
        _tournamentTitle.setTextAlignment( TextAlignment.CENTER );
        _tournamentTitle.textProperty( ).bind( _model.getTournament( ).getHeader( ).titleProperty( ) );
        _tournamentTitle.wrappingWidthProperty( ).bind( scene.widthProperty( ).subtract( 50 ) );
        _tournamentTitle.styleProperty( ).bind( Bindings.createStringBinding( ( ) -> {
            return "-fx-font-size: 3em; -fx-font-style: italic;";
        }, heightProperty( ) ) );

        final Text fullscreenButton = new Text( FontAwesome.FULLSCREEN );
        fullscreenButton.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
        fullscreenButton.setStyle( "-fx-opacity: 0.5; -fx-cursor: hand; -fx-font-size: 1.5em;" );
        fullscreenButton.setOnMouseClicked( e -> setFullScreen( true ) );
        fullScreenProperty( ).addListener( ( observable, oldValue, newValue ) -> {
            if ( newValue ) {
                _header.getChildren( ).setAll( _tournamentTitle );
            } else {
                _header.getChildren( ).setAll( _tournamentTitle, fullscreenButton );
            }
        } );

        _header.getChildren( ).setAll( _tournamentTitle, fullscreenButton );
        root.setTop( _header );

        _tournamentStatus = new Text( );
        _tournamentStatus.setTextAlignment( TextAlignment.CENTER );
        _tournamentStatus.wrappingWidthProperty( ).bind( scene.widthProperty( ).subtract( 50 ) );
        _tournamentStatus.styleProperty( ).bind( Bindings.createStringBinding( ( ) -> {
            return "-fx-font-size: 6em; -fx-font-weight: bold;";
        }, heightProperty( ) ) );

        _roundInfo = new Text( );
        _roundInfo.setTextAlignment( TextAlignment.CENTER );
        _roundInfo.styleProperty( ).bind( Bindings.createStringBinding( ( ) -> {
            return "-fx-font-size: 6em; -fx-font-weight: bold;";
        }, heightProperty( ) ) );

        _timerInfo = new Text( );
        _timerInfo.setTextAlignment( TextAlignment.CENTER );
        _timerInfo.setBoundsType( TextBoundsType.VISUAL );
        _timerInfo.getStyleClass( ).add( "clock" );

        _model.getTournament( ).currentRoundProperty( )
              .addListener( ( observable, oldValue, newValue ) -> updateInformation( ) );

        _model.getTournament( ).getHeader( ).statusProperty( )
              .addListener( ( observable, oldValue, newValue ) -> updateInformation( ) );

        updateInformation( );

        root.setCenter( _vbox );

        setFullScreenExitKeyCombination( new KeyCodeCombination( KeyCode.ESCAPE ) );
        fullScreenExitHintProperty( ).bind( _model.getI18n( ).get( "radiator.fullscreen.hint" ) );

        setScene( scene );
    }

    private void updateInformation( ) {
        final TournamentStatus status = _model.getTournament( ).getHeader( ).getStatus( );
        if ( status == TournamentStatus.NOT_CONFIGURED || status == TournamentStatus.ENROLMENT || status == TournamentStatus.PLANNED ) {
            _tournamentStatus.textProperty( ).unbind( );
            _tournamentStatus.textProperty( )
                             .bind( Bindings.concat( _model.getI18n( ).get( "radiator.tournament.enrolment" ),
                                                     "\n",
                                                     _model.getI18n( ).get( "radiator.tournament.teamCount" ), " ",
                                                     new SimpleListProperty<>( _model.getTournament( ).getTeams( ) )
                                                         .sizeProperty( )
                             ) );
            _vbox.getChildren( ).setAll( _tournamentStatus );
            return;
        }

        if ( status == TournamentStatus.CLOSED ) {
            _tournamentStatus.textProperty( ).unbind( );
            _tournamentStatus.textProperty( ).bind( _model.getI18n( ).get( "radiator.tournament.closed" ) );
            _vbox.getChildren( ).setAll( _tournamentStatus );
            return;
        }

        final ObservableRound currentRound = _model.getTournament( ).getCurrentRound( );
        if ( currentRound == null ) {
            return;
        }

        _roundInfo.textProperty( ).unbind( );
        _roundInfo.textProperty( ).bind( Bindings.createStringBinding( ( ) -> {
            if ( currentRound.isEnded( ) ) {
                return _model.getI18n( ).get( "radiator.round.ended", currentRound.getNumero( ) );
            }
            return _model.getI18n( ).get( "tournament.tab.round" )
                         .concat( " #" )
                         .concat( currentRound.numeroProperty( ) )
                         .get( );
        }, currentRound.numeroProperty( ), currentRound.endedProperty( ) ) );

        _timerInfo.textProperty( ).unbind( );
        _timerInfo.textProperty( ).bind( Bindings.createStringBinding( ( ) -> {
            return currentRound.isEnded( ) ? null : currentRound.getTimer( ).getText( );
        }, currentRound.endedProperty( ), currentRound.getTimer( ).textProperty( ) ) );

        _timerInfo.styleProperty( ).unbind( );
        _timerInfo.styleProperty( ).bind( Bindings.createStringBinding( ( ) -> {
            return "-fx-font-size: " + getHeight( ) * 0.4 + "px;"
                   + "-fx-fill: " + ( currentRound.getTimer( ).isOvertime( ) ? "darkred" : "darkgreen" );
        }, heightProperty( ), currentRound.getTimer( ).overtimeProperty( ) ) );

        _vbox.getChildren( ).setAll( _roundInfo, _timerInfo );
    }

    private final RadiatorDialogModel _model;
    private final Text _tournamentTitle;
    private final Text _tournamentStatus;
    private final Text _roundInfo;
    private final Text _timerInfo;
    private final VBox _vbox;
    private final HBox _header;
}
