import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;

public class VoiceAssistant {

    public static void main(String[] args) {

        Configuration config = new Configuration();

        config.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        config.setDictionaryPath("src/main/resources/6081.dic");  // Changed path separator to forward slash
        config.setLanguageModelPath("src/main/resources/6081.lm"); // Changed path separator to forward slash

        try {
            LiveSpeechRecognizer speechRecognizer = new LiveSpeechRecognizer(config);
            speechRecognizer.startRecognition(true);

            Map<String, Runnable> commandActions = new HashMap<>();
            commandActions.put("open chrome", () -> openChrome());
            commandActions.put("close chrome", () -> closeChrome());
            commandActions.put("calculate", () -> calculate(speechRecognizer));
            // Add more commands and corresponding actions as needed

            SpeechResult speechResult;

            while ((speechResult = speechRecognizer.getResult()) != null) {
                String voiceCommand = speechResult.getHypothesis();
                System.out.println("Voice Command is " + voiceCommand);

                for (String command : commandActions.keySet()) {
                    if (voiceCommand.equalsIgnoreCase(command)) {
                        commandActions.get(command).run();
                        break; // Exit loop after executing the command
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void openChrome() {
        try {
            Runtime.getRuntime().exec("cmd.exe /c start chrome www.google.com");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void closeChrome() {
        try {
            Runtime.getRuntime().exec("cmd.exe /c TASKKILL /IM chrome.exe");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void calculate(LiveSpeechRecognizer speechRecognizer) {
        try {
            // Prompt user for expression
            System.out.println("Please speak the arithmetic expression.");
            SpeechResult result = speechRecognizer.getResult();
            String expression = result.getHypothesis();

            // Build expression using exp4j library
            Expression exp = new ExpressionBuilder(expression).build();

            // Evaluate expression
            double calculationResult = exp.evaluate();

            // Voice feedback for the result
            System.out.println("The result is " + calculationResult);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
