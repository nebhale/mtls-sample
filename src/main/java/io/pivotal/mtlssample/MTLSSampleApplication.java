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

package io.pivotal.mtlssample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class MTLSSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(MTLSSampleApplication.class, args);
    }

    @GetMapping("/")
    String message() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return String.format("Thanks for authenticating with X509, %s", user.getUsername());
    }

    @EnableWebSecurity
    public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Bean
        public UserDetailsService userDetailsService() {
            return username -> User.withUsername(username).password("NOT-USED").roles("USER").build();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                .x509()
                    .subjectPrincipalRegex("OU=(.*?)(?:,|$)")
                    .and()
                .authorizeRequests()
                    .anyRequest().authenticated();
            // @formatter:on
        }

    }
}
