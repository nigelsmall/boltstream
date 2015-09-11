package org.neo4j.bolt.client;

import java.util.Map;

import org.neo4j.bolt.client.api.connector.Stream;
import org.neo4j.bolt.client.api.connector.StreamHandler;

public abstract class BaseStreamHandler implements StreamHandler
{
    public void onAvailable( Stream stream ) {}

    public void onRecord( Object[] record ) {}

    public void onMore( Map<String, Object> metadata ) {}

    public void onSuccess( Map<String, Object> metadata ) {}

    public void onIgnored( Map<String, Object> metadata ) {}

    public void onFailure( Map<String, Object> metadata ) {}

}
