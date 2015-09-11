package org.neo4j.bolt.client.api.connector;

import java.util.Map;

public interface Connection
{

    /**
     * Run a parameterised Cypher statement and handle the response stream with
     * the supplied handler.
     *
     * @param statement
     * @param parameters
     * @param handler
     */
    void run( String statement, Map<String, Object> parameters, Handler handler );

    void discard( long n, Handler handler );

    void discardAll( Handler handler );

    void pull( long n, StreamHandler handler );

    void pullAll( StreamHandler handler );

    void go();

}
