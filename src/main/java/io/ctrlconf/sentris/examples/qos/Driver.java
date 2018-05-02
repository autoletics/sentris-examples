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

package io.ctrlconf.sentris.examples.qos;

import io.ctrlconf.sentris.examples.DriverOps;

import static io.ctrlconf.sentris.examples.DriverOps.execute;
import static java.lang.Integer.getInteger;
import static java.lang.Long.getLong;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.concurrent.locks.LockSupport.parkNanos;

/**
 * A driver class used across the various QoS configuration examples.
 */
public final class Driver implements DriverOps {

  private static final long DEFAULT_DELAY    = 10L;
  private static final int  DEFAULT_THREADS  = 3;
  private static final long DEFAULT_DURATION = 30L;

  private static final long DELAY    = MILLISECONDS.toNanos(getLong(DRIVER_DELAY, DEFAULT_DELAY));
  private static final int  THREADS  = getInteger(DRIVER_THREADS, DEFAULT_THREADS);
  private static final long DURATION = SECONDS.toNanos(getLong(DRIVER_DURATION, DEFAULT_DURATION));

  /**
   * Simulates the execution of multiple competing service threads.
   */
  public static void main(
      final String... args) {

    // run simulation with double the threads
    // because we have two services calling

    execute(
        THREADS << 1,
        DURATION,
        id -> (id % 2) == 0
              ? Driver::s1
              : Driver::s2
    );

  }

  /**
   * Simulate the execution of a s1 service flow
   */
  public static void s1() {

    parkNanos(DELAY);

  }


  /**
   * Simulates the execution of a s2 service flow
   */
  public static void s2() {

    parkNanos(DELAY);

  }


}
