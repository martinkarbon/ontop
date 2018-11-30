package it.unibz.inf.ontop.model.term.functionsymbol.impl;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.inject.Inject;
import it.unibz.inf.ontop.exception.MinorOntopInternalBugException;
import it.unibz.inf.ontop.model.term.functionsymbol.*;
import it.unibz.inf.ontop.model.type.DBTermType;
import it.unibz.inf.ontop.model.type.RDFDatatype;
import it.unibz.inf.ontop.model.type.TypeFactory;
import org.apache.commons.rdf.api.RDF;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class FunctionSymbolFactoryImpl implements FunctionSymbolFactory {

    private static final String BNODE_PREFIX = "_:ontop-bnode-";
    private static final String PLACEHOLDER = "{}";

    private final TypeFactory typeFactory;
    private final RDFTermFunctionSymbol rdfTermFunctionSymbol;
    private final BooleanFunctionSymbol isARDFFunctionSymbol;
    private final DBFunctionSymbolFactory dbFunctionSymbolFactory;
    private final Map<String, IRIStringTemplateFunctionSymbol> iriTemplateMap;
    private final Map<String, BnodeStringTemplateFunctionSymbol> bnodeTemplateMap;
    private final ImmutableTable<String, Integer, SPARQLFunctionSymbol> sparqlFunctionTable;

    // NB: Multi-threading safety is NOT a concern here
    // (we don't create fresh bnode templates for a SPARQL query)
    private final AtomicInteger counter;

    @Inject
    private FunctionSymbolFactoryImpl(TypeFactory typeFactory, DBFunctionSymbolFactory dbFunctionSymbolFactory,
                                      RDF rdfFactory) {
        this.typeFactory = typeFactory;
        this.rdfTermFunctionSymbol = new RDFTermFunctionSymbolImpl(
                typeFactory.getDBTypeFactory().getDBStringType(),
                typeFactory.getMetaRDFTermType());
        this.dbFunctionSymbolFactory = dbFunctionSymbolFactory;
        this.iriTemplateMap = new HashMap<>();
        this.bnodeTemplateMap = new HashMap<>();
        this.counter = new AtomicInteger();

        DBTermType dbBooleanType = typeFactory.getDBTypeFactory().getDBBooleanType();
        this.isARDFFunctionSymbol = new IsARDFTermTypeFunctionSymbolImpl(typeFactory.getMetaRDFTermType(), dbBooleanType);

        this.sparqlFunctionTable = createSPARQLFunctionSymbolTable(rdfFactory, typeFactory, isARDFFunctionSymbol,
                dbFunctionSymbolFactory);
    }

    private static ImmutableTable<String, Integer, SPARQLFunctionSymbol> createSPARQLFunctionSymbolTable(
            RDF rdfFactory, TypeFactory typeFactory, BooleanFunctionSymbol isARDFFunctionSymbol,
            DBFunctionSymbolFactory dbFunctionSymbolFactory) {
        RDFDatatype xsdString = typeFactory.getXsdStringDatatype();

        ImmutableSet<SPARQLFunctionSymbol> functionSymbols = ImmutableSet.of(
            new UcaseSPARQLFunctionSymbolImpl(rdfFactory, xsdString, isARDFFunctionSymbol, dbFunctionSymbolFactory)
        );

        ImmutableTable.Builder<String, Integer, SPARQLFunctionSymbol> tableBuilder = ImmutableTable.builder();

        for(SPARQLFunctionSymbol functionSymbol : functionSymbols) {
            tableBuilder.put(functionSymbol.getOfficialName(), functionSymbol.getArity(), functionSymbol);
        }
        return tableBuilder.build();
    }


    @Override
    public RDFTermFunctionSymbol getRDFTermFunctionSymbol() {
        return rdfTermFunctionSymbol;
    }

    @Override
    public IRIStringTemplateFunctionSymbol getIRIStringTemplateFunctionSymbol(String iriTemplate) {
        return iriTemplateMap
                .computeIfAbsent(iriTemplate,
                        t -> IRIStringTemplateFunctionSymbolImpl.createFunctionSymbol(t, typeFactory));
    }

    @Override
    public BnodeStringTemplateFunctionSymbol getBnodeStringTemplateFunctionSymbol(String bnodeTemplate) {
        return bnodeTemplateMap
                .computeIfAbsent(bnodeTemplate,
                        t -> BnodeStringTemplateFunctionSymbolImpl.createFunctionSymbol(t, typeFactory));
    }

    @Override
    public BnodeStringTemplateFunctionSymbol getFreshBnodeStringTemplateFunctionSymbol(int arity) {
       String bnodeTemplate = IntStream.range(0, arity)
               .boxed()
               .map(i -> PLACEHOLDER)
               .reduce(
                       BNODE_PREFIX + counter.incrementAndGet(),
                       (prefix, suffix) -> prefix + "/" + suffix);

       return getBnodeStringTemplateFunctionSymbol(bnodeTemplate);
    }

    @Override
    public DBFunctionSymbolFactory getDBFunctionSymbolFactory() {
        return dbFunctionSymbolFactory;
    }

    @Override
    public BooleanFunctionSymbol isARDFTermTypeFunctionSymbol() {
        return isARDFFunctionSymbol;
    }

    @Override
    public SPARQLFunctionSymbol getUCase() {
        return getRequiredSPARQLFunctionSymbol("http://www.w3.org/2005/xpath-functions#upper-case", 1);
    }

    @Override
    public Optional<SPARQLFunctionSymbol> getSPARQLFunctionSymbol(String officialName, int arity) {
        return Optional.ofNullable(sparqlFunctionTable.get(officialName, arity));
    }

    protected SPARQLFunctionSymbol getRequiredSPARQLFunctionSymbol(String officialName, int arity) {
        return getSPARQLFunctionSymbol(officialName, arity)
                .orElseThrow(() -> new MinorOntopInternalBugException(
                        String.format("Not able to get the SPARQL function %s with arity %d", officialName, arity)));
    }
}
