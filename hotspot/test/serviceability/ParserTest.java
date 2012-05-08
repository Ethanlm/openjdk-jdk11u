/*
 * @test ParserTest
 * @summary verify that whitebox functions can be linked and executed
 * @run compile -J-XX:+UnlockDiagnosticVMOptions -J-XX:+WhiteBoxAPI ParserTest.java
 * @run main/othervm -XX:+UnlockDiagnosticVMOptions -XX:+WhiteBoxAPI ParserTest
 */

import java.math.BigInteger;

import sun.hotspot.parser.DiagnosticCommand;
import sun.hotspot.parser.DiagnosticCommand.DiagnosticArgumentType;
import sun.hotspot.WhiteBox;

public class ParserTest {
    WhiteBox wb;

    public ParserTest() throws Exception {
        wb = WhiteBox.getWhiteBox();

        testNanoTime();
        testJLong();
        testBool();
        testMemorySize();
    }

    public static void main(String... args) throws Exception  {
         new ParserTest();
    }

    public void testNanoTime() throws Exception {
        String name = "name";
        DiagnosticCommand arg = new DiagnosticCommand(name,
                "desc", DiagnosticArgumentType.NANOTIME,
                false, "0");
        DiagnosticCommand[] args = {arg};

        BigInteger bi = new BigInteger("7");
        //These should work
        parse(name, bi.toString(), name + "=7ns", args);

        bi = bi.multiply(BigInteger.valueOf(1000));
        parse(name, bi.toString(), name + "=7us", args);

        bi = bi.multiply(BigInteger.valueOf(1000));
        parse(name, bi.toString(), name + "=7ms", args);

        bi = bi.multiply(BigInteger.valueOf(1000));
        parse(name, bi.toString(), name + "=7s", args);

        bi = bi.multiply(BigInteger.valueOf(60));
        parse(name, bi.toString() , name + "=7m", args);

        bi = bi.multiply(BigInteger.valueOf(60));
        parse(name, bi.toString() , name + "=7h", args);

        bi = bi.multiply(BigInteger.valueOf(24));
        parse(name, bi.toString() , name + "=7d", args);

        parse(name, "0", name + "=0", args);

        shouldFail(name + "=7xs", args);
        shouldFail(name + "=7mms", args);
        shouldFail(name + "=7f", args);
        //Currently, only value 0 is allowed without unit
        shouldFail(name + "=7", args);
    }

    public void testJLong() throws Exception {
        String name = "name";
        DiagnosticCommand arg = new DiagnosticCommand(name,
                "desc", DiagnosticArgumentType.JLONG,
                false, "0");
        DiagnosticCommand[] args = {arg};

        wb.parseCommandLine(name + "=10", args);
        parse(name, "10", name + "=10", args);
        parse(name, "-5", name + "=-5", args);

        //shouldFail(name + "=12m", args); <-- should fail, doesn't
    }

    public void testBool() throws Exception {
        String name = "name";
        DiagnosticCommand arg = new DiagnosticCommand(name,
                "desc", DiagnosticArgumentType.BOOLEAN,
                false, "false");
        DiagnosticCommand[] args = {arg};

        parse(name, "true", name + "=true", args);
        parse(name, "false", name + "=false", args);
        parse(name, "true", name, args);

        //Empty commandline to parse, tests default value
        //of the parameter "name"
        parse(name, "false", "", args);
    }

    public void testMemorySize() throws Exception {
        String name = "name";
        String defaultValue = "1024";
        DiagnosticCommand arg = new DiagnosticCommand(name,
                "desc", DiagnosticArgumentType.MEMORYSIZE,
                false, defaultValue);
        DiagnosticCommand[] args = {arg};

        BigInteger bi = new BigInteger("7");
        parse(name, bi.toString(), name + "=7b", args);

        bi = bi.multiply(BigInteger.valueOf(1024));
        parse(name, bi.toString(), name + "=7k", args);

        bi = bi.multiply(BigInteger.valueOf(1024));
        parse(name, bi.toString(), name + "=7m", args);

        bi = bi.multiply(BigInteger.valueOf(1024));
        parse(name, bi.toString(), name + "=7g", args);
        parse(name, defaultValue, "", args);

        //shouldFail(name + "=7gg", args); <---- should fail, doesn't
        //shouldFail(name + "=7t", args);  <----- should fail, doesn't
    }

    public void parse(String searchName, String expectedValue,
            String cmdLine, DiagnosticCommand[] argumentTypes) throws Exception {
        //parseCommandLine will return an object array that looks like
        //{<name of parsed object>, <of parsed object> ... }
        Object[] res = wb.parseCommandLine(cmdLine, argumentTypes);
        for (int i = 0; i < res.length-1; i+=2) {
            String parsedName = (String) res[i];
            if (searchName.equals(parsedName)) {
                String parsedValue = (String) res[i+1];
                if (expectedValue.equals(parsedValue)) {
                    return;
                } else {
                    throw new Exception("Parsing of cmdline '" + cmdLine + "' failed!\n"
                            + searchName + " parsed as " + parsedValue
                            + "! Expected: " + expectedValue);
                }
            }
        }
        throw new Exception(searchName + " not found as a parsed Argument!");
    }

    private void shouldFail(String argument, DiagnosticCommand[] argumentTypes) throws Exception {
        try {
            wb.parseCommandLine(argument, argumentTypes);
            throw new Exception("Parser accepted argument: " + argument);
        } catch (IllegalArgumentException e) {
            //expected
        }
    }
}
