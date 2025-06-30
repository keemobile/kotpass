/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

class ObjectUtils {
    /**
     * Class used as a null placeholder where {@code null}
     * has another meaning.
     *
     * <p>For example, in a {@link HashMap} the
     * {@link java.util.HashMap#get(Object)} method returns
     * {@code null} if the {@link Map} contains {@code null} or if there is
     * no matching key. The {@code null} placeholder can be used to distinguish
     * between these two cases.</p>
     *
     * <p>Another example is {@link Hashtable}, where {@code null}
     * cannot be stored.</p>
     */
    public static class Null implements Serializable {
        /**
         * Required for serialization support. Declare serialization compatibility with Commons Lang 1.0
         *
         * @see java.io.Serializable
         */
        private static final long serialVersionUID = 7092611880189329093L;

        /**
         * Restricted constructor - singleton.
         */
        Null() {
        }

        /**
         * Ensure Singleton after serialization.
         *
         * @return the singleton value
         */
        private Object readResolve() {
            return NULL;
        }
    }

    /**
     * Singleton used as a {@code null} placeholder where
     * {@code null} has another meaning.
     *
     * <p>For example, in a {@link HashMap} the
     * {@link java.util.HashMap#get(Object)} method returns
     * {@code null} if the {@link Map} contains {@code null} or if there
     * is no matching key. The {@code null} placeholder can be used to
     * distinguish between these two cases.</p>
     *
     * <p>Another example is {@link Hashtable}, where {@code null}
     * cannot be stored.</p>
     *
     * <p>This instance is Serializable.</p>
     */
    public static final Null NULL = new Null();

    /**
     * Delegates to {@link Object#getClass()} using generics.
     *
     * @param <T>    The argument type or null.
     * @param object The argument.
     * @return The argument's Class or null.
     * @since 3.13.0
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClass(final T object) {
        return object == null ? null : (Class<T>) object.getClass();
    }

    /**
     * Tests whether the given object is an Object array or a primitive array in a null-safe manner.
     *
     * <p>
     * A {@code null} {@code object} Object will return {@code false}.
     * </p>
     *
     * <pre>
     * ObjectUtils.isArray(null)             = false
     * ObjectUtils.isArray("")               = false
     * ObjectUtils.isArray("ab")             = false
     * ObjectUtils.isArray(new int[]{})      = true
     * ObjectUtils.isArray(new int[]{1,2,3}) = true
     * ObjectUtils.isArray(1234)             = false
     * </pre>
     *
     * @param object the object to check, may be {@code null}
     * @return {@code true} if the object is an {@code array}, {@code false} otherwise
     * @since 3.13.0
     */
    public static boolean isArray(final Object object) {
        return object != null && object.getClass().isArray();
    }

    /**
     * Tests if an Object is empty or null.
     * <p>
     * The following types are supported:
     * <ul>
     * <li>{@link CharSequence}: Considered empty if its length is zero.</li>
     * <li>{@link Array}: Considered empty if its length is zero.</li>
     * <li>{@link Collection}: Considered empty if it has zero elements.</li>
     * <li>{@link Map}: Considered empty if it has zero key-value mappings.</li>
     * <li>{@link Optional}: Considered empty if {@link Optional#isPresent} returns false, regardless of the "emptiness" of the contents.</li>
     * </ul>
     *
     * <pre>
     * ObjectUtils.isEmpty(null)             = true
     * ObjectUtils.isEmpty("")               = true
     * ObjectUtils.isEmpty("ab")             = false
     * ObjectUtils.isEmpty(new int[]{})      = true
     * ObjectUtils.isEmpty(new int[]{1,2,3}) = false
     * ObjectUtils.isEmpty(1234)             = false
     * ObjectUtils.isEmpty(1234)             = false
     * ObjectUtils.isEmpty(Optional.of(""))  = false
     * ObjectUtils.isEmpty(Optional.empty()) = true
     * </pre>
     *
     * @param object the {@link Object} to test, may be {@code null}
     * @return {@code true} if the object has a supported type and is empty or null,
     * {@code false} otherwise
     * @since 3.9
     */
    public static boolean isEmpty(final Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof CharSequence) {
            return ((CharSequence) object).length() == 0;
        }
        if (isArray(object)) {
            return Array.getLength(object) == 0;
        }
        if (object instanceof Collection<?>) {
            return ((Collection<?>) object).isEmpty();
        }
        if (object instanceof Map<?, ?>) {
            return ((Map<?, ?>) object).isEmpty();
        }
        if (object instanceof Optional<?>) {
            // TODO Java 11 Use Optional#isEmpty()
            return !((Optional<?>) object).isPresent();
        }
        return false;
    }
}
