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

package land.tower.core.view.option;

import javafx.scene.control.ChoiceDialog;
import javax.inject.Inject;
import land.tower.core.ext.i18n.Language;

/**
 * Created on 31/12/2017
 * @author Cédric Longo
 */
public final class LanguageDialog extends ChoiceDialog<Language> {

    @Inject
    public LanguageDialog( LanguageDialogModel model ) {
        getDialogPane( ).getStylesheets( ).add( model.getConfig( ).getApplicationStyle( ) );

        titleProperty( ).bind( model.getI18n( ).get( "option.language.title" ) );
        headerTextProperty( ).bind( model.getI18n( ).get( "option.language.message" ) );
        contentTextProperty( ).bind( model.getI18n( ).get( "language" ) );

        getItems( ).addAll( Language.values( ) );
    }

}
