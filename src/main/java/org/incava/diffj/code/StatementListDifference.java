package org.incava.diffj.code;

import java.util.List;
import org.incava.analysis.FileDiff;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.util.diff.Difference;

public abstract class StatementListDifference extends Difference {
    private final List<TokenList> fromTokenLists;
    private final List<TokenList> toTokenLists;    

    public StatementListDifference(List<TokenList> fromTokenLists, List<TokenList> toTokenLists,
                                   Integer delStart, Integer delEnd, Integer addStart, Integer addEnd) {
        super(delStart, delEnd, addStart, addEnd);
        this.fromTokenLists = fromTokenLists;
        this.toTokenLists = toTokenLists;
    }

    private TokenList getAsTokenList(List<TokenList> tokenLists, Integer from, Integer to) {
        if (to == Difference.NONE) {
            return tokenLists.get(from);
        }
        
        int idx = from;
        TokenList list = tokenLists.get(idx++);
        while (idx <= to) {
            list.add(tokenLists.get(idx++));
        }

        return list;
    }

    public abstract void execute(String name, Differences differences);

    public TokenList getFromList() {
        return getAsTokenList(fromTokenLists, getDeletedStart(), getDeletedEnd());
    }

    public TokenList getToList() {
        return getAsTokenList(toTokenLists, getAddedStart(), getAddedEnd());
    }
}
