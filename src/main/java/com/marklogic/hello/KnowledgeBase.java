package com.marklogic.hello;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

public class KnowledgeBase {
    private DatabaseClient db;

    @Autowired
    public KnowledgeBase(DatabaseClient db) {
        this.db = db;
    }

//    public Entry loadEntry(String id) {
//        return this.loadEntry(this.db.newDocId(id));
//    }

    protected Entry loadEntry(String id) {
        Document doc = this.db.newXMLDocumentManager().read(id, new DOMHandle()).get();
        Entry entry = new Entry();
        entry.setDescription(doc.getElementsByTagName("description").item(0).getTextContent());
        entry.setTitle(doc.getElementsByTagName("title").item(0).getTextContent());
        entry.setAuthor(doc.getElementsByTagName("author").item(0).getTextContent());
        return entry;
    }

    public List<Entry> findEntries(final String query) {

        QueryManager queryMgr = this.db.newQueryManager();
        // null == default options
        StringQueryDefinition queryDef = queryMgr.newStringDefinition(null);
        queryDef.setCriteria(query);
        SearchHandle results = queryMgr.search(queryDef, new SearchHandle());

        List<Entry> list = new ArrayList<Entry>();
        for (MatchDocumentSummary docSummary : results.getMatchResults()) {
            list.add(this.loadEntry(docSummary.getUri()));
        }
        return list;
    }

    public void storeEntry(Entry entry) {
        throw new RuntimeException("Not implemented yet");
    }

    public void destroy() {
        this.db.release();
    }

}
