package edu.colorado.phet.website.test;

//import org.postgresql.jdbc2.optional.PoolingDataSource;

public class InitializeDatasource {
    /*
    public void init() {
        PoolingDataSource source = new PoolingDataSource();
        Properties props = new Properties();
        try {
            props.load( getClass().getResourceAsStream( "/datasource.properties" ) );
            source.setServerName( props.getProperty( "server" ) );
            source.setDatabaseName( props.getProperty( "database" ) );
            source.setUser( props.getProperty( "username" ) );
            source.setPassword( props.getProperty( "password" ) );
            source.setDataSourceName( "jdbc/phet" );
            source.setPortNumber( 0 );
            source.setInitialConnections( 1 );
            source.setMaxConnections( 20 );
            Context context = new InitialContext();
            //context.bind( "jdbc/phet", source );
            //context.rebind( "java:comp/env/jdbc/phet", source );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        catch( NamingException e ) {
            e.printStackTrace();
        }
    }
    */
}