package org.neo4j.bolt.client.api.connector;

import java.util.Map;

/**
 * Subscriber for simple, non-stream responses
 */
public interface Handler
{
    void onSuccess( Map<String, Object> metadata );

    void onIgnored( Map<String, Object> metadata );

    void onFailure( Map<String, Object> metadata );

}
