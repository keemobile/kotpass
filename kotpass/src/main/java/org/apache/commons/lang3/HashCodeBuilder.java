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

public class HashCodeBuilder implements Builder<Integer> {
    /**
     * Constant to use in building the hashCode.
     */
    private final int iConstant;

    /**
     * Running total of the hashCode.
     */
    private int iTotal;

    /**
     * Uses two hard coded choices for the constants needed to build a {@code hashCode}.
     */
    public HashCodeBuilder() {
        iConstant = 37;
        iTotal = 17;
    }

    /**
     * Append a {@code hashCode} for a {@code boolean}.
     *
     * <p>
     * This adds {@code 1} when true, and {@code 0} when false to the {@code hashCode}.
     * </p>
     * <p>
     * This is in contrast to the standard {@link Boolean#hashCode()} handling, which computes
     * a {@code hashCode} value of {@code 1231} for {@link Boolean} instances
     * that represent {@code true} or {@code 1237} for {@link Boolean} instances
     * that represent {@code false}.
     * </p>
     * <p>
     * This is in accordance with the <em>Effective Java</em> design.
     * </p>
     *
     * @param value the boolean to add to the {@code hashCode}
     * @return {@code this} instance.
     */
    public HashCodeBuilder append(final boolean value) {
        iTotal = iTotal * iConstant + (value ? 0 : 1);
        return this;
    }

    /**
     * Append a {@code hashCode} for a {@code boolean} array.
     *
     * @param array the array to add to the {@code hashCode}
     * @return {@code this} instance.
     */
    public HashCodeBuilder append(final boolean[] array) {
        if (array == null) {
            iTotal = iTotal * iConstant;
        } else {
            for (final boolean element : array) {
                append(element);
            }
        }
        return this;
    }

    /**
     * Append a {@code hashCode} for a {@code byte}.
     *
     * @param value the byte to add to the {@code hashCode}
     * @return {@code this} instance.
     */
    public HashCodeBuilder append(final byte value) {
        iTotal = iTotal * iConstant + value;
        return this;
    }

    /**
     * Append a {@code hashCode} for a {@code byte} array.
     *
     * @param array the array to add to the {@code hashCode}
     * @return {@code this} instance.
     */
    public HashCodeBuilder append(final byte[] array) {
        if (array == null) {
            iTotal = iTotal * iConstant;
        } else {
            for (final byte element : array) {
                append(element);
            }
        }
        return this;
    }

    /**
     * Append a {@code hashCode} for a {@code char}.
     *
     * @param value the char to add to the {@code hashCode}
     * @return {@code this} instance.
     */
    public HashCodeBuilder append(final char value) {
        iTotal = iTotal * iConstant + value;
        return this;
    }

    /**
     * Append a {@code hashCode} for a {@code char} array.
     *
     * @param array the array to add to the {@code hashCode}
     * @return {@code this} instance.
     */
    public HashCodeBuilder append(final char[] array) {
        if (array == null) {
            iTotal = iTotal * iConstant;
        } else {
            for (final char element : array) {
                append(element);
            }
        }
        return this;
    }

    /**
     * Append a {@code hashCode} for a {@code double}.
     *
     * @param value the double to add to the {@code hashCode}
     * @return {@code this} instance.
     */
    public HashCodeBuilder append(final double value) {
        return append(Double.doubleToLongBits(value));
    }

    /**
     * Append a {@code hashCode} for a {@code double} array.
     *
     * @param array the array to add to the {@code hashCode}
     * @return {@code this} instance.
     */
    public HashCodeBuilder append(final double[] array) {
        if (array == null) {
            iTotal = iTotal * iConstant;
        } else {
            for (final double element : array) {
                append(element);
            }
        }
        return this;
    }

    /**
     * Append a {@code hashCode} for a {@code float}.
     *
     * @param value the float to add to the {@code hashCode}
     * @return {@code this} instance.
     */
    public HashCodeBuilder append(final float value) {
        iTotal = iTotal * iConstant + Float.floatToIntBits(value);
        return this;
    }

    /**
     * Append a {@code hashCode} for a {@code float} array.
     *
     * @param array the array to add to the {@code hashCode}
     * @return {@code this} instance.
     */
    public HashCodeBuilder append(final float[] array) {
        if (array == null) {
            iTotal = iTotal * iConstant;
        } else {
            for (final float element : array) {
                append(element);
            }
        }
        return this;
    }

    /**
     * Append a {@code hashCode} for an {@code int}.
     *
     * @param value the int to add to the {@code hashCode}
     * @return {@code this} instance.
     */
    public HashCodeBuilder append(final int value) {
        iTotal = iTotal * iConstant + value;
        return this;
    }

