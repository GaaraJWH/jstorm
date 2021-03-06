/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.jstorm.window;

/**
 * Result type for trigger methods. This determines what happens with the window,
 * for example whether the window function should be called, or the window
 * should be discarded.
 */
public enum TriggerResult {

    /**
     * No action is taken on the window.
     */
    CONTINUE(false, false),

    /**
     * {@code FIRE_AND_PURGE} evaluates the window function and emits the window
     * result.
     */
    FIRE_AND_PURGE(true, true),

    /**
     * On {@code FIRE}, the window is evaluated and results are emitted.
     * The window is not purged, though, all elements are retained.
     */
    FIRE(true, false),

    /**
     * All elements in the window are cleared and the window is discarded,
     * without evaluating the window function or emitting any elements.
     */
    PURGE(false, true);

    // ------------------------------------------------------------------------

    private final boolean fire;
    private final boolean purge;

    TriggerResult(boolean fire, boolean purge) {
        this.purge = purge;
        this.fire = fire;
    }

    public boolean isFire() {
        return fire;
    }

    public boolean isPurge() {
        return purge;
    }

    // ------------------------------------------------------------------------

    /**
     * Merges two {@code TriggerResults}. This specifies what should happen if we have
     * two results from a Trigger, for example as a result from
     * {@link Trigger#onElement(Object, long, TimeWindow, Trigger.TriggerContext)} and
     * {@link Trigger#onEventTime(long, TimeWindow, Trigger.TriggerContext)}.
     *
     * <p>
     * For example, if one result says {@code CONTINUE} while the other says {@code FIRE}
     * then {@code FIRE} is the combined result;
     */
    public static TriggerResult merge(TriggerResult a, TriggerResult b) {
        if (a.purge || b.purge) {
            if (a.fire || b.fire) {
                return FIRE_AND_PURGE;
            } else {
                return PURGE;
            }
        } else if (a.fire || b.fire) {
            return FIRE;
        } else {
            return CONTINUE;
        }
    }
}
