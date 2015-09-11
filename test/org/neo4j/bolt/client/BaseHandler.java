package org.neo4j.bolt.client;

import java.util.Map;

import org.neo4j.bolt.client.api.connector.Handler;

public class BaseHandler implements Handler
{
    public static final BaseHandler DEFAULT = new BaseHandler();

    @Override
    public void onSuccess( Map<String, Object> metadata ) {}

    @Override
    public void onIgnored( Map<String, Object> metadata ) {}

    @Override
    public void onFailure( Map<String, Object> metadata ) {}

}
