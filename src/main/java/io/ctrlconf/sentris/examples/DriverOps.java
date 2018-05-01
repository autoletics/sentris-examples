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

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

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
   * Shutdown the runtime
   */
  static void shutdown() {

    // needed to force shutdown because
    // of possible active Swing/AWT threads

    System.exit(0);

  }

}
