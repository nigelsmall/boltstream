package org.neo4j.bolt.client.api.connector;

/**
 * Subscription
 */
public interface Stream
{

    /**
     * Discard a fixed number of records
     *
     * @param n
     */
    void discard( long n );

    /**
     * Discard the remainder of the records
     */
    void discardAll();

    /**
     * Transfer a fixed number of records
     *
     * @param n
     */
    void pull( long n );

    /**
     * Transfer the remainder of the records
     */
    void pullAll();

}
