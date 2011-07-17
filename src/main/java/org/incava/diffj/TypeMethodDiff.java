package org.incava.diffj;

import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.java.*;
import org.incava.ijdk.lang.*;
import org.incava.ijdk.util.*;
import org.incava.pmd.*;


public class TypeMethodDiff extends AbstractTypeItemDiff<ASTMethodDeclaration> {

    private final MethodUtil methodUtil;

    public TypeMethodDiff(Collection<FileDiff> differences) {
        super(differences, ASTMethodDeclaration.class);
        
        methodUtil = new MethodUtil();
    }    

    public void doCompare(ASTMethodDeclaration a, ASTMethodDeclaration b) {
        MethodDiff differ = new MethodDiff(getFileDiffs());
        differ.compareAccess(SimpleNodeUtil.getParent(a), SimpleNodeUtil.getParent(b));
        differ.compare(a, b);
    }

    public double getScore(ASTMethodDeclaration a, ASTMethodDeclaration b) {        
        return methodUtil.getMatchScore(a, b);
    }

    public String getName(ASTMethodDeclaration md) {
        return MethodUtil.getFullName(md);
    }

    public String getAddedMessage(ASTMethodDeclaration md) {
        return TypeDiff.METHOD_ADDED;
    }

    public String getRemovedMessage(ASTMethodDeclaration md) {
        return TypeDiff.METHOD_REMOVED;
    }
}