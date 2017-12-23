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

package land.tower.core.ext.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import javax.inject.Inject;
import javax.inject.Provider;
import land.tower.core.ext.logger.Loggers;
import land.tower.core.ext.service.IService;

/**
 * Created on 06/12/2017
 * @author Cédric Longo
 */
public final class I18nService implements IService, Provider<I18nTranslator> {

    @Inject
    public I18nService( ) {
    }

    @Override
    public void start( ) {
        final Locale defaultLocale = Locale.getDefault( );
        _logger.info( "Default Locale is: {}", defaultLocale );

        loadBundle( "i18n", defaultLocale );
        loadBundle( "nationalities", defaultLocale );
    }

    private void loadBundle( final String bundle, final Locale defaultLocale ) {
        load( "i18n/" + bundle + "_en.properties" )
            .ifPresent( _i18nTranslator::registerEntries );

        load( "i18n/" + bundle + "_" + defaultLocale.getLanguage( ) + ".properties" )
            .ifPresent( _i18nTranslator::registerEntries );

        load( "i18n/" + bundle + "_" + defaultLocale.getLanguage( )
              + "_" + defaultLocale.getCountry( ) + ".properties" )
            .ifPresent( _i18nTranslator::registerEntries );
    }

    @Override
    public void stop( ) {

    }

    @Override
    public I18nTranslator get( ) {
        return _i18nTranslator;
    }

    private Optional<Properties> load( final String name ) {
        try ( final InputStream in = getClass( ).getClassLoader( ).getResourceAsStream( name ) ) {
            if ( in == null ) {
                return Optional.empty( );
            }

            LoggerFactory.getLogger( Loggers.MAIN ).info( "Loading text entries from: {}", name );
            final Properties properties = new Properties( );
            properties.load( in );
            return Optional.of( properties );

        } catch ( final IOException e ) {
            return Optional.empty( );
        }
    }

    private final I18nTranslator _i18nTranslator = new I18nTranslator( );

    private final Logger _logger = LoggerFactory.getLogger( Loggers.MAIN );
}
