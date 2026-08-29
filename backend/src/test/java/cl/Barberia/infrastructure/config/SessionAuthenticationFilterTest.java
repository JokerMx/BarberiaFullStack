package cl.Barberia.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SessionAuthenticationFilterTest {

    private final SessionAuthenticationFilter filter = new SessionAuthenticationFilter();

    @AfterEach
    void clearSecurityContext() { SecurityContextHolder.clearContext(); }

    @Test
    void authenticatesUserFromSessionAndContinuesChain() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("AUTH_USERNAME")).thenReturn("cliente");
        when(session.getAttribute("AUTH_ROLE")).thenReturn("CLIENTE");

        filter.doFilterInternal(request, response, chain);

        assertEquals("cliente", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertEquals("ROLE_CLIENTE", SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority());
        verify(chain).doFilter(request, response);
    }

    @Test
    void leavesContextEmptyWhenSessionIsMissing() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        when(request.getSession(false)).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    void preservesExistingAuthentication() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("existing", null));
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("AUTH_USERNAME")).thenReturn("cliente");
        when(session.getAttribute("AUTH_ROLE")).thenReturn("CLIENTE");

        filter.doFilterInternal(request, mock(HttpServletResponse.class), mock(FilterChain.class));

        assertEquals("existing", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}