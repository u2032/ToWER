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

package land.tower.core.view.update;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import land.tower.core.ext.updater.VersionInformation;

/**
 * Created on 09/02/2018
 * @author Cédric Longo
 */
final class UpdateDialog extends Alert {

    UpdateDialog( final UpdateDialogModel model, final VersionInformation version ) {
        super( AlertType.INFORMATION );
        _model = model;
        initOwner( model.getOwner( ) );

        setTitle( "" );
        setHeaderText( _model.getI18n( ).get( "update.available.title", version.getVersion( ) ) );
        setContentText( _model.getI18n( ).get( "update.available.message", version.getVersion( ) ) );

        getButtonTypes( ).clear( );

        final ButtonType openButton =
            new ButtonType( model.getI18n( ).get( "action.open" ).get( ).toUpperCase( ), ButtonData.YES );
        final ButtonType laterButton =
            new ButtonType( model.getI18n( ).get( "action.later" ).get( ).toUpperCase( ), ButtonData.CANCEL_CLOSE );
        getDialogPane( ).getButtonTypes( ).addAll( openButton, laterButton );

        final Button openBtn = (Button) getDialogPane( ).lookupButton( openButton );
        openBtn.setDefaultButton( true );
        openBtn.setOnAction( e -> _model.fireOpenUrl( version ) );
    }

    private final UpdateDialogModel _model;
}
