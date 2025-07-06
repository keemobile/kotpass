/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.lang3;

final class IDKey {
    private final Object value;
    private final int id;

    /**
     * Constructs new instance.
     *
     * @param value The value
     */
    IDKey(final Object value) {
        this.id = System.identityHashCode(value);
        this.value = value;
    }

    /**
     * Tests if instances are equal.
     *
     * @param other The other object to compare to
     * @return if the instances are for the same object
     */
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof IDKey)) {
            return false;
        }
        final IDKey idKey = (IDKey) other;
        if (id != idKey.id) {
            return false;
        }
        return value == idKey.value;
    }

    /**
     * Gets the hash code, the system identity hash code.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        return id;
    }
}
