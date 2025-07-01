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

public class StringEscapeUtils {
    /**
     * Translator object for escaping XML 1.0.
     * <p>
     * While {@link #escapeXml10(String)} is the expected method of use, this
     * object allows the XML escaping functionality to be used
     * as the foundation for a custom translator.
     *
     * @since 3.3
     */
    public static final CharSequenceTranslator ESCAPE_XML10 =
        new AggregateTranslator(
            new LookupTranslator(EntityArrays.BASIC_ESCAPE()),
            new LookupTranslator(EntityArrays.APOS_ESCAPE()),
            new LookupTranslator(
                new String[][]{
                    {"\u0000", ""},
                    {"\u0001", ""},
                    {"\u0002", ""},
                    {"\u0003", ""},
                    {"\u0004", ""},
                    {"\u0005", ""},
                    {"\u0006", ""},
                    {"\u0007", ""},
                    {"\u0008", ""},
                    {"\u000b", ""},
                    {"\u000c", ""},
                    {"\u000e", ""},
                    {"\u000f", ""},
                    {"\u0010", ""},
                    {"\u0011", ""},
                    {"\u0012", ""},
                    {"\u0013", ""},
                    {"\u0014", ""},
                    {"\u0015", ""},
                    {"\u0016", ""},
                    {"\u0017", ""},
                    {"\u0018", ""},
                    {"\u0019", ""},
                    {"\u001a", ""},
                    {"\u001b", ""},
                    {"\u001c", ""},
                    {"\u001d", ""},
                    {"\u001e", ""},
                    {"\u001f", ""},
                    {"\ufffe", ""},
                    {"\uffff", ""}
                }),
            NumericEntityEscaper.between(0x7f, 0x84),
            NumericEntityEscaper.between(0x86, 0x9f),
            new UnicodeUnpairedSurrogateRemover()
        );

    /**
     * Translator object for escaping XML 1.1.
     * <p>
     * While {@link #escapeXml11(String)} is the expected method of use, this
     * object allows the XML escaping functionality to be used
     * as the foundation for a custom translator.
     *
     * @since 3.3
     */
    public static final CharSequenceTranslator ESCAPE_XML11 =
        new AggregateTranslator(
            new LookupTranslator(EntityArrays.BASIC_ESCAPE()),
            new LookupTranslator(EntityArrays.APOS_ESCAPE()),
            new LookupTranslator(
                new String[][]{
                    {"\u0000", ""},
                    {"\u000b", "&#11;"},
                    {"\u000c", "&#12;"},
                    {"\ufffe", ""},
                    {"\uffff", ""}
                }),
            NumericEntityEscaper.between(0x1, 0x8),
            NumericEntityEscaper.between(0xe, 0x1f),
            NumericEntityEscaper.between(0x7f, 0x84),
            NumericEntityEscaper.between(0x86, 0x9f),
            new UnicodeUnpairedSurrogateRemover()
        );

    /**
     * Escapes the characters in a {@link String} using XML entities.
     *
     * <p>For example: {@code "bread" & "butter"} =&gt;
     * {@code &quot;bread&quot; &amp; &quot;butter&quot;}.
     * </p>
     *
     * <p>Note that XML 1.0 is a text-only format: it cannot represent control
     * characters or unpaired Unicode surrogate code points, even after escaping.
     * {@code escapeXml10} will remove characters that do not fit in the
     * following ranges:</p>
     *
     * <p>{@code #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]}</p>
     *
     * <p>Though not strictly necessary, {@code escapeXml10} will escape
     * characters in the following ranges:</p>
     *
     * <p>{@code [#x7F-#x84] | [#x86-#x9F]}</p>
     *
     * <p>The returned string can be inserted into a valid XML 1.0 or XML 1.1
     * document. If you want to allow more non-text characters in an XML 1.1
     * document, use {@link #escapeXml11(String)}.</p>
     *
     * @param input the {@link String} to escape, may be null
     * @return a new escaped {@link String}, {@code null} if null string input
     * @since 3.3
     */
    public static String escapeXml10(final String input) {
        return ESCAPE_XML10.translate(input);
    }

    /**
     * Escapes the characters in a {@link String} using XML entities.
     *
     * <p>For example: {@code "bread" & "butter"} =&gt;
     * {@code &quot;bread&quot; &amp; &quot;butter&quot;}.
     * </p>
     *
     * <p>XML 1.1 can represent certain control characters, but it cannot represent
     * the null byte or unpaired Unicode surrogate code points, even after escaping.
     * {@code escapeXml11} will remove characters that do not fit in the following
     * ranges:</p>
     *
     * <p>{@code [#x1-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]}</p>
     *
     * <p>{@code escapeXml11} will escape characters in the following ranges:</p>
     *
     * <p>{@code [#x1-#x8] | [#xB-#xC] | [#xE-#x1F] | [#x7F-#x84] | [#x86-#x9F]}</p>
     *
     * <p>The returned string can be inserted into a valid XML 1.1 document. Do not
     * use it for XML 1.0 documents.</p>
     *
     * @param input the {@link String} to escape, may be null
     * @return a new escaped {@link String}, {@code null} if null string input
     * @since 3.3
     */
    public static String escapeXml11(final String input) {
        return ESCAPE_XML11.translate(input);
    }
}
