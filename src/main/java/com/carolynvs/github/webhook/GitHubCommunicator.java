package com.carolynvs.github.webhook;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class GitHubCommunicator
{
    private static final String HMAC_SHA1 = "HmacSHA1";

    public Boolean validWebHook(String secret, String body, String signature)
    {
        byte[] rawBody = body.getBytes(StandardCharsets.UTF_8);

        String webhookSecret = secret;
        SecretKeySpec keySpec = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), HMAC_SHA1);

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

    public void setPullRequestStatus(String token, Integer pullRequestId, String state, String description)
    {
        // curl -H "Authorization: token MY_TOKEN" --request POST --data '{"state": "pending", "description": "Build is running", "target_url": "${bamboo.buildResultsUrl}"}' https://api.github.com/repos/USER/REPO/statuses/${bamboo.repository.revision.number} > /dev/null
    }
}
