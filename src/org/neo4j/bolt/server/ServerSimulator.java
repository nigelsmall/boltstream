package org.neo4j.bolt.server;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import org.neo4j.bolt.util.Message;
import org.neo4j.bolt.util.MessageType;
import org.neo4j.bolt.util.StringObjectMap;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class ServerSimulator implements ServerProtocol
{

    private final Deque<Message> result = new ArrayDeque<>();
    private final Deque<Message> stream = new ArrayDeque<>();

    private final Deque<Response> responses;

    public ServerSimulator( Response... responses )
    {
        this.responses = new ArrayDeque<>( asList( responses ) );
    }

    @Override
    public void sendRun( String statement, Map<String, Object> parameters )
    {
        System.err.println( "C: RUN \"" + statement + "\" " + parameters.toString() );
        assert result.isEmpty();
        Response response = responses.removeFirst();
        stream.add( new Message( MessageType.SUCCESS,
                StringObjectMap.from( "fields", response.fields() ) ) );
        for ( Object[] record : response.records() )
        {
            result.add( new Message( MessageType.RECORD, record ) );
        }
    }

    @Override
    public void sendDiscard( long n )
    {
        System.err.println( "C: DISCARD " + n );
        while ( !result.isEmpty() && n > 0 )
        {
            result.removeFirst();
            n -= 1;
        }
        if ( result.isEmpty() )
        {
            stream.add( Message.SUCCESS );
        }
        else
        {
            stream.add( Message.MORE );
        }
    }

    @Override
    public void sendDiscardAll()
    {
        System.err.println( "C: DISCARD_ALL" );
        result.clear();
        stream.add( Message.SUCCESS );
    }

    @Override
    public void sendPull( long n )
    {
        System.err.println( "C: PULL " + n );
        while ( !result.isEmpty() && n > 0 )
        {
            stream.add( result.removeFirst() );
            n -= 1;
        }
        if ( result.isEmpty() )
        {
            stream.add( Message.SUCCESS );
        }
        else
        {
            stream.add( Message.MORE );
        }
    }

    @Override
    public void sendPullAll()
    {
        System.err.println( "C: PULL_ALL" );
        while ( !result.isEmpty() )
        {
            stream.add( result.removeFirst() );
        }
        stream.add( Message.SUCCESS );
    }

    @Override
    public Message recv()
    {
        Message message = stream.removeFirst();
        System.err.println( format( "S: %s %s", message.type().name(), message.argument(0) ) );
        return message;
    }

}
