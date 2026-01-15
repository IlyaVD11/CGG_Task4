package tests.ObjReader;

import com.cg.math.Vector3f;
import com.cg.objreader.ObjReaderException;
import com.cg.objreader.ObjReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

class ObjReaderTest {

    @Test
    public void testParseVertex01() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.01", "1.02", "1.03"));
        // Метод теперь возвращает твой Vector3f
        Vector3f result = ObjReader.parseVertex(wordsInLineWithoutToken, 5);
        Vector3f expectedResult = new Vector3f(1.01f, 1.02f, 1.03f);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testParseVertex02() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("ab", "o", "ba"));
        Assertions.assertThrows(ObjReaderException.class, () -> {
            ObjReader.parseVertex(wordsInLineWithoutToken, 10);
        });
    }

    @Test
    public void testParseFaceWithEmptyTexture() {
        // Проверяем формат f v//vn
        ArrayList<String> words = new ArrayList<>(Arrays.asList("1//1", "2//2", "3//3"));
        // Мы просто проверяем, что это не вызывает исключений
        Assertions.assertDoesNotThrow(() -> ObjReader.parseFace(words, 1));
    }
}