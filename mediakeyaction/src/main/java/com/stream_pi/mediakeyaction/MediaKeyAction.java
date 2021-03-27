package com.stream_pi.mediakeyaction;

import com.stream_pi.action_api.actionproperty.property.Property;
import com.stream_pi.action_api.actionproperty.property.Type;
import com.stream_pi.action_api.externalplugin.NormalAction;
import com.stream_pi.util.platform.Platform;
import com.stream_pi.util.version.Version;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class MediaKeyAction extends NormalAction
{

    public MediaKeyAction()
    {
        setName("Media Key Action");
        setCategory("Essentials");
        setAuthor("rnayabed");
        setServerButtonGraphic("fas-volume-up");
        setHelpLink("https://github.com/stream-pi/essentialactions/tree/master/mediakeyaction");
        setVersion(new Version(1, 1, 0));
    }

    @Override
    public void initProperties() throws Exception
    {
        List<String> states = List.of("Play/Pause", "Previous", "Next", "Vol Up", "Vol Down", "Mute", "Stop");

        Property mediaKeyTypeProperty = new Property("media_key_type", Type.LIST);
        mediaKeyTypeProperty.setListValue(states);
        mediaKeyTypeProperty.setDisplayName("Media Key");

        addClientProperties(mediaKeyTypeProperty);
    }

    @Override
    public void initAction() throws Exception
    {
        if (getServerConnection().getPlatform() == Platform.WINDOWS)
            extractKeyEXEToTmpDirectory();
    }

    private static String absolutePathToExe;

    private void extractKeyEXEToTmpDirectory() throws Exception
    {
        String tmpDirectory = System.getProperty("java.io.tmpdir");

        InputStream is = getClass().getResource("key.exe").openStream();
        absolutePathToExe = tmpDirectory + "/key.exe";
        File file = new File(absolutePathToExe);
        file.deleteOnExit();
        OutputStream os = new FileOutputStream(file);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1)
        {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }

    @Override
    public void onActionClicked() throws Exception
    {
        int state = getClientProperties().getSingleProperty("media_key_type").getSelectedIndex();

        switch (getServerConnection().getPlatform())
        {
            case LINUX:
                linuxHandler(state);
                break;

            case WINDOWS:
                windowsHandler(state);
                break;

            default:
                othersHandler();
        }
    }

    @Override
    public void onShutDown()
    {

    }

    private void runCommand(String command) throws Exception
    {
        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec(command);
        pr.waitFor();
    }

    private void linuxHandler(int state) throws Exception
    {
        try
        {
            switch (state)
            {
                case 0:
                    runCommand("xdotool key XF86AudioPlay");
                    break;
                case 1:
                    runCommand("xdotool key XF86AudioPrev");
                    break;
                case 2:
                    runCommand("xdotool key XF86AudioNext");
                    break;
                case 3:
                    runCommand("xdotool key XF86AudioRaiseVolume");
                    break;
                case 4:
                    runCommand("xdotool key XF86AudioLowerVolume");
                    break;
                case 5:
                    runCommand("xdotool key XF86AudioMute");
                    break;
                case 6:
                    runCommand("xdotool key XF86AudioStop");
                    break;
            }
        } catch (Exception e)
        {
            e.printStackTrace();

            throw new UnsupportedOperationException(
                    "It looks like 'xdotool' is not installed in this computer. Please install it and try again."
            );
        }
    }

    private void windowsHandler(int state) throws Exception
    {
        try
        {
            String exe = "\"" + absolutePathToExe + "\"";
            switch (state)
            {
                case 0:
                    runCommand(exe + " 0xB3");
                    break;
                case 1:
                    runCommand(exe + " 0xB1");
                    break;
                case 2:
                    runCommand(exe + " 0xB0");
                    break;
                case 3:
                    runCommand(exe + " 0xAF");
                    break;
                case 4:
                    runCommand(exe + " 0xAE");
                    break;
                case 5:
                    runCommand(exe + " 0xAD");
                    break;
                case 6:
                    runCommand(exe + " 0xB2");
                    break;
            }
        } catch (Exception e)
        {
            e.printStackTrace();

            throw new UnsupportedOperationException(
                    "Internal Error occurred. Check logs, stacktrace. Report to us."
            );
        }
    }


    private void othersHandler()
    {
        throw new UnsupportedOperationException(
                "Media Keys arent supported on this platform yet. Check out the supported platforms by clicking the '?' button on Plugins Pane"
        );
    }
}
