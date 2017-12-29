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

package land.tower.core.ext.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javafx.scene.image.Image;

/**
 * Created on 29/12/2017
 * @author Cédric Longo
 */
public final class Resources {

    public static Optional<Image> loadImage( final String path, final int width, final int height ) {
        final String key = String.format( "%s_%s_%s", path, width, height );

        final Optional<Image> inCache = Optional.ofNullable( _imageCache.getIfPresent( key ) );
        if ( inCache.isPresent( ) ) {
            return inCache;
        }

        try ( final InputStream imStream = Resources.class.getClassLoader( ).getResourceAsStream( path ) ) {
            if ( imStream != null ) {
                final Image icon = new Image( imStream, width, height, true, true );
                _imageCache.put( key, icon );
                return Optional.of( icon );
            }
        } catch ( IOException ignored ) {
        }
        return Optional.empty( );
    }

    private static final Cache<String, Image> _imageCache = CacheBuilder.newBuilder( )
                                                                        .expireAfterAccess( 5, TimeUnit.MINUTES )
                                                                        .build( );
}