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

package io.ctrlconf.sentris.examples;

import io.ctrlconf.sentris.examples.DriverOps.Action.Factory;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.locks.LockSupport.parkNanos;

/**
 * Common driver configuration options and routines.
 */
public interface DriverOps {

  /**
   * The delay, in milliseconds, for a simulated call
   */
  String DRIVER_DELAY = "driver.delay";

  /**
   * The number of threads created by the driver
   */
  String DRIVER_THREADS = "driver.threads";

  /**
   * The duration, in seconds, for the execution
   */
  String DRIVER_DURATION = "driver.duration";

  /**
   * An interface used for signaling an particular execution/state point in the processing.
   */
  @FunctionalInterface
  interface Callback {

    /**
     * Signals to the callback that the execution/state point has occurred
     */
    void signal();

  }

  /**
   * An interface for controlling the continuation of processing
   */
  @FunctionalInterface
  interface Control {

    /**
     * Indicates whether the worker should continue iterating over some operation
     *
     * @return <tt>true</tt> is processing should continue
     */
    boolean proceed();

  }

  /**
   * An interface for demarcating an action to be performed during repetitive processing
   */
  @FunctionalInterface
  interface Action {

    /**
     * Performs the action repeated during the processing
     */
    void perform();

    /**
     * An interface for
     */
    @FunctionalInterface
    interface Factory {

      /**
       */
      Action create(int id);

    }

  }

  /**
   * Creates and starts a thread with the specified callbacks, control, and action
   *
   * @param started  the callback used for signalling the actual readiness of a thread
   * @param control  the control interface used to terminate further processing
   * @param action   the action that is repeated during each iteration
   * @param finished the callback used for signalling the completion of processing by a thread
   * @see #run(Callback, Control, Action, Callback)
   */
  static void spawn(
      final Callback started,
      final Control control,
      final Action action,
      final Callback finished) {

    new Thread(
        () -> run(
            started,
            control,
            action,
            finished))
        .start();

  }

  /**
   * Waits on all parties to have arrived before returning
   *
   * @param barrier the barrier used for coordinate continuation
   */
  static void waitOn(
      final CyclicBarrier barrier) {

    try {

      barrier.await();

    } catch(
        final InterruptedException
            | BrokenBarrierException e) {

      e.printStackTrace();

    }

  }

  /**
   * Creates a number of runners (threads) performing a series of controlled calls within the scope of a created action.
   *
   * @param runners  the number of action runners spawned
   * @param duration the duration of the execution
   * @param factory  the factory used to create actions
   */
  static void execute(
      final int runners,
      final long duration,
      final Factory factory) {

    try {

      // used for signaling the starting of a thread
      final CyclicBarrier started =
          new CyclicBarrier(
              runners + 1);

      // used for signalling the finishing of a thread
      final CountDownLatch finished =
          new CountDownLatch(
              runners);

      // used for controlling further processing by threads
      final AtomicBoolean control =
          new AtomicBoolean(true);

      // the creation of both service threads is intermingled for
      // those examples where we don't want a convoy of one type

      for(int i = runners; i > 0; i--) {

        //noinspection Convert2MethodRef
        spawn(
            () -> waitOn(started),
            () -> control.get(),
            factory.create(i),
            () -> finished.countDown());

      }

      // kick off processing in threads
      started.await();

      // wait for the running time to elapse
      parkNanos(duration);

      // prevent further continuation of calls
      control.set(false);

      // don't wait too long for all to complete
      finished.await();

    } catch(InterruptedException | BrokenBarrierException e) {

      e.printStackTrace();

    } finally {

      shutdown();

    }

  }

  /**
   * Calls the provided {@link Action} until indicated otherwise by the specified {@link Control}
   */
  static void run(
      final Callback started,
      final Control control,
      final Action action,
      final Callback finished) {

    try {

      started.signal();

      //noinspection MethodCallInLoopCondition
      while(control.proceed())
        action.perform();

    } finally {

      finished.signal();

    }

  }

  /**
   * Shutdown the runtime
   */
  static void shutdown() {

    // needed to force shutdown because
    // of possible active Swing/AWT threads
    // created by the visualization extensions

    System.exit(0);

  }

}
