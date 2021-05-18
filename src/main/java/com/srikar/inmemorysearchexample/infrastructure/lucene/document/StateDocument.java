package com.srikar.inmemorysearchexample.infrastructure.lucene.document;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;
import org.jooq.lambda.Seq;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.srikar.inmemorysearchexample.infrastructure.lucene.query.LuceneQueryBuilder.STATE_KEY;
import static com.srikar.inmemorysearchexample.infrastructure.lucene.query.LuceneQueryBuilder.STATE_SHORT_FORM_KEY;
import static com.srikar.inmemorysearchexample.infrastructure.lucene.query.LuceneQueryBuilder.STATE_TOKENISED;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.lucene.document.Field.Store.NO;
import static org.apache.lucene.document.Field.Store.YES;
import static org.jooq.lambda.Seq.of;
import static org.jooq.lambda.Seq.seq;

@RequiredArgsConstructor
public class StateDocument implements Iterable<IndexableField> {

    private final String shortForm;
    private final String state;

    @Override
    public Iterator<IndexableField> iterator() {

        ImmutableList.Builder<IndexableField> fields = ImmutableList.builder();
        of(
            new TextField(STATE_SHORT_FORM_KEY, shortForm, NO),
            new TextField(STATE_KEY, state, YES)
        )
            .append(
                seq(analyze(state, new StandardAnalyzer()))
                    .map(term -> new TextField(STATE_TOKENISED, term, YES))
            ).forEach(fields::add);
        return fields.build().iterator();
    }

    @SneakyThrows
    public List<String> analyze(String text, Analyzer analyzer) {
        List<String> results = new ArrayList<>();
        TokenStream tokenStream = analyzer.tokenStream(STATE_TOKENISED, text);
        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while(tokenStream.incrementToken()) {
            results.add(attr.toString());
        }
        return results;
    }
}
