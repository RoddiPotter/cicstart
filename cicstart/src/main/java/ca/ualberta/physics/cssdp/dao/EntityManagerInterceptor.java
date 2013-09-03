package ca.ualberta.physics.cssdp.dao;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.configuration.InjectorHolder;
import ca.ualberta.physics.cssdp.domain.ServiceStats.ServiceName;
import ca.ualberta.physics.cssdp.service.StatsService;

import com.google.inject.Inject;

public class EntityManagerInterceptor implements Filter {

	private static final Logger logger = LoggerFactory
			.getLogger(EntityManagerInterceptor.class);

	@Inject
	private EntityManagerProvider emProvider;

	@Inject
	private StatsService statsService;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		InjectorHolder.inject(this);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		try {

			logger.debug("Creating Entity Manager");
			emProvider.get();

			if (request instanceof HttpServletRequest) {
				String url = ((HttpServletRequest) request).getRequestURL()
						.toString();

				if (url.contains("api-docs.json")) {
					// don't count stats on these.
				} else {

					if (url.contains("auth")) {
						statsService.incrementInvocationCount(ServiceName.AUTH);
					}
					if (url.contains("catalogue")) {
						statsService
								.incrementInvocationCount(ServiceName.CATALOGUE);
					}
					if (url.contains("file")) {
						statsService.incrementInvocationCount(ServiceName.FILE);
					}
					if (url.contains("macro")) {
						statsService
								.incrementInvocationCount(ServiceName.MACRO);
					}
					if (url.contains("vfs")) {
						statsService.incrementInvocationCount(ServiceName.VFS);
					}
					
				}
			}

			chain.doFilter(request, response);
		} catch (RuntimeException e) {
			logger.error("uh oh", e.getMessage(), e);
		} finally {
			emProvider.remove();
			logger.debug("Entity Manager destroyed");
		}

	}

	@Override
	public void destroy() {

	}

}
