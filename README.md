# mTLS Sample

This

## Development
The project depends on Java 8.  To build from source, run the following:

```shell
$ ./mvnw clean package
```

The project contains a Cloud Foundry `manifest.yml`.  To push to Cloud Foundry, run the following:

```shell
$ cf push -n <UNIQUE-HOSTNAME>
```
