package org.neo4j.bolt.client.api.connector;

import java.util.Map;

/**
 * Subscriber
 */
public interface StreamHandler extends Handler
{
    void onAvailable( Stream stream );

    void onRecord( Object[] record );

    void onMore( Map<String, Object> metadata );

}
