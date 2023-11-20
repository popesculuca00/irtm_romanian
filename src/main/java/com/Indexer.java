package com;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenStream;
import java.io.FileReader;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;


import org.apache.lucene.document.TextField;

import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.UUID;
import java.nio.file.Paths;


@SuppressWarnings({
        "serial", "deprecation",
        "rawtypes", "unchecked"})
public class Indexer {

    public static void CreateIndex() throws IOException, ParseException {
        CreateIndex("dataset", "index");
    }
    public static void CreateIndex(String path, String index_path) throws IOException, ParseException {

        if(path == null){
            System.out.println("Source files path can't be null. Changing to default path `dataset`");
            path = "dataset";
        }

        if(index_path == null){
            System.out.println("Index path can't be null. Changing to default path `index`");
            index_path = "index";
        }
        clearIndexDirectory(Paths.get(index_path));

        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog","fatal");

        File directory_src = new File(path);
        Directory idx_directory = FSDirectory.open(Paths.get(index_path));

        if(!directory_src.isDirectory()){
            System.out.println("Directory " + directory_src + " is not a valid directory");
        }

        System.out.println("Directory is valid, indexing files");
        File[] files = directory_src.listFiles();

        CustomAnalyzer analyzer = new CustomAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(idx_directory, config);

        for(File file : files){
            w = AddDocumentToIndex(w, file);
        }
        w.commit();
        w.close();
    }


    private static void clearIndexDirectory(Path directoryPath) throws IOException{
        EnumSet<FileVisitOption> options = EnumSet.noneOf(FileVisitOption.class);
        Files.walkFileTree(directoryPath, options, Integer.MAX_VALUE, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException{
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
        });

    }


    private static IndexWriter AddDocumentToIndex(IndexWriter w, File file) throws IOException{
        System.out.println("Adding file " + file.getName() + " to index.");
        String uuid = UUID.randomUUID().toString();

        String doc_name = file.getName().replaceFirst("[.][^.]+$", "");
        String doc_contents = ReadDocument(file);

        Document doc = new Document();
        doc.add(new TextField("original_content",  doc_contents, Field.Store.YES));
        doc.add(new StringField("doc_name", doc_name, Field.Store.YES));
        doc.add(new StringField("uuid", uuid, Field.Store.YES));
        w.addDocument(doc);

        return w;
    }


    private static String ReadDocument(File file){
        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();

        try (FileInputStream inputStream = new FileInputStream(file)) {
            parser.parse(inputStream, handler, metadata, context);
        } catch (IOException | TikaException | SAXException e) {
            if (!e.toString().contains("org.apache.tika.parser.pdf.PDFParser")) {
                e.printStackTrace();
            }
        }
        String result = handler.toString();
        result = ConvertToUTF8(result);
        return result;
    }

    private static String ConvertToUTF8(String input) {
        byte[] originalBytes = input.getBytes(StandardCharsets.UTF_8);
        String originalString = new String(originalBytes, StandardCharsets.UTF_8);

        if (originalString.equals(input)) {
            return input;
        }

        return new String(input.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }

    private static void printAnalyzedContents(Analyzer analyzer, File file) throws IOException {
        TokenStream tokenStream = analyzer.tokenStream("contents", new FileReader(file));

        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

        System.out.println("Analyzed contents for file: " + file.getName());
        try {
            tokenStream.reset();
            Integer x = 0;
            while (tokenStream.incrementToken()) {
                System.out.print(charTermAttribute.toString() + " ");
                if (++x > 20){
                    System.out.println("");
                    x = 0;
                }
            }
            tokenStream.end();
        } finally {
            tokenStream.close();
        }

    }



}
