package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.FilterProcessor;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService;

import java.io.IOException;
import java.util.List;

public class FilterProcessingNode extends UnaryOperationNode {
    protected String column;
    protected String condition;
    protected boolean equal;

    public FilterProcessingNode(String id, String group, String operation, String column, String condition, boolean equal) {
        super(id, group, operation);
        this.column = column;
        this.condition = condition;
        this.equal = equal;
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        super.process(csvData);

        CorrespondingCSV csv = ColumnNameService.deduceColumnName(column, csvData);

        return FilterProcessor.filter(csv.getCsv(), csv.getColumnName(), condition, equal);
    }

//    public static void main(String... args) {
//        try {
//            Object obj = testEvaluateNashorn();
//            System.out.println(obj);
//        } catch (ScriptException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static Object testEvaluateNashorn() throws ScriptException {
//        ScriptEngineManager m = new ScriptEngineManager();
//        ScriptEngine e = m.getEngineByName("nashorn");
////        return e.eval("1 < 3 && ( \"2\" == 2.0 || 3 === 2 ) && \"fight me\".startsWith(\"fight\")");
//
//
//        //no timeout, sleep, eval
//        //no for, while, do while
//        //work out the deduced column names and positions to replace..
//
//        String code = ("let () {" +
//                "while (true) {}" +
//                "}" +
//                "" +
//                "hello()");//.replaceAll("\\s", "");
////        if (
////            code.toLowerCase().contains("timeout") ||
////            code.toLowerCase().contains("sleep") ||
////            code.toLowerCase().contains("eval") ||
////            code.toLowerCase().contains("for(") ||
////            code.toLowerCase().contains("while(") ||
////            code.toLowerCase().contains("do{") ||
////            code.toLowerCase().contains("switch(")
////        ) {
////            throw new IllegalArgumentException("Filter conditions may only evaluate boolean expressions");
////        }
//
//        /*
//        function hello() {
//
//         */
//
//        return e.eval(code);
//    }
//
//    private static String validateFilterExpression() {
//        //one expression => ie one ; or
//
//        //a single expression is `(` `!` `!` "field" [ ) | SPACE operand SPACE["field" | value ]]
//        //recursively check each field is
//
//        //    ((() && ()) || (!()))
//
//
//        // (                            or "                            or !
//        // ( or " or !
//
//        return "";
//    }
//
////    private static final Map<State, Function<Character, State>> TURING_MACHINE = new HashMap<>();
////
////    static {
////        TURING_MACHINE.put(State.START, c -> {
////            switch (c) {
////                case '(':
////                    return State.BRACKET_OPEN;
////                case '!':
////                    return State.BRACKET_OPEN;
////                case '"':
////                    return State.FIELD_NAME;
////                default:
////                    return State.REJECT;
////            }
////        });
////    }
////
////    private enum State {
////        ACCEPT,
////        AND_END,
////        AND_START,
////        BOOLEAN_CAST,
////        BRACKET_OPEN,
////        BRACKET_CLOSE,
////        FIELD_NAME_START,
////        FIELD_NAME,
////        FIELD_NAME_END,
////        NOT,
////        OR_END,
////        OR_START,
////        START,
////        REJECT
////    }
}
