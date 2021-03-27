package com.stream_pi.runcommandaction;

import com.stream_pi.action_api.actionproperty.property.Property;
import com.stream_pi.action_api.actionproperty.property.Type;
import com.stream_pi.action_api.externalplugin.NormalAction;
import com.stream_pi.util.version.Version;

public class RunCommandAction extends NormalAction
{

    public RunCommandAction()
    {
        setName("Run Command");
        setCategory("Essentials");
        setAuthor("rnayabed");
        setServerButtonGraphic("fas-terminal");
        setHelpLink("https://github.com/stream-pi/essentialactions/blob/master/runcommandaction/README.md");
        setVersion(new Version(1, 1, 1));
    }

    @Override
    public void initProperties() throws Exception
    {
        Property commandProperty = new Property("command", Type.STRING);
        commandProperty.setDisplayName("Command");

        addClientProperties(commandProperty);
    }

    @Override
    public void initAction() throws Exception
    {
    }

    @Override
    public void onActionClicked() throws Exception
    {
        runCommand(getClientProperties().getSingleProperty("command").getStringValue());
    }

    @Override
    public void onShutDown() throws Exception
    {
        // TODO Auto-generated method stub

    }

    private void runCommand(String command) throws Exception
    {
        Runtime rt = Runtime.getRuntime();
        rt.exec(command);
    }
}
