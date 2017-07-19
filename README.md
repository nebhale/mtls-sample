# mTLS Sample

This project demonstrates mTLS authentication and authorization when running on Cloud Foundry.  Because SSL is terminated at the router in Cloud Foundry, special arrangements must be made to propagate any client certificate to the running application instance for authorization and authentication.  This sample application echos back a message to the caller indicating that the mTLS certificate was properly propagated.

## Building
The project depends on Java 8.  To build from source, run the following:

```shell
$ ./mvnw clean package
```

The project contains a Cloud Foundry `manifest.yml`.  To push to Cloud Foundry, run the following:

```shell
$ cf push -n <UNIQUE-HOSTNAME>
```

## Testing
The application is configued such that the [`OU` value of the certificate will be extracted](https://github.com/nebhale/mtls-sample/blob/master/src/main/java/io/pivotal/mtlssample/MTLSSampleApplication.java#L58) as the authentication subject.  That subject is then [echoed back to user](https://github.com/nebhale/mtls-sample/blob/master/src/main/java/io/pivotal/mtlssample/MTLSSampleApplication.java#L42) for validation.

To verify that authentication and authorization are in fact working properly, start by `curl`ing the root endpoint with no credentials.  You'll receive a `403 Forbidden`:

```shell
$: curl https://<UNIQUE-HOSTNAME>.cfapps.io/
{"timestamp":1500504303171,"status":403,"error":"Forbidden","message":"Access Denied","path":"/"}
```

Then `curl` the same endpoint with your client certificate and private key.  You'll received a `200 OK` and the value encoded in the certificate's `OU` field.

```shell
$: curl –-cert cert.pem --key key.pem https://<UNIQUE-HOSTNAME>.cfapps.io/
Thanks for authenticating with X509, app:23282fd1-35b4-45dd-a602-8f76f4a60d11
```
