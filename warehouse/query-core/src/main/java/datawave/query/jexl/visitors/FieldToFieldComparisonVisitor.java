package datawave.query.jexl.visitors;

import org.apache.commons.jexl2.parser.ASTEQNode;
import org.apache.commons.jexl2.parser.ASTEvaluationOnly;
import org.apache.commons.jexl2.parser.ASTGENode;
import org.apache.commons.jexl2.parser.ASTGTNode;
import org.apache.commons.jexl2.parser.ASTJexlScript;
import org.apache.commons.jexl2.parser.ASTLENode;
import org.apache.commons.jexl2.parser.ASTLTNode;
import org.apache.commons.jexl2.parser.ASTNENode;
import org.apache.commons.jexl2.parser.JexlNode;

import datawave.query.jexl.JexlASTHelper;

/**
 * Class to detect field to field comparison nodes and turns it into ASTEvaluationOnly node
 *
 * e.g.) "FIELD_A == FIELD_B" -&gt; "(_eval_) &amp;&amp; (FIELD_A == FIELD_B)"
 */
public class FieldToFieldComparisonVisitor extends RebuildingVisitor {
    /**
     * force evaluation for field to field comparison
     *
     * @param root
     *            the root node
     * @return a script
     */
    public static ASTJexlScript forceEvaluationOnly(JexlNode root) {
        FieldToFieldComparisonVisitor vis = new FieldToFieldComparisonVisitor();
        return (ASTJexlScript) root.jjtAccept(vis, null);
    }

    /**
     * detect identifier on both sides of nodes and wrap it with evaluation-only reference
     *
     * @param node
     *            a node
     * @return a jexl node
     */
    private JexlNode evaluationOnlyForFieldToFieldComparison(JexlNode node) {
        int identifierNodes = 0;

        // check both sides of nodes and count the nodes with identifier(s)
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (JexlASTHelper.getIdentifiers(node.jjtGetChild(i)).size() > 0) {
                identifierNodes++;
            }
        }

        JexlNode copy = copy(node);
        if (identifierNodes > 1) {
            return ASTEvaluationOnly.create(copy);
        }
        return copy;
    }

    @Override
    public Object visit(ASTEQNode node, Object data) {
        return evaluationOnlyForFieldToFieldComparison(node);
    }

    @Override
    public Object visit(ASTNENode node, Object data) {
        return evaluationOnlyForFieldToFieldComparison(node);
    }

    @Override
    public Object visit(ASTLTNode node, Object data) {
        return evaluationOnlyForFieldToFieldComparison(node);
    }

    @Override
    public Object visit(ASTGTNode node, Object data) {
        return evaluationOnlyForFieldToFieldComparison(node);
    }

    @Override
    public Object visit(ASTLENode node, Object data) {
        return evaluationOnlyForFieldToFieldComparison(node);
    }

    @Override
    public Object visit(ASTGENode node, Object data) {
        return evaluationOnlyForFieldToFieldComparison(node);
    }
}
