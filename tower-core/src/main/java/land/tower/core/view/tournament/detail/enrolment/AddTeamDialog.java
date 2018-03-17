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

import com.jfoenix.controls.JFXComboBox;

import org.controlsfx.control.textfield.AutoCompletionBinding.AutoCompletionEvent;
import org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import land.tower.core.ext.font.FontAwesome;
import land.tower.core.ext.util.Resources;
import land.tower.data.Player;
import land.tower.data.Team;

/**
 * Created on 10/12/2017
 * @author Cédric Longo
 */
final class AddTeamDialog extends Dialog<Team> {

    public AddTeamDialog( final AddTeamDialogModel model ) {
        initOwner( model.getOwner( ) );

        _model = model;
        titleProperty( ).bind( model.getI18n( ).get( "team.add.title" ) );
        headerTextProperty( ).bind( model.getI18n( ).get( "team.players" ) );

        final ButtonType saveButtonType =
            new ButtonType( model.getI18n( ).get( "action.save" ).get( ).toUpperCase( ), ButtonData.OK_DONE );
        final ButtonType cancelButtonType =
            new ButtonType( model.getI18n( ).get( "action.cancel" ).get( ).toUpperCase( ), ButtonData.CANCEL_CLOSE );
        getDialogPane( ).getButtonTypes( ).addAll( saveButtonType, cancelButtonType );

        final Button saveButton = (Button) getDialogPane( ).lookupButton( saveButtonType );
        saveButton.disableProperty( ).bind( Bindings.or( _model.errorInformationProperty( ).isNotEmpty( ),
                                                         _model.selectedPlayerCountProperty( ).isNotEqualTo(
                                                             _model.getTournament( ).getHeader( ).getTeamSize( ) ) ) );
        saveButton.setDefaultButton( true );

        final GridPane grid = new GridPane( );
        grid.setHgap( 20 );
        grid.setVgap( 10 );
        grid.setPadding( new Insets( 20, 20, 20, 15 ) );

        int line = 0;
        for ( ; line < _model.getTournament( ).getHeader( ).getTeamSize( ); line++ ) {
            addPlayerLine( line, grid );
        }

        line++;
        line++;

        final TextField nameField = new TextField( );
        nameField.textProperty( ).bindBidirectional( _model.teamNameProperty( ) );
        nameField.promptTextProperty( ).bind( _model.defaultTeamNameProperty( ) );

        final Label nameLabel = new Label( );
        nameLabel.textProperty( ).bind( _model.getI18n( ).get( "team.name" ) );
        nameLabel.setLabelFor( nameField );
        grid.add( nameLabel, 0, line );
        grid.add( nameField, 1, line );

        final Map<String, String[]> teamExtraInfo = _model.getTeamExtraInfo( );
        for ( Entry<String, String[]> e : teamExtraInfo.entrySet( ) ) {
            line++;
            final ComboBox<String> extraInfo = new JFXComboBox<>( );
            extraInfo.setItems( FXCollections.observableArrayList( e.getValue( ) ) );
            extraInfo.setConverter( new StringConverter<String>( ) {
                @Override
                public String toString( final String object ) {
                    return _model.getI18n( ).get( "team.extra." + e.getKey( ) + "." + object ).get( );
                }

                @Override
                public String fromString( final String string ) {
                    return null;
                }
            } );
            extraInfo.valueProperty( ).addListener( ( observable, oldValue, newValue ) -> {
                _model.registerExtraInfo( e.getKey( ), newValue );
            } );
            extraInfo.setValue( _model.getExtraInfo( e.getKey( ) ) );

            final Label extraInfoLabel = new Label( );
            extraInfoLabel.textProperty( ).bind( _model.getI18n( ).get( "team.extra." + e.getKey( ) ) );
            extraInfoLabel.setLabelFor( extraInfo );
            grid.add( extraInfoLabel, 0, line );
            grid.add( extraInfo, 1, line );
        }

        line++;
        final Text errorLabel = new Text( );
        errorLabel.setFill( Color.RED );
        errorLabel.textProperty( ).bind( _model.errorInformationProperty( ) );
        errorLabel.wrappingWidthProperty( ).bind( getDialogPane( ).widthProperty( ).subtract( 40 ) );
        grid.add( errorLabel, 0, line, 2, 1 );

        errorLabel.textProperty( ).addListener( ( observable, oldValue, newValue ) -> {
            getDialogPane( ).getScene( ).getWindow( ).sizeToScene( );
        } );

        getDialogPane( ).setContent( grid );

        setResultConverter( param -> {
            if ( param == saveButtonType ) {
                return _model.createTeam( );
            }
            return null;
        } );
    }

