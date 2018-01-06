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
import javafx.scene.layout.GridPane;
import javax.inject.Inject;
import land.tower.core.ext.config.Configuration;
import land.tower.core.ext.i18n.I18nTranslator;

/**
 * Created on 23/12/2017
 * @author Cédric Longo
 */
public final class AboutDialog extends Dialog<Void> {

    @Inject
    public AboutDialog( final Configuration config, final I18nTranslator translator ) {
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

        final Label licenceLabel = new Label( );
        licenceLabel.textProperty( ).bind( translator.get( "menu.about.credits" ) );
        licenceLabel.setStyle( "-fx-font-weight: bold" );
        GridPane.setValignment( licenceLabel, VPos.TOP );
        grid.add( licenceLabel, 0, 1 );

        final TextArea licenceText = new TextArea( );
        licenceText.setWrapText( true );
        licenceText.setEditable( false );
        licenceText.setText( translator.get( "about.licence.third.party" ).getValue( ) );
        grid.add( licenceText, 1, 1 );

        getDialogPane( ).setContent( grid );
    }
}
