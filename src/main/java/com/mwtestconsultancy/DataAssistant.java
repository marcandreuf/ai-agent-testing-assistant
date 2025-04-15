package com.mwtestconsultancy;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.vavr.control.Try;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Scanner;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;

public class DataAssistant {
    private static final Logger logger = LoggerFactory.getLogger(DataAssistant.class);

    static interface DataAssistantService {
        String sendPrompt(String userPrompt);
    }

    public static void main(String[] args) throws SQLException {
        OpenAiChatModel model = OpenAiChatModel
                .builder()
                .apiKey(System.getenv("OPENAI_KEY"))
                .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                .build();

        DataAssistantService dataAssistantChat = AiServices.builder(DataAssistantService.class)
                .chatLanguageModel(model)
                .tools(new DataAssistantTools())
                .build();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("What do you need?");
            String query = scanner.nextLine();
            if (query.equalsIgnoreCase("exit") || query.equalsIgnoreCase("q")) {
                System.out.println("Exiting...");
                break;
            }
            if (query.equalsIgnoreCase("reset") || query.equalsIgnoreCase("r")) {
                logger.debug("Resetting database...");
                DataQuery dataQuery = new DataQuery();
                dataQuery.resetDB();
                logger.debug("Database reset complete.");
                continue;
            }
            String response = sendPromptWithRetry(dataAssistantChat, query);
            if (response == null || response.isEmpty()) {
                logger.debug("No response received.");
                continue;
            }
            logger.debug(response);
        }
        scanner.close();
    }

    /**
     * Sends a prompt to the DataAssistantService with retry logic.
     *
     * TODO: Refactor and clean up this method with SOLID principles.
     * 
     * @param service The DataAssistantService instance.
     * @param query   The user query.
     * @return The response from the service.
     */
    private static String sendPromptWithRetry(DataAssistantService service, String query) {
        IntervalFunction intervalWithCustomExponentialBackoff = IntervalFunction
                .ofExponentialBackoff(Duration.ofSeconds(60).toMillis(), 2.0);

        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .intervalFunction(intervalWithCustomExponentialBackoff)
                .retryOnException(e -> {
                    if (e instanceof dev.ai4j.openai4j.OpenAiHttpException) {
                        return true;
                    }
                    return false;
                })
                .retryExceptions(Exception.class, dev.ai4j.openai4j.OpenAiHttpException.class)
                .build();

        Retry retry = Retry.of("openAiRetry", config);

        // Add retry event listeners
        retry.getEventPublisher()
                .onRetry(event -> {
                    logger.debug("=== Retry Attempt " + event.getNumberOfRetryAttempts() + " ===");
                    String eventMsg = event.getLastThrowable().getMessage();

                    try {
                        String jsonString = eventMsg.substring(eventMsg.indexOf("{"));
                        JSONObject jsonObject = new JSONObject(jsonString);
                        String errorCode = jsonObject.getJSONObject("error").getString("code");
                        
                        if (errorCode.equals("rate_limit_exceeded")) {                            
                            logger.debug("Rate limit exceeded. Waitting for the rate imit to reset...");                            
                            long waitDuration = intervalWithCustomExponentialBackoff
                                .apply(event.getNumberOfRetryAttempts());
                            logger.debug("Waiting {} seconds before next attempt...", 
                                waitDuration / 1000.0);
                        }else{
                            logger.debug("ErrorCode is not rate limit: " + errorCode);    
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.debug("Error parsing JSON: " + e.getMessage());
                    }
                });

        Supplier<String> decoratedSupplier = Retry
                .decorateSupplier(retry, () -> {
                    logger.debug("Attempting API call...");
                    return service.sendPrompt(query);
                });

        return Try.ofSupplier(decoratedSupplier)
                .recover(throwable -> "Error: " + throwable.getMessage())
                .get();
    }

}
