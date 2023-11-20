package com;
import com.uwyn.jhighlight.fastutil.Hash;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.analysis.ro.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.*;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.lucene.store.FSDirectory;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.IOException;

import com.CustomAnalyzer;

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

        this.indexPath = Paths.get(indexPath);

        this.directory = FSDirectory.open(this.indexPath);
        this.reader = DirectoryReader.open(this.directory);

        this.analyzer = new CustomAnalyzer();


        this.searcher = new IndexSearcher(this.reader);
        this.queryParser = new QueryParser("original_content", this.analyzer);

        this.resultHist = new HashSet<>();

//        this.searchField = "original_content";
//
//        this.analyzer = new StandardAnalyzer();
//        this.parser = new QueryParser(this.searchField, this.analyzer);

    }

    public void search(String input_query) throws ParseException, IOException{
        Query query = this.queryParser.parse(input_query);
        TopDocs topDocs = this.searcher.search(query, 100);
        this.resultHist.clear();

        ScoreDoc[] hits = topDocs.scoreDocs;
        for (ScoreDoc hit : hits) {
            this.resultHist.forEach(uuid -> System.out.print( "<" + uuid + ">    "));
            displayResult(hit);
        }


    }


    public void displayResult(ScoreDoc hit) throws IOException{
        int docId = hit.doc;
        Document document = this.searcher.doc(docId);

        String cnt_uuid = document.get("uuid");

        if (!this.resultHist.contains(cnt_uuid)) {

            this.resultHist.add(cnt_uuid);

            System.out.println("\n------------------------------------------------------");
            System.out.println("Document name: " + document.get("doc_name"));
            System.out.println("Search score: " + hit.score);
            System.out.println("Document contents: ");

            String formatted_content = formatResult(document.get("original_content"));

            System.out.println(formatted_content);
        }

    }

    public static String formatResult(String text){
        String[] works = text.replace("\n", " ").split(" ");
        StringBuilder line = new StringBuilder();
        StringBuilder result = new StringBuilder();
        for (String work : works) {
            if (line.length() + work.length() > 200) {
                result.append(line).append("\n");
                line = new StringBuilder();
            }
            line.append(work).append(" ");
        }
        result.append(line);
        return result.toString();
    }

}
