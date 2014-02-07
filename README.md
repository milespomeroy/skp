# Analyze Hit Data for Search Keyword Performance

This implementation can only handle files whose unique hit data can fit in the memory allocated to the Java VM.

## Test and Build

Requires Maven and Java 7. Run from source root directory (contains `pom.xml`).

```bash
$ mvn package
```

## Run

Provide filename as parameter. Source includes sample `data.sql` hit data file.

```bash
$ java -jar target/skp-1.0-jar-with-dependencies.jar data.sql
```

Outputs results to file.
