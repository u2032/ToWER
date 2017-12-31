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

import com.google.common.base.Strings;

import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.converter.IntegerStringConverter;

/**
 * Created on 31/12/2017
 * @author Cédric Longo
 */
final class SetScoreDialog extends Dialog<Void> {

    public SetScoreDialog( final SetScoreDialogModel model ) {
        getDialogPane( ).getStylesheets( ).add( model.getConfig( ).getApplicationStyle( ) );

        _model = model;
        titleProperty( ).bind( model.getI18n( ).get( "tournament.round.scoring" ) );
        headerTextProperty( ).bind( model.getI18n( ).get( "tournament.round.scoring" ) );

        final ButtonType saveButtonType =
            new ButtonType( model.getI18n( ).get( "action.save" ).get( ).toUpperCase( ), ButtonData.OK_DONE );

        final ButtonType cancelButtonType =
            new ButtonType( model.getI18n( ).get( "action.cancel" ).get( ).toUpperCase( ), ButtonData.CANCEL_CLOSE );
        getDialogPane( ).getButtonTypes( ).addAll( saveButtonType, cancelButtonType );

        final Button saveButton = (Button) getDialogPane( ).lookupButton( saveButtonType );
        saveButton.disableProperty( ).bind( _model.errorInformationProperty( ).isEmpty( ).not( ) );
        saveButton.setDefaultButton( true );
        saveButton.setOnAction( event -> {
            _model.fireSaveScore( );
        } );

        final GridPane grid = new GridPane( );
        grid.setHgap( 20 );
        grid.setVgap( 10 );
        grid.setPadding( new Insets( 20, 20, 20, 15 ) );

        int line = 0;

        final HBox scoreBox = new HBox( );
        scoreBox.setSpacing( 20 );

        final TextField leftWinsField = new TextField( );
        leftWinsField.setAlignment( Pos.CENTER );
        leftWinsField.promptTextProperty( )
                     .bind( Bindings.concat( _model.getI18n( ).get( "match.score.wins" ),
                                             " ",
                                             _model.getI18n( ).get( "match.team.left" ) ) );
        leftWinsField.setTextFormatter(
            new TextFormatter<>( new IntegerStringConverter( ),
                                 null,
                                 c -> {
                                     final boolean matches = Pattern.matches( "\\d*", c.getControlNewText( ) );
                                     if ( !c.getControlNewText( ).isEmpty( ) ) {
                                         final int count = Integer.parseInt( c.getControlNewText( ) );
                                         if ( count > 9 ) {
                                             return null;
                                         }
                                     }
                                     return matches ? c : null;
                                 } ) );
        leftWinsField.textProperty( ).bindBidirectional( _model.leftWinsProperty( ), new IntegerStringConverter( ) );
        scoreBox.getChildren( ).add( leftWinsField );

        final TextField drawsField = new TextField( );
        drawsField.setAlignment( Pos.CENTER );
        drawsField.promptTextProperty( ).bind( _model.getI18n( ).get( "match.score.draw" ) );
        drawsField.setTextFormatter(
            new TextFormatter<>( new IntegerStringConverter( ),
                                 null,
                                 c -> {
                                     final boolean matches = Pattern.matches( "\\d*", c.getControlNewText( ) );
                                     if ( !c.getControlNewText( ).isEmpty( ) ) {
                                         final int count = Integer.parseInt( c.getControlNewText( ) );
                                         if ( count > 9 ) {
                                             return null;
                                         }
                                     }
                                     return matches ? c : null;
                                 } ) );
        drawsField.textProperty( ).bindBidirectional( _model.drawsProperty( ), new IntegerStringConverter( ) );
        scoreBox.getChildren( ).add( drawsField );

        final TextField rightWinsField = new TextField( );
        rightWinsField.setAlignment( Pos.CENTER );
        rightWinsField.promptTextProperty( )
                      .bind( Bindings.concat( _model.getI18n( ).get( "match.score.wins" ),
                                              " ",
                                              _model.getI18n( ).get( "match.team.right" ) ) );
        rightWinsField.setTextFormatter(
            new TextFormatter<>( new IntegerStringConverter( ),
                                 null,
                                 c -> {
                                     final boolean matches = Pattern.matches( "\\d*", c.getControlNewText( ) );
                                     if ( !c.getControlNewText( ).isEmpty( ) ) {
                                         final int count = Integer.parseInt( c.getControlNewText( ) );
                                         if ( count > 9 ) {
                                             return null;
                                         }
                                     }
                                     return matches ? c : null;
                                 } ) );
        rightWinsField.textProperty( ).bindBidirectional( _model.rightWinsProperty( ), new IntegerStringConverter( ) );
        scoreBox.getChildren( ).add( rightWinsField );

        final Label scoreLabel = new Label( );
        scoreLabel.getStyleClass( ).add( "important" );
        scoreLabel.textProperty( ).bind( _model.getI18n( ).get( "match.score" ) );
        scoreLabel.setLabelFor( drawsField );
        grid.add( scoreLabel, 0, line );
        grid.add( scoreBox, 1, line );

        line++;
        final TextField positionField = new TextField( );
        positionField.setAlignment( Pos.CENTER );
        positionField.setTextFormatter(
            new TextFormatter<>( new IntegerStringConverter( ),
                                 null,
                                 c -> Pattern.matches( "\\d*", c.getControlNewText( ) ) ? c : null ) );
        positionField.textProperty( ).bindBidirectional( _model.positionProperty( ), new IntegerStringConverter( ) );
        positionField.setPrefWidth( 60 );
        positionField.setMaxWidth( Pane.USE_PREF_SIZE );

        final Label positionLabel = new Label( );
        positionLabel.getStyleClass( ).add( "important" );
        positionLabel.textProperty( ).bind( _model.getI18n( ).get( "match.position" ) );
        positionLabel.setLabelFor( positionField );
        grid.add( positionLabel, 0, line );
        grid.add( positionField, 1, line );

        line++;
        final Label errorLabel = new Label( );
        errorLabel.setStyle( "-fx-text-fill: red;" );
        errorLabel.textProperty( ).bind( _model.errorInformationProperty( ) );
        errorLabel.setWrapText( true );
        errorLabel.setPrefHeight( 40 );
        grid.add( errorLabel, 0, line, 2, 1 );

        getDialogPane( ).setContent( grid );

        // Configure autofocus
        leftWinsField.textProperty( )
                     .addListener( ( observable, oldValue, newValue ) -> {
                         if ( !Strings.isNullOrEmpty( newValue ) ) {
                             Platform.runLater( drawsField::requestFocus );
                         }
                     } );
        drawsField.textProperty( )
                  .addListener( ( observable, oldValue, newValue ) -> {
                      if ( !Strings.isNullOrEmpty( newValue ) ) {
                          Platform.runLater( rightWinsField::requestFocus );
                      }
                  } );
        rightWinsField.textProperty( )
                      .addListener( ( observable, oldValue, newValue ) -> {
                          if ( !Strings.isNullOrEmpty( newValue ) ) {
                              Platform.runLater( positionField::requestFocus );
                          }
                      } );

        setOnShowing( e -> Platform.runLater( leftWinsField::requestFocus ) );
    }

    SetScoreDialogModel getModel( ) {
        return _model;
    }

    private final SetScoreDialogModel _model;
}
