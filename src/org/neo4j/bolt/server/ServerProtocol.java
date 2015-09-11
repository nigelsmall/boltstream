package org.neo4j.bolt.server;

import java.util.Map;

import org.neo4j.bolt.util.Message;

public interface ServerProtocol
{

    void sendRun( String statement, Map<String, Object> parameters );

    void sendDiscard( long n );

    void sendDiscardAll();

    void sendPull( long n );

    void sendPullAll();

    Message recv();

}
