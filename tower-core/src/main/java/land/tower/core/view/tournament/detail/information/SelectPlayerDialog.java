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

package land.tower.core.view.tournament.detail.information;

import org.controlsfx.control.textfield.AutoCompletionBinding.AutoCompletionEvent;
import org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import javax.inject.Inject;
import land.tower.core.view.player.ObservablePlayer;
import land.tower.data.Player;

/**
 * Created on 30/03/2018
 * @author Cédric Longo
 */
final class SelectPlayerDialog extends Dialog<ObservablePlayer> {

    @Inject
    public SelectPlayerDialog( final SelectPlayerDialogModel model ) {
        initOwner( model.getOwner( ) );
        getDialogPane( ).addEventHandler( KeyEvent.KEY_RELEASED, ( KeyEvent event ) -> {
            if ( KeyCode.ESCAPE == event.getCode( ) ) {
                close( );
            }
        } );

        titleProperty( ).bind( model.getI18n( ).get( "player.select.title" ) );
        //        headerTextProperty( ).bind( model.getI18n( ).get( "player.select.title" ) );

        final ButtonType cancelButtonType =
            new ButtonType( model.getI18n( ).get( "action.cancel" ).get( ).toUpperCase( ), ButtonData.CANCEL_CLOSE );
        getDialogPane( ).getButtonTypes( ).addAll( cancelButtonType );

        final HBox hBox = new HBox( );
        hBox.setSpacing( 10 );
        hBox.setAlignment( Pos.CENTER );
        getDialogPane( ).setContent( hBox );

        final TextField numeroField = new TextField( );
        numeroField.promptTextProperty( ).bind( model.getI18n( ).get( "player.numero" ) );
        numeroField.setTextFormatter(
            new TextFormatter<>( new IntegerStringConverter( ),
                                 null,
                                 c -> Pattern.matches( "\\d*", c.getControlNewText( ) ) ? c : null ) );
        numeroField.textProperty( ).addListener( ( observable, oldValue, newValue ) -> {
            numeroField.setStyle( "-fx-text-fill: darkred; -fx-background-color: lightpink" );
            try {
                if ( model.getPlayerNumeroValidator( ).isValid( Long.parseLong( newValue ) ) ) {
                    numeroField.setStyle( "-fx-text-fill: darkgreen" );
                }
            } catch ( NumberFormatException ignored ) {
            }
        } );

        Platform.runLater( numeroField::requestFocus );

        final TextField lastnameField = new TextField( );
        lastnameField.promptTextProperty( ).bind( model.getI18n( ).get( "player.lastname" ) );

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
            _result = new ObservablePlayer( player );
            close( );
            return null;
        };

        final EventHandler<AutoCompletionEvent<Player>> autoCompletionHandler = event -> {
            final Player player = event.getCompletion( );
            Platform.runLater( ( ) -> playerSelectedHandler.apply( player ) );
        };

        final AutoCompletionTextFieldBinding<Player> autoCompleteName =
            new AutoCompletionTextFieldBinding<>( lastnameField,
                                                  param -> model.getPlayerSuggestionsForName( param.getUserText( ) ),
                                                  playerStringConverter );
        autoCompleteName.setOnAutoCompleted( autoCompletionHandler );
        autoCompleteName.setHideOnEscape( false );

        final Callback<ISuggestionRequest, Collection<Player>> suggestionProvider = param -> {
            final List<Player> suggestions = model.getPlayerSuggestionsForNumero( param.getUserText( ) );
            if ( !model.getPlayerNumeroValidator( ).generate( ).isPresent( ) && suggestions.size( ) == 1 ) {
                Platform.runLater( ( ) -> playerSelectedHandler.apply( suggestions.get( 0 ) ) );
                return Collections.emptyList( );
            }
            return suggestions;
        };
        final AutoCompletionTextFieldBinding<Player> autoCompleteNumero =
            new AutoCompletionTextFieldBinding<>( numeroField, suggestionProvider, playerStringConverter );
        autoCompleteNumero.setOnAutoCompleted( autoCompletionHandler );
        autoCompleteNumero.setHideOnEscape( false );

        hBox.getChildren( ).addAll( numeroField, lastnameField );
        setResultConverter( param -> _result );
    }

    private ObservablePlayer _result;
}
