import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.*;
import java.util.stream.*;


/**
 * MobileDeviceKeyboardTest provides sample tests for the Candidate and
 * AutocompleteProvider classes of MobileDeviceKeyboard.
 */
public class MobileDeviceKeyboardTest {
    MobileDeviceKeyboard.AutocompleteProvider AP = new MobileDeviceKeyboard.AutocompleteProvider();
    MobileDeviceKeyboard.Candidate candidate = new MobileDeviceKeyboard.Candidate("Test");

    /**
     * An instance of the Candidate class has the word "Test".
     * testCandidateGetWord tests that the method getWord returns "Test".
     */
    @Test
    public void testCandidateGetWord () {
        assertEquals("Test", candidate.getWord());
    }

    /**
     * testCandidateGetConfidence sets and then retrieves a value
     * of 1 for the Confidence value.
     */
    @Test
    public void testCandidateGetConfidence () {
        candidate.setConfidence(1);
        assertEquals(1, candidate.getConfidence());
    }

    /**
     * testCandidateAddGetChild adds a child and a grandchild and then
     * demonstrates retrieving them with getChildren.
     */
    @Test
    public void testCandidateAddGetChild () {
        MobileDeviceKeyboard.Candidate child = new MobileDeviceKeyboard.Candidate("child");
        MobileDeviceKeyboard.Candidate grandchild = new MobileDeviceKeyboard.Candidate("grandchild");
        candidate.addChild (child);
        child.addChild (grandchild);
        ArrayList <MobileDeviceKeyboard.Candidate> children = candidate.getChildren();
        assertEquals(Stream.of ("child","grandchild").collect(Collectors.toList()),
                     Stream.of (children.get(0).getWord(), children.get(1).getWord()).collect(Collectors.toList()));
    }

    List <String> expectedResult = Stream.of("t","th","the","ther","there").collect(Collectors.toList());

    /**
     * testEdgeNgramGoodString demonstrates that all Edge N-Grams of the
     * word "There" are returned by a call to edge_ngram.
     */
    @Test
    public void testEdgeNgramGoodString () {
        assertEquals(expectedResult, AP.edge_ngram("there"));
    }

    List <String> singleLetter = Collections.singletonList("a");

    /**
     * testEdgeNgramSingleLetterWord tests that the full word "a" is returned
     * in a single-word list from a call to edge_ngram.
     */
    @Test
    public void testEdgeNgramSingleLetterWord () {
        assertEquals(singleLetter, AP.edge_ngram("a"));
    }

    List <String> emptyList = new ArrayList <> ();

    /**
     * testEdgeNgram tests that edge_ngram returns an empty list if
     * an empty string is passed in.
     */
    @Test
    public void testEdgeNgramEmptyString (){
        assertEquals(emptyList, AP.edge_ngram(""));
    }

    /**
     * testEdgeNgramNullString tests that edge_ngram returns an empty list
     * if a null is passed in for "word".
     */
    @Test
    public void testEdgeNgramNullString () {
        assertEquals(emptyList, AP.edge_ngram(null));
    }

    /**
     * testTrainAndGetWord tests storing a word via a "train" call and 
     * then retrieving the word via the getWords call.  Note that the words
     * are stored and retrieved in all lowercase.
     */
    @Test
    public void testTrainAndGetWord () {
        AP.train ("There");
        assertEquals ("there", ((ArrayList <MobileDeviceKeyboard.Candidate>) AP.getWords("There")).get(0).getWord());
    }

    /**
     * testTrainAndGetWords tests by calling "train" with a short passage,
     * then getWords with the fragment "th".  Given the passage of
     * "There are more than three words here.", the expected words are:
     * "than", "there", and "three".  This test retrieves the words from
     * the Candidate objects returned by AutocompleteProvider.getWords().
     */
    @Test
    public void testTrainAndGetWords () {
        AP.train ("There are more than three words here.");
        ArrayList <MobileDeviceKeyboard.Candidate> words = (ArrayList <MobileDeviceKeyboard.Candidate>) AP.getWords("th");
        List <String> expectedWords = Stream.of("than","there","three").collect(Collectors.toList());
        List <String> actualWords = new ArrayList <String> ();
        for (MobileDeviceKeyboard.Candidate candidate: words) {
            actualWords.add(candidate.getWord());
        }
        assertEquals (expectedWords,actualWords);
    }
}
