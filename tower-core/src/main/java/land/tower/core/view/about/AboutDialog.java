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

package land.tower.core.view.about;

import javafx.geometry.VPos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javax.inject.Inject;
import land.tower.core.ext.config.Configuration;
import land.tower.core.ext.i18n.I18nTranslator;

/**
 * Created on 23/12/2017
 * @author Cédric Longo
 */
public final class AboutDialog extends Dialog<Void> {

    @Inject
    public AboutDialog( final Stage owner, final Configuration config, final I18nTranslator translator ) {
        initOwner( owner );
        getDialogPane( ).addEventHandler( KeyEvent.KEY_RELEASED, ( KeyEvent event ) -> {
            if ( KeyCode.ESCAPE == event.getCode( ) ) {
                close( );
            }
        } );

        final ButtonType saveButtonType =
            new ButtonType( translator.get( "action.ok" ).get( ).toUpperCase( ), ButtonData.OK_DONE );
        getDialogPane( ).getButtonTypes( ).addAll( saveButtonType );

        titleProperty( ).bind( translator.get( "menu.about.about" ) );

        final GridPane grid = new GridPane( );
        grid.setHgap( 10 );
        grid.setVgap( 10 );

        final Label label = new Label( );
        label.textProperty( ).bind( translator.get( "menu.about.version" ) );
        label.setStyle( "-fx-font-weight: bold" );
        grid.add( label, 0, 0 );

        final Label info = new Label( );
        info.setText( config.get( "version" ) );
        grid.add( info, 1, 0 );

        final Label creditLabel = new Label( );
        creditLabel.textProperty( ).bind( translator.get( "menu.about.credits" ) );
        creditLabel.setStyle( "-fx-font-weight: bold" );
        GridPane.setValignment( creditLabel, VPos.TOP );
        grid.add( creditLabel, 0, 1 );

        final TextArea creditText = new TextArea( );
        creditText.setWrapText( true );
        creditText.setEditable( false );
        creditText.setText( translator.get( "about.credits" ).getValue( ) );
        grid.add( creditText, 1, 1 );

        final Label licenseLabel = new Label( );
        licenseLabel.textProperty( ).bind( translator.get( "menu.about.license" ) );
        licenseLabel.setStyle( "-fx-font-weight: bold" );
        GridPane.setValignment( licenseLabel, VPos.TOP );
        grid.add( licenseLabel, 0, 2 );

        final TextArea licenseText = new TextArea( );
        licenseText.setWrapText( true );
        licenseText.setEditable( false );
        licenseText.setText( translator.get( "about.license.third.party" ).getValue( ) );
        licenseText.setPrefHeight( 20 );
        grid.add( licenseText, 1, 2 );


        getDialogPane( ).setContent( grid );
    }
}
