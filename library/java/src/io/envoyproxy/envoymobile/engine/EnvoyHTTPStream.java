package io.envoyproxy.envoymobile.engine;

import io.envoyproxy.envoymobile.engine.types.EnvoyHTTPCallbacks;

import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnvoyHTTPStream {

  private final long streamHandle;
  private final JvmCallbackContext callbacksContext;

  EnvoyHTTPStream(long streamHandle, EnvoyHTTPCallbacks callbacks) {
    this.streamHandle = streamHandle;
    callbacksContext = new JvmCallbackContext(callbacks);
    JniLibrary.startStream(streamHandle, callbacksContext);
  }

  /**
   * Send headers over an open HTTP streamHandle. This method can be invoked once
   * and needs to be called before send_data.
   *
   * @param headers,   the headers to send.
   * @param endStream, supplies whether this is headers only.
   */
  public void sendHeaders(Map<String, List<String>> headers, boolean endStream) {
    JniLibrary.sendHeaders(streamHandle, toJniLibraryHeaders(headers), endStream);
  }

  /**
   * Send data over an open HTTP streamHandle. This method can be invoked multiple
   * times.
   *
   * @param data,      the data to send.
   * @param endStream, supplies whether this is the last data in the streamHandle.
   */
  public void sendData(ByteBuffer data, boolean endStream) {
    JniLibrary.sendData(streamHandle, data, endStream);
  }

  /**
   * Send metadata over an HTTP streamHandle. This method can be invoked multiple
   * times.
   *
   * @param metadata, the metadata to send.
   */
  public void sendMetadata(Map<String, List<String>> metadata) {
    JniLibrary.sendMetadata(streamHandle, toJniLibraryHeaders(metadata));
  }

  /**
   * Send trailers over an open HTTP streamHandle. This method can only be invoked
   * once per streamHandle. Note that this method implicitly ends the
   * streamHandle.
   *
   * @param trailers, the trailers to send.
   */
  public void sendTrailers(Map<String, List<String>> trailers) {
    JniLibrary.sendTrailers(streamHandle, toJniLibraryHeaders(trailers));
  }

  /**
   * Cancel the streamHandle. This functions as an interrupt, and aborts further
   * callbacks and handling of the streamHandle.
   *
   * @return Success, unless the streamHandle has already been canceled.
   */
  public int resetStream() { return JniLibrary.resetStream(streamHandle); }

  /**
   * Cancel the stream. This functions as an interrupt, and aborts further
   * callbacks and handling of the stream.
   */
  public void cancel() { JniLibrary.cancel(); }

  private static byte[][] toJniLibraryHeaders(Map<String, List<String>> headers) {
    // Create array with some room for potential headers that have more than one value.
    final List<byte[]> convertedHeaders = new ArrayList<byte[]>(2 * headers.size());
    for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
      for (String value : entry.getValue()) {
        convertedHeaders.add(entry.getKey().getBytes(StandardCharsets.UTF_8));
        convertedHeaders.add(value.getBytes(StandardCharsets.UTF_8));
      }
    }
    return convertedHeaders.toArray(new byte[0][0]);
  }
}
