import static org.assertj.core.api.Assertions.assertThat;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.ThreadLocalRandom;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GetUser  {
    //В данной программе проводятся проверки общедоступного API https://reqres.in/api-docs
    @DisplayName("Запрос данных по валидному ID")
    @Test
    void successGetUser() throws Exception {
        //проверка со статичыным значением ID = 2
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("https://reqres.in/api/users/2"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        //проверка возвращения корректных значений
        assertThat(response.statusCode()).isEqualTo(200);

        JSONObject obj = new JSONObject(response.body());
        var data = obj.getJSONObject("data");
        assertThat(data).isNotNull();
        //Проверки сущности data
        assertThat(data.getInt("id")).isEqualTo(2);
        assertThat(data.getString("email")).isEqualTo("janet.weaver@reqres.in");
        assertThat(data.getString("first_name")).isEqualTo("Janet");
        assertThat(data.getString("last_name")).isEqualTo("Weaver");
        assertThat(data.getString("avatar")).isEqualTo("https://reqres.in/img/faces/2-image.jpg");

        var support = obj.getJSONObject("support");
        assertThat(support).isNotNull();
        //Проверки сущности support
        assertThat(support.getString("url")).isEqualTo("https://reqres.in/#support-heading");
        assertThat(support.getString("text"))
                .isEqualTo("To keep ReqRes free, contributions towards server costs are appreciated!");
    }

    @DisplayName("Запрос данных по несуществующему ID, но в валидном формате")
    @Test
    void errorGetUserByFictitiousID() throws Exception {
        //Рандом между 7 и 51, потому что не существует пользователя с данными ID
        int randomNum = ThreadLocalRandom.current().nextInt(7, 51);
        int id = randomNum;

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("https://reqres.in/api/users/"+id))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(response.body()).isEqualTo("{}");
    }

    @DisplayName("Запрос данных без указания ID")
    @Test
    void errorGetUserWithoutID() throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        //В Swagger данный запрос работал без указания ID совсем, здесь пришлось указать значение Null
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("https://reqres.in/api/users/Null"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(response.body()).isEqualTo("{}");
    }
    @DisplayName("Запрос данных по невалидному ID")
    @Test
    void errorGetUserWithIncorrectTypeID() throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        //В данном тесте вместо значения boolOrString можно укзать любое значение, отличное от integer
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("https://reqres.in/api/users/boolOrString"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(response.body()).isEqualTo("{}");
    }
}
