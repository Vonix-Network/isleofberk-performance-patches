# Local compile-only target jars

The files in this directory are developer-supplied references and are ignored by Git.
They must never be committed or embedded in the public companion artifact.

Required local files:

- `isleofberk-1.2.0.jar`
- `geckolib-forge-1.18-3.0.57.jar`
- `iobvariantloader-2.6.4-all.jar`

The Gradle project uses these for compilation and local userdev runtime testing.
The published JAR contains only Vonix source/classes and metadata.
