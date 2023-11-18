import com.Indexer;
import com.Searcher;
import com.CustomRomanianAnalyzer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;



public class Main {
    public static void main(String[] args) throws Exception, IOException, ParseException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching

//        Indexer idx = new Indexer;


//        Directory index = new ByteBuffersDirectory();

        Indexer.CreateIndex(); // the important stuff

        Searcher indexSearcher = new Searcher();

        indexSearcher.search("Doamna Calinichia ");

//        CustomRomanianAnalyzer analyzer = new CustomRomanianAnalyzer();

//        RomanianAnalyzer analyzer = new RomanianAnalyzer();
//
//        w.close();
//
//        int hitsPerPage = 10;
//
//        String querystr = args.length > 0 ? args[0] : "Principatul";
//        Query q = new QueryParser("original_content", analyzer).parse(querystr);
//        IndexReader reader = DirectoryReader.open(index);
//        IndexSearcher searcher = new IndexSearcher(reader);
//
//        TopDocs docs = searcher.search(q, hitsPerPage);
//        ScoreDoc[] hits = docs.scoreDocs;
//
//        // 4. display results
//        System.out.println("Found " + hits.length + " hits.");
//        for(int i=0; i<hits.length; i++) {
//            int docId = hits[i].doc;
//            Document d = searcher.doc(docId);
//            System.out.println((i + 1) + ". " + d.get("original_content"));
//        }



//        // 1. create the index
//        Directory index = new ByteBuffersDirectory();
//
//        IndexWriterConfig config = new IndexWriterConfig(analyzer);
//
//        IndexWriter w = new IndexWriter(index, config);
//        addDoc(w, "Lucene in Action", "193398817");
//        addDoc(w, "Lucene for Dummies", "55320055Z");
//        addDoc(w, "Managing Gigabytes", "55063554A");
//        addDoc(w, "The Art of Computer Science", "9900333X");
//        w.close();
//
//        // 2. query
//        String querystr = args.length > 0 ? args[0] : "lucene";
//
//        // the "title" arg specifies the default field to use
//        // when no field is explicitly specified in the query.
//        Query q = new QueryParser("title", analyzer).parse(querystr);
//
//        // 3. search
//        int hitsPerPage = 10;
//        IndexReader reader = DirectoryReader.open(index);
//        IndexSearcher searcher = new IndexSearcher(reader);
//        TopDocs docs = searcher.search(q, hitsPerPage);
//        ScoreDoc[] hits = docs.scoreDocs;
//
//        // 4. display results
//        System.out.println("Found " + hits.length + " hits.");
//        for(int i=0; i<hits.length; i++) {
//            int docId = hits[i].doc;
//            Document d = searcher.doc(docId);
//            System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
//        }
//
//        // reader can only be closed when there
//        // is no need to access the documents any more.
//        reader.close();
//


    }

    private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));

        // use a string field for isbn because we don't want it tokenized
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        w.addDocument(doc);
    }
}