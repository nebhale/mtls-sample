# mTLS Sample

This project demonstrates mTLS authentication and authorization when running on Cloud Foundry.  Because SSL is terminated at the router in Cloud Foundry, special arrangements must be made to propagate any client certificate to the running application instance for authorization and authentication.  This sample has both server and applications that demonstrate the configuration required to use mTLS as well as log output documenting the use of mTLS.

## Trusted Certificate, Authorized Application
This project consists of two applications, a `server` and a `client`.  To properly deploy and configure them, do the following

```shell
$ ./mvnw clean package

$ cd server
$ cf push mtls-sample-server --no-start

$ cd ../client
$ cf push mtls-sample-client --no-start
$ cf app mtls-sample-client --guid
```

The result of this final command is the client's application id.  This must be configured in the server as an "admin" client id allowing access to the `/admin` endpoint.

```shell
$ cf set-env mtls-sample-server MTLS_ADMIN_CLIENT_IDS <CLIENT_GUID>
$ cf start mtls-sample-server
```

At this point, the server has started and is configued to allow calls to `/admin` from the client application.  In the output of this final command is the server's host name (`urls`).  This must be configured in the client as the server route.

```shell
$ cf set-env mtls-sample-client MTLS_SERVER_ROUTE <SERVER_ROUTE>
$ cf start mtls-sample-client
```

At this point the client has started and will being calling the `/` and `/admin` endpoints every five minutes.  You'll see the following client and server output.

```plain
Requesting /admin with certificate SN 266754964882990302904004562024130247468
You authenticated using x509 certificate for app:a15d127a-621e-4b5f-beed-ce2dfa0763ea with SN 266754964882990302904004562024130247468
Requesting / with certificate SN 266754964882990302904004562024130247468
You authenticated using x509 certificate for app:a15d127a-621e-4b5f-beed-ce2dfa0763ea with SN 266754964882990302904004562024130247468
```

```plain
Received request for /admin with certificate for app:a15d127a-621e-4b5f-beed-ce2dfa0763ea with SN 266754964882990302904004562024130247468
Received request for / with certificate for app:a15d127a-621e-4b5f-beed-ce2dfa0763ea with SN 266754964882990302904004562024130247468
```

If you wait long enough that the container rotates its identity (certificate and private key), you'll expect to see the same application id used, but a different serial number on the certificate.

```plain
Requesting /admin with certificate SN 266754964882990302904004562024130247468
You authenticated using x509 certificate for app:a15d127a-621e-4b5f-beed-ce2dfa0763ea with SN 266754964882990302904004562024130247468
Requesting / with certificate SN 266754964882990302904004562024130247468
You authenticated using x509 certificate for app:a15d127a-621e-4b5f-beed-ce2dfa0763ea with SN 266754964882990302904004562024130247468
Updated KeyManager for /etc/cf-instance-credentials/instance.key and /etc/cf-instance-credentials/instance.crt
Updated KeyManager for /etc/cf-instance-credentials/instance.key and /etc/cf-instance-credentials/instance.crt
Requesting /admin with certificate SN 317113556697541063389275859994730153678
You authenticated using x509 certificate for app:a15d127a-621e-4b5f-beed-ce2dfa0763ea with SN 317113556697541063389275859994730153678
Requesting / with certificate SN 317113556697541063389275859994730153678
You authenticated using x509 certificate for app:a15d127a-621e-4b5f-beed-ce2dfa0763ea with SN 317113556697541063389275859994730153678
```

```plain
Received request for /admin with certificate for app:a15d127a-621e-4b5f-beed-ce2dfa0763ea with serial number 266754964882990302904004562024130247468
Received request for / with certificate for app:a15d127a-621e-4b5f-beed-ce2dfa0763ea with serial number 266754964882990302904004562024130247468
Received request for /admin with certificate for app:a15d127a-621e-4b5f-beed-ce2dfa0763ea with serial number 317113556697541063389275859994730153678
Received request for / with certificate for app:a15d127a-621e-4b5f-beed-ce2dfa0763ea with serial number 317113556697541063389275859994730153678
```

## Trusted Certificate, Unauthorized Application

To demonstrate the rejection of a trusted certificate, push the same client to a different application that isn't configured in the server.

```shell
$ cf push mtls-sample-client-2 --no-start
$ cf set-env mtls-sample-client-2 MTLS_SERVER_ROUTE <SERVER_ROUTE>
$ cf start mtls-sample-client-2
```

At this point the second client has started, and is receiving a rejection from the `/admin` endpoint for being unauthorized

```plain
Requesting /admin with certificate SN 239602280072486236703492394465041616501
Received response with status code 403
Requesting / with certificate SN 239602280072486236703492394465041616501
You authenticated using x509 certificate for app:bc01e56b-e798-44b5-b992-2f3e57272c46 with SN 239602280072486236703492394465041616501
```


## Cloud Foundry Configuration
This example requires two specific Cloud Foundry configurations in order to function.

First, the DNS entry for your application domain (typically `*.apps...`) must point to the **router's** IP address, not an intermediate device like a load balancer or HAProxy.

![Router IPs](https://user-images.githubusercontent.com/60754/31908222-05eb008c-b7eb-11e7-82e2-b4832b153889.png)

Second, the Router must be configured to [terminate TLS and propagate the client certificate in the `X-Forwarded-Client-Cert` header](https://docs.cloudfoundry.org/concepts/http-routing.html#forward-client-cert).

![Router Configuration](https://user-images.githubusercontent.com/60754/31908228-096d8ca2-b7eb-11e7-85fd-ef3fff28ba58.png)

## License
This project is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).
