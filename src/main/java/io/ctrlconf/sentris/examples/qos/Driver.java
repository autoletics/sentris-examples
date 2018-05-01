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

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.ctrlconf.sentris.examples.DriverOps.*;
import static java.lang.Integer.getInteger;
import static java.lang.Long.getLong;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.concurrent.locks.LockSupport.parkNanos;

/**
 * A driver class used across the various QoS configuration examples.
 */
@SuppressWarnings("Convert2MethodRef")
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
      final String... args)
  throws
      InterruptedException,
      BrokenBarrierException {

    try {

      // used for signaling the starting of a thread
      final CyclicBarrier started =
          new CyclicBarrier(
              (THREADS << 1) + 1);

      // used for signalling the finishing of a thread
      final CyclicBarrier finished =
          new CyclicBarrier(
              (THREADS << 1) + 1);

      // used for controlling further processing by threads
      final AtomicBoolean proceed =
          new AtomicBoolean(true);

      // the creation of both service threads is intermingled for
      // those examples where we don't want a convoy of one type

      for(int i = THREADS; i > 0; i--) {

        // start a "service 1" thread

        spawn(
            () -> waitOn(started),
            () -> proceed.get(),
            () -> s1(),
            () -> waitOn(finished));

        // start a "service 2" thread

        spawn(
            () -> waitOn(started),
            () -> proceed.get(),
            () -> s2(),
            () -> waitOn(finished));

      }

      // kick off processing in threads
      started.await();

      // wait for the running time to elapse
      parkNanos(DURATION);

      // prevent further continuation of calls
      proceed.set(true);

      // don't wait too long for all to complete
      finished.await();

    } finally {

      shutdown();

    }

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
