package com.srikar.inmemorysearchexample.infrastructure.lucene;

import com.srikar.inmemorysearchexample.domain.State;
import com.srikar.inmemorysearchexample.infrastructure.lucene.document.StateDocument;
import com.srikar.inmemorysearchexample.infrastructure.lucene.query.LuceneQueryBuilder;
import lombok.SneakyThrows;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.MMapDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.srikar.inmemorysearchexample.infrastructure.lucene.query.LuceneQueryBuilder.STATE_KEY;
import static java.nio.file.Paths.get;
import static org.apache.lucene.search.SortField.FIELD_SCORE;
import static org.jooq.lambda.Seq.of;
import static org.jooq.lambda.Seq.seq;
import static org.jooq.lambda.Unchecked.function;

@Component
public class LuceneStateSearchRepository {

    private static final String INDEX_NAME = "state-search-lucene-index";

    private final IndexWriter indexWriter;
    private final Integer maxsearchResults;
    private final SearcherManager searcherManager;
    private final LuceneQueryBuilder luceneQueryBuilder;

    public LuceneStateSearchRepository(@Value("${state.search.max.results:5}") Integer maxsearchResults) {
        this.indexWriter = indexWriter();
        this.maxsearchResults = maxsearchResults;
        this.searcherManager = searcherManager(indexWriter);
        this.luceneQueryBuilder = new LuceneQueryBuilder();
        clearIndex();
    }

    @SneakyThrows
    private void clearIndex() {
        indexWriter.deleteAll();
        indexWriter.commit();
    }

    @SneakyThrows
    private SearcherManager searcherManager(IndexWriter indexWriter) {
        return new SearcherManager(indexWriter, true, false, null);
    }

    @SneakyThrows
    private IndexWriter indexWriter() {
        return new IndexWriter(
            new MMapDirectory(get(INDEX_NAME)),
            new IndexWriterConfig(new StandardAnalyzer())
        );
    }

    @SneakyThrows
    public void save(Collection<State> states) {
        seq(states)
            .forEach(this::addDocumentToIndex);
        searcherManager.maybeRefreshBlocking();
    }

    @SneakyThrows
    private void addDocumentToIndex(State state) {
        indexWriter.addDocument(new StateDocument(state.getShortForm(), state.getState()));
    }

    public Set<String> search(String state) throws IOException {

        IndexSearcher indexSearcher = searcherManager.acquire();
        try {
            Query query = luceneQueryBuilder.buildQuery(state);
            TopFieldDocs topFieldDocs = indexSearcher.search(
                query,
                maxsearchResults,
                new Sort(FIELD_SCORE),
                false
            );

            return
                of(topFieldDocs.scoreDocs)
                    .map(function(scoreDoc -> indexSearcher.doc(scoreDoc.doc)))
                    .map(document -> document.get(STATE_KEY))
                    .toCollection(LinkedHashSet::new);

        } finally {
            searcherManager.release(indexSearcher);
        }
    }
}
