package com.stream_pi.runexecutableaction;

import com.stream_pi.action_api.actionproperty.property.*;
import com.stream_pi.action_api.externalplugin.NormalAction;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.platform.Platform;
import com.stream_pi.util.version.Version;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class RunExecutableAction extends NormalAction
{

    public RunExecutableAction()
    {
        setName("Run Executable");
        setCategory("Essentials");
        setAuthor("rnayabed");
        setServerButtonGraphic("fas-terminal");
        setHelpLink("https://github.com/stream-pi/essentialactions");
        setVersion(new Version(1,0,0));
    }

    @Override
    public void initProperties() throws MinorException
    {
        StringProperty executablePathProperty = new StringProperty("executable_location");
        executablePathProperty.setDisplayName("Executable Location");
        executablePathProperty.setControlType(ControlType.FILE_PATH);
        executablePathProperty.setExtensionFilters(
                new FileExtensionFilter("Executable", "*.*")
        );

        addClientProperties(executablePathProperty);

        if(getServerConnection().getPlatform() == Platform.WINDOWS)
        {
            BooleanProperty runAsAdminProperty = new BooleanProperty("run_as_admin");
            runAsAdminProperty.setDisplayName("Run as Administrator");
            addClientProperties(runAsAdminProperty);
        }
    }

    @Override
    public void onActionClicked() throws MinorException
    {
        String executableLocation = getClientProperties().getSingleProperty("executable_location").getStringValue();

        if(executableLocation.isBlank())
        {
            throw new MinorException("Executable File location is empty");
        }


        File executableFile = new File(executableLocation);

        if(!executableFile.exists())
        {
            throw new MinorException("The File at given path '"+executableLocation+"' does not exist.");
        }

        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(executableFile.getParentFile());

            String command = executableLocation;

            if(getServerConnection().getPlatform()==Platform.WINDOWS)
            {
                boolean runAsAdmin = getClientProperties().getSingleProperty("run_as_admin").getBoolValue();
                if(runAsAdmin)
                {
                    command = "powershell -Command \"Start-Process '"+executableLocation+"' -Verb runAs\"";
                }
            }

            processBuilder.command(command);
            processBuilder.start();
        }
        catch (IOException e)
        {
            throw new MinorException(e.getMessage());
        }
    }
}
