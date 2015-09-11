package org.neo4j.bolt.util;

import java.util.HashMap;
import java.util.Map;

public class StringObjectMap
{
    public static Map<String, Object> from( Object... keysAndValues )
    {
        Map<String, Object> map = new HashMap<>( keysAndValues.length / 2 );
        String key = null;
        for ( Object keyOrValue : keysAndValues )
        {
            if ( key == null )
            {
                key = keyOrValue.toString();
            }
            else
            {
                map.put( key, keyOrValue );
                key = null;
            }
        }
        return map;
    }

}
