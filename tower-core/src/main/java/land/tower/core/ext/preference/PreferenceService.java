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

package land.tower.core.ext.preference;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import land.tower.core.ext.logger.Loggers;
import land.tower.core.ext.service.IService;
import land.tower.core.ext.service.ServicePriority;

/**
 * Created on 04/01/2018
 * @author Cédric Longo
 */
final class PreferenceService implements IService {

    @Override
    public void start( ) {
        final Path path = Paths.get( "data", "preferences.json" );
        try ( FileReader in = new FileReader( path.toFile( ) ) ) {
            final Map<String, String> map = new Gson( )
                                                .fromJson( in, new TypeToken<Map<String, String>>( ) {
                                                }.getType( ) );
            if ( map != null ) {
                map.forEach( _preferences::save );
            }

            _logger.info( "Preferences loaded from: {}", path.toAbsolutePath( ).toString( ) );
        } catch ( final FileNotFoundException ignored ) {
        } catch ( final IOException e ) {
            _logger.error( "Exception thrown when loading preferences", e );
        }
    }

    @Override
    public void stop( ) {
        final Path path = Paths.get( "data", "preferences.json._COPYING_" );
        final Path finalPath = Paths.get( "data", "preferences.json" );
        try ( final FileWriter out = new FileWriter( path.toFile( ) ) ) {
            new Gson( ).toJson( _preferences.getItems( ), out );
            out.close( );

            Files.move( path, finalPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE );
            _logger.info( "Preferences saved into: {}", finalPath.toAbsolutePath( ).toString( ) );
        } catch ( final IOException e ) {
            _logger.error( "Exception thrown when saving preferences", e );
        }
    }

    @Override
    public ServicePriority getPriority( ) {
        return ServicePriority.HIGH;
    }

    public Preferences getPreferences( ) {
        return _preferences;
    }

    private final Preferences _preferences = new Preferences( );

    private Logger _logger = LoggerFactory.getLogger( Loggers.MAIN );
}
