package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import org.incava.pmdx.FieldUtil;

public class Fields extends Items<ASTFieldDeclaration> {
    public Fields(ASTClassOrInterfaceDeclaration type) {
        super(type, "net.sourceforge.pmd.ast.ASTFieldDeclaration");
    }

    public void doCompare(ASTFieldDeclaration fromFieldDecl, ASTFieldDeclaration toFieldDecl, Differences differences) {
        Field fromField = new Field(fromFieldDecl);
        Field toField = new Field(toFieldDecl);
        fromField.diff(toField, differences);
    }

    public String getName(ASTFieldDeclaration field) {
        return FieldUtil.getNames(field);
    }

    public String getAddedMessage(ASTFieldDeclaration field) {
        return Messages.FIELD_ADDED;
    }

    public String getRemovedMessage(ASTFieldDeclaration field) {
        return Messages.FIELD_REMOVED;
    }

    public double getScore(ASTFieldDeclaration fromField, ASTFieldDeclaration toField) {
        return FieldUtil.getMatchScore(fromField, toField);
    }
}