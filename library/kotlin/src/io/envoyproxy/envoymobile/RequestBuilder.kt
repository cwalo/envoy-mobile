package io.envoyproxy.envoymobile


/**
 * Builder used for constructing instances of `Request` types.
 *
 * @param method Method for the request.
 * @param scheme The URL scheme for the request (i.e., "https").
 * @param authority The URL authority for the request (i.e., "api.foo.com").
 * @param path The URL path for the request (i.e., "/foo").
 */
class RequestBuilder(
    val method: RequestMethod,
    val scheme: String,
    val authority: String,
    val path: String
) {
  // Headers to send with the request.
  // Multiple values for a given name are valid, and will be sent as comma-separated values.
  private val headers: MutableMap<String, MutableList<String>> = mutableMapOf()

  // Retry policy to use for this request.
  private var retryPolicy: RetryPolicy? = null

  /**
   * Add a retry policy to use for this request.
   *
   * @param retryPolicy the {@link io.envoyproxy.envoymobile.RetryPolicy} for this request.
   * @return this builder.
   */
  fun addRetryPolicy(retryPolicy: RetryPolicy?): RequestBuilder {
    this.retryPolicy = retryPolicy
    return this
  }

  /**
   * Append a value to the header key.
   *
   * @param name the header key.
   * @param value the value associated to the header key.
   * @return this builder.
   */
  fun addHeader(name: String, value: String): RequestBuilder {
    if (headers.containsKey(name)) {
      headers[name]!!.add(value)
    } else {
      headers[name] = mutableListOf(value)
    }
    return this
  }

  /**
   * Remove the value in the specified header.
   *
   * @param name the header key to remove.
   * @param value the value to be removed.
   * @return this builder.
   */
  fun removeHeader(name: String, value: String): RequestBuilder {
    if (headers.containsKey(name)) {
      headers[name]!!.remove(value)
      if (headers[name]!!.isEmpty()) {
        headers.remove(name)
      }
    }
    return this
  }

  /**
   * Remove all headers with this name.
   *
   * @param name the header key to remove.
   * @return this builder.
   */
  fun removeHeaders(name: String): RequestBuilder {
    headers.remove(name)
    return this
  }

  /**
   * Creates the {@link io.envoyproxy.envoymobile.Request} object using the data set in the builder.
   *
   * @return the {@link io.envoyproxy.envoymobile.Request} object.
   */
  fun build(): Request {
    return Request(
        method,
        scheme,
        authority,
        path,
        headers,
        retryPolicy
    )
  }

  internal fun setHeaders(headers: Map<String, List<String>>): RequestBuilder {
    this.headers.clear()
    for (entry in headers) {
      this.headers[entry.key] = entry.value.toMutableList()
    }
    return this
  }
}
