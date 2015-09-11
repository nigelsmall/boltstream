package org.neo4j.bolt.util;

public enum MessageType
{
    RUN( (byte) 0x10, false, false ),
    DISCARD( (byte) 0x20, false, false ),
    DISCARD_ALL( (byte) 0x2F, false, false ),
    PULL( (byte) 0x30, false, false ),
    PULL_ALL( (byte) 0x3F, false, false ),

    SUCCESS( (byte) 0x70, true, true ),
    RECORD( (byte) 0x71, false, false ),
    MORE( (byte) 0x72, true, false ),
    IGNORED( (byte) 0x7E, true, true ),
    FAILURE( (byte) 0x7F, true, true );

    private final byte signature;
    private final boolean marksEndOfResponse;
    private final boolean marksEndOfStream;

    MessageType( byte signature, boolean marksEndOfResponse, boolean marksEndOfStream )
    {
        this.signature = signature;
        this.marksEndOfResponse = marksEndOfResponse;
        this.marksEndOfStream = marksEndOfStream;
    }

    public byte signature()
    {
        return signature;
    }

    public boolean marksEndOfResponse()
    {
        return marksEndOfResponse;
    }

    public boolean marksEndOfStream()
    {
        return marksEndOfStream;
    }

}
