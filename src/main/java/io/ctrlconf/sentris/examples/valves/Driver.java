/*
 * Copyright © 2018 JINSPIRED BV (http://www.autoletics.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.ctrlconf.sentris.examples.valves;

import io.ctrlconf.sentris.examples.DriverOps;

import static io.ctrlconf.sentris.examples.DriverOps.execute;
import static java.lang.Integer.getInteger;
import static java.lang.Long.getLong;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.concurrent.locks.LockSupport.parkNanos;

/**
 * A driver class used across the various Adaptive Valve configuration examples.
 */
public final class Driver implements DriverOps {

  private static final Object LOCK = new Object();

  private static final String DRIVER_DEPTH = "driver.depth";

  private static final long DEFAULT_DELAY    = 50L;
  private static final int  DEFAULT_THREADS  = 10;
  private static final int  DEFAULT_DEPTH    = 10;
  private static final long DEFAULT_DURATION = 30L;

  private static final long DELAY    = MILLISECONDS.toNanos(getLong(DRIVER_DELAY, DEFAULT_DELAY));
  private static final int  THREADS  = getInteger(DRIVER_THREADS, DEFAULT_THREADS);
  private static final int  DEPTH    = getInteger(DRIVER_DEPTH, DEFAULT_DEPTH);
  private static final long DURATION = SECONDS.toNanos(getLong(DRIVER_DURATION, DEFAULT_DURATION));

  /**
   * Simulates a number of threads calling downwards into a content resource
   */
  public static void main(
      final String... args) {

    execute(
        THREADS,
        DURATION,
        id -> () -> call(DEPTH)
    );

  }

  /**
   * Simulates a call stack depth for use with the blink visualization.
   */
  private static void call(
      int depth) {

    if(--depth > 0) {

      call(depth);

    } else {

      // simulates contention - single threaded block

      synchronized(LOCK) {

        parkNanos(DELAY);

      }

    }

  }


}
