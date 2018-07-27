# Apache OpenWhisk Java Client

Java client library for the Apache OpenWhisk platform. Provides a wrapper around the OpenWhisk APIs (Swagger JSON).

## Installation

For maven users, use the follow dependency:

```xml
    <parent>
        <groupId>org.projectodd.openwhisk</groupId>
        <artifactId>java-client</artifactId>
        <version>0.1-SNAPSHOT</version>
    </parent>
```

## Building

The tests in `client` depend on jars being properly built in the `functions` module.  Running `mvn install` should
be sufficient for building from the console.  For development inside an IDE, you'll need to run `mvn package` inside
the `functions` module first.

## Usage

_TBD_