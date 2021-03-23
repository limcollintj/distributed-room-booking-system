package Test;

import Marshaller.Marshallable;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

public class MarshallableTest {


    public static class ClassForTesting extends Marshallable {

        List<Integer> integerList;
        List<List<Integer>> twoDIntegerList;
        Integer classInteger;
        int primitiveInteger;
        String string;
        String stringNull;
        Boolean classBoolean;
        boolean primitiveBoolean;
        Short classShort;
        short primitiveShort;
        Float classFloat;
        float primitiveFloat;
        Double classDouble;
        double primitiveDouble;

        public ClassForTesting() {
            super();

        }

        public ClassForTesting(List<Integer> integerList, List<List<Integer>> twoDIntegerList, Integer classInteger, int primitiveInteger, String string,
                               String stringNull, Boolean classBoolean, boolean primitiveBoolean, Short classShort,
                               short primitiveShort, Float classFloat, float primitiveFloat, Double classDouble, double primitiveDouble){
            super();
            this.integerList = integerList;
            this.twoDIntegerList = twoDIntegerList;
            this.classInteger = classInteger;
            this.primitiveInteger = primitiveInteger;
            this.string = string;
            this.stringNull = stringNull;
            this.classBoolean = classBoolean;
            this.primitiveBoolean = primitiveBoolean;
            this.classShort = classShort;
            this.primitiveShort = primitiveShort;
            this.classFloat = classFloat;
            this.primitiveFloat = primitiveFloat;
            this.classDouble = classDouble;
            this.primitiveDouble = primitiveDouble;
        }
    }

    @Test
    public void objShouldEqualUnmarshalledMarshalledObj() throws IllegalAccessException {
        final double DELTA = 1e-15;

        List<Integer> integerList = Arrays.asList(6,7,8);
        List<List<Integer>> twoDIntegerList = Arrays.asList(Arrays.asList(29,4,96),Arrays.asList(8,71,21));
        Integer classInteger = 123;
        int primitiveInteger = 8346;
        String string = "book room";
        String stringNull = null;
        Boolean classBoolean = true;
        boolean primitiveBoolean = false;
        Short classShort = 64;
        short primitiveShort = 90;
        Float classFloat = 3.14f;
        float primitiveFloat = 6.18f;
        Double classDouble = 42.8;
        double primitiveDouble = 1.08;

        ClassForTesting obj = new ClassForTesting(integerList, twoDIntegerList, classInteger, primitiveInteger, string, stringNull,
                classBoolean, primitiveBoolean, classShort, primitiveShort, classFloat, primitiveFloat, classDouble, primitiveDouble);

        List<Byte> seqBytes = obj.marshall();
        ClassForTesting result = Marshallable.unmarshall(seqBytes, ClassForTesting.class);

        assertEquals(integerList, result.integerList);
        assertEquals(twoDIntegerList, result.twoDIntegerList);
        assertEquals(classInteger, result.classInteger);
        assertEquals(primitiveInteger, result.primitiveInteger);
        assertEquals(string, result.string);
        assertEquals(stringNull, result.stringNull);
        assertEquals(classBoolean, result.classBoolean);
        assertEquals(primitiveBoolean, result.primitiveBoolean);
        assertEquals(classShort, result.classShort);
        assertEquals(primitiveShort, result.primitiveShort);
        assertEquals(classFloat, result.classFloat);
        assertEquals(primitiveFloat, result.primitiveFloat, DELTA);
        assertEquals(classDouble, result.classDouble);
        assertEquals(primitiveDouble, result.primitiveDouble, DELTA);
    }

}