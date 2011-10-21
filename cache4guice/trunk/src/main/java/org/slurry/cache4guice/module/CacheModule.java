package org.slurry.cache4guice.module;

import net.sf.ehcache.CacheManager;

import org.slurry.cache4guice.annotation.Cached;
import org.slurry.cache4guice.aop.CacheInterceptor;
import org.slurry.cache4guice.aop.CacheKeyGenerator;
import org.slurry.cache4guice.cache.StringBasedKeyGenerator;
import org.slurry.cache4guice.cache.util.Cache4GuiceHelper;
import org.slurry.cache4guice.cache.util.Cache4GuiceHelperImpl;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class CacheModule extends AbstractModule {
	protected void configure() {

		bind(CacheManager.class).toInstance(CacheManager.create());
		bind(CacheKeyGenerator.class).to(getCacheKeyGeneratorClass());
		CacheInterceptor cacheInterceptor = new CacheInterceptor();
		requestInjection(cacheInterceptor);
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(Cached.class),
				cacheInterceptor);
		bind(Cache4GuiceHelper.class).to(Cache4GuiceHelperImpl.class);
	}

	protected Class getCacheKeyGeneratorClass() {

		return StringBasedKeyGenerator.class;
	}
}
