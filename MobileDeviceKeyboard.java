import java.io.*;
import java.util.*;


/**
 * MobileDeviceKeyboard is a class that contains the AutocompleteProvider
 * class and the Candidate class, which together provide autocompletion of
 * word fragments.
 */

public class MobileDeviceKeyboard {

    /**
     * The Candidate class provides for Objects that store a candidate
     * word or fragment of a whole word with a certain confidence level
     * and a list of children that are potential completions for a fragment.
     * 
     * Considering the list of words and word fragments as a Trie Tree,
     * the Candidate objects are the nodes of the tree.
     * 
     * @see <a href="https://en.wikipedia.org/wiki/Trie">Trie</a>
     */

    static class Candidate {
        private String word;
        private int confidence;
        private ArrayList <Candidate> children;
 
        /**
         * At present, the constructor is the only way to set the "word"
         * string, but it suffices.
         * @param wordIn - The word, fragment or whole, to be represented by
         * the Candidate.
         */
        Candidate (String wordIn) {
            word = wordIn;
            confidence = 0;
            children = new ArrayList <Candidate> ();
        }
        void setConfidence (int confidenceLevel) {
            confidence = confidenceLevel;
        }
        String getWord() {
            return word;
        }
        int getConfidence() {
            return confidence;
        }
        void addChild (Candidate candidate) {
            children.add (candidate);
        }

        /**
         *  Really the only complex method in Candidate, "getChildren" recursively
         * retrieves all of the children of a given Candidate into the new list
         * "allkids", which it returns.  The "grandkids" list is handy for getting
         * the children of children of a Candidate, which is all that is needed
         * for the recursion.
         * 
         * @return ArrayList of Candidate allkids - all of the children
         * of this Candidate recursively retrieved.
         */
        ArrayList <Candidate> getChildren () {
            ArrayList <Candidate> allkids = new ArrayList <Candidate>();
            ArrayList <Candidate> grandkids = null;
            if (!this.children.isEmpty()) {
                for (Candidate child : this.children) {
                    allkids.add (child);
                    grandkids = child.getChildren();
                    if (grandkids != null) {
                        for (Candidate kid : grandkids) {
                             allkids.add (kid);
                        }
                    }
                }
            }
            return allkids;
        }
    }

    /**
     * The Autocomplete Provider provides autocompletion candidate words based
     * on words individually read from one or more passages via the"train"
     * method.  The train method also stores the "Edge N-grams" of each word.
     * An N-gram is a contiguous sequence of n items from a given sample of
     * text.
     * 
     * In this context, an N-gram is a sequence of characters in an individual
     * word.  The Edge N-grams of a word are all of the substrings of the word
     * from the beginning (or edge) of the word for sequences from one
     * character to the full length of the word.
     * 
     * Edge N-grams that are shorter than the full word have a confidence value
     * of zero.  This serves two purposes: the role of a flag indicating
     * whether an N-gram is a full word and establishing that non-word
     * N-grams have a confidence value of zero.
     *
     * This algorithm is a simplified Trie tree of Candidates hashed by
     * Edge N-grams.
     * 
     * @see <a href="https://en.wikipedia.org/wiki/Trie">Trie</a>
     */

    static class AutocompleteProvider {

        /**
         * The "word_indices" HashMap is the master list of all Edge N-grams,
         * including full words, stored as Candidate objects.
         */
        HashMap <String, Candidate> word_indices = new HashMap <> ();

        /**
         *  The edge_ngram method returns a list of all of the Edge N-grams
         * of a word.  For example, a call to edge_ngram("thing") returns:
         * {@code ArrayList <String> ["t","th", "thi", "thin", "thing"]}
         * @param word - the word to process for all prefixes (Edge N-grams).
         * @return ArrayList of String, a list of the input word and all of
         * its prefixes.
         */
        ArrayList <String> edge_ngram (String word) {
            ArrayList <String> tokenList = new ArrayList <String>();
            if (word == null) return tokenList;
            int length = word.length();
            if (length == 0) return tokenList;
            if (length == 1) {
                tokenList.add(word);
            }
            else {
                for (int i=1; i<=length;i++) {
                    tokenList.add(word.substring(0, i));
                }
            }
            return tokenList;
        }

        /**
         *  The train method accepts a string as "passage", parses words
         * individually from the passage, and adds each word and its
         * Edge N-grams to the word_indices HashMap.  If a given word
         * or N-gram is already in word_indices then it isn't added
         * again.  All N-grams after the first, including the
         * full word, are added as a child to the prior N-gram (Candidate)
         * for later retrieval traversals.
         *
         * Finally, the full word is always the last Edge N-gram returned
         * by edge_ngram, so the train method increments the confidence level
         * for the full word.  This means that sometimes a fragment Edge N-gram
         * can become a full word and still have traversable children.
         * For example if "there" was in the passage string before "the"
         * then "the" would have a confidence level of zero after train
         * added "there" (as an Edge N-gram of "there") until train adds
         * the first occurance of "the", at which time the confidence level
         * of "the" becomes one.
         *
         * Incrementing the confidence level also makes it an occurance count
         * for full words which is used when sorting them for output.
         * @param passage - A line of text to parse for Candidate words.
         */
        void train (String passage) {
            Scanner lineScanner = new Scanner (passage);
            String word = null;
            while (lineScanner.hasNext()) {
                word = lineScanner.next();
                ArrayList <String> tokenList = edge_ngram(word.toLowerCase());
                Candidate candidate = null;
                Candidate prevCandidate = null;
                for (String token : tokenList) {
                    candidate = word_indices.get (token);
                    if (candidate == null) {
                        candidate = new Candidate (token);
                        word_indices.put (token, candidate);
                        if (prevCandidate != null) {
                            prevCandidate.addChild (candidate);
                        }
                    }
                    prevCandidate = candidate;
                }
                candidate.setConfidence(candidate.getConfidence() + 1);
            }
            lineScanner.close();
        }

