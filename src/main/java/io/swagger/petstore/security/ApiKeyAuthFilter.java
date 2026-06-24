/**
 * Copyright 2018 SmartBear Software
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.swagger.petstore.security;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Servlet filter that validates API key authentication for protected endpoints.
 * The API key can be passed via the "api_key" header or "api_key" query parameter.
 *
 * Public endpoints (login, logout, openapi spec) are excluded from authentication.
 */
public class ApiKeyAuthFilter implements Filter {

    private static final String API_KEY_HEADER = "api_key";
    private static final String API_KEY_PARAM = "api_key";
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private static final Set<String> PUBLIC_PATHS = new HashSet<>(Arrays.asList(
            "/api/v3/openapi.json",
            "/api/v3/openapi.yaml",
            "/api/v3/user/login",
            "/api/v3/user/logout"
    ));

    private ApiKeyStore apiKeyStore;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.apiKeyStore = ApiKeyStore.getInstance();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        if (isPublicPath(path) || "OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String apiKey = extractApiKey(httpRequest);

        if (apiKey == null || apiKey.isEmpty()) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"code\":401,\"type\":\"error\",\"message\":\"Missing API key. Provide via 'api_key' header or 'Authorization: Bearer <key>'\"}");
            return;
        }

        if (!apiKeyStore.isValidKey(apiKey)) {
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"code\":403,\"type\":\"error\",\"message\":\"Invalid API key\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    private String extractApiKey(HttpServletRequest request) {
        String apiKey = request.getHeader(API_KEY_HEADER);
        if (apiKey != null && !apiKey.isEmpty()) {
            return apiKey;
        }

        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }

        return request.getParameter(API_KEY_PARAM);
    }

    private boolean isPublicPath(String path) {
        for (String publicPath : PUBLIC_PATHS) {
            if (path.endsWith(publicPath) || path.contains("/openapi")) {
                return true;
            }
        }
        return false;
    }
}
