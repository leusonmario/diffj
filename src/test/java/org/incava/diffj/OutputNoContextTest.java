package org.incava.diffj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.incava.ijdk.util.ANSI;

public class OutputNoContextTest extends OutputTest {
    public OutputNoContextTest(String name) {
        super(name);
    }

    public boolean showContext() {
        return false;
    }        

    public void testImportAdded() {
        String[] output = doImportAddedTest();
        tr.Ace.log("output", output);        
    }

    public void testCodeChangedSingleLine() {
        String[] output = doCodeChangedSingleLineTest();
        tr.Ace.log("output", output);        
    }

    public void testCodeChangedMultipleLines() {
        String[] output = doCodeChangedMultipleLinesTest();
        
        // output doesn't have end-of-lines
        List<String> expected = new ArrayList<String>();

        expected.add("- <=> -");
        expected.add("2c2,3 code changed in Test(int)");
        expected.add("<     Test(int i) { i = 1; }");
        expected.add("---");
        expected.add(">         int j = 0;");
        expected.add(">         i = 2; ");

        assertEquals(expected, Arrays.asList(output));
    }

    public void testCodeDeleted() {
        String[] output = doCodeDeletedTest();

        List<String> expected = new ArrayList<String>();

        // @todo change so that the non-context output shows the previous block.
        expected.add("- <=> -");
        expected.add("3d3 code removed in Test()");
        expected.add("<         int j = 0;");
        expected.add("---");
        expected.add(">     Test() { int i = -1; }");

        assertEquals(expected, Arrays.asList(output));
    }

    public void testCodeAdded() {
        String[] output = doCodeAddedTest();
        
        List<String> expected = new ArrayList<String>();

        // @todo change so that the non-context output shows the previous block.
        expected.add("- <=> -");
        expected.add("4a5 code added in Test()");
        expected.add("<         int i = -1;");
        expected.add("---");
        expected.add(">         int i = -1, k = 666;");

        assertEquals(expected, Arrays.asList(output));
    }
}
