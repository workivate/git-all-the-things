package ut.com.carolynvs.github.webhook;

import org.junit.Test;
import com.carolynvs.github.webhook.MyPluginComponent;
import com.carolynvs.github.webhook.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}