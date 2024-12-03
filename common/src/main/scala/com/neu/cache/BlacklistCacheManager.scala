package com.neu.cache

import com.neu.application.model.{ Blacklist, UserBlacklist }
import net.sf.ehcache.CacheManager

object BlacklistCacheManager {
  given cacheKeyGenerator: ToStringCacheKeyGenerator.type = ToStringCacheKeyGenerator
  given cacheManager: CacheManager                        = CacheManager.getInstance()

  val cacheName = "blacklistCache"

  val cache: Cache[String, Blacklist] =
    Ehcache[String, Blacklist](cacheName)

  /**
   * Save blacklist for Graph.
   */
  def saveBlacklist(userBlacklist: UserBlacklist): Unit =
    userBlacklist.blacklist.foreach(cache.put(userBlacklist.user + "blacklist", _))

  /**
   * Get blacklist of user for Graph
   *
   * @param user
   *   the user
   * @return
   *   [[Blacklist]]
   */
  def getBlacklist(user: String): Option[Blacklist] = cache.get(user + "blacklist")
}
