package it.unibz.inf.ontop.model.term.impl;

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.model.term.functionsymbol.BooleanFunctionSymbol;
import it.unibz.inf.ontop.model.term.GroundFunctionalTerm;
import it.unibz.inf.ontop.model.term.GroundTerm;

public class GroundExpressionImpl extends ImmutableExpressionImpl implements GroundFunctionalTerm {

    protected GroundExpressionImpl(BooleanFunctionSymbol functor, GroundTerm... terms) {
        super(functor, terms);
    }

    protected GroundExpressionImpl(BooleanFunctionSymbol functor, ImmutableList<? extends GroundTerm> terms) {
        super(functor, terms);
    }

    @Override
    public ImmutableList<? extends GroundTerm> getTerms() {
        return (ImmutableList<? extends GroundTerm>)super.getTerms();
    }

    @Override
    public boolean isGround() {
        return true;
    }

    @Override
    public boolean isVar2VarEquality() {
        return false;
    }
}
