package com.makaan.service.user;

import com.makaan.constants.ApiConstants;
import com.makaan.event.user.UserRegistrationCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.login.UserRegistrationDto;
import com.makaan.service.MakaanService;
import com.makaan.util.JsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sunil on 29/12/15.
 *
 * Interactor for registering user on server
 */
public class UserRegistrationService implements MakaanService{

    private final static String TAG = UserRegistrationService.class.getSimpleName();
    /**
     * Registers user on proptiger server
     *
     * @param dto {@link UserRegistrationDto}
     *
     * */
    public void registerUser(UserRegistrationDto dto) throws IllegalArgumentException{
        if(dto == null){
            throw new IllegalArgumentException("Please supply proper UserRestrationDto");
        }
        makeRegistrationRequest(dto);
    }

    private void makeRegistrationRequest(UserRegistrationDto userRegistrationDto) {
      String requestUrl = buildRegistrationRequest();
        try {
            JSONObject userRegistrationPayload = JsonBuilder.toJson(userRegistrationDto);
            MakaanNetworkClient.getInstance().loginRegisterPost(requestUrl, userRegistrationPayload,
                    new UserRegistrationCallback(), TAG);
        } catch (JSONException e) {
        }
    }

    public static String buildRegistrationRequest() {
        StringBuilder builder = new StringBuilder();
        builder.append(ApiConstants.BASE_URL);
        builder.append("/userservice/app/v1/register");

        return builder.toString();
    }
}
