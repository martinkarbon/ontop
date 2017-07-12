package it.unibz.inf.ontop.owlrefplatform.core.optimization;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.iq.IntermediateQuery;
import it.unibz.inf.ontop.iq.node.ConstructionNode;
import it.unibz.inf.ontop.iq.node.ImmutableQueryModifiers;
import it.unibz.inf.ontop.iq.node.QueryNode;
import it.unibz.inf.ontop.iq.node.impl.ImmutableQueryModifiersImpl;
import it.unibz.inf.ontop.iq.proposal.ConstructionNodeCleaningProposal;
import it.unibz.inf.ontop.iq.proposal.impl.ConstructionNodeCleaningProposalImpl;

import java.util.Optional;


/**
 * Gets rid of unnecessary ConstructionNodes.
 * <p>
 * The algorithm searches the query q depth-first.
 * <p>
 * When a ConstructionNode is c_1 encountered,
 * find the highest value i such that:
 * .c_1, .., c_n is a chain composed of ConstructionNodes only,
 * with c1 as root,
 * c2 as child of c_1,
 * c3 child of c_2,
 * etc., and
 * .c_2, .., c_i have empty substitutions
 * <p>
 * Note that n = 1 may hold.
 * <p>
 * Then proceed as follows:
 * .combine all query modifiers found in c_1 to c_n,
 * and assign the combined modifiers to c_1
 * .After this,
 * let c' = c_2 if c_1 has a nonempty substitution or query modifiers,
 * and c' = c_1 otherwise.
 * Then replace in q the subtree rooted in c' by the subtree rooted in the child of c_n.
 * <p>
 * TODO: make it more robust (handle complex substitutions, modifiers) ?
 */
public class ConstructionNodeCleaner extends NodeCentricDepthFirstOptimizer<ConstructionNodeCleaningProposal> {


    public ConstructionNodeCleaner() {
        super(false);
    }

    @Override
    protected Optional<ConstructionNodeCleaningProposal> evaluateNode(QueryNode node, IntermediateQuery query) {
        if (node instanceof ConstructionNode) {
            ConstructionNode castNode = (ConstructionNode) node;
            Optional<ImmutableQueryModifiers> optModifiers = castNode.getOptionalModifiers();
            ImmutableQueryModifiers modifiers = optModifiers.isPresent() ?
                    optModifiers.get() :
                    new ImmutableQueryModifiersImpl(
                            false,
                            -1,
                            -1,
                            ImmutableList.of()
                    );
            return makeProposal(
                    query,
                    castNode,
                    modifiers,
                    castNode,
                    query.getFirstChild(castNode).get()
            );
        }
        return Optional.empty();
    }

    private Optional<ConstructionNodeCleaningProposal> makeProposal(IntermediateQuery query,
                                                                    ConstructionNode constructionNodeChainRoot,
                                                                    ImmutableQueryModifiers modifiers,
                                                                    ConstructionNode currentParentNode,
                                                                    QueryNode currentChildNode) {

        if (currentChildNode instanceof ConstructionNode) {
            ConstructionNode castChild = (ConstructionNode) currentChildNode;
            if (castChild.getSubstitution().isEmpty()) {
                Optional<ImmutableQueryModifiers> combinedModifiers = combineModifiers(
                        modifiers,
                        castChild.getOptionalModifiers()
                );
                if (combinedModifiers.isPresent()) {
                    return makeProposal(
                            query,
                            constructionNodeChainRoot,
                            combinedModifiers.get(),
                            castChild,
                            query.getFirstChild(castChild).get()
                    );
                }
            }
        }

        boolean deleteConstructionNodeChain = modifiers.isIdle() &&
                constructionNodeChainRoot.getSubstitution().isEmpty() &&
                !constructionNodeChainRoot.equals(query.getRootConstructionNode());

        /* special case of a non-deletable unary chain */
        if (currentParentNode.equals(constructionNodeChainRoot) && !deleteConstructionNodeChain) {
            return Optional.empty();
        }

        return Optional.of(
                new ConstructionNodeCleaningProposalImpl(
                        constructionNodeChainRoot,
                        modifiers.isIdle() ?
                                Optional.empty() :
                                Optional.of(modifiers),
                        currentChildNode,
                        deleteConstructionNodeChain
                ));
    }

    private Optional<ImmutableQueryModifiers> combineModifiers(ImmutableQueryModifiers parentModifiers,
                                                               Optional<ImmutableQueryModifiers> optChildModifiers) {
        if (optChildModifiers.isPresent()) {
            return ImmutableQueryModifiersImpl.merge(
                    parentModifiers,
                    optChildModifiers.get()
            );
        }
        return Optional.of(parentModifiers);
    }
}
