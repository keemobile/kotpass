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

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Objects;

abstract class CharSequenceTranslator {
    /**
     * Helper for non-Writer usage.
     *
     * @param input CharSequence to be translated
     * @return String output of translation
     */
    public final String translate(final CharSequence input) {
        if (input == null) {
            return null;
        }
        try {
            final StringWriter writer = new StringWriter(input.length() * 2);
            translate(input, writer);
            return writer.toString();
        } catch (final IOException ioe) {
            // this should never ever happen while writing to a StringWriter
            throw new UncheckedIOException(ioe);
        }
    }

    /**
     * Translate a set of code points, represented by an int index into a CharSequence,
     * into another set of code points. The number of code points consumed must be returned,
     * and the only IOExceptions thrown must be from interacting with the Writer so that
     * the top level API may reliably ignore StringWriter IOExceptions.
     *
     * @param input CharSequence that is being translated
     * @param index int representing the current point of translation
     * @param out   Writer to translate the text to
     * @return int count of code points consumed
     * @throws IOException if and only if the Writer produces an IOException
     */
    public abstract int translate(CharSequence input, int index, Writer out) throws IOException;

    /**
     * Translate an input onto a Writer. This is intentionally final as its algorithm is
     * tightly coupled with the abstract method of this class.
     *
     * @param input  CharSequence that is being translated
     * @param writer Writer to translate the text to
     * @throws IOException if and only if the Writer produces an IOException
     */
    @SuppressWarnings("resource") // Caller closes writer
    public final void translate(final CharSequence input, final Writer writer) throws IOException {
        Objects.requireNonNull(writer, "writer");
        if (input == null) {
            return;
        }
        int pos = 0;
        final int len = input.length();
        while (pos < len) {
            final int consumed = translate(input, pos, writer);
            if (consumed == 0) {
                // inlined implementation of Character.toChars(Character.codePointAt(input, pos))
                // avoids allocating temp char arrays and duplicate checks
                final char c1 = input.charAt(pos);
                writer.write(c1);
                pos++;
                if (Character.isHighSurrogate(c1) && pos < len) {
                    final char c2 = input.charAt(pos);
                    if (Character.isLowSurrogate(c2)) {
                        writer.write(c2);
                        pos++;
                    }
                }
                continue;
            }
            // contract with translators is that they have to understand code points
            // and they just took care of a surrogate pair
            for (int pt = 0; pt < consumed; pt++) {
                pos += Character.charCount(Character.codePointAt(input, pos));
            }
        }
    }
}
