package de.openknowledge.workshop.cloud;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import static de.openknowledge.workshop.cloud.models.Category.newCategory;
import static de.openknowledge.workshop.cloud.models.Post.newPost;
import static de.openknowledge.workshop.cloud.models.Topic.newTopic;
import static de.openknowledge.workshop.cloud.models.User.newUser;
import static java.lang.String.format;

import de.openknowledge.workshop.cloud.models.Category;
import de.openknowledge.workshop.cloud.models.Post;
import de.openknowledge.workshop.cloud.models.Topic;
import de.openknowledge.workshop.cloud.models.User;
import de.openknowledge.workshop.cloud.repositories.CategoryRepository;
import de.openknowledge.workshop.cloud.repositories.TopicRepository;
import de.openknowledge.workshop.cloud.repositories.UserRepository;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class CloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudApplication.class, args);
    }
}

@Configuration
class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Allow all mappings
            .allowedOrigins("*") // Allow all origins
            .allowedMethods("*") // Allow all HTTP methods
            .allowedHeaders("*") // Allow all headers
            .allowCredentials(false) // Allow credentials (e.g., cookies)
            .maxAge(3600); // Max age of the CORS response (in seconds)
    }
}

@Component
class InsertTestData {

    private static final Logger LOGGER = Logger.getLogger(
        InsertTestData.class.getSimpleName()
    );

    @Value("${dynamodb.table}")
    private String tableName;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DynamoDbClient dynamoDbClient;

    @EventListener(ApplicationReadyEvent.class)
    public void insertTestData() {
        User userOne = newUser("Lars", "Röwekamp")
            .withUUID(UUID.fromString("a7ce7689-bf9b-41a4-b4de-8bb5aec9bb21"))
            .withNickName("mobileLarson")
            .withEmail("lars.roewekamp@gmail.com")
            .build();

        User userTwo = newUser("Max", "Mustermann")
            .withUUID(UUID.fromString("b82158e1-8d04-490f-aa25-857160eb6272"))
            .withNickName("madMax")
            .withEmail("max.mustermann@gmail.com")
            .build();

        User userThree = newUser("Frida", "Friedlich")
            .withUUID(UUID.fromString("107ff628-10fc-4fbc-8626-1240c0f25d74"))
            .withNickName("frida-68")
            .withEmail("frida.friedlich@gmail.com")
            .build();

        userRepository.addUser(userOne);
        userRepository.addUser(userTwo);
        userRepository.addUser(userThree);

        LOGGER.info(
            format(
                "Initializes user data: %d users created",
                userRepository.countUsers()
            )
        );

        Category categoryOne = newCategory("Artificial Intelligence")
            .withUUID(UUID.fromString("c2fa96eb-d7d6-4489-bf10-137edc55a1eb"))
            .withDescription("Category handling the different aspects of AI.")
            .withTopicsCount(1)
            .withPostsCount(2)
            .withSlug("artificial_intelligence")
            .build();

        Category categoryTwo = newCategory("Cloud Native")
            .withUUID(UUID.fromString("2fa13fb2-19fe-4f16-bbaa-eaffd3946908"))
            .withDescription(
                "Do you want to learn about Cloud Native? Then is YOUR topic."
            )
            .withTopicsCount(0)
            .withPostsCount(0)
            .withSlug("cloud_native")
            .build();

        Category categoryThree = newCategory("Microservices")
            .withUUID(UUID.fromString("32bcccf5-cd2b-4064-a399-3940e9328c54"))
            .withDescription(
                "Everything you want to know about the magic of microservices."
            )
            .withTopicsCount(0)
            .withPostsCount(0)
            .withSlug("microservices")
            .build();

        categoryRepository.addCategory(categoryOne);
        categoryRepository.addCategory(categoryTwo);
        categoryRepository.addCategory(categoryThree);

        LOGGER.info(
            format(
                "Initializes category data: %d category created",
                categoryRepository.countCategories()
            )
        );

        var topicOneId = UUID.fromString("c679a151-a50c-4928-bc9a-df3cf041370a");

        var getCategoryIdKey = Map.of(
            "pk", AttributeValue.builder().s(format("t->c:%s", topicOneId)).build(),
            "sk", AttributeValue.builder().s("category_id").build()
        );

        var getCategoryIdRequest = GetItemRequest.builder().tableName(tableName).consistentRead(true).key(getCategoryIdKey).build();

        var getCategoryIdResponse = dynamoDbClient.getItem(getCategoryIdRequest);

        if (getCategoryIdResponse.hasItem()) {
            // Test data already inserted
            return;
        }

        Topic topicOne = newTopic("Die Zukunft von AI")
            .withUUID(UUID.fromString("c679a151-a50c-4928-bc9a-df3cf041370a"))
            .withDescription(
                "Ist die künstliche Intelligenz der menschlichen überlegen?"
            )
            .inCategory(categoryOne)
            .createdBy(userOne.getNickName())
            .createdOn(new Date())
            .build();

        Post postOne = newPost(
            "Ich glaube AI hat keine Zukunft. Der Mensch wird immer " +
            "schlauer sein als eine Maschine"
        )
            .withHeader("Mensch der Maschine überlegen!")
            .createdBy(userOne)
            .createdOn(new Date())
            .build();

        Post postTwo = newPost(
            "Da bin ich nicht deiner Meinung Lars! " +
            "Ich glaube, dass die Maschinen uns irgendwann weit " +
            "überlegen sein werden"
        )
            .withHeader("Maschinen auf dem Vormarsch")
            .createdBy(userTwo)
            .createdOn(new Date())
            .build();

        topicOne.addPost(postOne);
        topicOne.addPost(postTwo);

        Topic topicTwo = newTopic("Neuronale Netzwerke")
            .withUUID(UUID.fromString("3efffdfe-b679-4613-8d05-9f25ccc1bc5e"))
            .withDescription("Neuronale Netze in Aktion.")
            .inCategory(categoryOne)
            .createdBy(userTwo.getNickName())
            .createdOn(new Date())
            .build();

        topicRepository.addTopic(topicOne);
        topicRepository.addTopic(topicTwo);

        LOGGER.info(
            format(
                "Initializes topic data: %d topics(s) created",
                topicRepository.countTopics()
            )
        );
    }
}
