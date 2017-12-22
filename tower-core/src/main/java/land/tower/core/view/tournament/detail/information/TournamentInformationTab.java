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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import javax.inject.Inject;
import land.tower.data.PairingMode;

/**
 * Created on 20/12/2017
 * @author Cédric Longo
 */
public final class TournamentInformationTab extends Tab {

    private static final int WIDTH = 600;

    @Inject
    public TournamentInformationTab( final TournamentInformationTabModel model ) {
        _model = model;
        textProperty( ).bind( _model.getI18n( ).get( "tournament.tab.information" ) );

        final VBox mainPane = new VBox( );
        mainPane.setAlignment( Pos.CENTER );
        mainPane.setSpacing( 10 );
        mainPane.setPadding( new Insets( 20, 0, 20, 0 ) );

        final ScrollPane scrollPane = new ScrollPane( mainPane );
        scrollPane.setFitToWidth( true );
        setContent( scrollPane );

        final GridPane grid = new GridPane( );
        grid.setAlignment( Pos.TOP_CENTER );
        grid.setVgap( 10 );
        grid.setHgap( 20 );

        int line = 0;

        /* General Section */
        final Label generalTitle = new Label( );
        generalTitle.textProperty( ).bind( _model.getI18n( ).get( "tournament.information.general" ) );
        generalTitle.getStyleClass( ).add( "important" );
        generalTitle.getStyleClass( ).add( "medium" );
        generalTitle.setAlignment( Pos.CENTER );
        generalTitle.setPrefWidth( WIDTH );
        grid.add( generalTitle, 0, line, 2, 1 );

        line++;
        final TextField titleField = new TextField( );
        titleField.textProperty( ).bindBidirectional( _model.getTournament( ).getHeader( ).titleProperty( ) );
        final Label titleLabel = new Label( );
        titleLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.title" ) );
        titleLabel.setLabelFor( titleField );
        grid.add( titleLabel, 0, line );
        grid.add( titleField, 1, line );

        line++;
        final DatePicker dateField = new DatePicker( );
        dateField.setPromptText( "yyyy-mm-dd" );
        dateField.setShowWeekNumbers( false );
        dateField.setValue( _model.getTournament( ).getHeader( ).getDate( ).toLocalDate( ) );
        dateField.valueProperty( ).addListener( ( observable, oldValue, newValue ) -> {
            final LocalDateTime tdate = _model.getTournament( ).getHeader( ).getDate( );
            _model.getTournament( ).getHeader( ).setDate( tdate.withYear( newValue.getYear( ) )
                                                               .withMonth( newValue.getMonthValue( ) )
                                                               .withDayOfYear( newValue.getDayOfYear( ) ) );
        } );
        dateField.setConverter( new StringConverter<LocalDate>( ) {
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
        dateField.setEditable( false );

        final Label dateLabel = new Label( );
        dateLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.date" ) );
        dateLabel.setLabelFor( dateField );
        grid.add( dateLabel, 0, line );
        grid.add( dateField, 1, line );

        line++;
        final HBox timeCell = new HBox( );
        timeCell.setSpacing( 3 );
        final TextField hourField = new TextField( );
        hourField.setPrefWidth( 40 );
        final int thour = _model.getTournament( ).getHeader( ).getDate( ).getHour( );
        hourField.textProperty( )
                 .setValue( ( thour < 10 ? "0" : "" ) + String.valueOf( thour ) );
        hourField.textProperty( ).addListener( ( observable, oldValue, newValue ) -> {
            if ( !newValue.matches( "[\\d]*" ) ) {
                hourField.setText( newValue.replaceAll( "[^\\d]", "" ) );
            }
            if ( newValue.length( ) > 2 ) {
                hourField.setText( newValue.substring( 0, 2 ) );
            }
            if ( !oldValue.equals( newValue ) ) {
                int hour = 0;
                try {
                    hour = Integer.parseInt( newValue );
                    hour = Math.max( hour, 0 );
                    hour = Math.min( hour, 23 );
                } catch ( final NumberFormatException ignored ) {
                }
                final LocalDateTime tdate = _model.getTournament( ).getHeader( ).getDate( );
                _model.getTournament( ).getHeader( ).setDate( tdate.withHour( hour ) );
                hourField.setText( ( hour < 10 ? "0" : "" ) + String.valueOf( hour ) );
            }
        } );

        final TextField minField = new TextField( );
        minField.setPrefWidth( 40 );
        final int tmin = _model.getTournament( ).getHeader( ).getDate( ).getMinute( );
        minField.textProperty( ).setValue( ( tmin < 10 ? "0" : "" ) + String.valueOf( tmin ) );
        minField.textProperty( ).addListener( ( observable, oldValue, newValue ) -> {
            if ( !newValue.matches( "[\\d]*" ) ) {
                minField.setText( newValue.replaceAll( "[^\\d]", "" ) );
            }
            if ( newValue.length( ) > 2 ) {
                minField.setText( newValue.substring( 0, 2 ) );
            }
            if ( !oldValue.equals( newValue ) ) {
                int minute = 0;
                try {
                    minute = Integer.parseInt( newValue );
                    minute = Math.max( minute, 0 );
                    minute = Math.min( minute, 59 );
                } catch ( final NumberFormatException ignored ) {
                }
                final LocalDateTime tdate = _model.getTournament( ).getHeader( ).getDate( );
                _model.getTournament( ).getHeader( ).setDate( tdate.withMinute( minute ) );
                minField.setText( ( minute < 10 ? "0" : "" ) + String.valueOf( minute ) );
            }
        } );
        timeCell.getChildren( ).addAll( hourField, new Label( ":" ), minField );

        final Label timeLabel = new Label( );
        timeLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.time" ) );
        timeLabel.setLabelFor( timeCell );
        grid.add( timeLabel, 0, line );
        grid.add( timeCell, 1, line );


        /* Configuration Section */
        line++;
        final Label configurationTitle = new Label( );
        configurationTitle.textProperty( ).bind( _model.getI18n( ).get( "tournament.configuration" ) );
        configurationTitle.getStyleClass( ).add( "important" );
        configurationTitle.getStyleClass( ).add( "medium" );
        configurationTitle.setAlignment( Pos.CENTER );
        configurationTitle.setPrefWidth( WIDTH );
        grid.add( configurationTitle, 0, line, 2, 1 );

        line++;
        final ChoiceBox<PairingMode> pairingField = new ChoiceBox<>( );
        pairingField.itemsProperty( )
                    .bind( new SimpleListProperty<>( FXCollections.observableArrayList( PairingMode.values( ) ) ) );
        pairingField.setConverter( new StringConverter<PairingMode>( ) {
            @Override
            public String toString( final PairingMode object ) {
                return _model.getI18n( ).get( "pairing." + object.name( ) ).get( );
            }

            @Override
            public PairingMode fromString( final String string ) {
                return null;
            }
        } );
        pairingField.valueProperty( )
                    .bindBidirectional( _model.getTournament( ).getHeader( ).pairingModeProperty( ) );
        final Label pairingModeLabel = new Label( );
        pairingModeLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.pairingMode" ) );
        pairingModeLabel.setLabelFor( pairingModeLabel );
        grid.add( pairingModeLabel, 0, line );
        grid.add( pairingField, 1, line );

        line++;
        final TextField teamSizeField = new TextField( );
        teamSizeField.setPrefWidth( 40 );
        teamSizeField.setMaxWidth( Pane.USE_PREF_SIZE );
        teamSizeField.textProperty( ).bindBidirectional( _model.getTournament( ).getHeader( ).teamSizeProperty( ),
                                                         new IntegerStringConverter( ) );
        teamSizeField.setTextFormatter(
            new TextFormatter<>( new IntegerStringConverter( ),
                                 _model.getTournament( ).getHeader( ).getTeamSize( ),
                                 c -> {
                                     final boolean matches = Pattern.matches( "\\d*", c.getControlNewText( ) );
                                     if ( !c.getControlNewText( ).isEmpty( ) ) {
                                         if ( Integer.parseInt( c.getControlNewText( ) ) > 20 ) {
                                             return null;
                                         }
                                     }
                                     return matches ? c : null;
                                 } ) );
        final Label teamSizeLabel = new Label( );
        teamSizeLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.teamSize" ) );
        teamSizeLabel.setLabelFor( teamSizeField );
        grid.add( teamSizeLabel, 0, line );
        grid.add( teamSizeField, 1, line );

        line++;
        final TextField matchDurationField = new TextField( );
        matchDurationField.setPrefWidth( 40 );
        matchDurationField.setMaxWidth( Pane.USE_PREF_SIZE );
        matchDurationField.textProperty( )
                          .bindBidirectional( _model.getTournament( ).getHeader( ).matchDurationProperty( ),
                                              new IntegerStringConverter( ) );
        matchDurationField.setTextFormatter(
            new TextFormatter<>( new IntegerStringConverter( ),
                                 _model.getTournament( ).getHeader( ).getMatchDuration( ),
                                 c -> Pattern.matches( "\\d*", c.getControlNewText( ) ) ? c : null ) );
        final Label matchDurationLabel = new Label( );
        matchDurationLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.matchDuration" ) );
        matchDurationLabel.setLabelFor( matchDurationField );
        grid.add( matchDurationLabel, 0, line );
        grid.add( matchDurationField, 1, line );


        /* Address Section */
        line++;
        final Label addressTitle = new Label( );
        addressTitle.textProperty( ).bind( _model.getI18n( ).get( "tournament.address" ) );
        addressTitle.getStyleClass( ).add( "important" );
        addressTitle.getStyleClass( ).add( "medium" );
        addressTitle.setAlignment( Pos.CENTER );
        addressTitle.setPrefWidth( WIDTH );
        grid.add( addressTitle, 0, line, 2, 1 );

        line++;
        final TextField addressNameField = new TextField( );
        addressNameField.textProperty( )
                        .bindBidirectional( _model.getTournament( ).getHeader( ).getAddress( ).nameProperty( ) );
        final Label addressNameLabel = new Label( );
        addressNameLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.address.name" ) );
        addressNameLabel.setLabelFor( addressNameLabel );
        grid.add( addressNameLabel, 0, line );
        grid.add( addressNameField, 1, line );

        line++;
        final TextField addressLine1Field = new TextField( );
        addressLine1Field.textProperty( )
                         .bindBidirectional( _model.getTournament( ).getHeader( ).getAddress( ).line1Property( ) );
        final Label addressLine1Label = new Label( );
        addressLine1Label.textProperty( ).bind( _model.getI18n( ).get( "tournament.address.line1" ) );
        addressLine1Label.setLabelFor( addressLine1Label );
        grid.add( addressLine1Label, 0, line );
        grid.add( addressLine1Field, 1, line );

        line++;
        final TextField addressLine2Field = new TextField( );
        addressLine2Field.textProperty( )
                         .bindBidirectional( _model.getTournament( ).getHeader( ).getAddress( ).line2Property( ) );
        final Label addressLine2Label = new Label( );
        addressLine2Label.textProperty( ).bind( _model.getI18n( ).get( "tournament.address.line2" ) );
        addressLine2Label.setLabelFor( addressLine2Label );
        grid.add( addressLine2Label, 0, line );
        grid.add( addressLine2Field, 1, line );

        line++;
        final TextField addressPostalCodeField = new TextField( );
        addressPostalCodeField.setPrefWidth( 100 );
        addressPostalCodeField.setMaxWidth( Pane.USE_PREF_SIZE );
        addressPostalCodeField.textProperty( )
                              .bindBidirectional(
                                  _model.getTournament( ).getHeader( ).getAddress( ).postalCodeProperty( ) );
        final Label addressPostalCodeLabel = new Label( );
        addressPostalCodeLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.address.postalCode" ) );
        addressPostalCodeLabel.setLabelFor( addressPostalCodeLabel );
        grid.add( addressPostalCodeLabel, 0, line );
        grid.add( addressPostalCodeField, 1, line );

        line++;
        final TextField addressCityField = new TextField( );
        addressCityField.textProperty( )
                        .bindBidirectional( _model.getTournament( ).getHeader( ).getAddress( ).cityProperty( ) );
        final Label addressCityLabel = new Label( );
        addressCityLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.address.city" ) );
        addressCityLabel.setLabelFor( addressCityLabel );
        grid.add( addressCityLabel, 0, line );
        grid.add( addressCityField, 1, line );

        line++;
        final TextField addressCountryField = new TextField( );
        addressCountryField.textProperty( )
                           .bindBidirectional( _model.getTournament( ).getHeader( ).getAddress( ).countryProperty( ) );
        final Label addressCountryLabel = new Label( );
        addressCountryLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.address.country" ) );
        addressCountryLabel.setLabelFor( addressCountryLabel );
        grid.add( addressCountryLabel, 0, line );
        grid.add( addressCountryField, 1, line );

        // TODO Judge
        mainPane.getChildren( ).add( grid );
    }

    private final TournamentInformationTabModel _model;
}
