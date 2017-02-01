package developers.elementsculmyca.com.elementsculmyca;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hemba on 1/29/2017.
 */

public class SharedPrefUtil implements ConstantUtils {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SharedPrefUtil(Context context) {
        sharedPreferences=context.getSharedPreferences(DETAILS_FILE_NAME,Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    public void savedata(String username,String password,String accessToken){
        editor.putString(USERNAME,username);
        editor.putString(PASSWORD,password);
        editor.putString(ACCESS_CODE,accessToken);
        editor.apply();
    }
    public PersonDetails getDetails(){
        String username=sharedPreferences.getString(USERNAME,null);
        String password=sharedPreferences.getString(PASSWORD,null);
        String accessCode=sharedPreferences.getString(ACCESS_CODE,null);
        PersonDetails p=new PersonDetails(username,password,accessCode);
        return  p;

    }
    public void logOut(){
        editor.clear().apply();
    }




}

