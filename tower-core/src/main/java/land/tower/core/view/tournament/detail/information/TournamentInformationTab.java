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

import static javafx.beans.binding.Bindings.createBooleanBinding;
import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;
import static land.tower.core.ext.binding.Strings.toUpperCase;
import static land.tower.data.TournamentScoringMode.BY_POINTS;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTimePicker;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import javax.inject.Inject;
import land.tower.core.ext.font.FontAwesome;
import land.tower.core.model.rules.TournamentRules;
import land.tower.core.model.tournament.ObservableTournamentHeader;
import land.tower.core.view.component.FaButton;
import land.tower.core.view.player.ObservablePlayer;
import land.tower.data.PairingMode;
import land.tower.data.TournamentScoringMode;
import land.tower.data.TournamentStatus;
import land.tower.data.TournamentType;

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

        final Label icon = new Label( FontAwesome.OPTIONS );
        icon.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
        setGraphic( icon );

        final BorderPane main = new BorderPane( );
        main.setTop( buildActionBox( ) );
        main.setCenter( buildTournamentInfo( ) );

        setContent( main );
    }

    private HBox buildActionBox( ) {
        final HBox hBox = new HBox( );
        hBox.setSpacing( 20 );
        hBox.setPadding( new Insets( 10 ) );
        hBox.setAlignment( Pos.CENTER_RIGHT );

        final FaButton saveTournamentButton = new FaButton( FontAwesome.SAVE, "white" );
        saveTournamentButton.textProperty( ).bind(
            toUpperCase( _model.getI18n( ).get( "tournament.info.save.as.preference" ) ) );
        saveTournamentButton.getStyleClass( ).add( "rich-button" );
        saveTournamentButton.getStyleClass( ).add( "action-button" );
        saveTournamentButton.setOnAction( event -> _model.fireSaveAsPreference( ) );
        hBox.getChildren( ).add( saveTournamentButton );

        hBox.visibleProperty( )
            .bind( createBooleanBinding(
                ( ) -> _model.getTournament( ).getHeader( ).getStatus( ) != TournamentStatus.CLOSED,
                _model.getTournament( ).getHeader( ).statusProperty( ) ) );

        return hBox;
    }

    private Node buildTournamentInfo( ) {

        final VBox mainPane = new VBox( );
        mainPane.setAlignment( Pos.CENTER );
        mainPane.setSpacing( 10 );
        mainPane.setPadding( new Insets( 20, 0, 20, 0 ) );

        final ScrollPane scrollPane = new ScrollPane( mainPane );
        scrollPane.setFitToWidth( true );

        final StackPane stackPane = new StackPane( scrollPane );

        final GridPane grid = new GridPane( );
        grid.setAlignment( Pos.TOP_CENTER );
        grid.setVgap( 10 );
        grid.setHgap( 20 );

        final BooleanBinding tournamentOpened = createBooleanBinding(
            ( ) -> _model.getTournament( ).getHeader( ).getStatus( ) != TournamentStatus.CLOSED,
            _model.getTournament( ).getHeader( ).statusProperty( ) );

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
        titleField.disableProperty( ).bind( tournamentOpened.not( ) );
        grid.add( titleLabel, 0, line );
        grid.add( titleField, 1, line );

        setOnSelectionChanged( t -> {
            if ( isSelected( ) && _model.getTournament( ).getHeader( ).titleProperty( ).isEmpty( ).get( ) ) {
                Platform.runLater( titleField::requestFocus );
            }
        } );

        line++;
        final List<String> gameList = _model.getConfiguration( ).gameList( );
        final BiConsumer<Node, String> updateField = ( field, value ) -> {
            if ( value == null || value.trim( ).isEmpty( ) ) {
                field.getStyleClass( ).add( "required-field" );
            } else {
                field.getStyleClass( ).remove( "required-field" );
            }
        };
        if ( gameList.isEmpty( ) ) {
            final TextField gameField = new TextField( );
            gameField.textProperty( ).bindBidirectional( _model.getTournament( ).getHeader( ).gameProperty( ) );
            gameField.textProperty( ).addListener( ( observable, oldValue, newValue ) -> {
                updateField.accept( gameField, newValue );
            } );
            updateField.accept( gameField, gameField.getText( ) );

            final Label gameLabel = new Label( );
            gameLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.game" ) );
            gameLabel.setLabelFor( gameField );
            gameField.disableProperty( ).bind( tournamentOpened.not( ) );
            grid.add( gameLabel, 0, line );
            grid.add( gameField, 1, line );

        } else {
            final JFXComboBox<String> gameField = new JFXComboBox<>( );
            gameField.setItems( FXCollections.observableArrayList( gameList ) );
            gameField.valueProperty( ).bindBidirectional( _model.getTournament( ).getHeader( ).gameProperty( ) );
            gameField.setConverter( new StringConverter<String>( ) {
                @Override
                public String toString( final String object ) {
                    return _model.getI18n( ).get( "games." + object ).get( );
                }

                @Override
                public String fromString( final String string ) {
                    return null;
                }
            } );
            gameField.disableProperty( ).bind( createBooleanBinding( ( ) -> {
                final TournamentStatus status = _model.getTournament( ).getHeader( ).statusProperty( ).get( );
                switch ( status ) {
                    case NOT_CONFIGURED:
                    case PLANNED:
                    case ENROLMENT:
                        return false;
                    default:
                        return true;
                }
            }, _model.getTournament( ).getHeader( ).statusProperty( ) ) );

            gameField.valueProperty( ).addListener( ( observable, oldValue, newValue ) -> {
                updateField.accept( gameField, newValue );
            } );
            updateField.accept( gameField, gameField.getValue( ) );

            final Label gameLabel = new Label( );
            gameLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.game" ) );
            gameLabel.setLabelFor( gameField );
            grid.add( gameLabel, 0, line );
            grid.add( gameField, 1, line );
        }

        line++;
        final JFXDatePicker dateField = new JFXDatePicker( );
        dateField.setDialogParent( stackPane );
        dateField.setOverLay( true );
        dateField.setPromptText( "yyyy-mm-dd" );
        dateField.setShowWeekNumbers( false );
        dateField.setValue( _model.getTournament( ).getHeader( ).getDate( ).toLocalDate( ) );
        dateField.valueProperty( ).addListener( ( observable, oldValue, newValue ) -> {
            final ZonedDateTime tdate = _model.getTournament( ).getHeader( ).getDate( );
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
        dateField.disableProperty( ).bind( tournamentOpened.not( ) );

        final Label dateLabel = new Label( );
        dateLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.date" ) );
        dateLabel.setLabelFor( dateField );
        grid.add( dateLabel, 0, line );
        grid.add( dateField, 1, line );

        line++;

        final HBox timeCell = new HBox( );
        timeCell.setSpacing( 5 );

        final JFXTimePicker timePicker = new JFXTimePicker( );
        timePicker.setDialogParent( stackPane );
        timePicker.setOverLay( true );
        timePicker.set24HourView( true );
        timePicker.setEditable( false );
        timePicker.setValue( _model.getTournament( ).getHeader( ).getDate( ).toLocalTime( ) );
        timePicker.valueProperty( ).addListener( ( observable, oldValue, newValue ) -> {
            final ZonedDateTime tdate = _model.getTournament( ).getHeader( ).getDate( );
            _model.getTournament( ).getHeader( ).setDate( tdate.withHour( newValue.getHour( ) )
                                                               .withMinute( newValue.getMinute( ) ) );
        } );
        timePicker.setConverter( new StringConverter<LocalTime>( ) {
            @Override
            public String toString( final LocalTime object ) {
                return object.toString( );
            }

            @Override
            public LocalTime fromString( final String string ) {
                return LocalTime.parse( string );
            }
        } );
        timePicker.disableProperty( ).bind( tournamentOpened.not( ) );

        final Label zoneLabel = new Label( );
        zoneLabel.setText( _model.getTournament( ).getHeader( ).getDate( ).getZone( ).toString( ) );
        zoneLabel.setStyle( "-fx-font-style: italic" );
        timeCell.setAlignment( Pos.CENTER_LEFT );
        timeCell.getChildren( ).addAll( timePicker, zoneLabel );

        final Label timeLabel = new Label( );
        timeLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.time" ) );
        timeLabel.setLabelFor( timePicker );
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
        final ComboBox<TournamentType> typeField = new JFXComboBox<>( );
        typeField.disableProperty( ).bind( tournamentOpened.not( ) );
        typeField.itemsProperty( )
                 .bind( new SimpleListProperty<>( FXCollections.observableArrayList( TournamentType.values( ) ) ) );
        typeField.setConverter( new StringConverter<TournamentType>( ) {
            @Override
            public String toString( final TournamentType object ) {
                return _model.getI18n( ).get( "type." + object.name( ) ).get( );
            }

            @Override
            public TournamentType fromString( final String string ) {
                return null;
            }
        } );
        typeField.valueProperty( ).bindBidirectional( _model.getTournament( ).getHeader( ).typeProperty( ) );
        final Label typeLabel = new Label( );
        typeLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.type" ) );
        typeLabel.setLabelFor( typeLabel );
        grid.add( typeLabel, 0, line );
        grid.add( typeField, 1, line );

        line++;
        final ComboBox<PairingMode> pairingField = new JFXComboBox<>( );
        pairingField.disableProperty( ).bind( tournamentOpened.not( ) );
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
        pairingField.disableProperty( ).bind( createBooleanBinding( ( ) -> {
            final TournamentStatus status = _model.getTournament( ).getHeader( ).statusProperty( ).get( );
            switch ( status ) {
                case NOT_CONFIGURED:
                case PLANNED:
                case ENROLMENT:
                    return false;
                default:
                    return true;
            }
        }, _model.getTournament( ).getHeader( ).statusProperty( ) ) );
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
                                         final int count = Integer.parseInt( c.getControlNewText( ) );
                                         if ( count < 1 || count > 20 ) {
                                             return null;
                                         }
                                     }
                                     return matches ? c : null;
                                 } ) );
        teamSizeField.disableProperty( ).bind( createBooleanBinding( ( ) -> {
            final TournamentStatus status = _model.getTournament( ).getHeader( ).statusProperty( ).get( );
            switch ( status ) {
                case NOT_CONFIGURED:
                case PLANNED:
                    return false;
                default:
                    return true;
            }
        }, _model.getTournament( ).getHeader( ).statusProperty( ) ) );
        final Label teamSizeLabel = new Label( );
        teamSizeLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.teamSize" ) );
        teamSizeLabel.setLabelFor( teamSizeField );
        grid.add( teamSizeLabel, 0, line );
        grid.add( teamSizeField, 1, line );

        line++;
        _scoringModeBox = new JFXComboBox<>( );
        _scoringModeBox.itemsProperty( )
                       .bind( new SimpleListProperty<>(
                           FXCollections.observableArrayList( TournamentScoringMode.values( ) ) ) );
        _scoringModeBox.setConverter( new StringConverter<TournamentScoringMode>( ) {
            @Override
            public String toString( final TournamentScoringMode object ) {
                return _model.getI18n( ).get( "scoringMode." + object.name( ) ).get( );
            }

            @Override
            public TournamentScoringMode fromString( final String string ) {
                return null;
            }
        } );
        _scoringModeBox.valueProperty( )
                       .bindBidirectional( _model.getTournament( ).getHeader( ).scoringModeProperty( ) );

        final Label scoringModeLabel = new Label( );
        scoringModeLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.scoringMode" ) );
        scoringModeLabel.setLabelFor( scoringModeLabel );
        grid.add( scoringModeLabel, 0, line );
        grid.add( _scoringModeBox, 1, line );

        line++;
        _scoreMaxField = new TextField( );
        _scoreMaxField.setPrefWidth( 40 );
        _scoreMaxField.setMaxWidth( Pane.USE_PREF_SIZE );
        _scoreMaxField.textProperty( )
                      .bindBidirectional( _model.getTournament( ).getHeader( ).scoreMaxProperty( ),
                                          new IntegerStringConverter( ) );
        _scoreMaxField.setTextFormatter(
            new TextFormatter<>( new IntegerStringConverter( ),
                                 _model.getTournament( ).getHeader( ).getScoreMax( ),
                                 c -> {
                                     final boolean matches = Pattern.matches( "\\d*", c.getControlNewText( ) );
                                     if ( !c.getControlNewText( ).isEmpty( )
                                          && _model.getTournament( ).getHeader( ).getScoringMode( ) != BY_POINTS ) {
                                         final int count = Integer.parseInt( c.getControlNewText( ) );
                                         if ( count < 1 || count > 9 ) {
                                             return null;
                                         }
                                     }
                                     return matches ? c : null;
                                 } ) );

        final Label scoreMaxLabel = new Label( );
        scoreMaxLabel.textProperty( )
                     .bind( Bindings.createStringBinding( ( ) -> {
                         return _model.getI18n( ).get( "tournament.scoreMax." + _scoringModeBox.getValue( ) ).get( );
                     }, _scoringModeBox.valueProperty( ) ) );
        scoreMaxLabel.setLabelFor( _scoreMaxField );
        grid.add( scoreMaxLabel, 0, line );
        grid.add( _scoreMaxField, 1, line );

        line++;
        _doubleScoreCheckbox = new CheckBox( );
        _doubleScoreCheckbox.selectedProperty( )
                            .bindBidirectional( _model.getTournament( ).getHeader( ).doubleScoreProperty( ) );

        final Label doubleScoreLabel = new Label( );
        doubleScoreLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.doubleScore" ) );
        doubleScoreLabel.setLabelFor( _doubleScoreCheckbox );
        grid.add( doubleScoreLabel, 0, line );
        grid.add( _doubleScoreCheckbox, 1, line );

        line++;
        _scoreMaxBisField = new TextField( );
        _scoreMaxBisField.setPrefWidth( 40 );
        _scoreMaxBisField.setMaxWidth( Pane.USE_PREF_SIZE );
        _scoreMaxBisField.textProperty( )
                         .bindBidirectional( _model.getTournament( ).getHeader( ).scoreMaxBisProperty( ),
                                             new IntegerStringConverter( ) );
        _scoreMaxBisField.setTextFormatter(
            new TextFormatter<>( new IntegerStringConverter( ),
                                 _model.getTournament( ).getHeader( ).getScoreMax( ),
                                 c -> {
                                     final boolean matches = Pattern.matches( "\\d*", c.getControlNewText( ) );
                                     return matches ? c : null;
                                 } ) );

        final Label scoreMaxBisLabel = new Label( );
        scoreMaxBisLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.scoreMaxBis" ) );
        scoreMaxBisLabel.setLabelFor( _scoreMaxBisField );
        grid.add( scoreMaxBisLabel, 0, line );
        grid.add( _scoreMaxBisField, 1, line );

        line++;
        final TextField matchDurationField = new TextField( );
        matchDurationField.disableProperty( ).bind( tournamentOpened.not( ) );
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
        addressNameField.disableProperty( ).bind( tournamentOpened.not( ) );
        addressNameField.textProperty( )
                        .bindBidirectional( _model.getTournament( ).getHeader( ).getAddress( ).nameProperty( ) );
        final Label addressNameLabel = new Label( );
        addressNameLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.address.name" ) );
        addressNameLabel.setLabelFor( addressNameField );
        grid.add( addressNameLabel, 0, line );
        grid.add( addressNameField, 1, line );

        line++;
        final TextField addressLine1Field = new TextField( );
        addressLine1Field.disableProperty( ).bind( tournamentOpened.not( ) );
        addressLine1Field.textProperty( )
                         .bindBidirectional( _model.getTournament( ).getHeader( ).getAddress( ).line1Property( ) );
        final Label addressLine1Label = new Label( );
        addressLine1Label.textProperty( ).bind( _model.getI18n( ).get( "tournament.address.line1" ) );
        addressLine1Label.setLabelFor( addressLine1Field );
        grid.add( addressLine1Label, 0, line );
        grid.add( addressLine1Field, 1, line );

        line++;
        final TextField addressLine2Field = new TextField( );
        addressLine2Field.disableProperty( ).bind( tournamentOpened.not( ) );
        addressLine2Field.textProperty( )
                         .bindBidirectional( _model.getTournament( ).getHeader( ).getAddress( ).line2Property( ) );
        final Label addressLine2Label = new Label( );
        addressLine2Label.textProperty( ).bind( _model.getI18n( ).get( "tournament.address.line2" ) );
        addressLine2Label.setLabelFor( addressLine2Field );
        grid.add( addressLine2Label, 0, line );
        grid.add( addressLine2Field, 1, line );

        line++;
        final TextField addressPostalCodeField = new TextField( );
        addressPostalCodeField.disableProperty( ).bind( tournamentOpened.not( ) );
        addressPostalCodeField.setPrefWidth( 100 );
        addressPostalCodeField.setMaxWidth( Pane.USE_PREF_SIZE );
        addressPostalCodeField.textProperty( )
                              .bindBidirectional(
                                  _model.getTournament( ).getHeader( ).getAddress( ).postalCodeProperty( ) );
        final Label addressPostalCodeLabel = new Label( );
        addressPostalCodeLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.address.postalCode" ) );
        addressPostalCodeLabel.setLabelFor( addressPostalCodeField );
        grid.add( addressPostalCodeLabel, 0, line );
        grid.add( addressPostalCodeField, 1, line );

        line++;
        final TextField addressCityField = new TextField( );
        addressCityField.disableProperty( ).bind( tournamentOpened.not( ) );
        addressCityField.textProperty( )
                        .bindBidirectional( _model.getTournament( ).getHeader( ).getAddress( ).cityProperty( ) );
        final Label addressCityLabel = new Label( );
        addressCityLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.address.city" ) );
        addressCityLabel.setLabelFor( addressCityField );
        grid.add( addressCityLabel, 0, line );
        grid.add( addressCityField, 1, line );

        line++;
        final TextField addressCountryField = new TextField( );
        addressCountryField.disableProperty( ).bind( tournamentOpened.not( ) );
        addressCountryField.textProperty( )
                           .bindBidirectional( _model.getTournament( ).getHeader( ).getAddress( ).countryProperty( ) );
        final Label addressCountryLabel = new Label( );
        addressCountryLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.address.country" ) );
        addressCountryLabel.setLabelFor( addressCountryField );
        grid.add( addressCountryLabel, 0, line );
        grid.add( addressCountryField, 1, line );

        /* Judges Section */
        line++;
        final Label judgeTitle = new Label( );
        judgeTitle.textProperty( ).bind( _model.getI18n( ).get( "tournament.judges" ) );
        judgeTitle.getStyleClass( ).add( "important" );
        judgeTitle.getStyleClass( ).add( "medium" );
        judgeTitle.setAlignment( Pos.CENTER );
        judgeTitle.setPrefWidth( WIDTH );
        grid.add( judgeTitle, 0, line, 2, 1 );

        line++;
        final HBox mainJudgeBox = new HBox( );
        mainJudgeBox.setSpacing( 10 );
        mainJudgeBox.setAlignment( Pos.CENTER_LEFT );

        final Label mainJudgeName = new Label( );
        mainJudgeName.textProperty( ).bind( Bindings.createStringBinding( ( ) -> {
            final ObservablePlayer mainJudge = _model.getTournament( ).getHeader( ).getMainJudge( );
            if ( mainJudge == null ) {
                return "";
            }
            return mainJudge.getNumero( ) + " – " + mainJudge.getLastname( ) + " " + mainJudge.getFirstname( );
        }, _model.getTournament( ).getHeader( ).mainJudgeProperty( ) ) );

        final Button clearMainJudge = new FaButton( FontAwesome.CROSS, "black" );
        clearMainJudge.setOnAction( e -> _model.fireClearMainJudge( ) );

        final Button selectMainJudge = new FaButton( FontAwesome.SEARCH_ADD, "black" );
        selectMainJudge.setOnAction( e -> _model.fireSelectMainJudge( ) );

        final Runnable updateMainJudgeSelection = ( ) -> {
            if ( _model.getTournament( ).getHeader( ).getMainJudge( ) != null ) {
                mainJudgeBox.getChildren( ).setAll( mainJudgeName, clearMainJudge );
            } else {
                mainJudgeBox.getChildren( ).setAll( selectMainJudge );
            }
        };
        updateMainJudgeSelection.run( );

        _model.getTournament( ).getHeader( ).mainJudgeProperty( ).addListener( ( observable, oldValue, newValue ) -> {
            updateMainJudgeSelection.run( );
        } );

        final Label mainJudgeLabel = new Label( );
        mainJudgeLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.judge.main" ) );
        mainJudgeLabel.setLabelFor( mainJudgeBox );
        grid.add( mainJudgeLabel, 0, line );
        grid.add( mainJudgeBox, 1, line );

        line++;
        final HBox secondaryJudgeBox = new HBox( );
        secondaryJudgeBox.setSpacing( 10 );
        secondaryJudgeBox.setAlignment( Pos.CENTER_LEFT );

        final TableView<ObservablePlayer> secondaryJudgesTable = new TableView<>( );
        secondaryJudgeBox.getChildren( ).add( secondaryJudgesTable );
        secondaryJudgesTable.setColumnResizePolicy( CONSTRAINED_RESIZE_POLICY );
        secondaryJudgesTable.itemsProperty( ).bind( _model.getTournament( ).getHeader( ).judgesProperty( ) );
        secondaryJudgesTable.setFixedCellSize( 25 );
        secondaryJudgesTable.prefWidthProperty( )
                            .bind( scrollPane.widthProperty( ).divide( 3 ) );
        secondaryJudgesTable.prefHeightProperty( )
                            .bind( Bindings.size( secondaryJudgesTable.getItems( ) )
                                           .add( 1 )
                                           .multiply( secondaryJudgesTable.getFixedCellSize( ) )
                                           .add( 25 ) );

        final TableColumn<ObservablePlayer, Long> numeroCol = new TableColumn<>( );
        numeroCol.textProperty( ).bind( _model.getI18n( ).get( "player.numero" ) );
        numeroCol.setCellValueFactory( new PropertyValueFactory<>( "numero" ) );
        secondaryJudgesTable.getColumns( ).add( numeroCol );

        final TableColumn<ObservablePlayer, String> nameCol = new TableColumn<>( );
        nameCol.textProperty( ).bind( _model.getI18n( ).get( "player.lastname" ) );
        nameCol.setCellValueFactory( param -> param.getValue( ).lastnameProperty( )
                                                   .concat( " " )
                                                   .concat( param.getValue( ).getFirstname( ) ) );
        secondaryJudgesTable.getColumns( ).add( nameCol );

        final TableColumn<ObservablePlayer, Void> actionColumn = new TableColumn<>( );
        actionColumn.setMaxWidth( 20 );
        actionColumn.setMinWidth( 20 );
        actionColumn.setResizable( false );
        actionColumn.setCellFactory( param -> new DeleteJudgeCell( ) );
        secondaryJudgesTable.getColumns( ).add( actionColumn );

        final Label emptyLabel = new Label( );
        emptyLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.judge.no.judge" ) );
        secondaryJudgesTable.setPlaceholder( emptyLabel );

        final Button selectSecondaryJudge = new FaButton( FontAwesome.SEARCH_ADD, "black" );
        selectSecondaryJudge.setOnAction( e -> _model.fireSelectSecondaryJudge( ) );
        secondaryJudgeBox.getChildren( ).add( selectSecondaryJudge );

        final Label secondaryJudgeLabel = new Label( );
        secondaryJudgeLabel.textProperty( ).bind( _model.getI18n( ).get( "tournament.judge.secondary" ) );
        secondaryJudgeLabel.setLabelFor( secondaryJudgesTable );
        GridPane.setValignment( secondaryJudgeLabel, VPos.TOP );
        grid.add( secondaryJudgeLabel, 0, line );
        grid.add( secondaryJudgeBox, 1, line );

        mainPane.getChildren( ).add( grid );

        _model.getTournament( ).getHeader( ).gameProperty( )
              .addListener( ( observable, oldValue, newValue ) -> checkTournamentRules( ) );

        checkTournamentRules( );

        return stackPane;
    }

    private void checkTournamentRules( ) {
        final ObservableTournamentHeader header = _model.getTournament( ).getHeader( );
        final String game = header.getGame( );
        final TournamentRules rules = _model.getTournamentRules( game );

        final BooleanBinding tournamentNotStarted = createBooleanBinding( ( ) -> {
            final TournamentStatus status = _model.getTournament( ).getHeader( ).statusProperty( ).get( );
            switch ( status ) {
                case NOT_CONFIGURED:
                case PLANNED:
                case ENROLMENT:
                    return false;
                default:
                    return true;
            }
        }, _model.getTournament( ).getHeader( ).statusProperty( ) );

        if ( rules.getScoringMode( ).isPresent( ) ) {
            _scoringModeBox.disableProperty( ).unbind( );
            _scoringModeBox.setDisable( true );
            header.setScoringMode( rules.getScoringMode( ).get( ) );
        } else {
            _scoringModeBox.disableProperty( ).unbind( );
            _scoringModeBox.disableProperty( ).bind( tournamentNotStarted );
        }

        if ( rules.getScoreMax( ).isPresent( ) ) {
            _scoreMaxField.disableProperty( ).unbind( );
            _scoreMaxField.setDisable( true );
            header.setScoreMax( rules.getScoreMax( ).get( ) );
        } else {
            _scoreMaxField.disableProperty( ).unbind( );
            _scoreMaxField.disableProperty( ).bind( tournamentNotStarted );
        }

        if ( rules.getScoreMaxBis( ).isPresent( ) ) {
            _doubleScoreCheckbox.disableProperty( ).unbind( );
            _doubleScoreCheckbox.setDisable( true );
            header.setDoubleScore( true );

            _scoreMaxBisField.disableProperty( ).unbind( );
            _scoreMaxBisField.setDisable( true );
            header.setScoreMaxBis( rules.getScoreMaxBis( ).get( ) );

        } else {
            _doubleScoreCheckbox.disableProperty( ).unbind( );
            _doubleScoreCheckbox.disableProperty( ).bind( tournamentNotStarted );

            _scoreMaxBisField.disableProperty( ).unbind( );
            _scoreMaxBisField.disableProperty( )
                             .bind( tournamentNotStarted.or( header.doubleScoreProperty( ).not( ) ) );
        }
    }

    private final TournamentInformationTabModel _model;
    private JFXComboBox<TournamentScoringMode> _scoringModeBox;
    private TextField _scoreMaxField;
    private TextField _scoreMaxBisField;
    private CheckBox _doubleScoreCheckbox;

    private class DeleteJudgeCell extends TableCell<ObservablePlayer, Void> {

        final Label _button = new Label( FontAwesome.CROSS );

        DeleteJudgeCell( ) {
            _button.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
            _button.getStyleClass( ).add( "small" );
            _button.setCursor( Cursor.HAND );
            _button.setOnMouseClicked( t -> {
                final ObservablePlayer player = getTableView( ).getItems( ).get( getIndex( ) );
                _model.fireDeleteSecondaryJudge( player );
            } );
        }

        @Override
        protected void updateItem( Void t, boolean empty ) {
            super.updateItem( t, empty );
            if ( !empty ) {
                setGraphic( _button );
            } else {
                setGraphic( null );
            }
        }
    }
}
