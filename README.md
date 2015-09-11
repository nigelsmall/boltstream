= Client API for Neo4j Bolt Protocol

The interfaces in this repository describe layers of API for Neo4j Bolt drivers.
At the lowest level is the protocol-idiomatic *Connector API* which is based on the reactive stream model.
Above that, language and framework idiomatic *Application API* are defined.


== Connector API

```java
public interface Connection
{
    void run( String statement, Map<String, Object> parameters, Handler handler );

    void discard( long n, Handler handler );

    void discardAll( Handler handler );

    void pull( long n, StreamHandler handler );

    void pullAll( StreamHandler handler );

    void go();

}
```

```java
public interface Handler
{
    void onSuccess( Map<String, Object> metadata );

    void onIgnored( Map<String, Object> metadata );

    void onFailure( Map<String, Object> metadata );

}

public interface StreamHandler extends Handler
{
    void onAvailable( Stream stream );

    void onRecord( Object[] record );

    void onMore( Map<String, Object> metadata );

}
```

```java
public interface Stream
{
    void discard( long n );

    void discardAll();

    void pull( long n );

    void pullAll();

}
```


== Application API

TODO
