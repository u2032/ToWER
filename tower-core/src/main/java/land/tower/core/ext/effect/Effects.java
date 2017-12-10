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

package land.tower.core.ext.effect;

import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.paint.Color;

/**
 * Created on 10/12/2017
 * @author Cédric Longo
 */
public final class Effects {

    public static GaussianBlur blur( ) {
        return new GaussianBlur( );
    }

    public static DropShadow dropShadow( ) {
        final DropShadow ds = new DropShadow( );
        ds.setOffsetY( 3.0f );
        ds.setColor( Color.color( 0.4f, 0.4f, 0.4f ) );
        return ds;
    }

    public static InnerShadow innerShadow( ) {
        InnerShadow is = new InnerShadow( );
        is.setOffsetX( 4.0f );
        is.setOffsetY( 4.0f );
        return is;
    }

    public static Reflection reflection( ) {
        final Reflection r = new Reflection( );
        r.setFraction( 0.7f );
        return r;
    }
}
