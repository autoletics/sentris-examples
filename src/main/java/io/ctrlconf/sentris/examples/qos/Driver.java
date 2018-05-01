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
import java.util.concurrent.TimeoutException;

import static io.ctrlconf.sentris.examples.DriverOps.shutdown;
import static io.ctrlconf.sentris.examples.DriverOps.waitOn;
import static java.lang.Integer.getInteger;
import static java.lang.Long.getLong;
import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
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
      final String... args) throws InterruptedException, TimeoutException, BrokenBarrierException {

    try {

      // used for signaling the starting of a thread
      final CyclicBarrier started =
          new CyclicBarrier(
              (THREADS << 1) + 1);

      // used for signalling the finishing of a thread
      final CyclicBarrier finished =
          new CyclicBarrier(
              (THREADS << 1) + 1);

      // the creation of both service threads is intermingled for
      // those examples where we don't want a convoy of one type

      for(int i = THREADS; i > 0; i--) {

        spawn(
            started,
            Driver::s1,
            finished);

        spawn(
            started,
            Driver::s2,
            finished);

      }

      // kick off threads at this point
      started.await();

      // don't wait around for all to complete
      finished.await(
          DURATION,
          NANOSECONDS);

    } finally {

      shutdown();

    }

  }


  /**
   * Creates and starts a service thread
   */
  private static void spawn(
      final CyclicBarrier started,
      final Runnable service,
      final CyclicBarrier finished) {

    new Thread(
        () ->
            run(
                () -> waitOn(started),
                service,
                () -> waitOn(finished)))
        .start();

  }


  /**
   * Calls the provided {@link Runnable} infinitely
   */
  private static void run(
      final Callback started,
      final Runnable service,
      final Callback finished) {

    try {

      started.signal();

      // for visualization reasons the timed
      // shutdown checks are performed within
      // each thread and not the main thread

      final long end = nanoTime() + DURATION;

      //noinspection MethodCallInLoopCondition
      do {

        service.run();

      } while(nanoTime() < end);

    } finally {

      finished.signal();

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
