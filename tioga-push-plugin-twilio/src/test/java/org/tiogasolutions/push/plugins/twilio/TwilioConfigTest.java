package org.tiogasolutions.push.plugins.twilio;

import org.tiogasolutions.push.kernel.clients.DomainProfileEntity;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

@Test
public class TwilioConfigTest {

    @Test
    public void constructorTest() {

        String configId = "123";
        String revision = "456";
        String domainId = "789";
        String accountSid = "0987654321";
        String authToken = "2341234dkfasdfdasdfasd";
        String fromPhoneNumber = "+15552221111";
        String recipient = "+12225551111";

        TwilioConfig twilioConfig = new TwilioConfig(configId, revision, domainId, accountSid, authToken, fromPhoneNumber, recipient);

        assertEquals(twilioConfig.getConfigId(), configId);
        assertEquals(twilioConfig.getRevision(), revision);
        assertEquals(twilioConfig.getDomainId(), domainId);
        assertEquals(twilioConfig.getAccountSid(), accountSid);
        assertEquals(twilioConfig.getAuthToken(), authToken);
        assertEquals(twilioConfig.getTestFromNumber(), fromPhoneNumber);
        assertEquals(twilioConfig.getTestToNumber(), recipient);
    }

    @Test
    public void configApplyTest() {
        String revision = "456";
        String domainId = "789";
        String accountSid = "0987654321";
        String authToken = "2341234dkfasdfdasdfasd";
        String fromPhoneNumber = "+15552221111";
        String recipient = "+12225551111";

        DomainProfileEntity domain = new DomainProfileEntity(domainId, revision, "domainKey", "domainPass", 3, new ArrayList<String>());
        UpdateTwilioConfigAction updateTwilioConfigAction = new UpdateTwilioConfigAction(domain, accountSid, authToken, fromPhoneNumber, recipient);
        TwilioConfig configClone = new TwilioConfig().apply(updateTwilioConfigAction);

        assertNull(configClone.getRevision());
        assertEquals(configClone.getDomainId(), domainId);
        assertEquals(configClone.getAccountSid(), accountSid);
        assertEquals(configClone.getAuthToken(), authToken);
        assertEquals(configClone.getTestFromNumber(), fromPhoneNumber);
        assertEquals(configClone.getTestToNumber(), recipient);
    }
}
