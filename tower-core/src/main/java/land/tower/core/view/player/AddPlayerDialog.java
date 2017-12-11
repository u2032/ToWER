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

import java.time.LocalDate;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import land.tower.data.Player;

/**
 * Created on 10/12/2017
 * @author Cédric Longo
 */
final class AddPlayerDialog extends Dialog<Player> {

    public AddPlayerDialog( final AddPlayerDialogModel model ) {
        _model = model;
        titleProperty( ).bind( model.i18nTitleProperty( ) );
        headerTextProperty( ).bind( model.i18nHeaderProperty( ) );

        final ButtonType saveButtonType = new ButtonType( model.getI18nSave( ), ButtonData.OK_DONE );
        final ButtonType cancelButtonType = new ButtonType( model.getI18nCancel( ), ButtonData.CANCEL_CLOSE );
        getDialogPane( ).getButtonTypes( ).addAll( saveButtonType, cancelButtonType );

        final Button saveButton = (Button) getDialogPane( ).lookupButton( saveButtonType );
        saveButton.disableProperty( ).bind( Bindings.not( model.isValidProperty( ) ) );
        saveButton.setDefaultButton( true );

        final GridPane grid = new GridPane( );
        grid.setHgap( 10 );
        grid.setVgap( 10 );
        grid.setPadding( new Insets( 20, 150, 10, 10 ) );

        final TextField numeroText = new TextField( );
        numeroText.textProperty( ).bindBidirectional( model.playerNumeroProperty( ) );
        numeroText.promptTextProperty( ).bind( model.i18nPlayerNumeroProperty( ) );
        numeroText.textProperty( ).addListener( ( observable, oldValue, newValue ) -> {
            if ( !newValue.matches( "[\\d ]*" ) ) {
                numeroText.setText( newValue.replaceAll( "[^\\d ]", "" ) );
            }
            if ( newValue.length( ) > 10 ) {
                numeroText.setText( newValue.substring( 0, 10 ) );
            }
        } );
        model.playerNumeroValidityProperty( ).addListener( ( observable, oldValue, newValue ) -> {
            if ( newValue ) {
                numeroText.setStyle( "-fx-text-fill: darkgreen" );
            } else {
                numeroText.setStyle( "-fx-text-fill: darkred; -fx-background-color: lightpink" );
            }
        } );
        if ( model.isPlayerNumeroValidity( ) ) {
            numeroText.setStyle( "-fx-text-fill: darkgreen" );
        } else {
            numeroText.setStyle( "-fx-text-fill: darkred; -fx-background-color: lightpink" );
        }

        final Label numeroLabel = new Label( );
        numeroLabel.textProperty( ).bind( model.i18nPlayerNumeroProperty( ) );
        numeroLabel.setLabelFor( numeroText );

        final TextField firstnameText = new TextField( );
        firstnameText.textProperty( ).bindBidirectional( model.playerFirstnameProperty( ) );
        firstnameText.promptTextProperty( ).bind( model.i18nPlayerFirstnameProperty( ) );

        final Label firstnameLabel = new Label( );
        firstnameLabel.textProperty( ).bind( model.i18nPlayerFirstnameProperty( ) );
        firstnameLabel.setLabelFor( firstnameText );

        final TextField lastnameText = new TextField( );
        lastnameText.textProperty( ).bindBidirectional( model.playerLastnameProperty( ) );
        lastnameText.promptTextProperty( ).bind( model.i18nPlayerLastnameProperty( ) );

        final Label lastnameLabel = new Label( );
        lastnameLabel.textProperty( ).bind( model.i18nPlayerLastnameProperty( ) );
        lastnameLabel.setLabelFor( lastnameText );

        final DatePicker birthdayDatePicker = new DatePicker( );
        birthdayDatePicker.setPromptText( "yyyy-mm-dd" );
        birthdayDatePicker.setShowWeekNumbers( false );
        birthdayDatePicker.valueProperty( ).bindBidirectional( model.playerBirthdayProperty( ) );
        birthdayDatePicker.setConverter( new StringConverter<LocalDate>( ) {
            @Override
            public String toString( final LocalDate object ) {
                return object == null ? "" : object.toString( );
            }

            @Override
            public LocalDate fromString( final String string ) {
                try {
                    return LocalDate.parse( string );
                } catch ( final Exception e ) {
                    return null;
                }
            }
        } );
        birthdayDatePicker.setEditable( false );

        final Label birthdayLabel = new Label( );
        birthdayLabel.textProperty( ).bind( model.i18nPlayerBirthdayProperty( ) );
        birthdayLabel.setLabelFor( birthdayDatePicker );

        grid.add( numeroLabel, 0, 0 );
        grid.add( numeroText, 1, 0 );
        grid.add( firstnameLabel, 0, 1 );
        grid.add( firstnameText, 1, 1 );
        grid.add( lastnameLabel, 0, 2 );
        grid.add( lastnameText, 1, 2 );
        grid.add( birthdayLabel, 0, 3 );
        grid.add( birthdayDatePicker, 1, 3 );

        getDialogPane( ).setContent( grid );

        setResultConverter( param -> {
            if ( param == saveButtonType ) {
                return new Player( Long.parseLong( _model.getPlayerNumero( ) ),
                                   _model.getPlayerFirstname( ),
                                   _model.getPlayerLastname( ),
                                   _model.getPlayerBirthday( ).toString( ) );
            }
            return null;
        } );

        Platform.runLater( ( ) -> {
            if ( numeroText.getText( ).isEmpty( ) ) {
                numeroText.requestFocus( );
            } else {
                firstnameText.requestFocus( );
            }
        } );
    }

    private final AddPlayerDialogModel _model;
}
