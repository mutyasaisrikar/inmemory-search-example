package com.srikar.inmemorysearchexample.infrastructure.lucene.query;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;

import java.util.List;

import static com.google.common.base.CharMatcher.whitespace;
import static com.google.common.base.Splitter.on;
import static com.srikar.inmemorysearchexample.infrastructure.lucene.LuceneUtils.sanitise;
import static java.lang.Math.pow;
import static org.apache.lucene.search.BooleanClause.Occur.MUST;
import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;
import static org.jooq.lambda.Seq.seq;

public class LuceneQueryBuilder {

    public static final String STATE_SHORT_FORM_KEY = "stateShortForm";
    public static final String STATE_KEY = "state";
    public static final String STATE_TOKENISED = "stateTokenised";

    public Query buildQuery(String inputState) {

        List<String> allTerms = seq(
            on(whitespace())
                .trimResults()
                .omitEmptyStrings()
                .split(sanitise(inputState))
        ).toList();

        Builder queryParts = new Builder();
        queryParts.add(new BoostQuery(new TermQuery(new Term(STATE_SHORT_FORM_KEY, sanitise(inputState))), 30), SHOULD);
        int total = 3;

        for(int termCount = allTerms.size(); termCount > 0; termCount--) {

            int queryPosition = 0;
            String queryString = seq(allTerms).limit(termCount).toString(" ");

            queryParts.add(new BoostQuery(new TermQuery(new Term(STATE_KEY, queryString)), calculateBoost(termCount, queryPosition++, total)), SHOULD);
            queryParts.add(new BoostQuery(new PrefixQuery(new Term(STATE_KEY, queryString)), calculateBoost(termCount, queryPosition++, total)), SHOULD);
            queryParts.add(new BoostQuery(new WildcardQuery(new Term(STATE_KEY, "*" + queryString + "*")), calculateBoost(termCount, queryPosition, total)), SHOULD);
            queryParts.add(new FuzzyQuery(new Term(STATE_TOKENISED, queryString), 2), SHOULD);
        }
        return new Builder().add(queryParts.build(), MUST).build();
    }

    private float calculateBoost(int termCount, int queryPosition, int totalQueryparts) {
        return (float) pow(2, (termCount * totalQueryparts) - queryPosition);
    }
}
