package org.neo4j.bolt.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import org.neo4j.bolt.server.Response;
import org.neo4j.bolt.server.ServerSimulator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class StandardConnectionTest
{
    public static final Response RESPONSE_1 = new Response( "name", "age" );
    public static final Response RESPONSE_2 = new Response( "name", "friend" );
    public static final Response RESPONSE_3 = new Response( "name", "friend" );
    public static final Response RESPONSE_4 = new Response( "name", "friend" );
    public static final Response RESPONSE_5 = new Response( "name", "friend" );

    static
    {
        RESPONSE_1.add( "Alice", 33 );
        RESPONSE_1.add( "Bob", 44 );
        RESPONSE_1.add( "Carol", 55 );
        RESPONSE_1.add( "Dave", 66 );

        RESPONSE_2.add( "Alice", "Bob" );
        RESPONSE_2.add( "Alice", "Carol" );

        RESPONSE_4.add( "Carol", "Alice" );
        RESPONSE_4.add( "Carol", "Dave" );

        RESPONSE_5.add( "Dave", "Carol" );
    }

    static class StreamCollector extends BaseStreamHandler
    {
        private final List<Object[]> collection;

        public StreamCollector( List<Object[]> collection )
        {
            this.collection = collection;
        }

        @Override
        public void onRecord( Object[] data )
        {
            collection.add( data );
        }

    }

    @Test
    public void testCanRunSimpleRequest()
    {
        // given
        StandardConnection session = new StandardConnection( new ServerSimulator( RESPONSE_1 ) );
        List<Object[]> records = new ArrayList<>();
        StreamCollector collector = new StreamCollector( records );

        // when
        session.run( "", Collections.emptyMap(), BaseHandler.DEFAULT );
        session.pullAll( collector );
        session.go();

        // then
        assertThat( records.size(), equalTo( 4 ) );
        assertThat( records.get( 0 ), equalTo( new Object[]{"Alice", 33} ) );
        assertThat( records.get( 1 ), equalTo( new Object[]{"Bob", 44} ) );
        assertThat( records.get( 2 ), equalTo( new Object[]{"Carol", 55} ) );
        assertThat( records.get( 3 ), equalTo( new Object[]{"Dave", 66} ) );

    }

    @Test
    public void testPullAndDiscardAndPullAll()
    {
        // given
        StandardConnection session = new StandardConnection( new ServerSimulator( RESPONSE_1 ) );
        List<Object[]> records = new ArrayList<>();
        StreamCollector collector = new StreamCollector( records );

        // when
        session.run( "", Collections.emptyMap(), BaseHandler.DEFAULT );
        session.pull( 1, collector );
        session.discard( 2, collector );
        session.pullAll( collector );
        session.go();

        // then
        assertThat( records.size(), equalTo( 2 ) );
        assertThat( records.get( 0 ), equalTo( new Object[]{"Alice", 33} ) );
        assertThat( records.get( 1 ), equalTo( new Object[]{"Dave", 66} ) );

    }

    @Test
    public void testPullAndDiscardAll()
    {
        // given
        StandardConnection session = new StandardConnection( new ServerSimulator( RESPONSE_1 ) );
        List<Object[]> records = new ArrayList<>();
        StreamCollector collector = new StreamCollector( records );

        // when
        session.run( "", Collections.emptyMap(), BaseHandler.DEFAULT );
        session.pull( 2, collector );
        session.discardAll( collector );
        session.go();

        // then
        assertThat( records.size(), equalTo( 2 ) );
        assertThat( records.get( 0 ), equalTo( new Object[]{"Alice", 33} ) );
        assertThat( records.get( 1 ), equalTo( new Object[]{"Bob", 44} ) );

    }

    @Test
    public void testCanRunNestedRequestsWithSingleCallToGo()
    {
        // given
        StandardConnection session = new StandardConnection( new ServerSimulator(
                RESPONSE_1, RESPONSE_2, RESPONSE_3, RESPONSE_4, RESPONSE_5 ) );
        final List<Object[]> records = new ArrayList<>();

        // when
        session.run( "", Collections.emptyMap(), BaseHandler.DEFAULT );
        session.pullAll( new StreamCollector( records )
        {
            @Override
            public void onRecord( Object[] data )
            {
                super.onRecord( data );
                session.run( "", Collections.emptyMap(), BaseHandler.DEFAULT );
                session.pullAll( new StreamCollector( records ) );
            }

        } );
        session.go();

        // then
        assertThat( records.size(), equalTo( 9 ) );
        assertThat( records.get( 0 ), equalTo( new Object[]{"Alice", 33} ) );
        assertThat( records.get( 1 ), equalTo( new Object[]{"Bob", 44} ) );
        assertThat( records.get( 2 ), equalTo( new Object[]{"Carol", 55} ) );
        assertThat( records.get( 3 ), equalTo( new Object[]{"Dave", 66} ) );
        assertThat( records.get( 4 ), equalTo( new Object[]{"Alice", "Bob"} ) );
        assertThat( records.get( 5 ), equalTo( new Object[]{"Alice", "Carol"} ) );
        assertThat( records.get( 6 ), equalTo( new Object[]{"Carol", "Alice"} ) );
        assertThat( records.get( 7 ), equalTo( new Object[]{"Carol", "Dave"} ) );
        assertThat( records.get( 8 ), equalTo( new Object[]{"Dave", "Carol"} ) );

    }

    @Test
    public void testCanRunNestedRequestsWithAdditionalCallsToGo()
    {
        // given
        StandardConnection session = new StandardConnection( new ServerSimulator(
                RESPONSE_1, RESPONSE_2, RESPONSE_3, RESPONSE_4, RESPONSE_5 ) );
        final List<Object[]> records = new ArrayList<>();

        // when
        session.run( "", Collections.emptyMap(), BaseHandler.DEFAULT );
        session.pullAll( new StreamCollector( records )
        {
            @Override
            public void onRecord( Object[] data )
            {
                super.onRecord( data );
                session.run( "", Collections.emptyMap(), BaseHandler.DEFAULT );
                session.pullAll( new StreamCollector( records ) );
                session.go();
            }

        } );
        session.go();

        // then
        assertThat( records.size(), equalTo( 9 ) );
        assertThat( records.get( 0 ), equalTo( new Object[]{"Alice", 33} ) );
        assertThat( records.get( 1 ), equalTo( new Object[]{"Bob", 44} ) );
        assertThat( records.get( 2 ), equalTo( new Object[]{"Carol", 55} ) );
        assertThat( records.get( 3 ), equalTo( new Object[]{"Dave", 66} ) );
        assertThat( records.get( 4 ), equalTo( new Object[]{"Alice", "Bob"} ) );
        assertThat( records.get( 5 ), equalTo( new Object[]{"Alice", "Carol"} ) );
        assertThat( records.get( 6 ), equalTo( new Object[]{"Carol", "Alice"} ) );
        assertThat( records.get( 7 ), equalTo( new Object[]{"Carol", "Dave"} ) );
        assertThat( records.get( 8 ), equalTo( new Object[]{"Dave", "Carol"} ) );

    }

}