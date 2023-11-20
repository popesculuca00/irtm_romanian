import com.Indexer;
import com.Searcher;

import org.apache.commons.cli.*;
import org.apache.lucene.queryparser.classic.ParseException;

public class Main {
    public static void main(String[] args) throws Exception{

        Options options = new Options();
        Option indexOption = Option.builder("index")
                .optionalArg(true)
                .desc("Index documents from a specified path. If not specified, the default path will be `dataset\\`")
                .build();
        options.addOption(indexOption);

        Option searchOption = Option.builder("search")
                .hasArg()
                .desc("Searches documents for query string")
                .build();
        options.addOption(searchOption);

        Option searchNumOption = Option.builder("n")
                .hasArg()
                .desc("Maximum number of documents to retrieve from a search")
                .build();
        options.addOption(searchNumOption);

        CommandLineParser parser = new DefaultParser();

        Integer n = 3;
        try{
            CommandLine cmd = parser.parse(options, args);

            if(cmd.hasOption("n")){
                n = Integer.parseInt(cmd.getOptionValue("n"));
            }

            if(cmd.hasOption("index")){
                String filesToIndexPath = cmd.getOptionValue("index");
                Indexer.createIndex(filesToIndexPath, "index");
            }

            if(cmd.hasOption("search")){
                String searchQuery = cmd.getOptionValue("search");
                System.out.println("Searching for: " + searchQuery);
                Searcher indexSearcher = new Searcher();
                indexSearcher.search(searchQuery, n);
            }

        } catch(ParseException e){
            System.err.println("Error parsing arguments: " +
                                e.getMessage()
                    );
        }

    }

}