package org.incava.analysis;

import java.awt.Point;
import java.io.*;
import java.util.*;
import org.incava.ijdk.io.FileExt;
import org.incava.qualog.Qualog;


/**
 * Reports errors (differences), in a format that is determined by the subclass.
 */
public abstract class Report {
    /**
     * The end-of-line character/sequence for this OS.
     */
    protected final static String EOLN = FileExt.EOLN;

    /**
     * The from-file to which this report currently applies. By default, this is
     * '-', denoting standard output.
     */
    protected String fromFileName = "-";

    /**
     * The to-file to which this report currently applies. By default, this is
     * '-', denoting standard output.
     */
    protected String toFileName = "-";

    /**
     * The writer to which this report sends output.
     */
    protected Writer writer;

    /**
     * The set of differences, which are maintained in sorted order.
     */
    protected Set<FileDiff> differences = new TreeSet<FileDiff>();

    /**
     * Creates a report for the given writer.
     *
     * @param writer The writer associated with this report.
     */
    public Report(Writer writer) {
        this.writer = writer;
    }

    /**
     * Creates a report for the given output stream.
     *
     * @param os The output stream associated with this report.
     */
    public Report(OutputStream os) {
        this(new OutputStreamWriter(os));
    }

    /**
     * Creates a report for the given writer, and a string source.
     *
     * @param writer The writer associated with this report.
     * @param fromSource The from-source code to which this report applies.
     * @param toSource The to-source code to which this report applies.
     */
    public Report(Writer writer, String fromSource, String toSource) {
        this(writer);
        
        reset(fromSource, toSource);
    }

    /**
     * Creates a report for the given writer, and a file source.
     *
     * @param writer The writer associated with this report.
     * @param fromFile The from-file, containing source code, to which this report applies.
     * @param toFile The to-file, containing source code, to which this report applies.
     */
    public Report(Writer writer, File fromFile, File toFile) {
        this(writer);
        
        reset(fromFile, toFile);
    }

    /**
     * Creates a report for the given output stream, and string source.
     *
     * @param os The output stream associated with this report.
     * @param fromSource The from-source code to which this report applies.
     * @param toSource The to-source code to which this report applies.
     */
    public Report(OutputStream os, String fromSource, String toSource) {
        this(os);

        reset(fromSource, toSource);
    }

    /**
     * Creates a report for the given output stream, and file.
     *
     * @param os The output stream associated with this report.
     * @param fromFile The from-file, containing source code, to which this report applies.
     * @param toFile The to-file, containing source code, to which this report applies.
     */
    public Report(OutputStream os, File fromFile, File toFile) {
        this(os);

        reset(fromFile, toFile);
    }
    
    /**
     * Associates the given file with the list of differences, including that are
     * adding to this report later, i.e., prior to <code>flush</code>.
     *
     * @param fromFile The from-file, containing source code, to which this report applies.
     * @param toFile The to-file, containing source code, to which this report applies.
     */
    public void reset(File fromFile, File toFile) {
        fromFileName = fromFile.getPath();
        toFileName   = toFile.getPath();
    }

    /**
     * Associates the given string source with the list of differences, including
     * that are adding to this report later, i.e., prior to <code>flush</code>.
     */
    public void reset(String fromSource, String toSource) {
        fromFileName = "-";
        toFileName   = "-";
    }

    /**
     * Associates the given string source with the list of differences, including
     * that are adding to this report later, i.e., prior to <code>flush</code>.
     */
    public void reset(String fromFileName, String fromContents, String toFileName, String toContents) {
        this.fromFileName = fromFileName;
        this.toFileName   = toFileName;
    }

    /**
     * Writes all differences, and clears the list.
     */
    public void flush() {
        try {
            tr.Ace.log("flushing differences");
            Collection<FileDiff> diffs = collateDifferences(differences);
            for (FileDiff ref : diffs) {
                String        str = toString(ref);
                // tr.Ace.log("ref    : " + ref);
                // tr.Ace.log("writing: " + str);
                writer.write(str);
            }
            // we can't close STDOUT
            writer.flush();
            // writer.close();
        }
        catch (IOException ioe) {
        }
        clear();
    }

    public Collection<FileDiff> collateDifferences(Collection<FileDiff> diffs) {
        return diffs;
    }

    public void printFileNames() {
        // only print file names once per report.
        
        if (fromFileName != null && toFileName != null) {
            StringBuffer buf = new StringBuffer();
            buf.append(fromFileName);
            buf.append(" <=> ");
            buf.append(toFileName);
            buf.append(System.getProperty("line.separator"));
            
            try {
                writer.write(buf.toString());
            }
            catch (IOException ioe) {
            }
            
            fromFileName = null;
            toFileName = null;
        }
    }

    public Collection<FileDiff> getDifferences() {
        return differences;
    }
    
    /**
     * Returns a string representing the given reference, consistent with the
     * format of the Report subclass.
     */
    protected abstract String toString(FileDiff ref);

    /**
     * Sends the given string to the writer associated with this Report.
     *
     * @param str The string to be written.
     */
    protected void write(String str) {
        tr.Ace.log("writing '" + str + "'");
        try {
            writer.write(str);
        }
        catch (IOException ioe) {
        }
    }

    protected String toString(Point pt) {
        StringBuffer buf = new StringBuffer();
        buf.append(pt.x);
        if (pt.x != pt.y) {
            buf.append(",").append(pt.y);
        }
        return buf.toString();
    }

    /**
     * Clears the list of differences.
     */
    protected void clear() {
        differences.clear();
    }

}