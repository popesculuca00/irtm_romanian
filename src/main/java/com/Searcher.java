package com;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class Searcher {
    private Path indexPath;
    private Directory directory;
    private IndexReader reader;
    private CustomAnalyzer analyzer;
    private IndexSearcher searcher;
    private QueryParser queryParser;
    private Set<String> resultHist;

    public Searcher() throws Exception{
        this("index/");
    }

    public Searcher(String indexPath) throws Exception{
        try {
            this.indexPath = Paths.get(indexPath);
        } catch(Exception e){
            System.out.println("Invalid index directory, switching to default 'index'");
            this.indexPath = Paths.get("index/");
        }

        this.directory = FSDirectory.open(this.indexPath);
        this.reader = DirectoryReader.open(this.directory);
        this.analyzer = new CustomAnalyzer();
        this.searcher = new IndexSearcher(this.reader);
        this.queryParser = new QueryParser("original_content", this.analyzer);
        this.resultHist = new HashSet<>();
    }


    public void search(String input_query, Integer n) throws ParseException, IOException{
        Query query = this.queryParser.parse(input_query);
        TopDocs topDocs = this.searcher.search(query, n);
        this.resultHist.clear();

        ScoreDoc[] hits = topDocs.scoreDocs;
        for (ScoreDoc hit : hits) {
            displayResult(hit);
        }
    }


    public void displayResult(ScoreDoc hit) throws IOException{
        int docId = hit.doc;
        Document document = this.searcher.doc(docId);

        String cnt_uuid = document.get("uuid");
        System.out.println("\n------------------------------------------------------");
        if (!this.resultHist.contains(cnt_uuid)) {
            this.resultHist.add(cnt_uuid);
            System.out.println("Document name: " + document.get("doc_name"));
            System.out.println("Search score: " + hit.score);
            System.out.println("Document contents: ");

            String formatted_content = formatResult(document.get("original_content"));
            System.out.println(formatted_content);
            System.out.println("------------------------------------------------------\n");

        }

    }


    public static String formatResult(String text){
        String[] inpStr = text.replace("\n", " ").split(" ");
        StringBuilder ln = new StringBuilder();
        StringBuilder res = new StringBuilder();
        for (String work : inpStr) {
            if (ln.length() + work.length() > 200) {
                res.append(ln).append("\n");
                ln = new StringBuilder();
            }
            ln.append(work).append(" ");
        }
        res.append(ln);
        return res.toString();
    }
}
