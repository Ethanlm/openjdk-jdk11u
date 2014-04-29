/*
 * Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @bug      4732864 6280605 7064544 8014636 8016328 8025633
 * @summary  Make sure that you can link from one member to another using
 *           non-qualified name, furthermore, ensure the right one is linked.
 * @author   jamieh
 * @library  ../lib/
 * @build    JavadocTester
 * @build    TestLinkTaglet
 * @run main TestLinkTaglet
 */

public class TestLinkTaglet extends JavadocTester {

    //Javadoc arguments.
    private static final String[] ARGS = new String[] {
        "-d", OUTPUT_DIR, "-sourcepath", SRC_DIR, "pkg", SRC_DIR +
        "/checkPkg/B.java"
    };

    //Input for string search tests.
    private static final String[][] TEST = {
        { "pkg/C.html",
            "Qualified Link: <a href=\"../pkg/C.InnerC.html\" title=\"class in pkg\"><code>C.InnerC</code></a>.<br/>\n" +
            " Unqualified Link1: <a href=\"../pkg/C.InnerC.html\" title=\"class in pkg\"><code>C.InnerC</code></a>.<br/>\n" +
            " Unqualified Link2: <a href=\"../pkg/C.InnerC.html\" title=\"class in pkg\"><code>C.InnerC</code></a>.<br/>\n" +
            " Qualified Link: <a href=\"../pkg/C.html#method-pkg.C.InnerC-pkg.C.InnerC2-\"><code>method(pkg.C.InnerC, pkg.C.InnerC2)</code></a>.<br/>\n" +
            " Unqualified Link: <a href=\"../pkg/C.html#method-pkg.C.InnerC-pkg.C.InnerC2-\"><code>method(C.InnerC, C.InnerC2)</code></a>.<br/>\n" +
            " Unqualified Link: <a href=\"../pkg/C.html#method-pkg.C.InnerC-pkg.C.InnerC2-\"><code>method(InnerC, InnerC2)</code></a>.<br/>"
        },
        { "pkg/C.InnerC.html",
            "Link to member in outer class: <a href=\"../pkg/C.html#MEMBER\"><code>C.MEMBER</code></a> <br/>\n" +
            " Link to member in inner class: <a href=\"../pkg/C.InnerC2.html#MEMBER2\"><code>C.InnerC2.MEMBER2</code></a> <br/>\n" +
            " Link to another inner class: <a href=\"../pkg/C.InnerC2.html\" title=\"class in pkg\"><code>C.InnerC2</code></a>"
        },
        { "pkg/C.InnerC2.html",
            "<dl>\n" +
            "<dt>Enclosing class:</dt>\n" +
            "<dd><a href=\"../pkg/C.html\" title=\"class in pkg\">C</a></dd>\n" +
            "</dl>"
        },
    };
    private static final String[][] NEGATED_TEST = {
        {WARNING_OUTPUT, "Tag @see: reference not found: A"},
    };

    /**
     * The entry point of the test.
     * @param args the array of command line arguments.
     */
    public static void main(String[] args) {
        TestLinkTaglet tester = new TestLinkTaglet();
        tester.run(ARGS, TEST, NEGATED_TEST);
        tester.printSummary();
    }
}
