package org.neo4j.bolt.server;

import java.util.ArrayList;
import java.util.List;

public class Response
{
    private final String[] fields;
    private final List<Object[]> records;

    public Response( String... fields )
    {
        this.fields = fields;
        this.records = new ArrayList<>();
    }

    public String[] fields()
    {
        return fields;
    }

    public Iterable<Object[]> records()
    {
        return records;
    }

    public void add( Object... record )
    {
        records.add( record );
    }

}
