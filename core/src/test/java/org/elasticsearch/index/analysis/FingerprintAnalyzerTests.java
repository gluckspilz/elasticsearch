package org.elasticsearch.index.analysis;

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.elasticsearch.test.ESTokenStreamTestCase;

public class FingerprintAnalyzerTests extends ESTokenStreamTestCase {

    public void testFingerprint() throws Exception {
        Analyzer a = new FingerprintAnalyzer(CharArraySet.EMPTY_SET, ' ', 255, false);
        assertAnalyzesTo(a, "foo bar@baz Baz $ foo foo FOO. FoO",
            new String[]{"bar baz foo"});
    }

    public void testReusableTokenStream() throws Exception {
        Analyzer a = new FingerprintAnalyzer(CharArraySet.EMPTY_SET, ' ', 255, false);
        assertAnalyzesTo(a, "foo bar baz Baz foo foo FOO. FoO",
            new String[]{"bar baz foo"});
        assertAnalyzesTo(a, "xyz XYZ abc 123.2 abc",
            new String[]{"123.2 abc xyz"});
    }

    public void testAsciifolding() throws Exception {
        Analyzer a = new FingerprintAnalyzer(CharArraySet.EMPTY_SET, ' ', 255, false);
        assertAnalyzesTo(a, "gödel escher bach",
            new String[]{"bach escher godel"});

        assertAnalyzesTo(a, "gödel godel escher bach",
            new String[]{"bach escher godel"});
    }

    public void testPreserveOriginal() throws Exception {
        Analyzer a = new FingerprintAnalyzer(CharArraySet.EMPTY_SET, ' ', 255, true);
        assertAnalyzesTo(a, "gödel escher bach",
            new String[]{"bach escher godel gödel"});
    }

    public void testLimit() throws Exception {
        Analyzer a = new FingerprintAnalyzer(CharArraySet.EMPTY_SET, ' ', 3, false);
        assertAnalyzesTo(a, "e d c b a",
            new String[]{});

        assertAnalyzesTo(a, "b a",
            new String[]{"a b"});
    }

    public void testSeparator() throws Exception {
        Analyzer a = new FingerprintAnalyzer(CharArraySet.EMPTY_SET, '_', 255, true);
        assertAnalyzesTo(a, "b c a",
            new String[]{"a_b_c"});
    }

}