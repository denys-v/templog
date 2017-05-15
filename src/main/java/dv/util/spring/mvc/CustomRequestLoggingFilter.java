package dv.util.spring.mvc;

import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Filter for logging request details (for debugging purposes).
 */
public class CustomRequestLoggingFilter extends AbstractRequestLoggingFilter {

    private ArrayList<String> includedHeaders = new ArrayList<>();

    private final ThreadLocal<HttpServletResponse> localResponse = new ThreadLocal<>();

    public void includeHeaders(String... headers) {
        Arrays.stream(headers).forEach(includedHeaders::add);
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        this.logger.debug(message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        this.logger.debug(message);
    }

    @Override
    protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
        StringBuilder msg = new StringBuilder();
        msg.append(prefix);
        msg.append(request.getMethod()).append(' ').append(request.getRequestURI());

        if (isIncludeQueryString()) {
            String queryString = request.getQueryString();
            if (queryString != null) {
                msg.append('?').append(queryString);
            }
        }

        if (request.getParameterMap().size() > 0) {
            msg.append("\nParameters:");
            request.getParameterMap().forEach((s, strings) -> {
                msg.append("\n").append("- ").append(s).append(": ");
                msg.append(Arrays.stream(strings)
                        .collect(Collectors.joining("\", \"", "[\"", "\"]")));
            });
        }

        if (isIncludeClientInfo()) {
            StringBuilder b = new StringBuilder();

            String client = request.getRemoteAddr();
            if (StringUtils.hasLength(client)) {
                b.append("\n- client=").append(client);
            }
            HttpSession session = request.getSession(false);
            if (session != null) {
                b.append("\n- session=").append(session.getId());
            }
            String user = request.getRemoteUser();
            if (user != null) {
                b.append("\n- user=").append(user);
            }

            if (b.length() > 0) {
                msg.append("\nClient info:").append(b);
            }
        }

        if (isIncludeHeaders()) {
            msg.append("\nHeaders:");

            new ServletServerHttpRequest(request).getHeaders().forEach((s, strings) -> {
                if (this.includedHeaders.size() > 0) {
                    if (this.includedHeaders.contains(s)) {
                        msg.append('\n').append("- ").append(s).append(": ");
                        msg.append(strings
                                .stream()
                                .collect(Collectors.joining("\", \"", "[\"", "\"]")));
                    }
                } else {
                    msg.append('\n').append("- ").append(s).append(": ");
                    msg.append(strings
                            .stream()
                            .collect(Collectors.joining("\", \"", "[\"", "\"]")));
                }
            });
        }

        if (isIncludePayload()) {
            ContentCachingRequestWrapper wrapper =
                    WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
            if (wrapper != null) {
                byte[] buf = wrapper.getContentAsByteArray();
                if (buf.length > 0) {
                    int length = Math.min(buf.length, getMaxPayloadLength());
                    String payload;
                    try {
                        payload = new String(buf, 0, length, wrapper.getCharacterEncoding());
                    }
                    catch (UnsupportedEncodingException ex) {
                        payload = "[unknown]";
                    }
                    msg.append("\nPayload:\n").append(payload);
                }
            }
        }

        HttpServletResponse response = this.localResponse.get();
        if (response != null) {
            StringBuilder b = new StringBuilder();
            if (response.getHeaderNames().size() > 0) {
                b.append("\nRESPONSE\n--------\nStatus: ").append(response.getStatus());
                b.append("\nHeaders:");
                response.getHeaderNames().stream().distinct().forEach(s -> {
                    b.append("\n- ").append(s).append(": ");
                    b.append(response.getHeaders(s)
                            .stream()
                            .collect(Collectors.joining("\", \"", "[\"", "\"]")));
                });
            }
            if (b.length() > 0) {
                msg.append(b.toString());
            }
        }

        msg.append(suffix);
        return msg.toString();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        this.localResponse.set(response);

        try {
            super.doFilterInternal(request, response, filterChain);
        } finally {
            this.localResponse.remove();
        }
    }
}