        class sortByConfidenceAndWord implements Comparator<Candidate>
        {
            /**
             *  Used for sorting in descending order of
             *  Confidence and Word first by Confidence then by Word.
             *  @return value expected by Comparator.
             */
            public int compare(Candidate a, Candidate b)
            {
                int result = b.getConfidence() - a.getConfidence();
                if (result == 0) {
                    result = a.getWord().compareTo(b.getWord());
                }
                return result;
            }
        }

        /**
         * The getWords method fulfills the goal of getting all full words
         * that have "fragment" as an Edge N-gram.  In other words, getting
         * suggested completions for "fragment".
         * @param fragment - a partial word for which completions are 
         * requested.
         * 
         * getWords retrieves the Candidate that matches the fragment from
         * word_indices, adds only the full words to the words list, sorts
         * the list by Confidence and then Word, and returns the words list.
         * Note that Candidate.getChildren recursively retrieves all of the
         * children of the Candidate that matches "fragment".
         * @return ArrayList of Candidate - a list of all Candidate objects
         * representing full words that are suggested completions for
         * "fragment".
         */
        List <Candidate> getWords (String fragment) {
            // Stored confidence value for the case where fragment is also
            // a full word.
            int canConfidence = 0; 
            ArrayList <Candidate> words = null;
            Candidate candidate = word_indices.get(fragment.toLowerCase());
            if (candidate != null) {
                ArrayList <Candidate> candidates = candidate.getChildren();
                if (candidates != null) {
                    words = new ArrayList <Candidate> (candidates.size());
                    canConfidence = candidate.getConfidence();
                    if (canConfidence > 0) {
                        // Add the fragment to the return list if it's a
                        // full word.
                        words.add (candidate);
                    }
                    int maxConfidence = 0;
                    for (Candidate c : candidates) {
                        int curConfidence = c.getConfidence();
                        if (curConfidence > 0) {
                            words.add (c);
                            if (maxConfidence < curConfidence) {
                                // Track maxConfidence in case it's needed
                                // when fragment is a full word.
                                maxConfidence = curConfidence;
                            }
                        }
                    }
                    // Special handling for when candidate is a full
                    // word and a fragment.  Force the candidate to be
                    // first in the sort.
                    if (canConfidence > 0 && canConfidence < maxConfidence) {
                        candidate.setConfidence(maxConfidence + 1);
                    }
                }
            }
            words.sort (new sortByConfidenceAndWord());
            // Restore the confidence level of the candidate in
            // case it was modified for the sort.
            candidate.setConfidence(canConfidence);
            return words;
        }
    }

    /**
     * The "main" method demonstrates some ways of getting test
     * data into the AutocompleteProvider.train method and then
     * testing various fragments through the
     * AutocompleteProvider.getWords method.
     * 
     * The main method expects plain-text data files that contain
     * one or more lines of "passage" text used for training, followed
     * by a single line with the string '---' indicating training is done.
     * Then the text file should have one or more lines with one word
     * per line of "fragment" words to test word completion.
     * 
     * There are two sample input files: MobileDeviceKeyboard-input00.txt
     * and MobileDeviceKeyboard-input01.txt.  The "00" file has the
     * passage text and fragments from the problem statement.
     * The "01" file tests full words that can also be fragments and
     * the word "a" as a full word and fragment.
     * 
     * The main method prints each fragment and its corresponding
     * possible completions with confidence numbers.
     * 
     * @param args - Standard main method arguments, not used.
     */
    public static void main(String[] args) {

        Scanner in = new Scanner ("");
        //File file = new File("MobileDeviceKeyboard-input00.txt");
        File file = new File("MobileDeviceKeyboard-input01.txt");
        try {
            in.close();
            in = new Scanner(file);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Only instantiate AutocompleteProvider once.
        AutocompleteProvider AP = new AutocompleteProvider();

        // The "trained" flag is false until a line having "---" as the first
        // characters appears in the input file, indicating the training lines
        // are finished.  The rest of the input file should be test words,
        // one per line.
        boolean trained = false;
        while (in.hasNext()) {
            String line = in.nextLine();
            if (!trained) {
                if (line.startsWith("---")) {
                    System.out.println ("---");
                    trained = true;
                    continue;
                }

                // Keep only lowercase letters and " ".
                String alphabet = new String(" abcdefghijklmnopqrstuvwxyz");
                line = line.toLowerCase();

                String passage = "";
                for (int index= 0; index < line.length(); index++) {
                    if (alphabet.indexOf(line.charAt(index)) >= 0) {
                        passage = passage + line.charAt(index);
                    }
                }

                System.out.println (passage);
                AP.train (passage);
            }
            else {
                System.out.println(line);
                ArrayList <Candidate> words = (ArrayList <Candidate>)AP.getWords (line);
                if (words != null) {
                    boolean firstLine = true;
                    for (Candidate c: words) {
                        if (firstLine) {
                            System.out.print(c.getWord() + 
                                             " (" + c.getConfidence() + ")");
                            firstLine = false;
                        }
                        else {
                            System.out.print(", " + c.getWord() + 
                                             " (" + c.getConfidence() + ")");
                        }
                    }
                    System.out.println();
                }
            }
        }
        in.close();

        System.exit(0);
    }
}
