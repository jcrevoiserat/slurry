package org.slurry.cache4guice.module;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.ehcache.CacheManager;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slurry.cache4guice.annotation.Cached;
import org.slurry.cache4guice.aop.CacheInterceptor;
import org.slurry.cache4guice.aop.CacheKeyGenerator;
import org.slurry.cache4guice.cache.StringBasedKeyGenerator;
import org.slurry.cache4guice.cache.util.Cache4GuiceHelper;
import org.slurry.cache4guice.cache.util.Cache4GuiceHelperImpl;
import org.slurry.cache4guice.cache.util.CacheEntryTimedFactory;
import org.slurry.cache4guice.cache.util.MethodInvocationHolder;
import org.slurry.cache4guice.quartz.CacheUpdatingJob;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

public class CacheModule extends AbstractModule {

	public final static String INVOCATION_MAP_NAME = "invocationmap";

	private static Logger logger = Logger
			.getLogger(CacheModule.class);

	
	protected void configure() {
		CacheManager cacheManager = CacheManager.create();
		bind(CacheManager.class).toInstance(cacheManager);
		bind(CacheKeyGenerator.class).to(getCacheKeyGeneratorClass());

		TypeLiteral<Map<String, MethodInvocationHolder>> mapType = new TypeLiteral<Map<String, MethodInvocationHolder>>() {
		};
		bind(mapType).annotatedWith(Names.named(INVOCATION_MAP_NAME))
				.toInstance(new ConcurrentHashMap<String, MethodInvocationHolder>());


		CacheInterceptor cacheInterceptor = new CacheInterceptor();
		requestInjection(cacheInterceptor);
		requestStaticInjection(CacheInterceptor.class,CacheEntryTimedFactory.class);

		bindInterceptor(Matchers.any(), Matchers.annotatedWith(Cached.class),
				cacheInterceptor);
		bind(Cache4GuiceHelper.class).to(Cache4GuiceHelperImpl.class);
		
		
		try {
			StdSchedulerFactory.getDefaultScheduler().start();
		} catch (SchedulerException e) {
			logger.fatal("unable to start scheduler, cannot update selfpopulating cache",e);
		}

//		install(new QuartzModule() {
//			@Override
//			protected void schedule() {
//				scheduleJob(CleanupInvocationsJob.class).withCronExpression("0 0/10 * 1/1 * ? *");
//
//			}
//		});

	}

	protected Class getCacheKeyGeneratorClass() {

		return StringBasedKeyGenerator.class;
	}
}
