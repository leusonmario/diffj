package org.incava.diffj.params;

import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.Token;
import org.incava.pmdx.ParameterUtil;

public class ParameterComparator {
    public enum StatusType { NAME_CHANGED, TYPE_CHANGED, REORDERED, REMOVED, ADDED };
    
    private final List<ASTFormalParameter> fromFormalParamList;
    private final List<ASTFormalParameter> toFormalParamList;
    
    public ParameterComparator(List<ASTFormalParameter> fromFormalParamList, List<ASTFormalParameter> toFormalParamList) {
        this.fromFormalParamList = fromFormalParamList;
        this.toFormalParamList = toFormalParamList;
    }

    public ParameterMatch toMatch(Integer[] score) {
        return new ParameterMatch(score);
    }

    public ParameterMatch getMatch(int fromIdx) {
        final ParameterMatch noMatch = new ParameterMatch(-1, -1);

        Integer[] typeAndNameMatch = getParamMatch(fromIdx);
        ParameterMatch paramMatch = new ParameterMatch(typeAndNameMatch);

        if (paramMatch.isExactMatch()) {
            clearFromLists(fromIdx, typeAndNameMatch[1]);
            return paramMatch;
        }

        Integer bestMatch = paramMatch.getFirstMatch();
        
        if (bestMatch < 0) {
            return noMatch;
        }

        // make sure there isn't an exact match for this somewhere else in
        // fromParameters
        // $$$ this apparently isn't reached
        ASTFormalParameter to = toFormalParamList.get(bestMatch);
        if (hasExactMatch(to)) {
            return noMatch;
        }
        
        clearFromLists(fromIdx, bestMatch);
        return paramMatch;
    }

    private Integer[] getParamMatch(int fromIdx) {
        int typeMatch = -1;
        int nameMatch = -1;

        Integer[] typeAndNameMatch = new Integer[] { -1, -1 };
        ASTFormalParameter fromParam = fromFormalParamList.get(fromIdx);

        for (int toIdx = 0; toIdx < toFormalParamList.size(); ++toIdx) {
            ASTFormalParameter toParam = toFormalParamList.get(toIdx);

            if (fromParam == null || toParam == null) {
                continue;
            }

            if (areTypesEqual(fromParam, toParam)) {
                typeAndNameMatch[0] = toIdx;
                typeMatch = toIdx;
            }

            if (areNamesEqual(fromParam, toParam)) {
                typeAndNameMatch[1] = toIdx;
                nameMatch = toIdx;
            }

            if (typeMatch == toIdx && nameMatch == toIdx) {
                return typeAndNameMatch;
            }
        }
        return typeAndNameMatch;
    }

    /**
     * Returns whether there is an exact match for the given parameter in this
     * list.
     */
    private boolean hasExactMatch(ASTFormalParameter toFormalParam) {
        Parameter toParam = new Parameter(toFormalParam);

        for (ASTFormalParameter from : fromFormalParamList) {
            if (from != null) {
                Parameter fromParam = new Parameter(from);
                if (fromParam.isTypeEqual(toParam) && fromParam.isNameEqual(toParam)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean areTypesEqual(ASTFormalParameter fromFormalParam, ASTFormalParameter toFormalParam) {
        Parameter fromParam = new Parameter(fromFormalParam);
        Parameter toParam = new Parameter(toFormalParam);
        return fromParam.isTypeEqual(toParam);
    }

    private boolean areNamesEqual(ASTFormalParameter fromFormalParam, ASTFormalParameter toFormalParam) {
        Parameter fromParam = new Parameter(fromFormalParam);
        Parameter toParam = new Parameter(toFormalParam);
        return fromParam.isNameEqual(toParam);
    }

    private void clearFromLists(int fromIdx, int toIdx) {
        fromFormalParamList.set(fromIdx, null);
        toFormalParamList.set(toIdx, null);
    }
}