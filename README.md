# Analyze Hit Data for Search Keyword Performance

## Test and Build

Requires Maven and Java 7. Run from source root directory (contains `pom.xml`).

```bash
$ mvn package
```

## Run

Provide filename as parameter. Source includes sample `data.sql` hit data file.

```bash
$ java -jar target/skp-1.5-jar-with-dependencies.jar data.sql
```

Outputs results to file.

**Note**: If running outside of the `skp` directory, `hit.xml`, `unique_hit.xml`, and `search_keyword_performance.xml` sort config files must be in the working directory.

If running on very large files where batched files still are too large to run in the memory allocated to the Java VM. You can add a flag to increase that allocation:

```bash
$ java -Xms2G -jar target/skp-1.5-jar-with-dependencies.jar data.sql
```

## Branches

`master` - version 1.5, refactored quick and dirty to use external sorting and streaming to create a scalable version.
`unscalable` - version 1.0, better tested and organized version but not scalable to larger files.
