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

package land.tower.core;

import static com.google.common.io.Resources.getResource;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import land.tower.data.Tournament;
import land.tower.data.adapter.ZonedDateTimeAdapter;

/**
 * Created on 11/02/2018
 * @author Cédric Longo
 */
public final class TestTournament {

    public static Tournament load( final String file ) {
        try {
            final String raw =
                Resources.toString(
                    getResource( file ),
                    StandardCharsets.UTF_8 );

            return new GsonBuilder( )
                       .registerTypeAdapter( ZonedDateTime.class, new ZonedDateTimeAdapter( ) )
                       .create( )
                       .fromJson( raw, Tournament.class );

        } catch ( IOException e ) {
            throw Throwables.propagate( e );
        }
    }

}
