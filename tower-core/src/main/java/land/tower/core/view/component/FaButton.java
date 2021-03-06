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

package land.tower.core.view.component;

import javafx.scene.control.Button;
import javafx.scene.text.Text;
import land.tower.core.ext.font.FontAwesome;

/**
 * Created on 23/12/2017
 * @author Cédric Longo
 */
public final class FaButton extends Button {

    public FaButton( final String icon, final String color ) {
        final Text faIcon = new Text( icon + " " );
        faIcon.setStyle( "-fx-fill: " + color ); // Should be inherited from CSS
        faIcon.getStyleClass( ).add( FontAwesome.FA_STYLE_NAME );
        setGraphic( faIcon );
    }

}
