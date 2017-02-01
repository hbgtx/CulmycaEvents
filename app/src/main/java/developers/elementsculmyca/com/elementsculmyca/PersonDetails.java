package developers.elementsculmyca.com.elementsculmyca;

/**
 * Created by hemba on 1/29/2017.
 */

public class PersonDetails {
    private String username;
    private String password;
    private String accessToken;

    public PersonDetails(String username, String password, String accessToken) {

        this.username = username;
        this.password = password;
        this.accessToken = accessToken;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAccessToken() {
        return accessToken;
    }

}

