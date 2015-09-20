package com.carolynvs.gitallthethings.webhook;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class GitHubCommunicator
{
    private static final String HMAC_SHA1 = "HmacSHA1";

    public boolean validWebHook(String secret, String body, String signature)
    {
        byte[] rawBody = body.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA1);
        signature = signature.replace("sha1=", "");

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

    public void setPullRequestStatus(String token, PullRequest pullRequest, GitHubSetCommitStatusRequest statusRequest)
            throws SetPullRequestStatusException

    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(statusRequest);

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(pullRequest.StatusUrl);
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Authorization", "token " + token);
            httpPost.setEntity(new StringEntity(json));

            HttpResponse response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode < 200 || statusCode > 299)
                throw new Exception(String.format("GitHub returned an HTTP status of %s", statusCode));
        }
        catch(Exception ex)
        {
            throw new SetPullRequestStatusException("An error occurred setting the pull request status.", ex);
        }
    }
}

