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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@EnableScheduling
@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Bean
    RestOperations restOperations() {
        return new RestTemplate();
    }

    @Component
    static final class Client {

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        private final String adminUri;

        private final RestOperations restOperations;

        private final SerialNumberExtractor serialNumberExtractor;

        private final String userUri;

        Client(RestOperations restOperations, SerialNumberExtractor serialNumberExtractor, @Value("${mtls_server_route}") String serverRoute) {
            this.restOperations = restOperations;
            this.serialNumberExtractor = serialNumberExtractor;
            this.adminUri = String.format("https://%s/admin", serverRoute);
            this.userUri = String.format("https://%s", serverRoute);
        }

        @Scheduled(fixedRate = 5 * 60 * 1_000)
        void test() {
            String certificateSerialNumber = this.serialNumberExtractor.getSerialNumber();

            try {
                this.logger.info("Requesting /admin with certificate SN {}", certificateSerialNumber);
                this.logger.info(this.restOperations.getForObject(this.adminUri, String.class));
            } catch (HttpClientErrorException e) {
                this.logger.error("Received response with status code {}", e.getStatusCode());
            }

            try {
                this.logger.info("Requesting / with certificate SN {}", certificateSerialNumber);
                this.logger.info(this.restOperations.getForObject(this.userUri, String.class));
            } catch (HttpClientErrorException e) {
                this.logger.error("Received response with status code {}", e.getStatusCode());
            }
        }

    }

}
