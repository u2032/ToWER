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

package land.tower.core.ext.report;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import javax.inject.Inject;
import land.tower.core.ext.config.Configuration;
import land.tower.core.ext.logger.Loggers;
import land.tower.core.ext.thread.ApplicationThread;

/**
 * Created on 28/01/2018
 * @author Cédric Longo
 */
public final class ReportEngine {

    @Inject
    public ReportEngine( final Configuration configuration,
                         @ApplicationThread final ExecutorService executorService ) {
        _executorService = executorService;
        _logoPath = configuration.dataDirectory( ).resolve( "report-logo.png" );
        _logoPathTmp = configuration.dataDirectory( ).resolve( "report-logo.png._COPYING_" );
    }

    public void generate( final Report report ) {
        _executorService.submit( ( ) -> {
            try {
                run( report );
            } catch ( final Exception e ) {
                _logger.error( "Error during report generation", e );
            }
        } );
    }

    private void run( final Report report ) {
        final JasperReport jreport =
            _reports.computeIfAbsent( report.getTemplate( ), key -> {
                try ( final InputStream source = getClass( ).getClassLoader( )
                                                            .getResourceAsStream( report.getTemplate( ) ) ) {
                    return JasperCompileManager.compileReport( source );
                } catch ( JRException | IOException e ) {
                    _logger.error( "Unable to compile jasper report", e );
                    return null;
                }
            } );

        try {
            final Map<String, Object> parameters = report.getParameters( );
            parameters.put( "logo.path", getLogoPath( ).toAbsolutePath( ).toString( ) );
            final JasperPrint jrPrint = JasperFillManager.fillReport( jreport,
                                                                      parameters,
                                                                      report.getJRDataSource( ) );
            final JasperViewer jasperViewer = new JasperViewer( jrPrint, false );
            jasperViewer.setTitle( report.getTitle( ) );
            jasperViewer.setVisible( true );

        } catch ( JRException e ) {
            _logger.error( "Unable to generate jasper report", e );
        }
    }

    private Path getLogoPath( ) {
        if ( Files.exists( _logoPath ) ) {
            return _logoPath;
        }

        try ( final OutputStream out = Files.newOutputStream( _logoPathTmp ) ) {
            Resources.copy( Resources.getResource( "img/icons/icon_128x128.png" ), out );
        } catch ( IOException e ) {
            throw Throwables.propagate( e );
        }

        try {
            Files.move( _logoPathTmp, _logoPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE );
        } catch ( IOException e ) {
            throw Throwables.propagate( e );
        }
        return _logoPath;
    }

    private final Path _logoPath;
    private final Path _logoPathTmp;
    private final ExecutorService _executorService;
    private final Map<String, JasperReport> _reports = new ConcurrentHashMap<>( );
    private Logger _logger = LoggerFactory.getLogger( Loggers.MAIN );
}
