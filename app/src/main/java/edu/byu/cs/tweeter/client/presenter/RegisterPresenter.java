package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.presenter.view.AuthenticateViewInterface;

public class RegisterPresenter extends AuthenticatePresenter{

    public RegisterPresenter(AuthenticateViewInterface view) {
        super(view);
    }

    public void validateRegistration(String firstName, String lastName, String alias, String password, ImageView imageToUpload) {
        validateAliasAndPassword(alias, password);

        if (firstName.length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastName.length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        if (imageToUpload.getDrawable() == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
    }

    public void register(String firstName, String lastName, String alias, String password, ImageView imageToUpload) {
        // Convert image to byte array.

        Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();

        // Intentionally, Use the java Base64 encoder so it is compatible with M4.
        String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);

        // Send register request.
        getUserService().register(firstName, lastName, alias, password, imageBytesBase64, new RegisterObserver());
    }

    public class RegisterObserver extends BaseAuthenticateObserver {

        @Override
        protected String getAuthenticateActionTag() {
            return "register";
        }
    }
}
