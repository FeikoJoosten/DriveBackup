package ratismal.drivebackup;

import org.bukkit.Bukkit;
import ratismal.drivebackup.config.Config;
import ratismal.drivebackup.ftp.FTPUploader;
import ratismal.drivebackup.googledrive.GoogleUploader;
import ratismal.drivebackup.handler.PlayerListener;
import ratismal.drivebackup.onedrive.OneDriveUploader;
import ratismal.drivebackup.util.*;
import ratismal.drivebackup.util.Timer;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Ratismal on 2016-01-22.
 */

public class UploadThread implements Runnable {

    private boolean forced = false;

    /**
     * Forced upload constructor
     *
     * @param forced Is the backup forced?
     */
    public UploadThread(boolean forced) {
        this.forced = forced;
    }

    /**
     * Base constructor
     */
    public UploadThread() {
    }

    /**
     * Run function in the upload thread
     */
    @Override
    public void run() {
        if (PlayerListener.doBackups || forced) {
            MessageUtil.sendMessageToAllPlayers(Config.getBackupStart());
            // Create Backup Here
            
            String format = Config.getBackupFormat();
            boolean create = Config.getShouldBackup();

            MessageUtil.sendConsoleMessage("Doing backups for the root folder");
            if (create) {
                FileUtil.makeBackup();
            }

            File file = FileUtil.getFileToUpload("", format, false);
            ratismal.drivebackup.util.Timer timer = new Timer();
            try {
                if (Config.isGoogleEnabled()) {
                    MessageUtil.sendConsoleMessage("Uploading backup to GoogleDrive");
                    timer.start();
                    GoogleUploader.uploadFile(file);
                    timer.end();
                    MessageUtil.sendConsoleMessage(timer.getUploadTimeMessage(file));
                }
                if (Config.isOnedriveEnabled()) {
                    MessageUtil.sendConsoleMessage("Uploading backup to OneDrive");
                    //Couldn't get around static issue, declared a new Instance.
                    OneDriveUploader oneDrive = new OneDriveUploader();
                    timer.start();
                    oneDrive.uploadFile(file);
                    timer.end();
                    MessageUtil.sendConsoleMessage(timer.getUploadTimeMessage(file));
                }
                if (Config.isFtpEnabled()) {
                    MessageUtil.sendConsoleMessage("Uploading backup to FTP");
                    timer.start();
                    FTPUploader.uploadFile(file);
                    timer.end();
                    MessageUtil.sendConsoleMessage(timer.getUploadTimeMessage(file));
                }

                if (!Config.keepLocalBackup()) {
                    if (file.delete()) {
                        MessageUtil.sendConsoleMessage("Old backup deleted.");
                    } else {
                        MessageUtil.sendConsoleMessage("Failed to delete backup " + file.getAbsolutePath());
                    }
                }
                //MessageUtil.sendConsoleMessage("File Uploaded.");
            } catch (Exception e) {
                if (Config.isDebug())
                    e.printStackTrace();
            }
            
            if (forced) {
                MessageUtil.sendMessageToAllPlayers(Config.getBackupDone());
            } else {
                MessageUtil.sendMessageToAllPlayers(Config.getBackupDone() + " " + Config.getBackupNext().replaceAll("%TIME", String.valueOf(Config.getBackupDelay() / 20 / 60)));
            }
            if (Bukkit.getOnlinePlayers().size() == 0 && PlayerListener.doBackups) {
                MessageUtil.sendConsoleMessage("Disabling automatic backups due to inactivity.");
                PlayerListener.doBackups = false;
            }
        } else {
            MessageUtil.sendConsoleMessage("Skipping backup.");
        }
    }

}