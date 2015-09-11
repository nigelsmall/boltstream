package org.neo4j.bolt.client;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.neo4j.bolt.client.api.connector.Connection;
import org.neo4j.bolt.client.api.connector.Handler;
import org.neo4j.bolt.client.api.connector.Stream;
import org.neo4j.bolt.client.api.connector.StreamHandler;
import org.neo4j.bolt.server.ServerSimulator;
import org.neo4j.bolt.util.Message;

/**
 * Publisher
 */
public class StandardConnection implements Connection
{

    class Request
    {
        final Runnable runnable;
        final Handler handler;

        public Request( Runnable runnable, Handler handler )
        {
            this.runnable = runnable;
            this.handler = handler;
        }

    }

    private final ServerSimulator server;
    private final Deque<Request> requests;

    private Handler currentHandler;

    public StandardConnection( ServerSimulator server )
    {
        this.server = server;
        this.requests = new LinkedList<>();
        this.currentHandler = null;
    }

    @Override
    public void run( String statement, Map<String, Object> parameters, Handler handler )
    {
        if ( !requests.isEmpty() ) requests.add( null );
        requests.add( new Request( () -> server.sendRun( statement, parameters ), handler ) );
    }

    @Override
    public void discard( long n, Handler handler )
    {
        requests.add( new Request( () -> server.sendDiscard( n ), handler ) );
    }

    @Override
    public void discardAll( Handler handler )
    {
        requests.add( new Request( server::sendDiscardAll, handler ) );
    }

    @Override
    public void pull( long n, StreamHandler handler )
    {
        requests.add( new Request( () -> server.sendPull( n ), handler ) );
    }

    @Override
    public void pullAll( StreamHandler handler )
    {
        requests.add( new Request( server::sendPullAll, handler ) );
    }

    public void go()
    {
        if ( currentHandler != null ) return;
        Request request = requests.pollFirst();
        if ( request == null ) return;
        List<Handler> handlers = new LinkedList<>();
        while ( request != null )
        {
            request.runnable.run();
            handlers.add( request.handler );
            request = requests.pollFirst();
        }
        for ( Handler handler : handlers )
        {
            currentHandler = handler;
            if ( currentHandler instanceof StreamHandler )
            {
                handleResponse( (StreamHandler) currentHandler );
            }
            else
            {
                handleResponse( currentHandler );
            }
        }
    }

    private void handleResponse( Handler handler )
    {
        assert !(handler instanceof StreamHandler);
        Message message;
        do
        {
            message = server.recv();
            switch ( message.type() )
            {
                case SUCCESS:
                    handler.onSuccess( message.mapArgument( 0 ) );
                    break;
                case IGNORED:
                    handler.onIgnored( message.mapArgument( 0 ) );
                    break;
                case FAILURE:
                    handler.onFailure( message.mapArgument( 0 ) );
                    break;
                default:
                    throw new RuntimeException();
            }
        }
        while ( !message.type().marksEndOfResponse() );
    }

    private void handleResponse( StreamHandler handler )
    {
        handler.onAvailable( new Stream()
                             {

                                 @Override
                                 public void discard( long n )
                                 {
                                     server.sendDiscard( n );
                                     handleResponse( handler );
                                 }

                                 @Override
                                 public void discardAll()
                                 {
                                     server.sendDiscardAll();
                                     handleResponse( handler );
                                 }

                                 @Override
                                 public void pull( long n )
                                 {
                                     server.sendPull( n );
                                     handleResponse( handler );
                                 }

                                 @Override
                                 public void pullAll()
                                 {
                                     server.sendPullAll();
                                     handleResponse( handler );
                                 }

                             }
        );
        Message message;
        do
        {
            message = server.recv();
            Object payload = message.argument( 0 );
            switch ( message.type() )
            {
                case SUCCESS:
                    handler.onSuccess( message.mapArgument( 0 ) );
                    break;
                case RECORD:
                    handler.onRecord( (Object[]) payload );
                    break;
                case MORE:
                    handler.onMore( message.mapArgument( 0 ) );
                    break;
                case IGNORED:
                    handler.onIgnored( message.mapArgument( 0 ) );
                    break;
                case FAILURE:
                    handler.onFailure( message.mapArgument( 0 ) );
                    break;
                default:
                    throw new RuntimeException();
            }
        }
        while ( !message.type().marksEndOfResponse() );
        if ( message.type().marksEndOfStream() )
        {
            currentHandler = null;
            go();
        }
    }

}
