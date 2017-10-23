/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.mtlssample.client;

import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.security.auth.x500.X500Principal;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.security.cert.X509Certificate;

@Component
final class SerialNumberExtractor {

    private static final String[] ALGORITHMS = {"RSA", "EC"};

    private static final Principal[] PRINCIPALS = {new X500Principal("CN=Diego Instance Identity Root CA")};

    private final X509ExtendedKeyManager keyManager;

    SerialNumberExtractor() throws GeneralSecurityException {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(null, null);
        KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
        KeyManager keyManager = keyManagers[0];
        this.keyManager = (X509ExtendedKeyManager) keyManager;
    }

    String getSerialNumber() {
        String alias = this.keyManager.chooseClientAlias(ALGORITHMS, PRINCIPALS, null);
        X509Certificate certificate = this.keyManager.getCertificateChain(alias)[0];
        return certificate.getSerialNumber().toString();
    }

}
