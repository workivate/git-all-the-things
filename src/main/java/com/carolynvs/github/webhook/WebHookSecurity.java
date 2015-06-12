package com.carolynvs.github.webhook;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class WebHookSecurity
{
    private static final String HMAC_SHA1 = "HmacSHA1";

    public Boolean validate(String body, String signature)
    {
        byte[] rawBody = body.getBytes(StandardCharsets.UTF_8);

        String token = "6ba8caf8dc2b3951e8a4a278aea23a5e97bd6d59";
        SecretKeySpec keySpec = new SecretKeySpec(token.getBytes(StandardCharsets.UTF_8), HMAC_SHA1);

        try {
            Mac mac = Mac.getInstance(HMAC_SHA1);
            mac.init(keySpec);
            byte[] result = mac.doFinal(rawBody);
            String encodedResult = Hex.encodeHexString(result);

            return encodedResult.equals(signature);
        } catch (NoSuchAlgorithmException e) {
            return false;
        } catch (InvalidKeyException e){
            return false;
        }
    }
}
