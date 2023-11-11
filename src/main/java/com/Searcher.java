package com;
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

public class Searcher {
    private Path indexPath;
    public Searcher() throws Exception{
        this("index/");
    }
    public Searcher(String indexPath) throws Exception{
        this.indexPath = Paths.get(indexPath);

        Directory directory = FSDirectory.open(this.indexPath);
        IndexReader reader = DirectoryReader.open(directory);
//        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));


        IndexSearcher searcher = new IndexSearcher(reader);

//        this.searchField = "original_content";
//
//        this.analyzer = new StandardAnalyzer();
//        this.parser = new QueryParser(this.searchField, this.analyzer);

    }

    public void search(String query){

    }

}