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

import static java.util.Comparator.comparing;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import javafx.util.converter.LongStringConverter;
import land.tower.data.Player;
import land.tower.data.PlayerNationality;

/**
 * Created on 10/12/2017
 * @author Cédric Longo
 */
final class AddPlayerDialog extends Dialog<Player> {

    public AddPlayerDialog( final AddPlayerDialogModel model ) {
        getDialogPane( ).getStylesheets( ).add( model.getConfig( ).getApplicationStyle( ) );

        _model = model;
        titleProperty( ).bind( model.getI18n( ).get( "player.add.title" ) );
        headerTextProperty( ).bind( model.getI18n( ).get( "player.information" ) );

        final ButtonType saveButtonType =
            new ButtonType( model.getI18n( ).get( "action.save" ).get( ).toUpperCase( ), ButtonData.OK_DONE );
        final ButtonType cancelButtonType =
            new ButtonType( model.getI18n( ).get( "action.cancel" ).get( ).toUpperCase( ), ButtonData.CANCEL_CLOSE );
        getDialogPane( ).getButtonTypes( ).addAll( saveButtonType, cancelButtonType );

        final Button saveButton = (Button) getDialogPane( ).lookupButton( saveButtonType );
        saveButton.disableProperty( ).bind( Bindings.not( model.isValidProperty( ) ) );
        saveButton.setDefaultButton( true );

        final GridPane grid = new GridPane( );
        grid.setHgap( 10 );
        grid.setVgap( 10 );
        grid.setPadding( new Insets( 20, 150, 10, 10 ) );

        final TextField numeroText = new TextField( );
        numeroText.textProperty( ).bindBidirectional( model.playerNumeroProperty( ), new LongStringConverter( ) );
        numeroText.promptTextProperty( ).bind( model.getI18n( ).get( "player.numero" ) );
        numeroText.setTextFormatter(
            new TextFormatter<>( new LongStringConverter( ),
                                 model.getPlayerNumero( ),
                                 c -> Pattern.matches( "[\\d]*", c.getControlNewText( ) ) ? c : null ) );
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

        if ( model.getPlayerNumero( ) != null ) {
            numeroText.setEditable( false );
        }

        final Label numeroLabel = new Label( );
        numeroLabel.textProperty( ).bind( model.getI18n( ).get( "player.numero" ) );
        numeroLabel.setLabelFor( numeroText );

        final TextField firstnameText = new TextField( );
        firstnameText.textProperty( ).bindBidirectional( model.playerFirstnameProperty( ) );
        firstnameText.promptTextProperty( ).bind( model.getI18n( ).get( "player.firstname" ) );

        final Label firstnameLabel = new Label( );
        firstnameLabel.textProperty( ).bind( model.getI18n( ).get( "player.firstname" ) );
        firstnameLabel.setLabelFor( firstnameText );

        final TextField lastnameText = new TextField( );
        lastnameText.textProperty( ).bindBidirectional( model.playerLastnameProperty( ) );
        lastnameText.promptTextProperty( ).bind( model.getI18n( ).get( "player.lastname" ) );

        final Label lastnameLabel = new Label( );
        lastnameLabel.textProperty( ).bind( model.getI18n( ).get( "player.lastname" ) );
        lastnameLabel.setLabelFor( lastnameText );

        final ComboBox<PlayerNationality> nationalityBox = new ComboBox<>( );
        nationalityBox.setVisibleRowCount( 10 );
        nationalityBox.valueProperty( ).bindBidirectional( model.playerNationalityProperty( ) );
        nationalityBox.itemsProperty( )
                      .bind( new SimpleObjectProperty<>( FXCollections.observableArrayList(
                          Stream.of( PlayerNationality.values( ) )
                                .sorted( comparing( n -> _model.getI18n( ).get( "nationality." + n.name( ) ).get( ) ) )
                                .toArray( PlayerNationality[]::new ) ) ) );
        nationalityBox.setCellFactory( param -> new NationalityCell( ) );
        nationalityBox.setConverter( new StringConverter<PlayerNationality>( ) {
            @Override
            public String toString( final PlayerNationality n ) {
                if ( n == null ) {
                    return null;
                }
                return model.getI18n( ).get( "nationality." + n.name( ) ).get( );
            }

            @Override
            public PlayerNationality fromString( final String string ) {
                return null;
            }
        } );

        final Label nationalityLabel = new Label( );
        nationalityLabel.textProperty( ).bind( model.getI18n( ).get( "player.nationality" ) );
        nationalityLabel.setLabelFor( nationalityBox );

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
        birthdayLabel.textProperty( ).bind( model.getI18n( ).get( "player.birthday" ) );
        birthdayLabel.setLabelFor( birthdayDatePicker );

        grid.add( numeroLabel, 0, 0 );
        grid.add( numeroText, 1, 0 );
        grid.add( firstnameLabel, 0, 1 );
        grid.add( firstnameText, 1, 1 );
        grid.add( lastnameLabel, 0, 2 );
        grid.add( lastnameText, 1, 2 );
        grid.add( nationalityLabel, 0, 3 );
        grid.add( nationalityBox, 1, 3 );
        grid.add( birthdayLabel, 0, 4 );
        grid.add( birthdayDatePicker, 1, 4 );

        getDialogPane( ).setContent( grid );

        setResultConverter( param -> {
            if ( param == saveButtonType ) {
                return new Player( _model.getPlayerNumero( ),
                                   _model.getPlayerFirstname( ).trim( ).substring( 0, 1 ).toUpperCase( )
                                   + _model.getPlayerFirstname( ).trim( ).substring( 1 ).toLowerCase( ),
                                   _model.getPlayerLastname( ).trim( ).toUpperCase( ),
                                   _model.getPlayerBirthday( ).toString( ),
                                   _model.getPlayerNationality( ) );
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

    private final class NationalityCell extends ListCell<PlayerNationality> {

        NationalityCell( ) {
            setGraphicTextGap( 10 );
        }

        @Override
        protected void updateItem( final PlayerNationality item, final boolean empty ) {
            super.updateItem( item, empty );
            if ( empty || item == null ) {
                setText( null );
                setGraphic( null );
                return;
            }

            setText( _model.getI18n( ).get( "nationality." + item.name( ) ).getValue( ) );

            final String iconName = "img/country/" + item.name( ).toLowerCase( ) + ".png";
            try ( final InputStream imStream = getClass( ).getClassLoader( ).getResourceAsStream( iconName ) ) {
                if ( imStream != null ) {
                    final Image icon = new Image( imStream, 20, 20, true, true );
                    setGraphic( new ImageView( icon ) );
                } else {
                    setGraphic( null );
                }
            } catch ( final IOException ignored ) {
            }
        }
    }

    private final AddPlayerDialogModel _model;
}
