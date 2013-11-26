package org.incava.diffj.code;

import java.text.MessageFormat;
import java.util.List;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffChange;
import org.incava.analysis.FileDiffCodeAdded;
import org.incava.analysis.FileDiffCodeDeleted;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.text.Message;
import org.incava.ijdk.util.diff.Differ;

public class Code {    
    public static final Message CODE_CHANGED = new Message("code changed in {0}");
    public static final Message CODE_ADDED = new Message("code added in {0}");
    public static final Message CODE_REMOVED = new Message("code removed in {0}");

    private final String name;
    private final TokenList tokenList;

    public Code(String name, List<Token> tokens) {
        this.name = name;
        this.tokenList = new TokenList(tokens);
    }

    public void diff(Code toCode, Differences differences) {
        TokenList toTokenList = toCode.tokenList;
        Differ<Token, TokenDifference> tokenDiff = tokenList.diff(toTokenList);
        
        FileDiff currFileDiff = null;
        List<TokenDifference> diffList = tokenDiff.execute();

        for (TokenDifference diff : diffList) {
            currFileDiff = processDifference(diff, toTokenList, currFileDiff, differences);
            if (currFileDiff == null) {
                break;
            }
        }
    }

    protected FileDiff replaceReference(FileDiff fileDiff, LocationRange fromLocRg, LocationRange toLocRg, Differences differences) {
        String newMsg = CODE_CHANGED.format(name);
        FileDiff newDiff = new FileDiffChange(newMsg, fileDiff, fromLocRg, toLocRg);
        differences.getFileDiffs().remove(fileDiff);
        return addFileDiff(newDiff, differences);
    }

    protected FileDiff addFileDiff(FileDiff fileDiff, Differences differences) {
        differences.add(fileDiff);
        return fileDiff;
    }

    protected FileDiff codeAdded(LocationRange fromLocRg, LocationRange toLocRg, Differences differences) {
        String str = CODE_ADDED.format(name);
        
        // this will show as add when highlighted, as change when not.
        FileDiff fileDiff = new FileDiffCodeAdded(str, fromLocRg, toLocRg);
        return addFileDiff(fileDiff, differences);
    }

    protected FileDiff codeRemoved(LocationRange fromLocRg, LocationRange toLocRg, Differences differences) {
        String str = CODE_REMOVED.format(name);
        FileDiff fileDiff = new FileDiffCodeDeleted(str, fromLocRg, toLocRg);
        return addFileDiff(fileDiff, differences);
    }    

    protected FileDiff codeChanged(LocationRange fromLocRg, LocationRange toLocRg, Differences differences) {
        String str = CODE_CHANGED.format(name);
        FileDiff fileDiff = new FileDiffChange(str, fromLocRg, toLocRg);
        return addFileDiff(fileDiff, differences);
    }
    
    protected FileDiff processDifference(TokenDifference diff, TokenList toTokenList, FileDiff currFileDiff, Differences differences) {
        int delStart = diff.getDeletedStart();
        int delEnd   = diff.getDeletedEnd();
        int addStart = diff.getAddedStart();
        int addEnd   = diff.getAddedEnd();

        LocationRange fromLocRg = tokenList.getLocationRange(delStart, delEnd);
        LocationRange toLocRg = toTokenList.getLocationRange(addStart, addEnd);

        if (currFileDiff != null && currFileDiff.isOnSameLine(fromLocRg)) {
            return replaceReference(currFileDiff, fromLocRg, toLocRg, differences);
        }
        else {
            return diff.execute(this, fromLocRg, toLocRg, differences);
        }
    }
}