    /**
     * Append a {@code hashCode} for an {@code int} array.
     *
     * @param array the array to add to the {@code hashCode}
     * @return {@code this} instance.
     */
    public HashCodeBuilder append(final int[] array) {
        if (array == null) {
            iTotal = iTotal * iConstant;
        } else {
            for (final int element : array) {
                append(element);
            }
        }
        return this;
    }

    /**
     * Append a {@code hashCode} for a {@code long}.
     *
     * @param value the long to add to the {@code hashCode}
     * @return {@code this} instance.
     */
    public HashCodeBuilder append(final long value) {
        iTotal = iTotal * iConstant + (int) (value ^ value >> 32);
        return this;
    }

    /**
     * Append a {@code hashCode} for a {@code long} array.
     *
     * @param array the array to add to the {@code hashCode}
     * @return {@code this} instance.
     */
    public HashCodeBuilder append(final long[] array) {
        if (array == null) {
            iTotal = iTotal * iConstant;
        } else {
            for (final long element : array) {
                append(element);
            }
        }
        return this;
    }

    /**
     * Append a {@code hashCode} for an {@link Object}.
     *
     * @param object the Object to add to the {@code hashCode}
     * @return {@code this} instance.
     */
    public HashCodeBuilder append(final Object object) {
        if (object == null) {
            iTotal = iTotal * iConstant;

        } else if (ObjectUtils.isArray(object)) {
            // factor out array case in order to keep method small enough
            // to be inlined
            appendArray(object);
        } else {
            iTotal = iTotal * iConstant + object.hashCode();
        }
        return this;
    }

    /**
     * Append a {@code hashCode} for an {@link Object} array.
     *
     * @param array the array to add to the {@code hashCode}
     * @return {@code this} instance.
     */
    public HashCodeBuilder append(final Object[] array) {
        if (array == null) {
            iTotal = iTotal * iConstant;
        } else {
            for (final Object element : array) {
                append(element);
            }
        }
        return this;
    }

    /**
     * Append a {@code hashCode} for a {@code short}.
     *
     * @param value the short to add to the {@code hashCode}
     * @return {@code this} instance.
     */
    public HashCodeBuilder append(final short value) {
        iTotal = iTotal * iConstant + value;
        return this;
    }

    /**
     * Append a {@code hashCode} for a {@code short} array.
     *
     * @param array the array to add to the {@code hashCode}
     * @return {@code this} instance.
     */
    public HashCodeBuilder append(final short[] array) {
        if (array == null) {
            iTotal = iTotal * iConstant;
        } else {
            for (final short element : array) {
                append(element);
            }
        }
        return this;
    }

    /**
     * Append a {@code hashCode} for an array.
     *
     * @param object the array to add to the {@code hashCode}
     */
    private void appendArray(final Object object) {
        // 'Switch' on type of array, to dispatch to the correct handler
        // This handles multidimensional arrays
        if (object instanceof long[]) {
            append((long[]) object);
        } else if (object instanceof int[]) {
            append((int[]) object);
        } else if (object instanceof short[]) {
            append((short[]) object);
        } else if (object instanceof char[]) {
            append((char[]) object);
        } else if (object instanceof byte[]) {
            append((byte[]) object);
        } else if (object instanceof double[]) {
            append((double[]) object);
        } else if (object instanceof float[]) {
            append((float[]) object);
        } else if (object instanceof boolean[]) {
            append((boolean[]) object);
        } else {
            // Not an array of primitives
            append((Object[]) object);
        }
    }

    /**
     * Adds the result of super.hashCode() to this builder.
     *
     * @param superHashCode the result of calling {@code super.hashCode()}
     * @return {@code this} instance.
     * @since 2.0
     */
    public HashCodeBuilder appendSuper(final int superHashCode) {
        iTotal = iTotal * iConstant + superHashCode;
        return this;
    }

    /**
     * Returns the computed {@code hashCode}.
     *
     * @return {@code hashCode} based on the fields appended
     * @since 3.0
     */
    @Override
    public Integer build() {
        return Integer.valueOf(toHashCode());
    }

    /**
     * Implements equals using the hash code.
     *
     * @since 3.13.0
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HashCodeBuilder)) {
            return false;
        }
        final HashCodeBuilder other = (HashCodeBuilder) obj;
        return iTotal == other.iTotal;
    }

    /**
     * The computed {@code hashCode} from toHashCode() is returned due to the likelihood
     * of bugs in mis-calling toHashCode() and the unlikeliness of it mattering what the hashCode for
     * HashCodeBuilder itself is.
     *
     * @return {@code hashCode} based on the fields appended
     * @since 2.5
     */
    @Override
    public int hashCode() {
        return toHashCode();
    }

    /**
     * Returns the computed {@code hashCode}.
     *
     * @return {@code hashCode} based on the fields appended
     */
    public int toHashCode() {
        return iTotal;
    }
}
