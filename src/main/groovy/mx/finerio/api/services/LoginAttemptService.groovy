package mx.finerio.api.services

import org.springframework.stereotype.Service

import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import org.springframework.beans.factory.annotation.Value
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache

@Service
class LoginAttemptService {

	
	private final int maxAttempts	
	private final int minutesBlocked
	private LoadingCache<String, Integer> attemptsCache

	public LoginAttemptService( @Value('${finerio.security.login.maxAttempts}') final int maxAttempts, 	
		   @Value('${finerio.security.login.minutesBlocked}') final int minutesBlocked ){
		
		this.maxAttempts = maxAttempts
		this.minutesBlocked = minutesBlocked

		attemptsCache = CacheBuilder.newBuilder().
		expireAfterWrite( minutesBlocked, TimeUnit.MINUTES).build(new CacheLoader<String, Integer>() {
		  public Integer load(String key) {
			  return 0
		  }
	  })

	}
  	
	void loginSucceeded(String key) throws Exception {
		attemptsCache.invalidate(key)
	}
	
	void loginFailed(String key) throws Exception {
		
		int attempts = 0
		try {
			attempts = attemptsCache.get(key)
		} catch (ExecutionException e) {
			attempts = 0
		}
		attempts++
		attemptsCache.put(key, attempts)		
	}
		
	boolean isBlocked(String key) throws Exception {
		try {
			return attemptsCache.get(key) >= maxAttempts
		} catch (ExecutionException e) {
			return false
		}
	}	

}