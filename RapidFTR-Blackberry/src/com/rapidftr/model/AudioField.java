package com.rapidftr.model;

import java.io.IOException;

import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.RecordControl;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.rapidftr.controls.AudioControl;
import com.rapidftr.controls.AudioRecordListener;
import com.rapidftr.screens.ManageChildScreen;
import com.rapidftr.utilities.AudioStore;
import com.rapidftr.utilities.Logger;

public class AudioField extends FormField implements AudioRecordListener{
	private Player player;
	private RecordControl rcontrol;
    private AudioStore audioStore;
	private String location = null;
	protected static final String TYPE = "audio_upload_box";
	private VerticalFieldManager manager;
	private LabelField locationField;
	protected AudioField(String name, String helpText) {
		super(name, "Audio", TYPE, helpText);
	}
	
	public String getValue() {
		return (null == location)? "" : location;
	}

    public void setValue(String value) {
    	this.location = value;
    	this.locationField.setText(" " + getValue());
    }

    public void initializeLayout(final ManageChildScreen newChildScreen) {
		manager = new VerticalFieldManager(Field.FIELD_LEFT);
		manager.add(new LabelField("Record Audio: "));
		HorizontalFieldManager recordControl = new HorizontalFieldManager();
		recordControl.add(new AudioControl(this));
		locationField = new LabelField(" " + this.location);
		recordControl.add(locationField);
		manager.add(recordControl);
	}
	
	public boolean start() {
		if (null == this.location || confirmOverWriteAudio()) {
			record();
			return true;
		}
		return false;
	}
	
	private boolean confirmOverWriteAudio() {
        return Dialog.ask(Dialog.D_YES_NO,
                "This will overwrite previously recorded audio. Are you sure?") == Dialog.YES;
    }

	private void record() {
		try {
			player = Manager.createPlayer("capture://audio?encoding=amr");
			player.realize();
			rcontrol = (RecordControl) player.getControl("RecordControl");
			audioStore = new AudioStore();
			rcontrol.setRecordStream(audioStore.getOutputStream());
			rcontrol.startRecord();
			player.start();
		} catch (final Exception e) {
            closeAudioStore();
			throw new RuntimeException(e.getMessage());
		}
	}

    private void closeAudioStore() {
        try {
            if (audioStore != null) {
                audioStore.close();
            }
        } catch (IOException e) {
            Logger.log("Error closing audio file:" + e.toString());
        }
    }

    public void stop() {
		try {
			rcontrol.commit();
            location = audioStore.getFilePath();
			player.close();
		} catch (Exception e) {
			e.printStackTrace();
			new RuntimeException(e.getMessage());
		} finally {
           closeAudioStore(); 
        }
	}

    public net.rim.device.api.ui.Manager getLayout() {
		return manager;
	}
	
	public static AudioField createdFormField(String name, String type, String helpText) {
		if (type.equals(TYPE)) {
			return new AudioField(name, helpText);
		}
		return null;
	}
}