    private void addPlayerLine( final int line, final GridPane grid ) {
        final HBox hbox = new HBox( );
        hbox.setSpacing( 10 );
        hbox.setAlignment( Pos.CENTER_LEFT );

        final TextField numeroField = new TextField( );
        numeroField.promptTextProperty( ).bind( _model.getI18n( ).get( "player.numero" ) );
        numeroField.setTextFormatter(
            new TextFormatter<>( new IntegerStringConverter( ),
                                 null,
                                 c -> Pattern.matches( "\\d*", c.getControlNewText( ) ) ? c : null ) );
        numeroField.textProperty( ).addListener( ( observable, oldValue, newValue ) -> {
            numeroField.setStyle( "-fx-text-fill: darkred; -fx-background-color: lightpink" );
            try {
                if ( _model.getPlayerNumeroValidator( ).isValid( Long.parseLong( newValue ) ) ) {
                    numeroField.setStyle( "-fx-text-fill: darkgreen" );
                }
            } catch ( NumberFormatException ignored ) {
            }
        } );

        if ( line == 0 ) {
            Platform.runLater( numeroField::requestFocus );
        }

        final TextField lastnameField = new TextField( );
        lastnameField.promptTextProperty( ).bind( _model.getI18n( ).get( "player.lastname" ) );

        final StringConverter<Player> playerStringConverter = new StringConverter<Player>( ) {
            @Override
            public String toString( final Player object ) {
                return String.format( "%s %s (%s) [%s]",
                                      object.getLastname( ).toUpperCase( ),
                                      object.getFirstname( ),
                                      object.getBirthday( ),
                                      object.getNumero( ) );
            }

            @Override
            public Player fromString( final String string ) {
                return null;
            }
        };

        // Configure autocompletion

        final Function<Player, Void> playerSelectedHandler = player -> {
            final Label pLabel = new Label( );
            pLabel.textProperty( ).bind( Bindings.concat(
                player.getNumero( ), " – ",
                player.getLastname( ).toUpperCase( ),
                " ",
                player.getFirstname( )
            ) );

            Resources.loadImage(
                "img/country/" + player.getNationality( ).name( ).toLowerCase( ) + ".png", 20, 20 )
                     .ifPresent( image -> {
                         pLabel.setGraphicTextGap( 10 );
                         pLabel.setGraphic( new ImageView( image ) );
                     } );

            final Label resetButton = new Label( FontAwesome.CLOSE );
            resetButton.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
            resetButton.setCursor( Cursor.HAND );
            resetButton.setOnMouseClicked( e -> {
                numeroField.setText( null );
                lastnameField.setText( null );
                hbox.getChildren( ).setAll( numeroField, lastnameField );
                _model.firePlayerRemoved( line );
            } );

            hbox.getChildren( ).setAll( pLabel, resetButton );

            _model.firePlayerAdded( player, line );
            return null;
        };

        final EventHandler<AutoCompletionEvent<Player>> autoCompletionHandler = event -> {
            final Player player = event.getCompletion( );
            Platform.runLater( ( ) -> playerSelectedHandler.apply( player ) );
        };

        final AutoCompletionTextFieldBinding<Player> autoCompleteName =
            new AutoCompletionTextFieldBinding<>( lastnameField,
                                                  param -> _model.getPlayerSuggestionsForName( param.getUserText( ) ),
                                                  playerStringConverter );
        autoCompleteName.setOnAutoCompleted( autoCompletionHandler );
        autoCompleteName.setHideOnEscape( false );

        final Callback<ISuggestionRequest, Collection<Player>> suggestionProvider = param -> {
            final List<Player> suggestions = _model.getPlayerSuggestionsForNumero( param.getUserText( ) );
            if ( !_model.getPlayerNumeroValidator( ).generate( ).isPresent( ) && suggestions.size( ) == 1 ) {
                Platform.runLater( ( ) -> playerSelectedHandler.apply( suggestions.get( 0 ) ) );
                return Collections.emptyList( );
            }
            return suggestions;
        };
        final AutoCompletionTextFieldBinding<Player> autoCompleteNumero =
            new AutoCompletionTextFieldBinding<Player>( numeroField, suggestionProvider, playerStringConverter );
        autoCompleteNumero.setOnAutoCompleted( autoCompletionHandler );
        autoCompleteNumero.setHideOnEscape( false );

        hbox.getChildren( ).add( numeroField );
        hbox.getChildren( ).add( lastnameField );

        final Label label = new Label( );
        label.setLabelFor( hbox );
        label.getStyleClass( ).add( "important" );
        if ( line == 0 ) {
            label.textProperty( ).bind( _model.getI18n( ).get( "team.player" ) );
        } else {
            label.textProperty( ).bind( Bindings.concat( _model.getI18n( ).get( "team.player" ), " #",
                                                         String.valueOf( line + 1 ) ) );
        }

        grid.add( label, 0, line );
        grid.add( hbox, 1, line );
    }

    private final AddTeamDialogModel _model;
}
