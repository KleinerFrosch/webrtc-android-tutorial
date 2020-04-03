package com.wace.step_03.rtc.participant;

import android.content.Context;

import com.wace.step_03.rtc.PCFactoryProxy;

import org.webrtc.AudioTrack;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

/**
 * author：lwf
 * date：2020/4/1
 * description：
 */
public abstract class Participant {

	protected String connectionId;
	protected String participantName;
	protected List<IceCandidate> candidateList = new ArrayList<>();
	protected PeerConnection peerConnection;
	protected AudioTrack audioTrack;
	protected VideoTrack videoTrack;
	protected MediaStream mediaStream;
	protected PCFactoryProxy pcFactoryProxy;
	protected Context context;

	public Participant(String connectionId, String participantName, Context context) {
		this.connectionId = connectionId;
		this.participantName = participantName;
		this.context = context;
		pcFactoryProxy = new PCFactoryProxy(context);
	}

	public String getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}

	public String getParticipantName() {
		return participantName;
	}

	public void setParticipantName(String participantName) {
		this.participantName = participantName;
	}

	public List<IceCandidate> getCandidateList() {
		return candidateList;
	}

	public void setCandidateList(List<IceCandidate> candidateList) {
		this.candidateList = candidateList;
	}

	public PeerConnection getPeerConnection() {
		return peerConnection;
	}

	public void setPeerConnection(PeerConnection peerConnection) {
		this.peerConnection = peerConnection;
	}

	public AudioTrack getAudioTrack() {
		return audioTrack;
	}

	public void setAudioTrack(AudioTrack audioTrack) {
		this.audioTrack = audioTrack;
	}

	public VideoTrack getVideoTrack() {
		return videoTrack;
	}

	public void setVideoTrack(VideoTrack videoTrack) {
		this.videoTrack = videoTrack;
	}

	public MediaStream getMediaStream() {
		return mediaStream;
	}

	public void setMediaStream(MediaStream mediaStream) {
		this.mediaStream = mediaStream;
	}

	public void dispose() {
		if (peerConnection != null) {
			peerConnection.close();
		}
	}

}
