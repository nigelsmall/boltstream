package org.neo4j.bolt.util;

import java.util.Map;

public class Message
{
    public static final Map<String, Object> EMPTY_MAP = StringObjectMap.from();

    public static final Message SUCCESS = new Message( MessageType.SUCCESS, EMPTY_MAP );
    public static final Message MORE = new Message( MessageType.MORE, EMPTY_MAP );
    public static final Message IGNORED = new Message( MessageType.IGNORED, EMPTY_MAP );
    public static final Message FAILURE = new Message( MessageType.FAILURE, EMPTY_MAP );

    private final MessageType type;
    private final Object[] arguments;

    public Message( MessageType type, Object argument1, Object argument2 )
    {
        this.type = type;
        this.arguments = new Object[]{argument1, argument2};
    }

    public Message( MessageType type, Object argument1 )
    {
        this.type = type;
        this.arguments = new Object[]{argument1};
    }

    public MessageType type()
    {
        return type;
    }

    public Object argument( int index )
    {
        return arguments[index];
    }

    @SuppressWarnings( "unchecked" )
    public Map<String, Object> mapArgument( int index )
    {
        return (Map<String, Object>) arguments[index];
    }

}
