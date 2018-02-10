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

package land.tower.core.view.tournament.detail.ladder;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;

import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import javafx.util.converter.LongStringConverter;
import land.tower.core.model.tournament.ObservableTeam;
import land.tower.data.PairingMode;

/**
 * Created on 10/12/2017
 * @author Cédric Longo
 */
final class ChainTournamentDialog extends Dialog<Void> {

    public ChainTournamentDialog( final ChainTournamentDialogModel model ) {
        initOwner( model.getOwner( ) );

        _model = model;
        titleProperty( ).bind( model.getI18n( ).get( "tournament.chaining.title" ) );
        headerTextProperty( ).bind( model.getI18n( ).get( "tournament.chaining.message" ) );

        final ButtonType saveButtonType =
            new ButtonType( model.getI18n( ).get( "action.ok" ).get( ).toUpperCase( ), ButtonData.OK_DONE );
        final ButtonType cancelButtonType =
            new ButtonType( model.getI18n( ).get( "action.cancel" ).get( ).toUpperCase( ), ButtonData.CANCEL_CLOSE );
        getDialogPane( ).getButtonTypes( ).addAll( saveButtonType, cancelButtonType );

        final Button okButton = (Button) getDialogPane( ).lookupButton( saveButtonType );
        okButton.disableProperty( ).bind( model.isValidProperty( ).not( ) );
        okButton.setDefaultButton( true );
        okButton.setOnAction( event -> _model.fireCreateTournament( ) );

        final GridPane grid = new GridPane( );
        grid.setHgap( 10 );
        grid.setVgap( 10 );
        grid.setPadding( new Insets( 20, 150, 10, 10 ) );

        int line = 0;
        final ComboBox<PairingMode> pairingBox = new JFXComboBox<>( );
        pairingBox.setItems( FXCollections.observableArrayList( PairingMode.values( ) ) );
        pairingBox.valueProperty( ).bindBidirectional( model.pairingModeProperty( ) );
        pairingBox.setConverter( new StringConverter<PairingMode>( ) {
            @Override
            public String toString( final PairingMode object ) {
                return _model.getI18n( ).get( "pairing." + object.name( ) ).get( );
            }

            @Override
            public PairingMode fromString( final String string ) {
                return null;
            }
        } );

        final Label pairingModeLabel = new Label( );
        pairingModeLabel.getStyleClass( ).add( "important" );
        pairingModeLabel.textProperty( ).bind( model.getI18n( ).get( "tournament.pairingMode" ) );
        pairingModeLabel.setLabelFor( pairingBox );
        grid.add( pairingModeLabel, 0, line );
        grid.add( pairingBox, 1, line );

        line++;
        final TextField teamCountField = new TextField( );
        teamCountField.setMaxWidth( 50 );
        teamCountField.textProperty( ).bindBidirectional( model.teamCountProperty( ), new LongStringConverter( ) );
        teamCountField.setTextFormatter(
            new TextFormatter<>( new LongStringConverter( ),
                                 8L,
                                 c -> Pattern.matches( "\\d*", c.getControlNewText( ) ) ? c : null ) );

        final Label teamCountLabel = new Label( );
        teamCountLabel.getStyleClass( ).add( "important" );
        teamCountLabel.textProperty( ).bind( model.getI18n( ).get( "tournament.chaining.team.count" ) );
        teamCountLabel.setLabelFor( teamCountField );
        grid.add( teamCountLabel, 0, line );
        grid.add( teamCountField, 1, line );

        line++;
        _activeTeamsOnlyCheckbox = new JFXCheckBox( );
        _activeTeamsOnlyCheckbox.selectedProperty( ).bindBidirectional( model.activeTeamsOnlyProperty( ) );

        final Label activeTeamOnlyLabel = new Label( );
        activeTeamOnlyLabel.getStyleClass( ).add( "important" );
        activeTeamOnlyLabel.textProperty( ).bind( model.getI18n( ).get( "tournament.chaining.active.teams.only" ) );
        activeTeamOnlyLabel.setLabelFor( _activeTeamsOnlyCheckbox );
        grid.add( activeTeamOnlyLabel, 0, line );
        grid.add( _activeTeamsOnlyCheckbox, 1, line );

        line++;
        final CheckBox selectedTeamsOnlyCheckbox = new JFXCheckBox( );
        selectedTeamsOnlyCheckbox.selectedProperty( ).bindBidirectional( model.selectedTeamsOnlyProperty( ) );

        final Label selectedTeamOnlyLabel = new Label( );
        selectedTeamOnlyLabel.getStyleClass( ).add( "important" );
        selectedTeamOnlyLabel.textProperty( ).bind( model.getI18n( ).get( "tournament.chaining.selected.teams.only" ) );
        selectedTeamOnlyLabel.setLabelFor( selectedTeamOnlyLabel );
        grid.add( selectedTeamOnlyLabel, 0, line );
        grid.add( selectedTeamsOnlyCheckbox, 1, line );

        if ( model.getSelectedTeams( ) == null || model.getSelectedTeams( ).size( ) < 2 ) {
            selectedTeamsOnlyCheckbox.setSelected( false );
            selectedTeamsOnlyCheckbox.setDisable( true );
        }

        selectedTeamsOnlyCheckbox.selectedProperty( )
                                 .addListener( ( observable, oldValue, newValue ) -> updateActiveTeamsCheckbox( ) );
        updateActiveTeamsCheckbox( );

        getDialogPane( ).setContent( grid );
        getDialogPane( ).setPrefWidth( 650 );
    }

    private void updateActiveTeamsCheckbox( ) {
        final ObservableList<ObservableTeam> teams =
            _model.isSelectedTeamsOnly( ) ? _model.getSelectedTeams( ) : _model.getTournament( ).getTeams( );

        final long activeTeams = teams.stream( )
                                      .filter( ObservableTeam::isActive )
                                      .count( );
        if ( activeTeams < 2 ) {
            _activeTeamsOnlyCheckbox.setSelected( false );
            _activeTeamsOnlyCheckbox.setDisable( true );
        } else {
            _activeTeamsOnlyCheckbox.setDisable( false );
        }
    }

    private final ChainTournamentDialogModel _model;
    private final CheckBox _activeTeamsOnlyCheckbox;
}
