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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javax.inject.Inject;

/**
 * Created on 20/12/2017
 * @author Cédric Longo
 */
public final class TournamentInformationTab extends Tab {

    @Inject
    public TournamentInformationTab( final TournamentInformationTabModel model ) {
        _model = model;
        textProperty( ).bind( _model.getI18n( ).get( "tournament.tab.information" ) );

        final VBox mainPane = new VBox( );
        mainPane.setSpacing( 10 );
        mainPane.setPadding( new Insets( 10, 0, 0, 0 ) );
        setContent( mainPane );

        final GridPane grid = new GridPane( );
        grid.setAlignment( Pos.TOP_CENTER );
        grid.setVgap( 10 );
        grid.setHgap( 20 );

        int line = 0;

        // TODO General : title, date
        /* General Section */
        final Label generalTitle = new Label( );
        generalTitle.textProperty( ).bind( _model.getI18n( ).get( "tournament.information.general" ) );
        generalTitle.getStyleClass( ).add( "important" );
        generalTitle.getStyleClass( ).add( "medium" );
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
            if ( !newValue.matches( "[\\d ]*" ) ) {
                hourField.setText( newValue.replaceAll( "[^\\d ]", "" ) );
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
            if ( !newValue.matches( "[\\d ]*" ) ) {
                minField.setText( newValue.replaceAll( "[^\\d ]", "" ) );
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

        // TODO Adresse
        // TODO Configuration : kvalue, pairing mode
        // TODO Arbitrage
        mainPane.getChildren( ).add( grid );

    }

    private final TournamentInformationTabModel _model;
}
