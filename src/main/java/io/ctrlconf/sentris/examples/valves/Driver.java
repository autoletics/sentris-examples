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

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeoutException;
import java.util.function.IntConsumer;

import static io.ctrlconf.sentris.examples.DriverOps.shutdown;
import static io.ctrlconf.sentris.examples.DriverOps.waitOn;
import static java.lang.Integer.getInteger;
import static java.lang.Long.getLong;
import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.*;
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
      final String... args)
  throws
      BrokenBarrierException,
      InterruptedException,
      TimeoutException {

    try {

      // used for signaling the starting of a thread
      final CyclicBarrier started =
          new CyclicBarrier(
              THREADS + 1);

      // used for signalling the finishing of a thread
      final CyclicBarrier finished =
          new CyclicBarrier(
              THREADS + 1);

      for(int i = THREADS; i > 0; i--) {

        spawn(
            started,
            finished
        );

      }

      // kick off threads at this point
      started.await();

      // don't wait too long for all to complete
      finished.await(
          DURATION << 1,
          NANOSECONDS);

    } finally {

      // this is done to shutdown
      // the Swing/AWT threads

      shutdown();

    }

  }

  /**
   * Creates and starts a new worker thread.
   *
   * @param started  the barrier used for signaling an actual start of a thread
   * @param finished the barrier used for signaling the actual completion of a thread
   */
  private static void spawn(
      final CyclicBarrier started,
      final CyclicBarrier finished) {

    new Thread(
        () ->
            run(
                () -> waitOn(started),
                Driver::call,
                () -> waitOn(finished)))
        .start();

  }


  /**
   * Calls the provided {@link Runnable} infinitely
   */
  private static void run(
      final Callback started,
      final IntConsumer service,
      final Callback finished) {

    try {

      started.signal();

      // for visualization reasons the timed
      // shutdown checks are performed within
      // each thread and not the main thread

      final long end = nanoTime() + DURATION;

      //noinspection MethodCallInLoopCondition
      do {

        service.accept(DEPTH);

      } while(nanoTime() <= end);

    } finally {

      finished.signal();

    }

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
